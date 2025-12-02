package com.example.smart_air_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.log_rescue_attempt.LogRescueAttemptActivity;
import com.example.smart_air_app.utils.Logout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.smart_air_app.controller_log.ControllerLoggingScreen;
import com.example.smart_air_app.controller_log.PEFZones;
import com.example.smart_air_app.controller_log.PEFZonesDatabase;

import java.util.ArrayList;
import java.util.Date;

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

        TextView trendSnippetLabel = findViewById(R.id.labelTrendSnippet);
        TextView todaysZone = findViewById(R.id.textTodaysZone);
        TextView lastRescueTime = findViewById(R.id.textLastRescueTime);
        TextView weeklyRescueCount = findViewById(R.id.textWeeklyCount);
        FrameLayout chartContainer = findViewById(R.id.chartContainer);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String childUID = FirebaseAuth.getInstance().getUid();

        helperOnboard(FirebaseDatabase.getInstance().getReference("Users"), childUID);
        trendSnippetLabel.setText("Rescues/Day:7 Day");


        TextView childNameText = findViewById(R.id.childsName);
        dbRef.child("Users").child(childUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot childSnapshot) {
                String firstName = childSnapshot.child("firstName").getValue(String.class);
                String lastName = childSnapshot.child("lastName").getValue(String.class);
                String childName = firstName + " " + lastName;
                childNameText.setText(childName);



            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        String childName = childNameText.getText().toString();


        final boolean[] showing7Days = { true };
        Button chartToggleButton = findViewById(R.id.chartToggleButton);

        chartToggleButton.setOnClickListener(v -> {
            if (showing7Days[0]) {
                buildTrendSnippet(7);
            }
            else {
                buildTrendSnippet(30);
            }

            showing7Days[0] = !showing7Days[0];

            if (showing7Days[0]) {
                trendSnippetLabel.setText("Rescues/Day:7 Day");
            }
            else {
                trendSnippetLabel.setText("Rescues/Day:30 Day");
            }
        });

        buildTrendSnippet(7);

        /// Hossein
        MaterialButton dailyCheckIn = findViewById(R.id.parentDailyCheckInButton);
        dailyCheckIn.setOnClickListener(view -> {
            ////Log.d("DEBUG", "Button was definitely clicked!");
            ///Toast.makeText(this, "CLICKED!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChildHomeScreen.this, DailyCheckIn.class);
            intent.putExtra("childUID", childUID);
            intent.putExtra("childName", childName);

            startActivity(intent);
        });

        ///Hossein out

        ///
        MaterialButton Streaks = findViewById(R.id.parentDailyCheckInButton);
        dailyCheckIn.setOnClickListener(view -> {
            ////Log.d("DEBUG", "Button was definitely clicked!");
            ///Toast.makeText(this, "CLICKED!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChildHomeScreen.this, DailyCheckIn.class);
            intent.putExtra("childUID", childUID);
            intent.putExtra("childName", childName);

            startActivity(intent);
        });

        ///


        Button logRescueAttempt = findViewById(R.id.parentStreaksAndBadgesButton);
        logRescueAttempt.setOnClickListener(view -> {
            Intent intent = new Intent(ChildHomeScreen.this, StreaksAndBadges.class);
            intent.putExtra("childUID", childUID);
            intent.putExtra("childName", childName);
            startActivity(intent);
        });

        Button logController = findViewById(R.id.parentLogControllerUsageButton);
        logController.setOnClickListener(v->{
            Intent intent = new Intent(ChildHomeScreen.this, ControllerLoggingScreen.class);
            intent.putExtra("childUID", childUID);
            intent.putExtra("childName", childName);
            startActivity(intent);
        });

        Button triageButton = findViewById(R.id.parentEmergencyTriageButton);
        triageButton.setOnClickListener(view -> {
            Intent intent = new Intent(ChildHomeScreen.this, TriageScreen.class);
            intent.putExtra("childUID", childUID);
            intent.putExtra("childName", childName);
            startActivity(intent);
        });

        Button childMedicineLogsButton = findViewById(R.id.childMedicineLogs);
        childMedicineLogsButton.setOnClickListener(view -> {
            Intent intent = new Intent(ChildHomeScreen.this, MedicineLogs.class);
            intent.putExtra("childUID", childUID);
            startActivity(intent);
        });

        Button logoutButton = findViewById(R.id.childLogout);
        logoutButton.setOnClickListener(v -> {
            Logout.logout(this);
        });

        //the child wants to set a new PEF a dialog for an input field shows up
        Button setPEFButton = findViewById(R.id.setPEFButton);
        setPEFButton.setOnClickListener(v -> {
            //setting info like title, hint, where to show (this)
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("Enter new PEF");

            final EditText inputText = new EditText(this);
            inputText.setHint("Enter Today's PEF (Eg 67)");
            build.setView(inputText);

            //confirm button means check input for a number and save it if its valid
            build.setPositiveButton("Confirm", (d, w) -> {
                //get input
                String input = inputText.getText().toString().trim();
                try {
                    //try converting it
                    int int_input = Integer.parseInt(input);

                    //using a lambda expression to ensure asynch calls work
                    PEFZonesDatabase.loadPEFZones(childUID, (pb, highest_pef, date) ->{
                        PEFZones zone2 = new PEFZones();

                        //creating zone object
                        zone2.setPB(pb);
                        zone2.setHighest_pef(highest_pef);
                        zone2.setDate(date);

                        //zone object handles logic
                        zone2.setHighest_pef(int_input);

                        //save to database
                        PEFZonesDatabase.savePEFZones(childUID, zone2);
                    });
                }
                catch (NumberFormatException e) {
                    //nothing really happens here so leave it
                }
            });

            //leave button so obliterate the dialog
            build.setNegativeButton("Cancel", (d, w) -> {
                d.cancel();
            });

            //show the dialog
            build.show();
        });

        PEFZones zone = new PEFZones();
        //loads in the zone info and updates the textview
        PEFZonesDatabase.loadPEFZones(childUID, (pb, pef, date) -> {
            zone.initializePEF(pb, pef, date);
            todaysZone.setText(zone.calculateZone());
        });

        Button goOnboard = findViewById(R.id.onboardingButton);
        goOnboard.setOnClickListener(v->{
            startActivity(new Intent(ChildHomeScreen.this, ChildOnboardingScreen1.class));
        });
    }

    private void helperOnboard(DatabaseReference d_ref, String id) {
        //reference to see if the user is onboarded
        DatabaseReference o_ref = d_ref.child(id).child("isOnboarded");

        o_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //the isOnboarded should exist this is just a safety net
                if (snapshot.exists()) {
                    Boolean val = snapshot.getValue(Boolean.class);

                    //if false then send them to parent onboarding also check if null for safety
                    if (val != null && !val) {
                        Intent intent = new Intent(ChildHomeScreen.this, ChildOnboardingScreen1.class);
                        startActivity(intent);

                        //when they finish the onboarding set this to true
                        o_ref.setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void buildTrendSnippet(int numDays) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String childUID = FirebaseAuth.getInstance().getUid();
        int[] rescueAttempts = new int[numDays];
        for (int i = 0; i < numDays; i++) {
            rescueAttempts[i] = 0;
        }
        dbRef.child("RescueAttempts").child(childUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot rescueAttemptSnapshot : dataSnapshot.getChildren()) {
                    long timestamp = rescueAttemptSnapshot.child("timestamp").getValue(Long.class);
                    Date rescueDate = new Date(timestamp);
                    Date today = new Date();
                    int numDaysAgo = (int) ((today.getTime() - rescueDate.getTime()) / (1000 * 60 * 60 * 24));

                    for (int day = 0; day< numDays ; day++) {
                        if (numDaysAgo == day) {
                            rescueAttempts[day] ++;
                        }
                    }
                }
                ArrayList<Entry> entries = new ArrayList<>();
                for (int i = 0; i < numDays ; i++) {
                    entries.add(new Entry(i, rescueAttempts[numDays - i - 1]));
                }
                LineDataSet dataSet = new LineDataSet(entries, "Rescue Attempts");

                dataSet.setColor(Color.parseColor("#415f91"));
                dataSet.setDrawCircles(false);
                dataSet.setDrawValues(false);

                LineChart chart = findViewById(R.id.lineChart);
                chart.setData(new LineData(dataSet));
                chart.invalidate();

                chart.getDescription().setEnabled(false);
                chart.setNoDataText("No data available");

                Legend legend = chart.getLegend();
                chart.getLegend().setEnabled(false);
                legend.setEnabled(false);

                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(true);
                xAxis.setGridColor(Color.parseColor("#DDDDDD"));
                xAxis.setTextColor(Color.BLACK);
                xAxis.setTextSize(11f);
                xAxis.setGranularity(1f);
                xAxis.setAvoidFirstLastClipping(true);
                xAxis.setDrawLabels(false);

                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setDrawGridLines(true);
                leftAxis.setGridColor(Color.parseColor("#DDDDDD"));
                leftAxis.setTextColor(Color.BLACK);
                leftAxis.setTextSize(11f);
                leftAxis.setAxisMinimum(0f);

                leftAxis.setGranularity(1f);
                leftAxis.setGranularityEnabled(true);

                chart.getAxisRight().setEnabled(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}