package com.example.smart_air_app.log_rescue_attempt;

import java.util.EnumSet;

public interface LogRescueAttemptPresenter {
    void onSubmitPressed(double dosage, EnumSet<Trigger> triggers, EnumSet<Symptom> symptoms,
                         double peakFlowBefore, double peakFlowAfter, boolean triageIncident);
}
