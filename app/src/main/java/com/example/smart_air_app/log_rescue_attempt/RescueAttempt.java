package com.example.smart_air_app.log_rescue_attempt;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

public class RescueAttempt {
    private double dosage;
    private List<String> triggers;
    private List<String> symptoms;
    private double peakFlowBefore;
    private double peakFlowAfter;
    private boolean triageIncident;

    @Override
    public String toString() {
        return "RescueAttempt{" +
                "dosage=" + dosage +
                ", triggers=" + triggers +
                ", symptoms=" + symptoms +
                ", peakFlowBefore=" + peakFlowBefore +
                ", peakFlowAfter=" + peakFlowAfter +
                ", triageIncident=" + triageIncident +
                '}';
    }

    public void setDosage(double dosage) {
        this.dosage = dosage;
    }

    public void setTriggers(List<String> triggers ) {
        this.triggers = triggers;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public void setPeakFlowBefore(double peakFlowBefore) {
        this.peakFlowBefore = peakFlowBefore;
    }

    public void setPeakFlowAfter(double peakFlowAfter) {
        this.peakFlowAfter = peakFlowAfter;
    }

    public void setTriageIncident(boolean triageIncident) {
        this.triageIncident = triageIncident;
    }

    public double getDosage() {
        return dosage;
    }

    public List<String> getTriggers() {
        return triggers;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public double getPeakFlowBefore() {
        return peakFlowBefore;
    }

    public double getPeakFlowAfter() {
        return peakFlowAfter;
    }

    public boolean isTriageIncident() {
        return triageIncident;
    }
}
