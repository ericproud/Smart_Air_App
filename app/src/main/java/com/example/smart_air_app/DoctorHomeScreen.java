package com.example.smart_air_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.utils.BuildPDFs;
import com.example.smart_air_app.utils.Logout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorHomeScreen extends AppCompatActivity {
    String childUID;
    String childName;
    HashMap<String, String> mapOfFields;
    DatabaseReference dbRef;
    int fieldsToLoad = 1;
    int fieldsLoaded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_home_screen);

        patientSpinner = findViewById(R.id.patientSpinner);
        patientName = findViewById(R.id.patientName);
        enterOTC = findViewById(R.id.enterOTC);
        submitOTCButton = findViewById(R.id.submitOTCButton);

        dbRef = FirebaseDatabase.getInstance().getReference();
        childUID = getIntent().getStringExtra("patientUID");
        childName = getIntent().getStringExtra("patientName");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            getPatientUIDs();
            setUpOTC();

            return insets;
        });

        String doctorID = FirebaseAuth.getInstance().getUid();
        DatabaseReference d_ref = FirebaseDatabase.getInstance().getReference("Users");
        helperOnboard(d_ref, doctorID);

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            Logout.logout(this);
        });enterOTC.setHint("Enter One-Time Code");


        TextView doctorNameText = findViewById(R.id.doctorNameText);
        dbRef.child("Users").child(doctorID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot childSnapshot){
                String firstName = childSnapshot.child("firstName").getValue(String.class);
                String lastName = childSnapshot.child("lastName").getValue(String.class);
                String childName = firstName + " " + lastName;
                doctorNameText.setText(childName);
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){
            }
        });
    }

    Spinner patientSpinner;
    TextView patientName;
    EditText enterOTC;
    Button submitOTCButton;
    String UID = FirebaseAuth.getInstance().getUid();
    ArrayList<String> patientUIDs = new ArrayList<>();
    ArrayList<String> patientNames = new ArrayList<>();
    HashMap<String, String> patientNameToUID = new HashMap<>();

    void getPatientUIDs() {
        // Get patient UIDs from children linked to doctor from db
        dbRef.child("Users").child(UID).child("Patients").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    patientUIDs.add(childSnapshot.getKey());
                }
                getPatientNames();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    void getPatientNames() {
        // Get patient names from children linked to doctor from db
        for (String patientUID : patientUIDs) {
            dbRef.child("Users").child(patientUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String patientName = firstName + " " + lastName;
                    if (firstName != null && lastName != null) {
                        patientNames.add(patientName);
                        patientNameToUID.put(patientName, patientUID);
                    }
                    if (patientNames.size() == patientUIDs.size()) {
                        setUpSpinner();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    void setUpSpinner() {
        // Create dropdown menu and set it so that when a patient is selected, it sets the behavior of the buttons to access patient records
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, patientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patientSpinner.setAdapter(adapter);
        patientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String currentPatientName = patientSpinner.getSelectedItem().toString();
                String currentPatientUID = patientNameToUID.get(currentPatientName);

                // Setting Button functionality
                setButtons(currentPatientName, currentPatientUID);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    void setButtons(String patientName, String patientUID){
        Button summaryChartsButton = findViewById(R.id.summaryChartsButton);

        summaryChartsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildPDFs.buildProviderReport(DoctorHomeScreen.this, dbRef, patientUID, patientName);
            }
        });
    }

    void setUpOTC() {
        // Check if the one time code corresponds to a child's UID and if it does, add that child to the doctors list of patients
        submitOTCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OTC = enterOTC.getText().toString();
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child("OTC's").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean foundChild = false;
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            if (childSnapshot.getValue() == null) {
                                continue;
                            }
                            else if (childSnapshot.getValue().equals(OTC)) {
                                foundChild = true;
                                String patientUID = childSnapshot.getKey();
                                dbRef.child("Users").child(UID).child("Patients").child(patientUID).setValue(true);
                            }
                        }
                        if (!foundChild) {
                            enterOTC.setHint("Invalid One Time Code, try again");
                            enterOTC.setText("");
                            enterOTC.setHintTextColor(Color.parseColor("#FF0000"));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void helperOnboard(DatabaseReference d_ref, String id) {
        //reference to see if the user is onboarded
        DatabaseReference o_ref = d_ref.child(id).child("isOnboarded");

        o_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //the isOnboarded should exist this is just a safety net
                if (snapshot.exists()) {
                    Boolean val = snapshot.getValue(Boolean.class);

                    //if false then send them to parent onboarding also check if null for safety
                    if (val != null && !val) {
                        Intent intent = new Intent(DoctorHomeScreen.this, ProviderOnboardingScreen1.class);
                        startActivity(intent);

                        //when they finish the onboarding set this to true
                        o_ref.setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}