package com.example.smart_air_app.utils;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.smart_air_app.log_rescue_attempt.FirebaseRescueAttemptRepository;
import com.example.smart_air_app.log_rescue_attempt.RescueAttempt;
import com.example.smart_air_app.log_rescue_attempt.RescueAttemptRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BuildPDFs {
    private static final String TAG = "BuildPDFs";

    public static void buildProviderReport(Context context, DatabaseReference dbRef, String childUID, String childName) {
        dbRef.child("Permissions").child(childUID).get().addOnSuccessListener(permissionsSnapshot -> {
            Integer timeframe = permissionsSnapshot.child("sharing timeframe").getValue(Integer.class);
            if (timeframe == null) {
                timeframe = 3;
            }
            String startDate = getDateMinusMonths(timeframe);

            // Alex's helper function for adherence
            AdherenceCalculator.CalculateAdherence(childUID, startDate, adherencePercent -> {

                // Alex's helper function for getting pef
                PEFHistoryCalculator.CalculateAdherence(childUID, startDate, PEFDistribution -> {

                    // Store necessary fields from the triage branch as a snapshot
                    dbRef.child("TriageEntries").child(childUID).get().addOnSuccessListener(triageSnapshot -> {

                        dbRef.child("rescueAttempts").child(childUID).get().addOnSuccessListener(rescueIdSnapshot -> {

                            HashMap<String, String> mapOfFields = new HashMap<>();
                            mapOfFields.put("Shortness of breath", "0");
                            mapOfFields.put("Chest tightness", "0");
                            mapOfFields.put("Chest pain", "0");
                            mapOfFields.put("Wheezing", "0");
                            mapOfFields.put("Trouble sleeping", "0");
                            mapOfFields.put("Coughing", "0");
                            mapOfFields.put("Other", "0");
                            mapOfFields.put("Rescue Attempts Per Day", "0");

                            for (DataSnapshot rescueAttemptSnapshot : rescueIdSnapshot.getChildren()) {
                                DataSnapshot symptomSnapshot = rescueAttemptSnapshot.child("symptoms");
                                for (DataSnapshot symptom : symptomSnapshot.getChildren()) {
                                    String symptomName = symptom.getValue(String.class);
                                    mapOfFields.put(symptomName, String.valueOf(Integer.parseInt(mapOfFields.get(symptomName)) + 1));
                                }
                            }

                            // Make pdf with all the needed info
                            createPDF(context, childUID, childName, mapOfFields, startDate,
                                    permissionsSnapshot, adherencePercent, PEFDistribution, triageSnapshot);
                        }).addOnFailureListener(e -> {
                        });
                    }).addOnFailureListener(e -> {
                    });

                });
            });

        }).addOnFailureListener(e -> {
        });
    }

    private static void createPDF(Context context, String childUID, String childName,
                                  HashMap<String, String> mapOfFields, String startDate,
                                  DataSnapshot permissionsSnapshot, double adherencePercent,
                                  int[][] PEFDistribution, DataSnapshot triageSnapshot) {

        PdfDocument summaryPDF = new PdfDocument();

        try {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = summaryPDF.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            // Paint for title
            Paint titlePaint = new Paint();
            titlePaint.setTextSize(24);
            titlePaint.setFakeBoldText(true);
            titlePaint.setColor(Color.BLACK);

            // Paint for text
            Paint textPaint = new Paint();
            textPaint.setTextSize(16);
            textPaint.setColor(Color.BLACK);

            var rescueRepo = new FirebaseRescueAttemptRepository();
            rescueRepo.setUid(childUID);

            rescueRepo.fetchRescueAttempt(new RescueAttemptRepository.FetchCallback() {
                private void setWeeklyRescueCount(List<RescueAttempt> attempts) {
                    long now = System.currentTimeMillis();
                    List<RescueAttempt> filtered = attempts.stream()
                            .filter(rescueAttempt -> now - rescueAttempt.getTimestamp() <= 1000L * 60 * 60 * 24 * 7)
                            .collect(Collectors.toList());

                    mapOfFields.put("Rescue Attempts Per Day", String.valueOf(filtered.size()));
                }

                @Override
                public void onSuccess(List<RescueAttempt> attempts) {
                    setWeeklyRescueCount(attempts);
                }

                @Override
                public void onError(String e) {}
            });

            canvas.drawText("Summary Report: " + childName, 50, 50, titlePaint);

            if (permissionsSnapshot.child("controller adherence summary").getValue(Boolean.class)) {
                canvas.drawText("Controller Adherence: " + adherencePercent * 100 + "%", 50, 100, textPaint);
            } else {
                canvas.drawText("Controller Adherence: NOT PROVIDED", 50, 100, textPaint);
            }

            if (permissionsSnapshot.child("rescue logs").getValue(Boolean.class)) {
                String rescueAttempts = mapOfFields.get("Rescue Attempts Per Day");
                canvas.drawText("Rescue Attempts Per Week: " + mapOfFields.get("Rescue Attempts Per Day"), 50, 150, textPaint);
            } else {
                canvas.drawText("Rescue Attempts Per Day: NOT PROVIDED", 50, 150, textPaint);
            }

            if (permissionsSnapshot.child("symptoms").getValue(Boolean.class)) {
                canvas.drawText("Symptom Burdens (days): ", 50, 200, textPaint);
                drawSymptomHorizontalBarGraph(mapOfFields, canvas, 50, 220, 500, 300);
            } else {
                canvas.drawText("Symptom Burdens (days): NOT PROVIDED", 50, 200, textPaint);
            }

            if (permissionsSnapshot.child("pef").getValue(Boolean.class)) {
                canvas.drawText("Monthly PEF Zone Distribution: ", 50, 540, textPaint);
                // Conversion for drawing
                float[][] distribution = new float[PEFDistribution.length][3];
                for (int i = 0; i < PEFDistribution.length; i++) {
                    for (int j = 0; j < 3; j++) {
                        distribution[i][j] = PEFDistribution[i][j];
                    }
                }
                drawPEFDistribution(distribution, startDate, canvas, 50, 550, 560, 200);
            } else {
                canvas.drawText("Monthly PEF Zone Distribution: NOT PROVIDED", 50, 540, textPaint);
            }

            summaryPDF.finishPage(page);

            PdfDocument.PageInfo page2Info = new PdfDocument.PageInfo.Builder(595, 842, 2).create();
            PdfDocument.Page page2 = summaryPDF.startPage(page2Info);
            Canvas canvas2 = page2.getCanvas();


            if (permissionsSnapshot.child("triage incidents").getValue(Boolean.class)) {
                float currentY = 100;

                Paint triageTextPaint = new Paint();
                triageTextPaint.setTextSize(12);
                triageTextPaint.setColor(Color.BLACK);

                if (!triageSnapshot.exists() || triageSnapshot.getChildrenCount() == 0) {
                    canvas2.drawText("No triage incidents recorded", 70, currentY, triageTextPaint);
                } else {
                    canvas2.drawText("Notable Triage Incidents: ", 50, 200, textPaint);
                    int numTriages = 0;
                    for (DataSnapshot triageEntry : triageSnapshot.getChildren()) {
                        if (numTriages >= 3) {
                            break;
                        }

                        Boolean blueGrayLipsNails = triageEntry.child("BlueGrayLipsNails").getValue(Boolean.class);
                        Boolean noFullSentences = triageEntry.child("NoFullSentences").getValue(Boolean.class);
                        Long PEF = triageEntry.child("PEF").getValue(Long.class);
                        Boolean recentRescueDone = triageEntry.child("RecentRescueDone").getValue(Boolean.class);
                        Boolean retractions = triageEntry.child("Retractions").getValue(Boolean.class);
                        Boolean emergencyStatus = triageEntry.child("emergencyStatus").getValue(Boolean.class);

                        if (PEF == null) {
                            continue;
                        }
                        if (emergencyStatus == null || !emergencyStatus) {
                            continue;
                        }

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

                        currentY += 20;

                        numTriages++;
                    }
                    if (numTriages == 0) {
                        canvas2.drawText("No emergency triage incidents found", 70, currentY, triageTextPaint);
                    }
                }
            } else {
                canvas2.drawText("Notable Triage Incidents: NOT PROVIDED", 50, 200, textPaint);
            }

            summaryPDF.finishPage(page2);
            String timeStamp = String.valueOf(System.currentTimeMillis());
            File file = new File(context.getExternalFilesDir(null), "MyGeneratedPDF" + timeStamp + ".pdf");

            try {
                summaryPDF.writeTo(new FileOutputStream(file));
            } catch (Exception e) {
            }

            summaryPDF.close();

            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                context.startActivity(intent);
            } catch (Exception e) {
            }
        }
        catch (Exception e) {
        }
    }

    static void populateMapOfFields() {

    }

    static void drawPEFDistribution(float[][] zoneDistribution, String startDate, Canvas canvas, float startX, float startY, float chartWidth, float chartHeight) {
        int[] zoneColors = {Color.GREEN, Color.YELLOW, Color.RED};

        int monthCount = zoneDistribution.length;
        int zonesPerMonth = 3;

        String[] monthLabels = getMonthLabels(startDate, monthCount);

        // Calculate dimensions
        float groupWidth = chartWidth / monthCount;
        float barWidth = groupWidth * 0.7f / zonesPerMonth;
        float barSpacing = groupWidth * 0.3f / (zonesPerMonth + 1);

        // Loop through months
        for (int month = 0; month < monthCount; month++) {
            float groupStartX = startX + (month * groupWidth);

            float totalDays = zoneDistribution[month][0] + zoneDistribution[month][1] + zoneDistribution[month][2];
            // Loop through zone (R, Y, G)
            for (int zone = 0; zone < zonesPerMonth; zone++) {
                // Make bar for zone
                float barLeft = groupStartX + barSpacing + (zone * (barWidth + barSpacing));
                float barHeight = (zoneDistribution[month][zone] / totalDays) * chartHeight;
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
            canvas.drawText(monthLabels[month], groupStartX + (groupWidth / 2), startY + chartHeight + 20, monthPaint);
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

    static void drawSymptomHorizontalBarGraph(HashMap<String, String> mapOfFields, Canvas canvas, float startX, float startY, float chartWidth, float chartHeight) {
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
                String countStr = mapOfFields.get(symptoms[i]);
                if (countStr != null) {
                    symptomCounts[i] = Integer.parseInt(countStr);
                } else {
                    symptomCounts[i] = 0;
                }
            } catch (NumberFormatException e) {
                symptomCounts[i] = 0;
                Log.w(TAG, "Could not parse symptom count for: " + symptoms[i]);
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

    static void drawSymptomLegend(Canvas canvas, float startX, float startY, String[] symptoms, int[] colors) {
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

    static String getDateMinusMonths(int monthsBack) {
        Calendar cal = Calendar.getInstance();

        // subtract months safely
        cal.add(Calendar.MONTH, -monthsBack);

        // formatter
        SimpleDateFormat format = new SimpleDateFormat("MMM dd yy", Locale.getDefault());

        return format.format(cal.getTime());
    }

    // Make array of months for labels based off of start date
    static String[] getMonthLabels(String startDate, int timeframe) {
        String[] months = new String[timeframe];

        // Parse start date
        SimpleDateFormat format = new SimpleDateFormat("MMM dd yy", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        try {
            if (startDate != null) {
                cal.setTime(format.parse(startDate));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing start date: " + startDate, e);
        }

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
        for (int i = 0; i < timeframe; i++) {
            months[i] = monthFormat.format(cal.getTime());
            cal.add(Calendar.MONTH, 1);
        }

        return months;
    }
}
