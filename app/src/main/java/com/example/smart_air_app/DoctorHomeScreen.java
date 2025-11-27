package com.example.smart_air_app;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorHomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            patientSpinner = findViewById(R.id.patientSpinner);
            patientName = findViewById(R.id.patientName);
            enterOTC = findViewById(R.id.enterOTC);
            submitOTCButton = findViewById(R.id.submitOTCButton);

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
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
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
        Button controllerAdherenceButton = findViewById(R.id.controllerAdherenceButton);
        Button rescueLogsButton = findViewById(R.id.rescueLogsButton);
        Button triageIncidentsButton = findViewById(R.id.triageIncidentsButton);
        Button summaryChartsButton = findViewById(R.id.summaryChartsButton);
        /*
        controllerAdherenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorHomeScreen.this, DoctorControllerAdherenceScreen.class);
                intent.putExtra("patientName", patientName);
                intent.putExtra("PatientUID", patientUID);
                startActivity(intent);
            }
        });

        rescueLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorHomeScreen.this, DoctorRescueLogsScreen.class);
                intent.putExtra("patientName", patientName);
                intent.putExtra("PatientUID", patientUID);
                startActivity(intent);
            }
        });

        triageIncidentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorHomeScreen.this, DoctorTriageIncidentsScreen.class);
                intent.putExtra("patientName", patientName);
                intent.putExtra("PatientUID", patientUID);
                startActivity(intent);
            }
        });

        summaryChartsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorHomeScreen.this, DoctorSummaryChartsScreen.class);
                intent.putExtra("patientName", patientName);
                intent.putExtra("PatientUID", patientUID);
                startActivity(intent);
            }
        });
        */
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