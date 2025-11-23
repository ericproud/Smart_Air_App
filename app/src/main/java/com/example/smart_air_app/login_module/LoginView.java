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
    Button backButton;
    Button loginButton;
    Button recoverAccountButton;
    EditText inputUsernameOrEmail;
    EditText inputPassword;
    TextView authFailedText;
    LoginPresenter presenter;


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

        backButton = findViewById(R.id.buttonLoginBack);
        loginButton = findViewById(R.id.LoginButton);
        inputUsernameOrEmail = findViewById(R.id.inputLoginUsernameOrEmail);
        inputPassword = findViewById(R.id.inputLoginPassword);
        authFailedText = findViewById(R.id.AuthFailedText);
        recoverAccountButton = findViewById(R.id.buttonRecoverAccount);
        presenter = new LoginPresenter(new LoginModel(), this);

        authFailedText.setVisibility(View.INVISIBLE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputUsernameOrEmail.getText().toString();
                String password = inputPassword.getText().toString();
                presenter.Login(email, password);
            }
        });

        recoverAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginView.this, ResetPasswordScreen.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginView.this, StartScreen.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void redirectToHome(String userType) {
        Intent intent;

        if (userType.equals("child")) {
            intent = new Intent(LoginView.this, ChildHomeScreen.class);
        }
        else if (userType.equals("parent")) {
            intent = new Intent(LoginView.this, ParentHomeScreen.class);
        }
        else if (userType.equals("doctor")) {
            intent = new Intent(LoginView.this, DoctorHomeScreen.class);
        }
        else intent = new Intent(LoginView.this, StartScreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showAuthFailedMessage() {
        authFailedText.setVisibility(View.VISIBLE);
    };

    @Override
    public void resetInputs(){
        inputUsernameOrEmail.setText("");
        inputPassword.setText("");
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    };

    @Override
    public void onEmptyUsernameOrEmail() {
        inputUsernameOrEmail.setHintTextColor(getResources().getColor(android.R.color.holo_red_dark));
    }

    @Override
    public void onEmptyPassword() {
        inputPassword.setHintTextColor(getResources().getColor(android.R.color.holo_red_dark));
    }
}