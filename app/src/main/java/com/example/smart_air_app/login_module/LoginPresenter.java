package com.example.smart_air_app.login_module;

public class LoginPresenter {
    LoginModel model;
    LoginView view;

    public LoginPresenter(LoginModel model, LoginView view) {
        this.model = model;
        this.view = view;
    }

    // Callback interface to be implemented by the view
    public interface AuthView {
        void redirectToHome(String userType);   // Callback to redirect to the home screen

        void showAuthFailedMessage();           // Callback to show the authentication failed message

        void resetInputs();                     // Callback to reset the inputs to the form

        void showError(String error);           // Callback to show an error message

        void onEmptyUsernameOrEmail();          // Callback to handle empty username or email

        void onEmptyPassword();                 // Callback to handle empty password
    }

    private class LoginCallback implements LoginModel.AuthCallback {
        @Override
        public void onAuthSuccess(String userType) {    // When login is successful
            view.redirectToHome(userType);              // Redirect to the home page
        }

        @Override
        public void onAuthFailure(String error) {   // When login fails
            view.showAuthFailedMessage();           // Show default failure message
            view.resetInputs();                     // Reset input fields
            view.showError(error);                  // Show specific error message
        }
    }
    // login method takes username and password and will pass it to the model
    public void login(String usernameOrEmail, String password) {
        String handledUsernameOrEmail;
        LoginCallback callback;

        if (!validateInputs(usernameOrEmail, password)) {   // If inputs are invalid
            return;                                         // Do not continue (call to validateTnputs handles errors)
        }
        callback = new LoginCallback();     // Initialize the callback
        handledUsernameOrEmail = convertUsername(usernameOrEmail);  // Convert username to email if needed
        model.login(handledUsernameOrEmail, password, callback);    // Call the model's login method
    }

    // Ensures neither the email nor the password are vailid
    public boolean validateInputs(String usernameOrEmail, String password) {
        boolean validUsernameOrEmail;
        boolean validPassword;

        validUsernameOrEmail = validateUsernameOrEmail(usernameOrEmail);    // Check if username / email is valid
        validPassword = validatePassword(password);                         // Check if password is valid

        if (!validUsernameOrEmail) {        // If username / email is invalid
            handleEmptyUsernameOrEmail();   // Call the callback to handle the error
        }
        if (!validPassword) {               // If password is invalid
            handleEmptyPassword();          // Call the callback to handle the error
        }
        return validUsernameOrEmail && validPassword;  // Return true iff ( username / email ) and password are valid
    }

    // Checks if username / email is valid
    public boolean validateUsernameOrEmail(String usernameOrEmail) {
        if (usernameOrEmail.isEmpty()) {    // If username / email is empty
            return false;                   // Return false
        }
        return true;    // Otherwise return true
    }

    // Checks if password is valid
    public boolean validatePassword(String password) {
        if (password.isEmpty()) {   // If password is empty
            return false;           // Return false
        }
        return true;    // Otherwise return true
    }

    // Handles the empty username / email error with a callback to the view
    public void handleEmptyUsernameOrEmail() {
        view.onEmptyUsernameOrEmail();  // Call the callback to handle the error
    }

    // Handles the empty password error with a callback to the view
    public void handleEmptyPassword() {
        view.onEmptyPassword();         // Call the callback to handle the error
    }


    // Convert childs username to an email for the purposes of the app as allowed on Piazza
    // Lets us register an account for a child as though they had an email
    public String convertUsername(String usernameOrEmail) {
        if (usernameOrEmail.contains("@")) {    // If username / email is already an email
            return usernameOrEmail;             // Return as is
        } else
            return usernameOrEmail + "@xyz.com";   // Otherwise, add fake email extension to end of username
    }
}
