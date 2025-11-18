package com.example.smart_air_app.user_classes;

import com.example.smart_air_app.utils.DateValidator;

import java.util.HashMap;

public class Child extends User{
    public int height;
    public int weight;
    public String DOB;
    HashMap<String, Integer> permissions;

    public Child(String firstName, String lastName, int height, int weight, String DOB, int userID) {
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
        permissions = new HashMap<>();

        // Controller adherence summary and rescue logs are 0 or 3-6 for NOT SHARED / 3-6 MONTHS SHARED
        // All others are 0 or 1 for NOT SHARED / SHARED
        permissions.put("controller adherence summary", 0);
        permissions.put("rescue logs", 0);
        permissions.put("symptoms", 0);
        permissions.put("triggers", 0);
        permissions.put("pef", 0);
        permissions.put("triage incidents", 0);
        permissions.put("summary charts", 0);
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
