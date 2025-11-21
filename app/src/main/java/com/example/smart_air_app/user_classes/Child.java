package com.example.smart_air_app.user_classes;

import com.example.smart_air_app.utils.DateValidator;

import java.util.Date;
import java.util.HashMap;

public class Child extends User{
    private String height;
    private String weight;
    private String DOB;
    private boolean isOnboarded;
    HashMap<String, Integer> permissions;
    HashMap<String, Integer> inventoryRemaining;
    HashMap<String, String> inventoryExpiresOn;
    HashMap<String, Integer> streaks;
    HashMap<String, Integer> badges;

    public Child(String firstName, String lastName, String height, String weight, String DOB, String userID) {
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
        permissions = new HashMap<>();
        inventoryRemaining = new HashMap<>();
        inventoryExpiresOn = new HashMap<>();
        streaks = new HashMap<>();
        badges = new HashMap<>();

        // Controller adherence summary and rescue logs are 0 or 3-6 for NOT SHARED / 3-6 MONTHS SHARED
        // All others are 0 or 1 for NOT SHARED / SHARED
        permissions.put("controller adherence summary", 0);
        permissions.put("rescue logs", 0);
        permissions.put("symptoms", 0);
        permissions.put("triggers", 0);
        permissions.put("pef", 0);
        permissions.put("triage incidents", 0);
        permissions.put("summary charts", 0);

        // Inventory remaining initialized to 0 (unit is puffs)
        inventoryRemaining.put("controller medicine puffs remaining", 0);
        inventoryRemaining.put("rescue inhaler puffs remaining", 0);

        // The day the inventory expires is set to the date of account creation
        inventoryExpiresOn.put("controller medicine expiry date", DateValidator.getTodaysDate());
        inventoryExpiresOn.put("rescue inhaler expiry date", DateValidator.getTodaysDate());

        // Set streaks to 0 days (unit is days)
        streaks.put("consecutive controller use days", 0);
        streaks.put("consecutive technique conpleted days", 0);

        // Set badges to 0 (unit - 0 if no badge, 1 if has badge)
        badges.put("first perfect controller week", 0);
        badges.put("10 high quality technique sessions", 0);
        badges.put("low rescue month", 0);
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
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
