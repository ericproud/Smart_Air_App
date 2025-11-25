package com.example.smart_air_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManageChildAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_child_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button generateOTCButton = findViewById(R.id.sendOTCButton);
        Button revokeProviderAccessButton = findViewById(R.id.revokeProviderAccessButton);
        Button managePermissionsButton = findViewById(R.id.manageChildPermissions);

        generateOTCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateOTCButton.setEnabled(false);
                int OTC = (int)(Math.random() * (999999999 - 100000000 + 1) + 100000000);
            }
        });

        managePermissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageChildAccount.this, ManageSharingScreen.class);
                startActivity(intent);
            }
        });
    }
}