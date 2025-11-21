package com.example.smart_air_app.log_rescue_attempt;

import java.util.EnumSet;
import java.util.List;

public interface LogRescueAttemptPresenter {
    void onSubmitPressed(double dosage, List<String> triggers, List<String> symptoms,
                         double peakFlowBefore, double peakFlowAfter, boolean triageIncident);
}
