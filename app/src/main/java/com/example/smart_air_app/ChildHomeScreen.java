package com.example.smart_air_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.log_rescue_attempt.LogRescueAttemptActivity;
import com.google.firebase.auth.FirebaseAuth;

import controller_log.ControllerLoggingScreen;
import controller_log.PEFZones;
import controller_log.PEFZonesDatabase;

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

        String childUID = FirebaseAuth.getInstance().getUid();

        Button logRescueAttempt = findViewById(R.id.parentLogRescueAttemptButton);
        logRescueAttempt.setOnClickListener(view -> {
            startActivity(new Intent(ChildHomeScreen.this, LogRescueAttemptActivity.class));
        });

        Button logController = findViewById(R.id.parentLogControllerUsageButton);
        logController.setOnClickListener(v->{
            Intent intent = new Intent(ChildHomeScreen.this, ControllerLoggingScreen.class);
            intent.putExtra("childUID", childUID);
            startActivity(intent);
        });

        Button triageButton = findViewById(R.id.parentEmergencyTriageButton);
        triageButton.setOnClickListener(view -> {
            startActivity(new Intent(ChildHomeScreen.this, TriageScreen.class));
        });

        Button childMedicineLogsButton = findViewById(R.id.childMedicineLogs);
        childMedicineLogsButton.setOnClickListener(view -> {
            Intent intent = new Intent(ChildHomeScreen.this, MedicineLogs.class);
            intent.putExtra("childUID", childUID);
            startActivity(intent);
        });

        Button setPEFButton = findViewById(R.id.setPEFButton);
        setPEFButton.setOnClickListener(v -> {
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("Enter new PEF");

            final EditText inputText = new EditText(this);
            inputText.setHint("Enter Today's PEF (Eg 67)");
            build.setView(inputText);

            build.setPositiveButton("Confirm", (d, w) -> {
                String input = inputText.getText().toString().trim();
                try {
                    int int_input = Integer.parseInt(input);

                    //using a lambda expression to ensure asynch calls work
                    PEFZonesDatabase.loadPEFZones(childUID, (pb, highest_pef, date) ->{
                        PEFZones zone2 = new PEFZones();

                        zone2.setPB(pb);
                        zone2.setHighest_pef(highest_pef);
                        zone2.setDate(date);

                        zone2.setHighest_pef(int_input);

                        PEFZonesDatabase.savePEFZones(childUID, zone2);
                    });
                }
                catch (NumberFormatException e) {
                }
            });

            build.setNegativeButton("Cancel", (d, w) -> {
                d.cancel();
            });

            build.show();
        });
    }
}