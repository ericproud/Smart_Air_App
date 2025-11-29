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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

        // Back button
        MaterialToolbar toolbar = findViewById(R.id.materialToolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        incidentLogField = findViewById(R.id.incidentLogField);

        String childUID = getIntent().getStringExtra("childUID");
        loadTriageEntries(childUID);
    }

    void loadTriageEntries(String childUID) {

        FirebaseDatabase.getInstance()
               .getReference("TriageEntries")
               .child(childUID)
               .get()
               .addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            DataSnapshot triageList = task.getResult();
            if (!triageList.exists()) {
                return;
            }

            // Sort triages by dateTime
            List<DataSnapshot> triages = new ArrayList<>();
            for (DataSnapshot triage : triageList.getChildren()) {
                triages.add(triage);
            }

            triages.sort(Comparator.comparing(o ->
                    o.child("dateTime").getValue(Long.class)
            ));

            for (DataSnapshot triage : triages) {
                // Convert snapshot into TriageEntry
                boolean noFullSentences = triage.child("NoFullSentences").getValue(Boolean.class);
                boolean retractions = triage.child("Retractions").getValue(Boolean.class);
                boolean blueGray = triage.child("BlueGrayLipsNails").getValue(Boolean.class);
                boolean[] redFlags = {noFullSentences, retractions, blueGray};
                boolean recentRescue = triage.child("RecentRescueDone").getValue(Boolean.class);
                double PEF = triage.child("PEF").getValue(Double.class);
                boolean emergency = triage.child("emergencyStatus").getValue(Boolean.class);
                long realDate = triage.child("dateTime").getValue(Long.class);


                TriageEntry entry = new TriageEntry(redFlags, recentRescue, PEF, emergency);
                addTriageToScreen(entry, realDate);
            }
        });

    }

    @SuppressLint("SetTextI18n") // Added to get rid of annoying warning in code about long string
    void addTriageToScreen(TriageEntry entry, long realDate) {
        // Create TextView for display triage info
        TextView triageInfo = new TextView(this);

        String redFlagsText = "";
        if (!entry.getRedFlag(0) && !entry.getRedFlag(1) && !entry.getRedFlag(2)) {
            // No red flags
            redFlagsText = "None\n";
        } else {
            if (entry.getRedFlag(0)) {
                redFlagsText += "Can't speak full sentences\n";
            }
            if (entry.getRedFlag(1)) {
                redFlagsText += "Chest pulling in (retractions)\n";
            }
            if (entry.getRedFlag(2)) {
                redFlagsText += "Blue/gray lips or nails\n";
            }
        }

        String recentRescueText;
        if (entry.getRecentRescue()) {
            recentRescueText = "Rescue inhaler was used recently\n";
        } else {
            recentRescueText = "Rescue inhaler was NOT used recently\n";
        }

        String guidanceShown;
        if (entry.getEmergencyStatus()) {
            guidanceShown = "Child was advised to call emergency services (911)";
        } else {
            guidanceShown = "Child was given home steps to improve their condition";
        }

        String PEFText;
        if (entry.getPEF() == -1) {
            PEFText = "Child did not enter a PEF value\n";
        } else {
            PEFText = "PEF: " + entry.getPEF();
        }

        Date date = new Date(realDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        triageInfo.setText(
                "Date: " + dateFormat.format(date) + "\n\n" +
                "Red flags:\n" +
                        redFlagsText + "\n" +
                        recentRescueText + "\n" +
                        PEFText + "\n\n" +
                        guidanceShown
        );

        triageInfo.setPadding(20, 20, 20, 20);
        triageInfo.setTextSize(16f);
        triageInfo.setBackgroundColor(Color.parseColor("#90D5FF"));

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