package com.example.smart_air_app.log_rescue_attempt;

import java.util.EnumSet;
import java.util.HashSet;

public class RescueAttempt {
    private double dosage;
    private EnumSet<Trigger> triggers;
    private EnumSet<Symptom>  symptoms;
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

    public void setTriggers(EnumSet<Trigger> triggers ) {
        this.triggers = triggers;
    }

    public void setSymptoms(EnumSet<Symptom> symptoms) {
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
}
