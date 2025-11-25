package com.example.smart_air_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.log_rescue_attempt.LogRescueAttemptActivity;

public class ChildHomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_child_home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button logRescueAttempt = findViewById(R.id.parentLogRescueAttemptButton);
        logRescueAttempt.setOnClickListener(view -> {
            startActivity(new Intent(ChildHomeScreen.this, LogRescueAttemptActivity.class));
        });

        Button triageButton = findViewById(R.id.parentEmergencyTriageButton);
        triageButton.setOnClickListener(view -> {
            startActivity(new Intent(ChildHomeScreen.this, TriageScreen.class));
        });
    }
}