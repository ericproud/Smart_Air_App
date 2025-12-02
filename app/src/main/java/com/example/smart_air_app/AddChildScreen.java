package com.example.smart_air_app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.alerts.FirebaseDatabaseListeners;
import com.example.smart_air_app.inventory.FirebaseInventoryRepository;
import com.example.smart_air_app.inventory.InventoryRepository;
import com.example.smart_air_app.user_classes.Child;
import com.example.smart_air_app.utils.DateValidator;
import com.example.smart_air_app.utils.FormHelperFunctions;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class AddChildScreen extends AppCompatActivity {
    FirebaseAuth mAuth;
    private DatePickerDialog datePickerDialog;
    private Button inputDOBButton;
    private Button createAccountButton;
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputHeight;
    private EditText inputWeight;
    private EditText inputUsername;
    private EditText inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_child_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initDatePicker();
        mAuth = FirebaseAuth.getInstance();
        inputFirstName = findViewById(R.id.inputChildFirstName);
        inputLastName = findViewById(R.id.inputChildLastName);
        inputHeight = findViewById(R.id.inputChildHeight);
        inputWeight = findViewById(R.id.inputChildWeight);
        inputUsername = findViewById(R.id.inputChildUsername);
        inputPassword = findViewById(R.id.inputChildPassword);
        createAccountButton = findViewById(R.id.createAccountButton);
        inputDOBButton = findViewById(R.id.inputChildDOB);
        inputDOBButton.setText(getTodaysDate());

        createAccountButton.setOnClickListener(v -> {
            createAccount();
        });
    }

    private void createAccount() {
        boolean invalidField =
                (      FormHelperFunctions.handleEmpty(inputFirstName) ||
                        FormHelperFunctions.handleEmpty(inputLastName) ||
                        FormHelperFunctions.handleEmpty(inputHeight) ||
                        FormHelperFunctions.handleEmpty(inputWeight) ||
                        FormHelperFunctions.handleInvalidUsername(inputUsername) ||
                        FormHelperFunctions.handleEmpty(inputPassword) ||
                        !DateValidator.isValidDate(inputDOBButton.getText().toString().trim())
                );


        if (invalidField) return;

        String firstName = inputFirstName.getText().toString().trim();
        String lastName = inputLastName.getText().toString().trim();
        String height = inputHeight.getText().toString().trim();
        String weight = inputWeight.getText().toString().trim();
        String DOB = inputDOBButton.getText().toString().trim();
        String username = inputUsername.getText().toString().trim() + "@xyz.com"; // Add fake email extension for firebase authentication to be easier
        String password = inputPassword.getText().toString().trim();

        HashMap<String, Boolean> permissions = new HashMap<>();
        HashMap<String, Integer> streaks = new HashMap<>();
        HashMap<String, Integer> badges = new HashMap<>();

        permissions.put("controller adherence summary", false);
        permissions.put("rescue logs", false);
        permissions.put("symptoms", false);
        permissions.put("triggers", false);
        permissions.put("pef", false);
        permissions.put("triage incidents", false);
        permissions.put("summary charts", false);

        streaks.put("consecutive controller use days", 0);
        streaks.put("consecutive technique conpleted days", 0);

        badges.put("first perfect controller week", 0);
        badges.put("10 high quality technique sessions", 0);
        badges.put("low rescue month", 0);

        String parentUID = FirebaseAuth.getInstance().getUid();
        FirebaseAuth childAuth = createChildAuth();

        childAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uID = childAuth.getCurrentUser().getUid();
                        Child newChild = new Child(firstName, lastName, height, weight, DOB, uID, parentUID);
                        FirebaseDatabase.getInstance().getReference("Users").child(uID).setValue(newChild);
                        FirebaseDatabase.getInstance().getReference("Badges").child(uID).setValue(badges);
                        FirebaseDatabase.getInstance().getReference("Permissions").child(uID).setValue(permissions);
                        FirebaseDatabase.getInstance().getReference("Streaks").child(uID).setValue(streaks);

                        FirebaseInventoryRepository inv = new FirebaseInventoryRepository();
                        inv.setUid(uID);
                        inv.initInventory();


                        // needed for alerts
                        DatabaseReference triageRef = FirebaseDatabase.getInstance().getReference("Triage").child(uID);
                        DatabaseReference rescueRef = FirebaseDatabase.getInstance().getReference("RescueAttempts").child(uID);
                        DatabaseReference controllerRef = FirebaseDatabase.getInstance().getReference("ControllerLogs").child(uID);
                        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("Users")
                                .child(parentUID)
                                .child("children")
                                .child(uID);
                        DatabaseReference zoneRef = FirebaseDatabase.getInstance().getReference("Users").child(uID).child("Zones");

                        triageRef.setValue("null").addOnSuccessListener(aVoid1 -> {
                            rescueRef.setValue("null").addOnSuccessListener(aVoid2 -> {
                                controllerRef.setValue("null").addOnSuccessListener(aVoid3 -> {
                                    childRef.setValue(true).addOnSuccessListener(aVoid4 -> {
                                        zoneRef.setValue("null").addOnSuccessListener(aVoid5 -> {
                                            FirebaseDatabaseListeners.getInstance().attachListeners(uID, firstName + " " + lastName);
                                        }).addOnFailureListener(e -> {});
                                    }).addOnFailureListener(e -> {});
                                }).addOnFailureListener(e -> {});
                            }).addOnFailureListener(e -> {});
                        }).addOnFailureListener(e -> {});
                        childAuth.signOut();

                        Intent intent = new Intent(AddChildScreen.this, ParentHomeScreen.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(AddChildScreen.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                    childAuth.getApp().delete(); // dispose temp child auth instance
                });
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return DateValidator.makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = DateValidator.makeDateString(day, month, year);
                inputDOBButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    private FirebaseAuth createChildAuth() {
        // work around: create a new auth for whenever parent creates a new child
        // to avoid signing out the parent
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyAYEH_8fDLodoHSWkihUMGkiz0aHEYQ4-A")
                .setApplicationId("1:282364377779:android:eebc52271deecfcf2d0ec6")
                .setProjectId("smart-air-app-database")
                .build();

        String appName = "ChildApp_" + System.currentTimeMillis();
        FirebaseApp childApp = FirebaseApp.initializeApp(this, options, appName);
        return FirebaseAuth.getInstance(childApp);
    }
}