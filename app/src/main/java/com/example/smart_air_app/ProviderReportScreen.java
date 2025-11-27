package com.example.smart_air_app;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
}