package com.example.smart_air_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.login_module.LoginView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordScreen extends AppCompatActivity {
    private Button resetButton;
    private Button backButton;
    private EditText inputEmail;
    private TextView emailSentText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);


            resetButton = findViewById(R.id.resetButton);
            backButton = findViewById(R.id.buttonResetBack);
            inputEmail = findViewById(R.id.inputResetEmail);
            emailSentText = findViewById(R.id.emailSentText);
            mAuth = FirebaseAuth.getInstance();

            emailSentText.setVisibility(View.INVISIBLE);

            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = inputEmail.getText().toString();
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        emailSentText.setText("Please check your email for a link to reset your password.");
                                        emailSentText.setVisibility(View.VISIBLE);
                                    } else {
                                        emailSentText.setText("An error has occured, please verify the email address submitted.");
                                        emailSentText.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                }
            });

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ResetPasswordScreen.this, LoginView.class);
                    startActivity(intent);
                }
            });

            return insets;
        });
    }
}