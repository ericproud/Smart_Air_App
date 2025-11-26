package com.example.smart_air_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.inventory.InventoryActivity;

public class ParentChildHomeScreen extends AppCompatActivity {

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
        String childUserId = childData.getStringExtra("childUID");
        String childName = getIntent().getStringExtra("childName");

        TextView childNameView = findViewById(R.id.childsName);
        Button parentDailyCheckinButton = findViewById(R.id.parentDailyCheckInButton);
        Button parentLogControllerUsageButton = findViewById(R.id.parentLogControllerUsageButton);
        Button parentLogRescueAttemptButton = findViewById(R.id.parentLogRescueAttemptButton);
        Button parentEmergencyTriageButton = findViewById(R.id.parentEmergencyTriageButton);
        Button parentInventoryButton = findViewById(R.id.parentInventoryButton);
        Button parentStreaksAndBadgesButton = findViewById(R.id.parentStreaksAndBadgesButton);
        Button parentSummaryChartsButton = findViewById(R.id.parentSummaryChartsButton);
        Button parentManageAccountButton = findViewById(R.id.parentManageAccountButton);

        parentInventoryButton.setOnClickListener(view -> {
            Intent intent = new Intent(ParentChildHomeScreen.this, InventoryActivity.class);
            intent.putExtra("childUID", childUserId);
            startActivity(intent);
        });

        childNameView.setText(childName);
        parentManageAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(ParentChildHomeScreen.this, ManageChildAccount.class);
            intent.putExtra("childUID", childUserId);
            intent.putExtra("childName", childName);
            startActivity(intent);
        });
    }
}