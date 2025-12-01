package com.example.smart_air_app;

////import static com.example.smart_air_app.utils.AlertSender.showAlert;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_air_app.alerts.FirebaseDatabaseListeners;
import com.example.smart_air_app.utils.NotificationUtils;
import com.example.smart_air_app.utils.Logout;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParentHomeScreen extends AppCompatActivity {
    String parentUID;
    DatabaseReference dbRef;
    List<String> childUIDs;
    LinearLayout container;
    Button addChildButton;
    Button logoutButton;


    private final FirebaseDatabaseListeners fdl = FirebaseDatabaseListeners.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home_screen);
        ///

        ///
        container = findViewById(R.id.childButtonContainer);
        addChildButton = findViewById(R.id.addChildButton);
        logoutButton = findViewById(R.id.parentLogoutButton);

        parentUID = FirebaseAuth.getInstance().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        childUIDs = new ArrayList<>();
        /////showAlert(parentUID, this);
        addChildButton.setOnClickListener(v -> {
            Intent intent = new Intent(ParentHomeScreen.this, AddChildScreen.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            Logout.logout(this);
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

        NotificationUtils.ensurePermissionAndChannel(this);
        fdl.setContext(getApplicationContext());
        fdl.setParentId(FirebaseAuth.getInstance().getUid());
        fdl.attachListeners();

        String parentUID = FirebaseAuth.getInstance().getUid();
        TextView parentNameText = findViewById(R.id.parentNameText);
        dbRef.child(parentUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot childSnapshot) {
                String firstName = childSnapshot.child("firstName").getValue(String.class);
                String lastName = childSnapshot.child("lastName").getValue(String.class);
                String childName = firstName + " " + lastName;
                parentNameText.setText(childName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fdl.removeListeners();

    }


    void createChildButtons(List<String> childUIDs) {
        for (String childUID : childUIDs) {
            dbRef.child(childUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot childSnapshot) {
                    String firstName = childSnapshot.child("firstName").getValue(String.class);
                    String lastName = childSnapshot.child("lastName").getValue(String.class);
                    String childName = firstName + " " + lastName;
                    createChildButton(childUID, childName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }

    void createChildButton(String childUID, String childName) {
        MaterialButton button = new MaterialButton(this);

        button.setText(childName);
        button.setTextColor(getResources().getColor(android.R.color.white));
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        button.setPadding(0, 0, 0, 0);

        button.setBackgroundColor(Color.parseColor("#415f91"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(300), dpToPx(70)
        );
        params.setMargins(dpToPx(55), dpToPx(20), dpToPx(56), 0);
        button.setLayoutParams(params);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(ParentHomeScreen.this, ParentChildHomeScreen.class);
            intent.putExtra("childUID", childUID);
            intent.putExtra("childName", childName);
            startActivity(intent);
        });

        container.addView(button);
    }
    private int dpToPx(int dp){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
}