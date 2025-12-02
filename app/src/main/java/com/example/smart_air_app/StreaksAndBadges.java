package com.example.smart_air_app;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_air_app.log_rescue_attempt.RescueAttempt;
import com.example.smart_air_app.utils.RescueCounter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StreaksAndBadges extends AppCompatActivity {

    int threshold_perfect_week = 7;
    int threshold_technique = 10;
    int threshold_low_rescue = 4;

    /// /////////////I need two int here, one is login days, the other is the number of something el
    /// that can be bundeled for a badges ( i.e. number of t excersises done)
/// PS baraye milistone haye dige bebin kare dige ie mishe card ya hoseleh nadari
    /// /// mitoni 3 ta az ye aks dorost koni ba addad haye moktaleft faghat backend moskeleh


    int login = 0 ;
    int tecq = 0;
    int Days = 0;
    int exercise ;

    String type;
    String childUId;

    ImageView badgeController, badgeTechnique, badgeRescue;
    TextView statsText;

    @Override
    protected void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaks_and_badges);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());


        badgeController = findViewById(R.id.left);
        badgeTechnique = findViewById(R.id.middle);
        badgeRescue = findViewById(R.id.right);
        statsText = findViewById(R.id.streakNumber);


        setupSettingsButtons();


        childUId = getIntent().getStringExtra("childUId");
        if (childUId == null) {
            FirebaseUser usr = FirebaseAuth.getInstance().getCurrentUser();
            if (usr != null) childUId = usr.getUid();
            else { finish(); return; }
        }


        fetchGoals();  //gets the custum thresholda
        fetchStreaks();  ///gets the login and technique
        calculateRescueCount(); //gets the rescue days
    }


    private void calculateRescueCount() {
        long Min = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);  ///you cant be b4 this and count

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("RescueAttempts").child(childUId);

        // Run the query directly here
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    // Try to read timestamp. If strict mapping fails, try reading just the "timestamp" field
                    Long timestamp = data.child("timestamp").getValue(Long.class);

                    if (timestamp != null && timestamp >= Min) {  //check it
                        count++;
                    }
                }


                Days = count;
                updateBadges();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    private void fetchStreaks() {
        DatabaseReference streakRef = FirebaseDatabase.getInstance().getReference("Streaks").child(childUId);
        streakRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("consecutive controller use days").exists())
                    login = snapshot.child("consecutive controller use days").getValue(Integer.class);

                if (snapshot.child("consecutive technique conpleted days").exists())
                    tecq = snapshot.child("consecutive technique conpleted days").getValue(Integer.class);

                updateBadges();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void fetchGoals() {
        DatabaseReference goalsRef = FirebaseDatabase.getInstance().getReference("Users").child(childUId).child("goals");
        goalsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("goal_controller").exists())
                    threshold_perfect_week = snapshot.child("goal_controller").getValue(Integer.class);
                if (snapshot.child("goal_technique").exists())
                    threshold_technique = snapshot.child("goal_technique").getValue(Integer.class);
                if (snapshot.child("limit_rescue").exists())
                    threshold_low_rescue = snapshot.child("limit_rescue").getValue(Integer.class);

                updateBadges();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void updateBadges() {
        // Badge 1: Controller
        if (login >= threshold_perfect_week) {
            badgeController.setImageResource(R.drawable.forest);
            badgeController.setAlpha(1.0f);
        } else {
            badgeController.setImageResource(R.drawable.tree);
            badgeController.setAlpha(0.5f);
        }
        badgeController.setOnClickListener(v -> Toast.makeText(this,
                "Controller Streak: "
                + login + "/" + threshold_perfect_week, Toast.LENGTH_SHORT).show());

        // Badge 2: Technique
        if (tecq >= threshold_technique) {
            badgeTechnique.setImageResource(R.drawable.lungs2);
            badgeTechnique.setAlpha(1.0f);
        } else {
            badgeTechnique.setImageResource(R.drawable.lungs);
            badgeTechnique.setAlpha(0.5f);
        }
        badgeTechnique.setOnClickListener(v -> Toast.makeText(this,
                "Technique Sessions: " + tecq + "/" +
                        threshold_technique, Toast.LENGTH_SHORT).show());

        // Badge 3: Rescue (Calculated live!)
        if (Days <= threshold_low_rescue) {
            badgeRescue.setImageResource(R.drawable.trophy);
            badgeRescue.setAlpha(1.0f);
        } else {
            badgeRescue.setImageResource(R.drawable.locked); /// you need to have lower days to be better!
            badgeRescue.setAlpha(0.5f);
        }
        badgeRescue.setOnClickListener(v -> Toast.makeText(this,
                "Rescue Usage (30d): " + Days +
                        " (Limit: " + threshold_low_rescue + ")", Toast.LENGTH_SHORT).show());

        // Text
        statsText.setText("Controller Streak: " + login +
                "\nTechnique: " + tecq + "\nRescue Uses (30d): " + Days);
    }


    private void setupSettingsButtons() {
        Button btnCon = findViewById(R.id.btnSetController);
        Button btnTech = findViewById(R.id.btnSetTechnique);
        Button btnRes = findViewById(R.id.btnSetRescue);

        btnCon.setOnClickListener(v -> showEditDialog("Controller Goal", "threshold_perfect_week"));
        btnTech.setOnClickListener(v -> showEditDialog("Technique Goal", "threshold_technique"));
        btnRes.setOnClickListener(v -> showEditDialog("Rescue Limit", "threshold_low_rescue"));
    }

    private void showEditDialog(String title, String field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String text = input.getText().toString();
            if (!text.isEmpty()) {
                saveGoalToFirebase(field, Integer.parseInt(text));
            }
        });
        builder.setNegativeButton("Cancel", (d, w) -> d.cancel());
        builder.show();
    }

    private void saveGoalToFirebase(String fieldName, int value) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(childUId).child("goals").child(fieldName).setValue(value);
        Toast.makeText(this, "Goal Updated!", Toast.LENGTH_SHORT).show();
    }
}