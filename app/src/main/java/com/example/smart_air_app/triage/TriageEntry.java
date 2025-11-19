package com.example.smart_air_app.triage;

public class TriageEntry {

    private int triageID;
    private boolean[] redFlags = new boolean[3];
    private boolean recentRescue;
    private double PEF;

    public TriageEntry() {
        triageID = -1;
        redFlags[0] = false;
        redFlags[1] = false;
        redFlags[2] = false;
        recentRescue = false;
        PEF = -1;
    }

}
