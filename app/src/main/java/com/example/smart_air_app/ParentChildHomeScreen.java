package com.example.smart_air_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.inventory.InventoryActivity;
import com.example.smart_air_app.log_rescue_attempt.FirebaseRescueAttemptRepository;
import com.example.smart_air_app.log_rescue_attempt.LogRescueAttemptActivity;
import com.example.smart_air_app.log_rescue_attempt.RescueAttempt;
import com.example.smart_air_app.log_rescue_attempt.RescueAttemptRepository;
import com.example.smart_air_app.utils.BuildPDFs;
import com.example.smart_air_app.utils.Logout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.android.material.button.MaterialButton;

import com.example.smart_air_app.controller_log.ControllerLoggingScreen;
import com.example.smart_air_app.controller_log.PEFZones;
import com.example.smart_air_app.controller_log.PEFZonesDatabase;
import com.example.smart_air_app.controller_log.ControllerLoggingScreen;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

        TextView trendSnippetLabel = findViewById(R.id.labelTrendSnippet);
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
        MaterialButton medicineLogsButton = findViewById(R.id.btnMedicineLog);
        MaterialButton logoutButton = findViewById(R.id.btnLogout);
        MaterialButton setPBButton = findViewById(R.id.setPBButton);
        MaterialButton onboardButton = findViewById(R.id.onboardingButton);

        TextView todaysZone = findViewById(R.id.textTodaysZone);
        TextView lastRescueTime = findViewById(R.id.textLastRescueTime);
        TextView weeklyRescueCount = findViewById(R.id.textWeeklyCount);
        FrameLayout chartContainer = findViewById(R.id.chartContainer);

        trendSnippetLabel.setText("Rescues/Day:7 Day");
        final boolean[] showing7Days = { true };
        Button chartToggleButton = findViewById(R.id.chartToggleButton);

        chartToggleButton.setOnClickListener(v -> {
            if (showing7Days[0]) {
                buildTrendSnippet(7);
            }
            else {
                buildTrendSnippet(30);
            }
            if (showing7Days[0]) {
                trendSnippetLabel.setText("Rescues/Day:7 Day");
            }
            else {
                trendSnippetLabel.setText("Rescues/Day:30 Day");
            }

            showing7Days[0] = !showing7Days[0];
        });

        buildTrendSnippet(7);

        childNameText.setText(childName);

        var rescueRepo = new FirebaseRescueAttemptRepository();
        rescueRepo.setUid(childUserId);
        rescueRepo.fetchRescueAttempt(new RescueAttemptRepository.FetchCallback() {

            private void setLastRescueTime(List<RescueAttempt> attempts) {
                if (attempts.isEmpty()) {
                    lastRescueTime.setText("N/A");
                    return;
                }

                long max = attempts.get(0).getTimestamp();
                for (RescueAttempt attempt: attempts) {
                    max = Math.max(max, attempt.getTimestamp());
                }
                long now = System.currentTimeMillis();
                long diff = now - max;

                long days = diff / (1000 * 60 * 60 * 24);
                long hours = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);

                String ago;
                if (days == 0) {
                    ago = String.format("%dh ago", hours);
                } else {
                    ago = String.format("%dd %dh ago", days, hours);
                }
                lastRescueTime.setText(ago);
            }

            private void setWeeklyRescueCount(List<RescueAttempt> attempts) {
                long now = System.currentTimeMillis();
                List<RescueAttempt> filtered = attempts.stream()
                        .filter(rescueAttempt -> now - rescueAttempt.getTimestamp() <= 1000L * 60 * 60 * 24 * 7)
                        .collect(Collectors.toList());

                weeklyRescueCount.setText(String.valueOf(filtered.size()));
            }

            @Override
            public void onSuccess(List<RescueAttempt> attempts) {
                setLastRescueTime(attempts);
                setWeeklyRescueCount(attempts);
            }

            @Override
            public void onError(String e) {}
        });

        emergencyButton.setOnClickListener(view -> {
            startActivityWithChildInfo(TriageScreen.class);
        });

        logControllerButton.setOnClickListener(v-> {
            startActivityWithChildInfo(ControllerLoggingScreen.class);
        });

        logRescueButton.setOnClickListener(view -> {
            startActivityWithChildInfo(LogRescueAttemptActivity.class);
        });

        ///Hossein part
        streaksAndBadgesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //the child

                Intent intent = new Intent(ParentChildHomeScreen.this, StreaksAndBadges.class);

                intent.putExtra("the kid", childUserId);
                startActivity(intent);



            }
        });



        /// Hossein done

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

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        summaryChartsButton.setOnClickListener(v -> {
            BuildPDFs.buildProviderReport(ParentChildHomeScreen.this, dbRef, childUserId, childName);
            Log.d("ParentChildHomeScreen", ">>> buildProviderReport called for child: " + childName);
        });

        //loads in teh zone from the database and updates the text view
        PEFZones zone = new PEFZones();

        PEFZonesDatabase.loadPEFZones(childUserId, (pb, pef, date) -> {
            zone.initializePEF(pb, pef, date);
            todaysZone.setText(zone.calculateZone());
        });

        //when you click the pb button a new dialog shows up asking for input
        setPBButton.setOnClickListener(v -> {
            //putting in information like where it should pop up (here), hints, a title etc.
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("Enter new PB");

            final EditText inputText = new EditText(this);
            inputText.setHint("Enter new PB (Eg 67)");
            build.setView(inputText);

            //label the button confirm and handle the logic
            build.setPositiveButton("Confirm", (d, w) -> {
                //get input
                String input = inputText.getText().toString().trim();
                try {
                    //try parsing integer
                    int int_input = Integer.parseInt(input);

                    //using a lambda expression to ensure asynch calls work
                    PEFZonesDatabase.loadPEFZones(childUserId, (pb, highest_pef, date) ->{
                        PEFZones zone2 = new PEFZones();

                        //load in info
                        zone2.setPB(pb);
                        zone2.setHighest_pef(highest_pef);
                        zone2.setDate(date);

                        //set the pb (zone handles logic)
                        zone2.setPB(int_input);

                        //updates the database
                        PEFZonesDatabase.savePEFZones(childUserId, zone2);
                    });
                }
                catch (NumberFormatException e) {
                    //nothing really happens here
                }
            });

            //leave button is called so we destroy the dialog
            build.setNegativeButton("Cancel", (d, w) -> {
                d.cancel();
            });

            //show the build
            build.show();
        });

        medicineLogsButton.setOnClickListener(view -> {
            startActivityWithChildInfo(MedicineLogs.class);
        });

        onboardButton.setOnClickListener(v-> {
            startActivity(new Intent(ParentChildHomeScreen.this, ParentOnboardingScreen1.class));
        });
    }

    public void buildTrendSnippet(int numDays) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        int[] rescueAttempts = new int[numDays];
        for (int i = 0; i < numDays; i++) {
            rescueAttempts[i] = 0;
        }
        dbRef.child("RescueAttempts").child(childUserId).addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void startActivityWithChildInfo(Class<?> cls) {
        Intent intent = new Intent(ParentChildHomeScreen.this, cls);
        intent.putExtra("childUID", childUserId);
        intent.putExtra("childName", childName);
        startActivity(intent);
    }
}