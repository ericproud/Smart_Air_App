package com.example.smart_air_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        mapOfFields = new HashMap<>();
        mapOfFields.put("Shortness of breath", "8");
        mapOfFields.put("Chest tightness", "5");
        mapOfFields.put("Chest pain", "3");
        mapOfFields.put("Wheezing", "1");
        mapOfFields.put("Trouble sleeping", "0");
        mapOfFields.put("Coughing", "3");
        mapOfFields.put("Other", "22");
        mapOfFields.put("Controller Adherence", "0%");
        mapOfFields.put("Rescue Attempts Per Day", "0");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            getPatientUIDs();
            setUpOTC();

            return insets;
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

                // Setting patient name text
                patientName.setText(currentPatientName);

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
                BuildPDFs.buildProviderReport(DoctorHomeScreen.this, dbRef, patientUID, patientName, mapOfFields);
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
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            if (childSnapshot.getValue() == null) {
                                continue;
                            }
                            else if (childSnapshot.getValue().equals(Long.parseLong(OTC))) {
                                String patientUID = childSnapshot.getKey();
                                dbRef.child("Users").child(UID).child("Patients").child(patientUID).setValue(true);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }
}