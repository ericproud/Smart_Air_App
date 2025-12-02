package com.example.smart_air_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.FirebaseDatabase;

public class ManageChildAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_child_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            Button sendOTCButton = findViewById(R.id.generateOTCButton);
            Button revokeProviderAccessButton = findViewById(R.id.revokeProviderAccessButton);
            Button managePermissionsButton = findViewById(R.id.manageChildPermissions);
            TextView OTCText = findViewById(R.id.OTCText);

            String childUID = getIntent().getStringExtra("childUID");
            String childName = getIntent().getStringExtra("childName");

            sendOTCButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String OTC = Integer.toString((int) (Math.random() * (999999999 - 100000000 + 1) + 100000000));
                    String text = "Your code is: " + OTC + " please give this to your provider.";
                    OTCText.setText(text);
                    FirebaseDatabase.getInstance().getReference("OTC's").child(childUID).setValue(OTC);
                }
            });

            managePermissionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ManageChildAccount.this, ManageSharingScreen.class);
                    intent.putExtra("childUID", childUID);
                    intent.putExtra("childName", childName);
                    startActivity(intent);
                }
            });

            return insets;
        });
    }
}