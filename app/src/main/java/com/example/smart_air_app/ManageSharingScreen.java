package com.example.smart_air_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManageSharingScreen extends AppCompatActivity {
    private DatabaseReference dbRef;
    private String childUID;
    private Button submitButton;
    private Chip toggleControllerMedicine;
    private Chip toggleRescueMedicine;
    private Chip toggleTriage;
    private Chip togglePEF;
    private Chip toggleSymptoms;
    private Chip toggleTriggers;
    private Chip toggleSummaries;
    private MaterialButtonToggleGroup toggleTimeframe;
    private Button toggleTimeframeDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_sharing_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbRef = FirebaseDatabase.getInstance().getReference();
        childUID = getIntent().getStringExtra("childUID");

        submitButton = findViewById(R.id.submitButton);
        toggleControllerMedicine = findViewById(R.id.toggleControllerMedicine);
        toggleRescueMedicine = findViewById(R.id.toggleRescueMedicine);
        toggleTriage = findViewById(R.id.toggleTriage);
        togglePEF = findViewById(R.id.togglePEF);
        toggleSymptoms = findViewById(R.id.toggleSymptoms);
        toggleTriggers = findViewById(R.id.toggleTriggers);
        toggleSummaries = findViewById(R.id.toggleSummaries);
        toggleTimeframe = findViewById(R.id.toggleTimeframe);

        submitButton.setOnClickListener(v -> {
            setPermissions();
            Intent intent = new Intent(ManageSharingScreen.this, ManageChildAccount.class);
            startActivity(intent);
        });
    }

    public void setPermissions() {
        String childUID = getIntent().getStringExtra("childUID");
        boolean controllerMedicine = toggleControllerMedicine.isChecked();
        boolean rescueMedicine = toggleRescueMedicine.isChecked();
        boolean triage = toggleTriage.isChecked();
        boolean pef = togglePEF.isChecked();
        boolean symptoms = toggleSymptoms.isChecked();
        boolean triggers = toggleTriggers.isChecked();
        boolean summaries = toggleSummaries.isChecked();
        int selectedId = toggleTimeframe.getCheckedButtonId();
        int timeframe;

        if (selectedId != View.NO_ID) {
            Button selectedButton = findViewById(selectedId);
            timeframe = Integer.parseInt(selectedButton.getText().toString().trim());
        }
        else {
            timeframe = 3;
        }
        dbRef.child("Permissions").child(childUID).child("controller adherence summary").setValue(controllerMedicine);
        dbRef.child("Permissions").child(childUID).child("rescue logs").setValue(rescueMedicine);
        dbRef.child("Permissions").child(childUID).child("triage").setValue(triage);
        dbRef.child("Permissions").child(childUID).child("pef").setValue(pef);
        dbRef.child("Permissions").child(childUID).child("symptoms").setValue(symptoms);
        dbRef.child("Permissions").child(childUID).child("triggers").setValue(triggers);
        dbRef.child("Permissions").child(childUID).child("summary charts").setValue(summaries);
        dbRef.child("Permissions").child(childUID).child("sharing timeframe").setValue(timeframe);
    }
}
