package com.example.smart_air_app.triage;

public class TriageEntry {

    private static int numTriages = 0; // Number of total triage entries across all users
    private int triageID;
    private boolean[] redFlags = new boolean[3];
    // redFlags[0]: can't speak full sentences
    // redFlags[1]: chest pulling in/retractions
    // redFlags[2]: blue/gray lips or nails
    private boolean recentRescue;
    private double PEF;

    public TriageEntry() {
        triageID = numTriages;
        numTriages++;
        redFlags[0] = false;
        redFlags[1] = false;
        redFlags[2] = false;
        recentRescue = false;
        PEF = -1;
    }

    public TriageEntry(boolean[] redFlags, boolean recentRescue, double PEF) {
        triageID = numTriages;
        numTriages++;
        this.redFlags = redFlags;
        this.recentRescue = recentRescue;
        this.PEF = PEF;
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

}
