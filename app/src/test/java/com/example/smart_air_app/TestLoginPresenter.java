package com.example.smart_air_app;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;

import android.util.Log;

import com.example.smart_air_app.login_module.LoginModel;
import com.example.smart_air_app.login_module.LoginPresenter;
import com.example.smart_air_app.login_module.LoginView;


public class TestLoginPresenter {
    @Mock
    LoginModel model;
    @Mock
    LoginView view;
    @Test
    public void checkEmptyPassword() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String password = "";
        assertFalse(presenter.validatePassword(password));
    }
}
