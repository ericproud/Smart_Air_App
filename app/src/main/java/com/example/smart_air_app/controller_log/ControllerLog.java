package com.example.smart_air_app.controller_log;

public class ControllerLog {
    private String feeling;
    private String time;
    private String date;
    private int doseInput;
    private int preInput;
    private int postInput;
    private int breathShortness;

    @Override
    public String toString() {
        return "controller_log: \nfeeling: " + feeling + "\ndose input: "
                + doseInput + "\npre input: " + preInput + "\npost input: " + postInput +
                "\nbreath shortness input: " + breathShortness;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDoseInput(int doseInput) {
        this.doseInput = doseInput;
    }

    public int getDoseInput() {
        return doseInput;
    }
    public void setPreInput(int preInput) {
        this.preInput = preInput;
    }

    public int getPreInput() {
        return preInput;
    }
    public void setPostInput(int postInput) {
        this.postInput = postInput;
    }

    public int getPostInput() {
        return postInput;
    }

    public void setBreathShortness(int breathShortness) {
        this.breathShortness = breathShortness;
    }

    public int getBreathShortness() {
        return breathShortness;
    }
}
