package com.example.smart_air_app;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.concurrent.atomic.AtomicInteger;


import com.example.smart_air_app.triage.TriageEntry;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class TriageScreen extends AppCompatActivity {

    boolean yesornoChecked() {
        // Returns true when either yes or no is checked
        MaterialButton btnRescueYes = findViewById(R.id.btnRescueYes);
        MaterialButton btnRescueNo = findViewById(R.id.btnRescueNo);
        return btnRescueNo.isChecked() || btnRescueYes.isChecked();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_triage_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ChipGroup redFlagsGroup = findViewById(R.id.redFlagsGroup);
        Button emergencyBtn = findViewById(R.id.btnEmergency);
        Button homeStepsBtn = findViewById(R.id.btnHomeSteps);
        MaterialButton btnRescueYes = findViewById(R.id.btnRescueYes);
        MaterialButton btnRescueNo = findViewById(R.id.btnRescueNo);
        MaterialButtonToggleGroup btnRescueGroup = findViewById(R.id.btnRescueGroup);
        Chip redFlag0 = findViewById(R.id.btnNoFullSentences);
        Chip redFlag1 = findViewById(R.id.btnRetractions);
        Chip redFlag2 = findViewById(R.id.btnBlueGray);

        redFlagsGroup.setOnCheckedStateChangeListener((chipGroup, checkedIds) -> {
            if (yesornoChecked()) {
                boolean emergencyStatus = false; // true when one of the red flags is checked
                if (checkedIds.isEmpty()) {
                    emergencyStatus = false;
                } else {
                    emergencyStatus = true;
                }
                emergencyBtn.setEnabled(emergencyStatus); // enable emergency btn when red flag checked
                homeStepsBtn.setEnabled(!emergencyStatus); // enable home steps btn when no red flag checked
            } else {
                // if neither "yes" nor "no" is checked, don't enable a button
                emergencyBtn.setEnabled(false);
                homeStepsBtn.setEnabled(false);
            }
        });

        btnRescueGroup.addOnButtonCheckedListener((toggleGroup, checkedId, isChecked) -> {
            if (yesornoChecked()) {
                boolean emergencyStatus = false; // true when one of the red flags is checked
                if (redFlag0.isChecked() || redFlag1.isChecked() || redFlag2.isChecked()) {
                    emergencyStatus = true;
                }

                emergencyBtn.setEnabled(emergencyStatus); // enable emergency btn when red flag checked
                homeStepsBtn.setEnabled(!emergencyStatus); // enable home steps btn when no red flag checked
            } else {
                // if neither "yes" nor "no" is checked, don't enable a button
                emergencyBtn.setEnabled(false);
                homeStepsBtn.setEnabled(false);
            }
        });

        // Auto-escalate to emergency timer
        TextView timerText = findViewById(R.id.emergencyTimer);

        CountDownTimer emergencyTimer;
        // 10 mins = 600000ms
        emergencyTimer = new CountDownTimer(600000, 1000) {
            @Override
            public void onTick(long msUntilFinished) {
                long seconds = msUntilFinished / 1000;
                long minutes = seconds / 60;
                long remainingSeconds = seconds % 60;

                String formatted = String.format("%02d:%02d", minutes, remainingSeconds);
                timerText.setText(formatted);
            }

            @Override
            public void onFinish() {
                timerText.setText("00:00");
                emergencyButtonPressed(findViewById(R.id.btnEmergency));
            }
        };

        emergencyTimer.start();

    }

    public TriageEntry createTriageEntry() {

        // Get triage form data

        TextInputEditText inputBoxPEF = findViewById(R.id.inputPEF);
        double PEFvalue;
        if (inputBoxPEF.getText() == null || inputBoxPEF.getText().toString().trim().isEmpty()) {
            PEFvalue = -1; // User did not input PEF
        }
        else {
            PEFvalue = Double.parseDouble(inputBoxPEF.getText().toString().trim());
        }

        Chip redFlag0 = findViewById(R.id.btnNoFullSentences);
        Chip redFlag1 = findViewById(R.id.btnRetractions);
        Chip redFlag2 = findViewById(R.id.btnBlueGray);
        boolean[] redFlags = {redFlag0.isChecked(), redFlag1.isChecked(), redFlag2.isChecked()};

        boolean emergency = redFlag0.isChecked() || redFlag1.isChecked() || redFlag2.isChecked();

        // At least one of yes/no must be checked. Only check for btnRescueYes
        MaterialButton btnRescueYes = findViewById(R.id.btnRescueYes);
        boolean recentRescue = btnRescueYes.isChecked();

        return new TriageEntry(redFlags, recentRescue, PEFvalue, emergency);

    }

    public void emergencyButtonPressed(View view) {

        TriageEntry entry = createTriageEntry();
        saveTriageToDatabase(entry);

    }

    public void btnHomeSteps(View view) {

        TriageEntry entry = createTriageEntry();
        saveTriageToDatabase(entry);

    }

    public void saveTriageToDatabase(TriageEntry entry) {

        // Get current child UID
        String childUID = FirebaseAuth.getInstance().getUid();

        System.out.println(entry.getTriageID());

        FirebaseDatabase.getInstance()
                .getReference("TriageEntries")
                .child(childUID)
                .child("TriageID" + Integer.toString(entry.getTriageID()))
                .child("PEF")
                .setValue(entry.getPEF());
        FirebaseDatabase.getInstance()
                .getReference("TriageEntries")
                .child(childUID)
                .child("TriageID" + Integer.toString(entry.getTriageID()))
                .child("childUID")
                .setValue(entry.getChildUID());

        // Firebase doesn't accept arrays, so save a boolean for each red flag
        FirebaseDatabase.getInstance()
                .getReference("TriageEntries")
                .child(childUID)
                .child("TriageID" + Integer.toString(entry.getTriageID()))
                .child("NoFullSentences")
                .setValue(entry.getRedFlag(0));
        FirebaseDatabase.getInstance()
                .getReference("TriageEntries")
                .child(childUID)
                .child("TriageID" + Integer.toString(entry.getTriageID()))
                .child("Retractions")
                .setValue(entry.getRedFlag(1));
        FirebaseDatabase.getInstance()
                .getReference("TriageEntries")
                .child(childUID)
                .child("TriageID" + Integer.toString(entry.getTriageID()))
                .child("BlueGrayLipsNails")
                .setValue(entry.getRedFlag(2));

        FirebaseDatabase.getInstance()
                .getReference("TriageEntries")
                .child(childUID)
                .child("TriageID" + Integer.toString(entry.getTriageID()))
                .child("RecentRescueDone")
                .setValue(entry.getRecentRescue());
        FirebaseDatabase.getInstance()
                .getReference("TriageEntries")
                .child(childUID)
                .child("TriageID" + Integer.toString(entry.getTriageID()))
                .child("emergencyStatus")
                .setValue(entry.getEmergencyStatus());

    }

}