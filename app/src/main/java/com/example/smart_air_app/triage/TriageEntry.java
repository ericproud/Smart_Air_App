package com.example.smart_air_app.triage;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TriageEntry {

    private String triageID;
    private boolean[] redFlags = new boolean[3];
    // redFlags[0]: can't speak full sentences
    // redFlags[1]: chest pulling in/retractions
    // redFlags[2]: blue/gray lips or nails
    private boolean recentRescue;
    private double PEF;
    private boolean emergency;

    public TriageEntry() {

    }

    public TriageEntry(boolean[] redFlags, boolean recentRescue, double PEF, boolean emergency) {

        this.triageID = UUID.randomUUID().toString();
        this.redFlags = redFlags;
        this.recentRescue = recentRescue;
        this.PEF = PEF;
        this.emergency = emergency;
    }

    public String getTriageID() {
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

    public boolean getEmergencyStatus() {
        return emergency;
    }

}
