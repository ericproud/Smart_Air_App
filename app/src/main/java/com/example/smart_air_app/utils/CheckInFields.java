package com.example.smart_air_app.utils;

public class CheckInFields {

        public Long date;
        public String author;
        public boolean nightWaking;
        public boolean activityLimits;
        public boolean cough;
        public String triggers;

        public CheckInFields() {}

        public CheckInFields(Long date, String author, boolean night, boolean activity, boolean cough, String triggers) {
            this.date = date;
            this.author = author;
            this.nightWaking = night;
            this.activityLimits = activity;
            this.cough = cough;
            this.triggers = triggers;
        }

}
