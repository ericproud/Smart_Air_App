package com.example.smart_air_app.session;

import com.example.smart_air_app.user_classes.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getUserId() {
        return currentUser == null ? "" : currentUser.userID;
    }
}
