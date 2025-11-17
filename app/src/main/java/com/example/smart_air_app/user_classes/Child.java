package com.example.smart_air_app.user_classes;

import com.example.smart_air_app.utils.DateValidator;

public class Child extends User{
    public int height;
    public int weight;
    public String DOB;

    public Child(String firstName, String lastName, int height, int weight, String DOB, String userID) {
        if (firstName.isEmpty()) {
            throw new IllegalArgumentException("Invalid first name: can not be empty");
        }
        if (lastName.isEmpty()) {
            throw new IllegalArgumentException("Invalid last name: can not be empty");
        }
        if(!DateValidator.isValidDate(DOB)) {
            throw new IllegalArgumentException("Invalid date format: " + DOB);
        }
        if(height < 0) {
            throw new IllegalArgumentException("Invalid height: " + height);
        }
        if(weight < 0) {
            throw new IllegalArgumentException("Invalid weight: " + weight);
        }
        this.type = "child";
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
        this.height = height;
        this.weight = weight;
        this.DOB = DOB;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        if(DateValidator.isValidDate(DOB)) {
            this.DOB = DOB;
        }
        else {
            throw new IllegalArgumentException("Invalid date format: " + DOB);
        }
    }
}
