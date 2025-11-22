package com.example.smart_air_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_air_app.session.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParentHomeScreen extends AppCompatActivity {
    String parentUID;
    DatabaseReference dbRef;
    List<String> childUIDs;
    LinearLayout container;
    Button addChildButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home_screen);

        container = findViewById(R.id.childButtonContainer);
        addChildButton = findViewById(R.id.addChildButton);

        parentUID = SessionManager.getInstance().getUserId();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        childUIDs = new ArrayList<>();

        addChildButton.setOnClickListener(v -> {
            Intent intent = new Intent(ParentHomeScreen.this, AddChildScreen.class);
            startActivity(intent);
        });

        dbRef.child(parentUID).child("children").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    childUIDs.add(childSnapshot.getKey());
                }
                createChildButtons(childUIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    void createChildButtons(List<String> childUIDs) {
        for (String childUID : childUIDs) {
            Button button = new Button(this);

            String childName = dbRef.child(childUID).child("firstName") + " " + dbRef.child(childUID).child("lastName");
            button.setText(childName);
            button.setTextColor(Color.WHITE);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(Color.parseColor("#673AB7"));
            drawable.setCornerRadius(dpToPx(35));
            button.setBackground(drawable);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            button.setPadding(0, 0, 0, 0);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(297), dpToPx(67)
            );
            params.setMargins(dpToPx(55), dpToPx(20), dpToPx(56), 0);
            button.setLayoutParams(params);

            button.setClickable(true);

            button.setOnClickListener(v -> {
                Intent intent = new Intent(ParentHomeScreen.this, ParentChildHomeScreen.class);
                intent.putExtra("childUID", childUID);
                intent.putExtra("childName", childName);
                startActivity(intent);
            });

            container.addView(button);
        }
    }
    private int dpToPx(int dp){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
}