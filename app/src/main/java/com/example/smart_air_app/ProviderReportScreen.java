package com.example.smart_air_app;

import android.content.Intent;
import android.graphics.Canvas;
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

public class ProviderReportScreen extends AppCompatActivity {
    String childUID;
    String childName;
    TextView screenName;
    HashMap<String, String> mapOfFields;
    DatabaseReference dbRef;

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
        childUID = getIntent().getStringExtra("childUID");
        childName = getIntent().getStringExtra("childName");
        screenName = findViewById(R.id.screenName);
        screenName.setText(childName + "'s Report");
        mapOfFields = new HashMap<>();

    }

    void buildPDF() {
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

        mapOfFields.put("Shortness of breath", "0");
        mapOfFields.put("Chest tightness", "0");
        mapOfFields.put("Chest pain", "0");
        mapOfFields.put("Wheezing", "0");
        mapOfFields.put("Trouble sleeping", "0");
        mapOfFields.put("Coughing", "0");
        mapOfFields.put("Other", "0");
        mapOfFields.put("Controller Adherence", "0%");
        mapOfFields.put("Rescue Attempts Per Day", "0");


        canvas.drawText("Summary Report: " + childName, 50, 50, titlePaint);

        canvas.drawText("Controller Adherence: " + mapOfFields.get("Controller Adherence"), 50, 100, textPaint);

        canvas.drawText("Rescue Attempts Per Day: " + mapOfFields.get("Rescue Attempts Per Day"), 50, 150, textPaint);

        canvas.drawText("Symptom Burdens (days):", 50, 200, textPaint);

        canvas.drawText("Shortness of breath: " + mapOfFields.get("Shortness of breath"), 100, 250, textPaint);
        canvas.drawText("Chest tightness: " + mapOfFields.get("Chest tightness"), 100, 300, textPaint);
        canvas.drawText("Chest pain: " + mapOfFields.get("Chest pain"), 100, 350, textPaint);
        canvas.drawText("Wheezing: " + mapOfFields.get("Wheezing"), 100, 400, textPaint);
        canvas.drawText("Trouble sleeping: " + mapOfFields.get("Trouble sleeping"), 100, 450, textPaint);
        canvas.drawText("Coughing: " + mapOfFields.get("Coughing"), 100, 500, textPaint);
        canvas.drawText("Other: " + mapOfFields.get("Other"), 100, 550, textPaint);

        canvas.drawText("Zone distribution:", 50, 600, textPaint);

        summaryPDF.finishPage(page);

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File file = new File(path, "MyGeneratedPDF.pdf");

        try {
            summaryPDF.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            Toast.makeText(this, "Error creating PDF", Toast.LENGTH_SHORT).show();
        }

        summaryPDF.close();

        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error opening PDF", Toast.LENGTH_SHORT).show();
        }
    }

    void setFields() {
        setControllerAdherence();

    }

    void setControllerAdherence() {
        dbRef.child("ControllerLogs").child(childUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long logCount = 0;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    logCount = childSnapshot.getChildrenCount();
                }
                calculateControllerAdherence(logCount);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
    void calculateControllerAdherence(long logCount) {
        dbRef.child("ControllerSchedule").child(childUID).child("Schedule").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long expectedLogs = snapshot.getChildrenCount();
                long adherence = (logCount / expectedLogs) * 100;
                int adherenceInt = (int) adherence;
                mapOfFields.put("Controller Adherence", Integer.toString(adherenceInt) + "%");
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}