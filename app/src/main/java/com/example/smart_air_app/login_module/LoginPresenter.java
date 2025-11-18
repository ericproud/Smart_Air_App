package com.example.smart_air_app.login_module;

public class LoginPresenter {
    LoginModel model;
    LoginView view;
    public LoginPresenter(LoginModel model, LoginView view) {
        this.model = model;
        this.view = view;
    }
}
