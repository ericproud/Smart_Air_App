package com.example.smart_air_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

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

        Button generateOTCButton = findViewById(R.id.generateOTCButton);
        Button revokeProviderAccessButton = findViewById(R.id.revokeProviderAccessButton);

        TextView OTCText = findViewById(R.id.OTCText);

        generateOTCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateOTCButton.setEnabled(false);
                int OTC = (int)(Math.random() * (999999999 - 100000000 + 1) + 100000000);
                OTCText.setText(Integer.toString(OTC));
            }
        });
    }
}