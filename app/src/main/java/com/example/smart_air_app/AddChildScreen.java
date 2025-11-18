package com.example.smart_air_app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.user_classes.User;
import com.example.smart_air_app.utils.DateValidator;
import com.example.smart_air_app.utils.FormHelperFunctions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

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

        createAccountButton.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {
        boolean invalidField =
                (      FormHelperFunctions.handleEmpty(inputFirstName) ||
                        FormHelperFunctions.handleEmpty(inputLastName) ||
                        FormHelperFunctions.handleEmpty(inputHeight) ||
                        FormHelperFunctions.handleEmpty(inputWeight) ||
                        !DateValidator.isValidDate(inputDOBButton.getText().toString().trim()) ||
                        FormHelperFunctions.handleInvalidUsername(inputUsername) ||
                        FormHelperFunctions.handleEmpty(inputPassword)
                );

        if (invalidField) return;

        String firstName = inputFirstName.getText().toString().trim();
        String lastName = inputLastName.getText().toString().trim();
        String height = inputHeight.getText().toString().trim();
        String weight = inputWeight.getText().toString().trim();
        String DOB = inputDOBButton.getText().toString().trim();
        String username = inputUsername.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uID = mAuth.getCurrentUser().getUid();
                            User newUser = new User("doctor", firstName, lastName, uID);
                            FirebaseDatabase.getInstance().getReference("Users").child(uID).setValue(newUser);
                            startActivity(new Intent(RegisterAsDoctor.this, LoginScreen.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterAsDoctor.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                inputDOBButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";
        return "JAN";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}