package com.example.smart_air_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getIntent().getStringExtra("childUID");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_child_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            Button backButton = findViewById(R.id.buttonManageBack);
            Button sendOTCButton = findViewById(R.id.generateOTCButton);
            Button revokeProviderAccessButton = findViewById(R.id.revokeProviderAccessButton);
            Button managePermissionsButton = findViewById(R.id.manageChildPermissions);
            TextView OTCText = findViewById(R.id.OTCText);

            String childUID = getIntent().getStringExtra("childUID");
            String childName = getIntent().getStringExtra("childName");

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ManageChildAccount.this, ParentHomeScreen.class);
                    intent.putExtra("childUID", getIntent().getStringExtra("childUID"));
                    intent.putExtra("childName", getIntent().getStringExtra("childName"));
                    startActivity(intent);
                    finish();
                }
            });

            sendOTCButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String OTC = Integer.toString((int) (Math.random() * (999999999 - 100000000 + 1) + 100000000));
                    String text = "Your code is: " + OTC + " please give this to your provider.";
                    OTCText.setText(text);
                    FirebaseDatabase.getInstance().getReference("OTC's").child(id).setValue(OTC);
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