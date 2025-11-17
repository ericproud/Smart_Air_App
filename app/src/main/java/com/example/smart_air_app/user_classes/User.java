package com.example.smart_air_app.user_classes;

public class User {
    public String userID;
    public String type; // 3 options - "child", "parent", "doctor"
    public String firstName;
    public String lastName;

    public User() {};
    public User(String type, String firstName, String lastName, String userID) {
        if (type.equals("child")) {
            throw new IllegalArgumentException("Initialize a child object if type is 'child'");
        }
        if (!type.equals("parent") && !type.equals("doctor")) {
            throw new IllegalArgumentException("Invalid user type: " + type);
        }
        if (firstName.isEmpty()) {
            throw new IllegalArgumentException("Invalid first name: can not be empty");
        }
        if (lastName.isEmpty()) {
            throw new IllegalArgumentException("Invalid last name: can not be empty");
        }
        this.type = type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
    }

    // Only use when instantiating child

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName.isEmpty()) {
            throw new IllegalArgumentException("Invalid first name: can not be empty");
        }
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName.isEmpty()) {
            throw new IllegalArgumentException("Invalid last name: can not be empty");
        }
        this.lastName = lastName;
    }

    public String getType() {
        return type;
    }

    public String getUserID() {
        return userID;
    }
}
