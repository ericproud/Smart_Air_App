package com.example.smart_air_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.inventory.InventoryActivity;
import com.example.smart_air_app.log_rescue_attempt.LogRescueAttemptActivity;
import com.example.smart_air_app.utils.Logout;
import com.google.android.material.button.MaterialButton;

import controller_log.ControllerLoggingScreen;

public class ParentChildHomeScreen extends AppCompatActivity {

    private String childUserId;
    private String childName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parent_child_home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent childData = getIntent();
        childUserId = childData.getStringExtra("childUID");
        childName = getIntent().getStringExtra("childName");

        TextView childNameText = findViewById(R.id.childName);
        MaterialButton dailyCheckInButton = findViewById(R.id.btnDailyCheckIn);
        MaterialButton logControllerButton = findViewById(R.id.btnLogController);
        MaterialButton logRescueButton = findViewById(R.id.btnLogRescue);
        MaterialButton emergencyButton = findViewById(R.id.btnEmergency);
        MaterialButton inventoryButton = findViewById(R.id.btnInventory);
        MaterialButton streaksAndBadgesButton = findViewById(R.id.btnStreaks);
        MaterialButton summaryChartsButton = findViewById(R.id.btnSummaryCharts);
        MaterialButton manageAccountButton = findViewById(R.id.btnManageAccount);
        MaterialButton incidentLogButton = findViewById(R.id.btnIncidentLog);
        MaterialButton logoutButton = findViewById(R.id.btnLogout);

        TextView todaysZone = findViewById(R.id.textTodaysZone);
        TextView lastRescueTime = findViewById(R.id.textLastRescueTime);
        TextView weeklyRescueCount = findViewById(R.id.textWeeklyCount);
        FrameLayout chartContainer = findViewById(R.id.chartContainer);

        childNameText.setText(childName);

        emergencyButton.setOnClickListener(view -> {
            startActivityWithChildInfo(TriageScreen.class);
        });
        
        logControllerButton.setOnClickListener(v-> {
            startActivityWithChildInfo(ControllerLoggingScreen.class);
        });

        logRescueButton.setOnClickListener(view -> {
            startActivityWithChildInfo(LogRescueAttemptActivity.class);
        });

        inventoryButton.setOnClickListener(view -> {
            startActivityWithChildInfo(InventoryActivity.class);
        });

        manageAccountButton.setOnClickListener(v -> {
            startActivityWithChildInfo(ManageChildAccount.class);
        });

        incidentLogButton.setOnClickListener(view -> {
            startActivityWithChildInfo(IncidentLog.class);
        });

        logoutButton.setOnClickListener(v -> {
            Logout.logout(this);
        });
    }

    private void startActivityWithChildInfo(Class<?> cls) {
        Intent intent = new Intent(ParentChildHomeScreen.this, cls);
        intent.putExtra("childUID", childUserId);
        intent.putExtra("childName", childName);
        startActivity(intent);
    }
}