package com.example.smart_air_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.smart_air_app.user_classes.User;
import com.example.smart_air_app.utils.FormHelperFunctions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterAsDoctor extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputPassword;
    private Button doctorRegisterButton;
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_as_doctor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        inputFirstName = findViewById(R.id.inputDoctorFirstName);
        inputLastName = findViewById(R.id.inputDoctorLastName);
        inputEmail = findViewById(R.id.inputDoctorEmail);
        inputPassword = findViewById(R.id.inputDoctorPassword);
        doctorRegisterButton = findViewById(R.id.doctorRegisterButton);
        backButton = findViewById((R.id.doctorRegisterBackButton));

        doctorRegisterButton.setOnClickListener(v -> createAccount());
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterAsDoctor.this, StartScreen.class));
            finish();
        });
    }

    private void createAccount() {
        boolean invalidField =
                (      FormHelperFunctions.handleEmpty(inputFirstName) ||
                        FormHelperFunctions.handleEmpty(inputLastName) ||
                        FormHelperFunctions.handleInvalidEmail(inputEmail) ||
                        FormHelperFunctions.handleEmpty(inputPassword)
                );

        if (invalidField) return;

        String firstName = inputFirstName.getText().toString().trim();
        String lastName = inputLastName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
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
}
