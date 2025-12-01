package com.example.smart_air_app.user_classes;

import com.example.smart_air_app.utils.DateValidator;

import java.util.Date;
import java.util.HashMap;

public class Child extends User{
    private String height;
    private String weight;
    private String DOB;
    private boolean isOnboarded;

    private String ParentUID;


    public Child(String firstName, String lastName, String height, String weight, String DOB, String userID, String ParentUID) {
        super("child", firstName, lastName, userID);

        if(!DateValidator.isValidDate(DOB)) {
            throw new IllegalArgumentException("Invalid date format: " + DOB);
        }
        if(Integer.parseInt(height) < 0) {
            throw new IllegalArgumentException("Invalid height: " + height);
        }
        if(Integer.parseInt(weight) < 0) {
            throw new IllegalArgumentException("Invalid weight: " + weight);
        }

        this.height = height;
        this.weight = weight;
        this.DOB = DOB;
        this.isOnboarded = false;
        this.ParentUID = ParentUID;
    }
    public String getWeight() {
        return weight;
    }

    public String getDOB() {
        return DOB;
    }

    public boolean getIsOnboarded() {
        return isOnboarded;
    }

    public String getHeight() {
        return height;
    }

    public String getParentUID() {
        return ParentUID;
    }
}
