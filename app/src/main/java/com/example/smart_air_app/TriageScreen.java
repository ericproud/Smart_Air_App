package com.example.smart_air_app;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.ChipGroup;

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
}