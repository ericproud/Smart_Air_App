package com.example.smart_air_app;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.triage.TriageEntry;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MedicineLogs extends AppCompatActivity {
    LinearLayout MedicineLogField;
    MaterialButtonToggleGroup toggleLogType;
    String logType;
    String childUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medicine_logs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        childUID = getIntent().getStringExtra("childUID");
        MedicineLogField = findViewById(R.id.MedicinelogFields);
        loadMedicineEntries(childUID);

        MaterialButtonToggleGroup toggleLogType = findViewById(R.id.toggleLogType);
        toggleLogType.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    MedicineLogField.removeAllViews();
                    loadMedicineEntries(childUID);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String childUID = getIntent().getStringExtra("childUID");
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Permissions")
                .child(childUID)
                .child("rescue logs");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean rescueLogsShared = snapshot.getValue(Boolean.class);
                    Chip sharedTag = findViewById(R.id.sharedTag);
                    if (rescueLogsShared) {
                        sharedTag.setVisibility(View.VISIBLE);
                    } else {
                        sharedTag.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    void loadMedicineEntries(String childUID) {
        toggleLogType = findViewById(R.id.toggleLogType);
        int logTypeID = toggleLogType.getCheckedButtonId();

        if (logTypeID != View.NO_ID) {
            Button selectedButton = findViewById(logTypeID);
            logType = selectedButton.getText().toString().trim();
        } else {
            return;
        }

        if (logType.equals("Controller Medicine")) {
            loadControllerMedicine(childUID);
        }
        if (logType.equals("Rescue Medicine")) {
            loadRescueMedicine(childUID);
        }
    }

    void loadControllerMedicine(String childUID) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("ControllerLogs")
                .child(childUID);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String date = getDate(childSnapshot.getKey().toString());
                    String time = getTime(childSnapshot.getKey().toString());
                    Long amountUsed = childSnapshot.child("amountUsed").getValue(Long.class);
                    String breathRating = childSnapshot.child("breathRating").getValue(String.class);
                    Long PEFbefore = childSnapshot.child("Pre PEF").getValue(Long.class);
                    Long PEFafter = childSnapshot.child("postPEF").getValue(Long.class);
                    Long shortnessRating = childSnapshot.child("shortnessBreathRating").getValue(Long.class);
                    writeControllerLog(date, time, amountUsed, breathRating, PEFbefore, PEFafter, shortnessRating);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    void loadRescueMedicine(String childUID) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("RescueAttempts")
                .child(childUID);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                    Long dosage = childSnapshot.child("dosage").getValue(Long.class);
                    Long PEFbefore = childSnapshot.child("peakFlowBefore").getValue(Long.class);
                    Long PEFafter = childSnapshot.child("peakFlowAfter").getValue(Long.class);
                    Long timestamp = childSnapshot.child("timestamp").getValue(Long.class);
                    Boolean isTriageIncident = childSnapshot.child("triageIncident").getValue(Boolean.class);

                    List<String> symptomsList = new ArrayList<>();
                    for (DataSnapshot symptomsSnapshot : childSnapshot.child("symptoms").getChildren()) {
                        symptomsList.add(symptomsSnapshot.getValue(String.class));
                    }

                    List<String> triggersList = new ArrayList<>();
                    for (DataSnapshot triggersSnapshot : childSnapshot.child("triggers").getChildren()) {
                        triggersList.add(triggersSnapshot.getValue(String.class));
                    }

                    String symptoms = "    ";
                    for (String s : symptomsList) {
                        symptoms += s + "\n    ";
                    }
                    if (symptoms.equals("    ")) {
                        symptoms = "    None";
                    }

                    String triggers = "    ";
                    for (String t : triggersList) {
                        triggers += t + "\n    ";
                    }
                    if (triggers.equals("    ")) {
                        triggers = "    None";
                    }

                    String triageIncident = "";
                    if (isTriageIncident) {
                        triageIncident = "Yes";
                    } else {
                        triageIncident = "No";
                    }

                    if (dosage == null) {
                        dosage = 0L;
                    }

                    if (PEFbefore == null) {
                        PEFbefore = 0L;
                    }

                    if (PEFafter == null) {
                        PEFafter = 0L;
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yy");
                    String date = dateFormat.format(new Date(timestamp));

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    String time = timeFormat.format(new Date(timestamp));

                    writeRescueLog(date, time, dosage, PEFbefore, PEFafter, triageIncident, triggers, symptoms);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    void writeControllerLog(String date, String time, Long amountUsed, String breathRating, Long PEFbefore, Long PEFafter, Long shortnessRating) {
        TextView MedicineInfo = new TextView(this);

        MedicineInfo.setText(
                "Date: " + date + "\n" +
                        "Time: " + time + "\n" +
                        "Shortness Of Breath Rating: " + shortnessRating + "\n" +
                        "Dose Administered Used: " + amountUsed + "\n" +
                        "PEF Before Administration: " + PEFbefore + "\n" +
                        "PEF After Adiministration: " + PEFafter + "\n" +
                        "Feeling After Usage: " + breathRating + "\n"
        );

        MedicineInfo.setPadding(20, 20, 20, 20);
        MedicineInfo.setTextSize(16f);
        MedicineInfo.setBackgroundColor(Color.parseColor("#90D5FF"));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);

        MedicineInfo.setLayoutParams(params);

        MedicineLogField.addView(MedicineInfo);
    }

    void writeRescueLog(String date, String time, long dosage, long PEFbefore, long PEFafter, String isTriageIncident, String triggers, String symptoms) {
        TextView MedicineInfo = new TextView(this);

        MedicineInfo.setText(
                "Date: " + date + "\n" +
                        "Time: " + time + "\n" +
                        "Was This A Triage Event?: " + isTriageIncident + "\n" +
                        "Dose Administered Used: " + dosage + "\n" +
                        "PEF Before Administration: " + PEFbefore + "\n" +
                        "PEF After Adiministration: " + PEFafter + "\n" +
                        "Triggers: \n" + triggers + "\n" +
                        "Symptoms: \n" + symptoms + "\n"
        );


                MedicineInfo.setPadding(20, 20, 20, 20);
        MedicineInfo.setTextSize(16f);
        MedicineInfo.setBackgroundColor(Color.parseColor("#90D5FF"));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);

        MedicineInfo.setLayoutParams(params);

        MedicineLogField.addView(MedicineInfo);
    }

    public static String getDate(String dateTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
            Date date = inputFormat.parse(dateTime);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy");
            return outputFormat.format(date).toUpperCase();
        } catch (ParseException e) {
            return "";
        }
    }

    public static String getTime(String dateTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
            Date date = inputFormat.parse(dateTime);
            SimpleDateFormat outputFormat = new SimpleDateFormat("H:mm");
            return outputFormat.format(date);
        } catch (ParseException e) {
            return "";
        }
    }
}


