package com.example.smart_air_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.log_rescue_attempt.LogRescueAttemptActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class EmergencyScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emergency_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // Back button
        MaterialToolbar toolbar = findViewById(R.id.materialToolbar);
        toolbar.setNavigationOnClickListener(view -> {
            if (TriageScreen.currentActivity != null) {
                TriageScreen.currentActivity.finish();
            }
            finish();
        });

        Button returnButton = findViewById(R.id.btnFeelingBetter);
        returnButton.setOnClickListener(view -> {
            if (TriageScreen.currentActivity != null) {
                TriageScreen.currentActivity.finish();
            }
            finish();
        });
    }
}