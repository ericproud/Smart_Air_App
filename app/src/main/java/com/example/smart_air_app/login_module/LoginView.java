package com.example.smart_air_app.login_module;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.ChildHomeScreen;
import com.example.smart_air_app.DoctorHomeScreen;
import com.example.smart_air_app.ParentHomeScreen;
import com.example.smart_air_app.R;
import com.example.smart_air_app.ResetPasswordScreen;
import com.example.smart_air_app.StartScreen;

public class LoginView extends AppCompatActivity implements LoginPresenter.AuthView {
    LoginPresenter presenter;
    Button backButton;
    Button loginButton;
    Button recoverAccountButton;
    EditText inputUsernameOrEmail;
    EditText inputPassword;
    TextView authFailedText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        presenter = new LoginPresenter(new LoginModel(), this);     // Initialize presenter with a login model
                                                                         // And the current view (this)

        // Init buttons
        backButton = findViewById(R.id.buttonLoginBack);
        loginButton = findViewById(R.id.LoginButton);
        recoverAccountButton = findViewById(R.id.buttonRecoverAccount);

        // Init input fields
        inputUsernameOrEmail = findViewById(R.id.inputLoginUsernameOrEmail);
        inputPassword = findViewById(R.id.inputLoginPassword);

        // Init popup text for if / when login fails and make it hidden at first
        authFailedText = findViewById(R.id.AuthFailedText);
        authFailedText.setVisibility(View.INVISIBLE);

        // Set on click listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputUsernameOrEmail.getText().toString();   // Get inputted username / email
                String password = inputPassword.getText().toString();       // Get inputted password
                presenter.Login(email, password);   // Call the presenter's login method with the username / email and password
            }
        });

        // Set on click listener for recover account button
        recoverAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginView.this, ResetPasswordScreen.class);  // Create intent to go to reset password screen
                startActivity(intent);  // Send user to reset password screen
                finish();   // Terminate current activity
            }
        });

        // Set on click listener for back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginView.this, StartScreen.class);  // Create intent to go to start screen
                startActivity(intent);  // Send user to start screen
                finish();   // Terminate current activity
            }
        });
    }

    // Callback method for when login is successful to redirect to the home screen

    @Override
    public void redirectToHome(String userType) {
        Intent intent;
        if (userType.equals("child")) {         // If user is a child
            intent = new Intent(LoginView.this, ChildHomeScreen.class);     // Create intent to go to child home screen
        }
        else if (userType.equals("parent")) {   // If user is a parent
            intent = new Intent(LoginView.this, ParentHomeScreen.class);    // Create intent to go to parent home screen
        }
        else if (userType.equals("doctor")) {   // If user is a doctor
            intent = new Intent(LoginView.this, DoctorHomeScreen.class);    // Create intent to go to doctor home screen
        }
        else intent = new Intent(LoginView.this, StartScreen.class);        // Otherwise, something has gone wrong,
                                                                                          // Create intent to go to start screen
        startActivity(intent);      // Send user to their respective home screen
        finish();                   // Terminate current activity
    }

    // Callback method for when login fails to show the default failure message
    @Override
    public void showAuthFailedMessage() {
        authFailedText.setVisibility(View.VISIBLE);     // Make the popup failure text visible
    };

    // Callback method for when login fails / an input field is empty to reset the input fields
    @Override
    public void resetInputs(){
        inputUsernameOrEmail.setText("");   // Reset the username / email input field
        inputPassword.setText("");          // Reset the password input field
    }

    // Callback method for when login fails to show an error message
    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();  // Show the error message
    };

    // Callback method for when the username / email is invalid
    @Override
    public void onEmptyUsernameOrEmail() {
        // Set the hint text colour to red
        inputUsernameOrEmail.setHintTextColor(getResources().getColor(android.R.color.holo_red_dark));
    }

    // Callback method for when the password is invalid
    @Override
    public void onEmptyPassword() {
        // Set the hint text colour to red
        inputPassword.setHintTextColor(getResources().getColor(android.R.color.holo_red_dark));
    }
}