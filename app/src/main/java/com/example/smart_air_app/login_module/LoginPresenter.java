package com.example.smart_air_app.login_module;

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
        public void onAuthSuccess(String userType) {
            // store basic information about the user like id to be used to make later requests
            view.redirectToHome(userType);
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
        handledUsernameOrEmail = handleUsername(usernameOrEmail);
        model.signIn(handledUsernameOrEmail, password, callback);
    }

    public boolean validateInputs(String usernameOrEmail, String password) {
        boolean validUsernameOrEmail;
        boolean validPassword;

        validUsernameOrEmail = validateUsernameOrEmail(usernameOrEmail);
        validPassword = validatePassword(password);

        return validUsernameOrEmail && validPassword;
    }

    public boolean validateUsernameOrEmail(String usernameOrEmail) {
        if (usernameOrEmail.isEmpty()) {
            view.onEmptyUsernameOrEmail();
            return false;
        }
        return true;
    }

    public boolean validatePassword(String password) {
        if (password.isEmpty()) {
            view.onEmptyPassword();
            return false;
        }
        return true;
    }

    public String handleUsername(String usernameOrEmail) {
        if (usernameOrEmail.contains("@")) {
            return usernameOrEmail;
        }
        else return usernameOrEmail + "@xyz.com";
    }
}
