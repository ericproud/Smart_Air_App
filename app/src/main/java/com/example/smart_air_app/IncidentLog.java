package com.example.smart_air_app;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.triage.TriageEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IncidentLog extends AppCompatActivity {

    LinearLayout incidentLogField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_incident_log);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        incidentLogField = findViewById(R.id.incidentLogField);

        String childUID = getIntent().getStringExtra("childUID");
        loadTriageEntries(childUID);
    }

    void loadTriageEntries(String childUID) {
       DatabaseReference ref = FirebaseDatabase.getInstance()
               .getReference("TriageEntries")
               .child(childUID);

        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            DataSnapshot triageList = task.getResult();
            if (!triageList.exists()) {
                return;
            }

            // Go thru each triage ID
            for (DataSnapshot triage : triageList.getChildren()) {
                // Convert snapshot into your TriageEntry class
                TriageEntry entry = triage.getValue(TriageEntry.class);

                if (entry != null) {
                    addTriageToScreen(entry);
                }
            }
        });

    }

    @SuppressLint("SetTextI18n") // Added to get rid of annoying warning in code about long string
    void addTriageToScreen(TriageEntry entry) {
        // Create TextView for display triage info
        TextView triageInfo = new TextView(this);

        triageInfo.setText(
                "Triage ID: " + entry.getTriageID() + "\n" +
                        "No Full Sentences: " + entry.getRedFlag(0) + "\n" +
                        "Retractions: " + entry.getRedFlag(1) + "\n" +
                        "Blue/Gray Lips/Nails: " + entry.getRedFlag(2) + "\n" +
                        "Recent Rescue Done: " + entry.getRecentRescue() + "\n" +
                        "PEF: " + entry.getPEF() + "\n" +
                        "Emergency: " + entry.getEmergencyStatus() + "\n"
        );

        triageInfo.setPadding(20, 20, 20, 20);
        triageInfo.setTextSize(16f);
        triageInfo.setBackgroundColor(Color.parseColor("#EEEEEE"));

        // Add spacing between entries
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);

        triageInfo.setLayoutParams(params);

        // Add the view to your layout
        incidentLogField.addView(triageInfo);
    }

}