package com.example.smart_air_app.triage;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicInteger;

public class TriageEntry {

    private static int numTriages = 0; // Number of total triage entries across all users
    private String childUID;
    private int triageID;
    private boolean[] redFlags = new boolean[3];
    // redFlags[0]: can't speak full sentences
    // redFlags[1]: chest pulling in/retractions
    // redFlags[2]: blue/gray lips or nails
    private boolean recentRescue;
    private double PEF;
    private boolean emergency;

    public TriageEntry() { // need a public empty constructor?

        this.triageID = numTriages;
        numTriages++;

        childUID = "-1";
        redFlags[0] = false;
        redFlags[1] = false;
        redFlags[2] = false;
        recentRescue = false;
        PEF = -1;
        emergency = false;
    }

    public TriageEntry(boolean[] redFlags, boolean recentRescue, double PEF, boolean emergency) {

        this.triageID = numTriages;
        numTriages++;

        this.childUID = "-1";
        this.redFlags = redFlags;
        this.recentRescue = recentRescue;
        this.PEF = PEF;
        this.emergency = emergency;
    }

    public int getTriageID() {
        return triageID;
    }

    public void setRedFlag(int flagNum, boolean value) {
        redFlags[flagNum] = value;
    }

    public boolean getRedFlag(int flagNum) {
        return redFlags[flagNum];
    }

    public boolean[] getAllRedFlags() {
        return redFlags;
    }

    public boolean getRecentRescue() {
        return recentRescue;
    }

    public void setRecentRescue(boolean value) {
        recentRescue = value;
    }

    public double getPEF() {
        return PEF;
    }

    public void setPEF(double value) {
        PEF = value;
    }

    public String getChildUID() {
        return childUID;
    }

    public boolean getEmergencyStatus() {
        return emergency;
    }

}
