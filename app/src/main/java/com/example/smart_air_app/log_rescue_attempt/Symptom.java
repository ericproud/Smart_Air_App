package com.example.smart_air_app.log_rescue_attempt;

public enum Symptom {
    SHORTNESS_OF_BREATH("Shortness of breath"),
    CHEST_TIGHTNESS("Chest tightness"),
    CHEST_PAIN("Chest pain"),
    WHEEZING("Wheezing"),
    TROUBLE_SLEEPING("Trouble sleeping"),
    COUGHING("Coughing"),
    OTHER("Other");

    private final String label;
    Symptom(String label) {this.label = label; }

    public String getLabel() {
        return label;
    }
}