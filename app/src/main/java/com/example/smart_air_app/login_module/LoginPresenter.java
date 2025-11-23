package com.example.smart_air_app.login_module;

import com.example.smart_air_app.session.SessionManager;
import com.example.smart_air_app.user_classes.User;

public class LoginPresenter {
    LoginModel model;
    LoginView view;
    public LoginPresenter(LoginModel model, LoginView view) {
        this.model = model;
        this.view = view;
    }

    public interface AuthView {
        void redirectToHome(String userType);
        void showAuthFailedMessage();
        void resetInputs();
        void showError(String error);
        void onEmptyUsernameOrEmail();
        void onEmptyPassword();
    }

    private class LoginCallback implements LoginModel.AuthCallback {
        @Override
        public void onAuthSuccess(User user) {
            // store basic information about the user like id to be used to make later requests
            SessionManager.getInstance().setCurrentUser(user);
            view.redirectToHome(user.type);
        }

        @Override
        public void onAuthFailure(String error) {
            view.showAuthFailedMessage();
            view.resetInputs();
            view.showError(error);
        }
    }

    public void Login(String usernameOrEmail, String password) {
        String handledUsernameOrEmail;
        LoginCallback callback;

        if (!validateInputs(usernameOrEmail, password)) {
            return;
        }
        callback = new LoginCallback();
        handledUsernameOrEmail = convertUsername(usernameOrEmail);
        model.signIn(handledUsernameOrEmail, password, callback);
    }

    public boolean validateInputs(String usernameOrEmail, String password) {
        boolean validUsernameOrEmail;
        boolean validPassword;

        validUsernameOrEmail = validateUsernameOrEmail(usernameOrEmail);
        validPassword = validatePassword(password);

        if (!validateUsernameOrEmail(usernameOrEmail)) {
            handleEmptyUsernameOrEmail();
        }
        if (!validPassword) {
            handleEmptyPassword();
        }
        return validUsernameOrEmail && validPassword;
    }

    public boolean validateUsernameOrEmail(String usernameOrEmail) {
        if (usernameOrEmail.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean validatePassword(String password) {
        if (password.isEmpty()) {
            return false;
        }
        return true;
    }

    public void handleEmptyUsernameOrEmail() {
        view.onEmptyUsernameOrEmail();
    }

    public void handleEmptyPassword() {
        view.onEmptyPassword();
    }

    public String convertUsername(String usernameOrEmail) {
        if (usernameOrEmail.contains("@")) {
            return usernameOrEmail;
        }
        else return usernameOrEmail + "@xyz.com";
    }
}
