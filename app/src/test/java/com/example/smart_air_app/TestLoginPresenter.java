package com.example.smart_air_app;

import static org.junit.Assert.assertFalse;

import com.example.smart_air_app.login_module.LoginModel;
import com.example.smart_air_app.login_module.LoginPresenter;
import com.example.smart_air_app.login_module.LoginView;
import com.example.smart_air_app.session.SessionManager;
import com.example.smart_air_app.user_classes.User;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestLoginPresenter {
    @Mock
    LoginModel model;
    @Mock
    LoginView view;

    @Test
    public void checkValidatePasswordWithEmptyString() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String password = "";
        assertFalse(presenter.validatePassword(password));
    }

    @Test
    public void checkValidatePasswordWithValidPassword() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String password = "password";
        assertTrue(presenter.validatePassword(password));
    }

    @Test
    public void checkValidateUsernameOrEmailWithEmptyString() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String usernameOrEmail = "";
        assertFalse(presenter.validateUsernameOrEmail(usernameOrEmail));
    }

    @Test
    public void checkValidateUsernameOrEmailWithValidUsername() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String username = "user";

        assertTrue(presenter.validateUsernameOrEmail(username));
    }

    @Test
    public void checkValidateUsernameOrEmailWithValidEmail() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String email = "email@gmail.com";

        assertTrue(presenter.validateUsernameOrEmail(email));
    }

    @Test
    public void checkHandleUsernameWithEmail() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String email = "email@gmail.com";

        assertEquals(email, presenter.handleUsername(email));
    }

    @Test
    public void checkHandleUsernameWithUsername() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String username = "user";

        assertEquals("user@xyz.com", presenter.handleUsername(username));
    }

    @Test
    public void checkValidateInputsBothInputsInvalid() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String usernameOrEmail = "";
        String password = "";

        assertFalse(presenter.validateInputs(usernameOrEmail, password));
    }

    @Test
    public void checkValidateInputsWithInvalidUsernameOrEmail() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String usernameOrEmail = "";
        String password = "password";

        assertFalse(presenter.validateInputs(usernameOrEmail, password));
    }

    @Test
    public void checkValidateInputsWithInvalidPassword() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String usernameOrEmail = "user@xyz.com";
        String password = "";

        assertFalse(presenter.validateInputs(usernameOrEmail, password));
    }

    @Test
    public void checkValidateInputsWithBothInputsValid() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String usernameOrEmail = "user@xyz.com";
        String password = "password";

        assertTrue(presenter.validateInputs(usernameOrEmail, password));
    }

    @Test
    public void checkLoginWithBothInputsInvalid() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String usernameOrEmail = "";
        String password = "";
        presenter.Login(usernameOrEmail, password);

        verify(model, never()).signIn(anyString(), anyString(), any());
    }

    @Test
    public void checkLoginWithValidUsernameOrEmailAndInvalidPassword() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String usernameOrPassword = "username";
        String password = "";
        presenter.Login(usernameOrPassword, password);

        verify(model, never()).signIn(anyString(), anyString(), any());
    }

    @Test
    public void checkLoginWithInvalidUsernameOrEmailAndValidPassword() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String usernameOrPassword = "";
        String password = "password";
        presenter.Login(usernameOrPassword, password);

        verify(model, never()).signIn(anyString(), anyString(), any());
    }


    @Test
    public void checkLoginWithValidUsernameAndValidPassword() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String username = "username";
        String password = "password";
        presenter.Login(username, password);

        verify(model).signIn(eq(presenter.handleUsername(username)), eq(password), any());
    }

    @Test
    public void checkLoginWithValidEmailAndValidPassword() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String email = "email@gmail.com";
        String password = "password";
        presenter.Login(email, password);

        verify(model).signIn(eq(presenter.handleUsername(email)), eq(password), any());
    }

    @Test
    public void checkOnAuthSuccess() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String email = "email@gmail.com";
        String password = "password";
        presenter.Login(email, password);

        ArgumentCaptor<LoginModel.AuthCallback> callbackCaptor = ArgumentCaptor.forClass(LoginModel.AuthCallback.class);
        verify(model).signIn(anyString(), anyString(), callbackCaptor.capture());

        User user = mock(User.class);
        user.type = "doctor";
        callbackCaptor.getValue().onAuthSuccess(user);

        assertEquals(user, SessionManager.getInstance().getCurrentUser());
        verify(view).redirectToHome("doctor");
    }

    @Test
    public void checkOnAuthFailure() {
        LoginPresenter presenter = new LoginPresenter(model, view);
        String email = "username";
        String password = "password";
        presenter.Login(email, password);

        ArgumentCaptor<LoginModel.AuthCallback> callbackCaptor = ArgumentCaptor.forClass(LoginModel.AuthCallback.class);
        verify(model).signIn(anyString(), anyString(), callbackCaptor.capture());

        String errorMessage = "error";
        callbackCaptor.getValue().onAuthFailure(errorMessage);

        verify(view).showAuthFailedMessage();
        verify(view).resetInputs();
        verify(view).showError(errorMessage);
    }
}
