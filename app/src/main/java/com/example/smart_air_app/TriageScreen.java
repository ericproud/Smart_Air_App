package com.example.smart_air_app;

import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;

import com.example.smart_air_app.triage.TriageEntry;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

public class TriageScreen extends AppCompatActivity {

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

        redFlagsGroup.setOnCheckedStateChangeListener((chipGroup, checkedIds) -> {
            boolean emergencyStatus = false; // true when one of the red flags is checked
            if (checkedIds.isEmpty()) {
                emergencyStatus = false;
            } else {
                emergencyStatus = true;
            }
            emergencyBtn.setEnabled(emergencyStatus); // enable emergency btn when red flag checked
        });

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

        // At least one of yes/no must be checked. Only check for btnRescueYes!!!!!!!!!!!!!!!!
        MaterialButton btnRescueYes = findViewById(R.id.btnRescueYes);
        boolean recentRescue = btnRescueYes.isChecked();

        return new TriageEntry(redFlags, recentRescue, PEFvalue);

    }

    public void emergencyButtonPressed(View view) {

        TriageEntry entry = createTriageEntry();

    }

}