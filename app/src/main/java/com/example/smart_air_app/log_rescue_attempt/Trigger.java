package com.example.smart_air_app.log_rescue_attempt;

public enum Trigger {
    EXERCISE("Exercise"),
    POLLEN("Pollen"),
    COLD_AIR("Cold air"),
    DUST("Dust"),
    SMOKE("Smoke"),
    STRESS("Stress"),
    OTHER("Other");

    private final String label;
    Trigger(String label) {this.label = label; }

    public String getLabel() {
        return label;
    }
}
