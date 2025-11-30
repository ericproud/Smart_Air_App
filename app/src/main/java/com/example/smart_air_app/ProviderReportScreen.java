package com.example.smart_air_app;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProviderReportScreen extends AppCompatActivity {
    String childUID;
    String childName;
    TextView screenName;
    HashMap<String, String> mapOfFields;
    DatabaseReference dbRef;
    int fieldsToLoad = 1;
    int fieldsLoaded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_provider_report_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbRef = FirebaseDatabase.getInstance().getReference();
        childUID = getIntent().getStringExtra("patientUID");
        childName = getIntent().getStringExtra("patientName");
        screenName = findViewById(R.id.screenName);
        screenName.setText(childName + "'s Report");
        mapOfFields = new HashMap<>();

        setFields();
    }

    void buildPDF() {
        dbRef.child("Permissions").child(childUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot permissionsSnapshot) {
                PdfDocument summaryPDF = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                PdfDocument.Page page = summaryPDF.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                // Paint for title
                Paint titlePaint = new Paint();
                titlePaint.setTextSize(24);
                titlePaint.setFakeBoldText(true);

                // Paint for regular text
                Paint textPaint = new Paint();
                textPaint.setTextSize(16);

                mapOfFields.put("Shortness of breath", "8");
                mapOfFields.put("Chest tightness", "5");
                mapOfFields.put("Chest pain", "3");
                mapOfFields.put("Wheezing", "1");
                mapOfFields.put("Trouble sleeping", "0");
                mapOfFields.put("Coughing", "3");
                mapOfFields.put("Other", "22");
                mapOfFields.put("Controller Adherence", "0%");
                mapOfFields.put("Rescue Attempts Per Day", "0");

                canvas.drawText("Summary Report: " + childName, 50, 50, titlePaint);

                if (permissionsSnapshot.child("controller adherence summary").getValue(Boolean.class)) {
                    canvas.drawText("Controller Adherence: " + mapOfFields.get("Controller Adherence"), 50, 100, textPaint);
                }
                else {
                    canvas.drawText("Controller Adherence: NOT PROVIDED", 50, 100, textPaint);
                }

                if (permissionsSnapshot.child("rescue logs").getValue(Boolean.class)) {
                    canvas.drawText("Rescue Attempts Per Day: " + mapOfFields.get("Controller Adherence"), 50, 150, textPaint);
                }
                else {
                    canvas.drawText("Rescue Attempts Per Day: NOT PROVIDED", 50, 150, textPaint);
                }

                if (permissionsSnapshot.child("symptoms").getValue(Boolean.class) || permissionsSnapshot.child("summary charts").getValue(Boolean.class)) {
                    canvas.drawText("Symptom Burdens (days): ", 50, 200, textPaint);
                    drawSymptomHorizontalBarGraph(canvas, 50, 220, 500, 300);
                }
                else {
                    canvas.drawText("Symptom Burdens (days): NOT PROVIDED", 50, 200, textPaint);
                }

                if (permissionsSnapshot.child("pef").getValue(Boolean.class) || permissionsSnapshot.child("summary charts").getValue(Boolean.class)) {
                    canvas.drawText("Monthly PEF Zone Distribution: ", 50, 540, textPaint);
                    drawPEFDistribution(canvas, 50, 550, 560, 200); // More space on second page
                }
                else {
                    canvas.drawText("Monthly PEF Zone Distribution: NOT PROVIDED", 50, 540, textPaint);
                }

                summaryPDF.finishPage(page);

                PdfDocument.PageInfo page2Info = new PdfDocument.PageInfo.Builder(595, 842, 2).create();
                PdfDocument.Page page2 = summaryPDF.startPage(page2Info);
                Canvas canvas2 = page2.getCanvas();

                if (permissionsSnapshot.child("triage incidents").getValue(Boolean.class)) {
                    canvas2.drawText("Notable Triage Incidents: ", 50, 50, textPaint);
                    dbRef.child("TriageEntries").child(childUID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot triageSnapshot) {
                            float currentY = 100; // Start below the title

                            Paint textPaint = new Paint();
                            textPaint.setTextSize(12);
                            textPaint.setColor(Color.BLACK);

                            Paint labelPaint = new Paint();
                            labelPaint.setTextSize(12);
                            labelPaint.setColor(Color.BLACK);
                            labelPaint.setFakeBoldText(true);

                            int numTriages = 0;
                            // Loop through each triage ID
                            for (DataSnapshot triageEntry : triageSnapshot.getChildren()) {
                                if (!triageSnapshot.exists() || triageSnapshot.getChildrenCount() == 0) {
                                    canvas2.drawText("No triage incidents recorded", 70, currentY, textPaint);
                                }
                                if (numTriages > 3) {
                                    return;
                                }
                                // Get triage data
                                Boolean blueGrayLipsNails = triageEntry.child("BlueGrayLipsNails").getValue(Boolean.class);
                                Boolean noFullSentences = triageEntry.child("NoFullSentences").getValue(Boolean.class);
                                Long PEF = triageEntry.child("PEF").getValue(Long.class);
                                Boolean recentRescueDone = triageEntry.child("RecentRescueDone").getValue(Boolean.class);
                                Boolean retractions = triageEntry.child("Retractions").getValue(Boolean.class);
                                Boolean emergencyStatus = triageEntry.child("emergencyStatus").getValue(Boolean.class);

                                if (PEF == null) {
                                    return;
                                }
                                if (!emergencyStatus) {
                                    return;
                                }

                                // Draw triage details
                                canvas2.drawText("PEF: " + PEF, 100, currentY, textPaint);
                                currentY += 20;

                                canvas2.drawText("Emergency: " + (emergencyStatus ? "YES" : "NO"), 100, currentY, textPaint);
                                currentY += 20;

                                canvas2.drawText("Blue/Gray Lips/Nails: " + (blueGrayLipsNails ? "YES" : "NO"), 100, currentY, textPaint);
                                currentY += 20;

                                canvas2.drawText("Cannot Speak Full Sentences" + (noFullSentences ? "YES" : "NO"), 90, currentY, textPaint);
                                currentY += 20;

                                canvas2.drawText("Chest Retractions" + (retractions ? "YES" : "NO"), 90, currentY, textPaint);
                                currentY += 20;

                                canvas2.drawText("Recent Rescue Controller Use: " + (recentRescueDone ? "YES" : "NO"), 90, currentY, textPaint);
                                currentY += 20;

                                currentY += 20; // Space between triage entries

                                numTriages++;
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                }
                else {
                    canvas2.drawText("Notable Triage Incidents: NOT PROVIDED", 50, 200, textPaint);
                }



                summaryPDF.finishPage(page2);

                String timeStamp = String.valueOf(System.currentTimeMillis());
                File file = new File(getExternalFilesDir(null), "MyGeneratedPDF" + timeStamp + ".pdf");

                try {
                    summaryPDF.writeTo(new FileOutputStream(file));
                } catch (Exception e) {
                }

                summaryPDF.close();

                Uri uri = FileProvider.getUriForFile(ProviderReportScreen.this,
                        getPackageName() + ".provider", file);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(intent);
                } catch (Exception e) {
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
    void setFields() {
        fieldsToLoad = 1;
        fieldsLoaded = 0;
        //setControllerAdherence();
        buildPDF();
    }

    void setFieldLoaded() {
        fieldsLoaded++;
        if (fieldsLoaded == fieldsToLoad) {
            buildPDF();
        }
    }

    void drawPEFDistribution(Canvas canvas, float startX, float startY, float chartWidth, float chartHeight) {
        // PLACEHOLDER DATA TO BE REPLACED
        float[][] zoneDistribution = {
                {60, 30, 10},
                {50, 40, 10},
                {70, 20, 10},
                {40, 40, 20},
                {80, 15, 5},
                {65, 25, 10}
        };

        // PLACEHOLDER MONTHS TO BE REPLACED
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};

        int[] zoneColors = {Color.GREEN, Color.YELLOW, Color.RED};

        int monthCount = months.length;
        int zonesPerMonth = 3;

        // Calculate dimensions
        float groupWidth = chartWidth / monthCount;
        float barWidth = groupWidth * 0.7f / zonesPerMonth;
        float barSpacing = groupWidth * 0.3f / (zonesPerMonth + 1);

        // Loop through months
        for (int month = 0; month < monthCount; month++) {
            float groupStartX = startX + (month * groupWidth);

            // Loop through zone (R, Y, G)
            for (int zone = 0; zone < zonesPerMonth; zone++) {
                // Make bar for zone
                float barLeft = groupStartX + barSpacing + (zone * (barWidth + barSpacing));
                float barHeight = (zoneDistribution[month][zone] / 100f) * chartHeight;
                float barTop = startY + chartHeight - barHeight;

                Paint barPaint = new Paint();
                barPaint.setColor(zoneColors[zone]);
                canvas.drawRect(barLeft, barTop, barLeft + barWidth, startY + chartHeight, barPaint);
            }

            // Month Label
            Paint monthPaint = new Paint();
            monthPaint.setTextSize(14);
            monthPaint.setColor(Color.BLACK);
            monthPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(months[month], groupStartX + (groupWidth / 2), startY + chartHeight + 20, monthPaint);
        }

        // Y axis
        Paint axisPaint = new Paint();
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(2);
        canvas.drawLine(startX - 5, startY, startX - 5, startY + chartHeight, axisPaint);

        // X axis
        canvas.drawLine(startX - 5, startY + chartHeight, startX + chartWidth, startY + chartHeight, axisPaint);

        // % labels for Y axis
        Paint labelPaint = new Paint();
        labelPaint.setTextSize(14);
        labelPaint.setColor(Color.BLACK);
        String[] yLabels = {"100%", "75%", "50%", "25%", "0%"};
        float[] yPositions = {startY, startY + chartHeight * 0.25f, startY + chartHeight * 0.5f,
                startY + chartHeight * 0.75f, startY + chartHeight};
        for (int i = 0; i < yLabels.length; i++) {
            canvas.drawText(yLabels[i], startX - 25, yPositions[i] + 4, labelPaint);
        }
    }

    void drawSymptomHorizontalBarGraph(Canvas canvas, float startX, float startY, float chartWidth, float chartHeight) {
        String[] symptoms = {
                "Shortness of breath", "Chest tightness", "Chest pain",
                "Wheezing", "Trouble sleeping", "Coughing", "Other"
        };

        int[] symptomColors = {
                Color.RED,
                Color.BLUE,
                Color.GREEN,
                Color.MAGENTA,
                Color.CYAN,
                Color.YELLOW,
                Color.GRAY
        };

        // Get count of days for each symptom
        int[] symptomCounts = new int[symptoms.length];
        for (int i = 0; i < symptoms.length; i++) {
            try {
                symptomCounts[i] = Integer.parseInt(mapOfFields.get(symptoms[i]));
            } catch (NumberFormatException e) {
                symptomCounts[i] = 0;
            }
        }

        // Find max value for scaling
        int maxCount = 1;
        for (int count : symptomCounts) {
            if (count > maxCount) {
                maxCount = count;
            }
        }

        float barHeight = 25f;
        float barSpacing = 8f;
        float maxBarWidth = chartWidth * 0.6f;
        float labelWidth = chartWidth * 0.35f;

        Paint barPaint = new Paint();
        barPaint.setStyle(Paint.Style.FILL);

        Paint textPaint = new Paint();
        textPaint.setTextSize(12);
        textPaint.setColor(Color.BLACK);

        // Draw bars
        for (int i = 0; i < symptoms.length; i++) {
            float barTop = startY + (i * (barHeight + barSpacing));
            float barWidth = (symptomCounts[i] / (float) maxCount) * maxBarWidth;

            // Set colour
            barPaint.setColor(symptomColors[i]);

            // Draw bar
            canvas.drawRect(startX + labelWidth, barTop, startX + labelWidth + barWidth, barTop + barHeight, barPaint);

            // Draw label
            canvas.drawText(symptoms[i], startX, barTop + barHeight - 8, textPaint);

            // Draw count
            textPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(symptomCounts[i] + " days", startX + labelWidth - 5, barTop + barHeight - 8, textPaint);
            textPaint.setTextAlign(Paint.Align.LEFT);
        }
        // Draw legend
        drawSymptomLegend(canvas, startX, startY + (symptoms.length * (barHeight + barSpacing)) + 20, symptoms, symptomColors);
    }

    void drawSymptomLegend(Canvas canvas, float startX, float startY, String[] symptoms, int[] colors) {
        Paint textPaint = new Paint();
        textPaint.setTextSize(14);
        textPaint.setColor(Color.BLACK);

        int columns = 4;

        for (int i = 0; i < symptoms.length; i++) {
            int column = i % columns;
            int row = i / columns;

            float xPos = startX + (column * 150);
            float yPos = startY + (row * 20);

            Paint colorPaint = new Paint();
            colorPaint.setColor(colors[i]);
            canvas.drawRect(xPos, yPos, xPos + 12, yPos + 12, colorPaint);
            canvas.drawText(symptoms[i], xPos + 18, yPos + 10, textPaint);
        }
    }
}