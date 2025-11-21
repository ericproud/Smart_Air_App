package com.example.smart_air_app.log_rescue_attempt;

import android.util.Log;

import java.util.EnumSet;

public class LogRescueAttemptPresenterImpl implements  LogRescueAttemptPresenter{
    private final LogRescueAttemptView view;
    public LogRescueAttemptPresenterImpl(LogRescueAttemptView view) {
        this.view = view;
    }
    @Override
    public void onSubmitPressed(double dosage, EnumSet<Trigger> triggers, EnumSet<Symptom> symptoms,
                                double peakFlowBefore, double peakFlowAfter, boolean triageIncident) {

        view.resetErrors();
        if (dosage < 0) {
            view.showDosageError("Invalid dosage");
            return;
        }

        if (triggers.isEmpty()) {
            view.showTriggersError("No trigger selected");
            return;
        }

        if (symptoms.isEmpty()) {
            view.showSymptomsError("No symptom selected");
            return;
        }

        if (peakFlowBefore < 0) {
            view.showPeakFlowBeforeError("Invalid peak flow");
            return;
        }

        if (peakFlowAfter < 0) {
            view.showPeakFlowAfterError("Invalid peak flow");
            return;
        }

        RescueAttempt info = new RescueAttempt();
        info.setDosage(dosage);
        info.setSymptoms(symptoms);
        info.setTriggers(triggers);
        info.setPeakFlowBefore(peakFlowBefore);
        info.setPeakFlowAfter(peakFlowAfter);
        info.setTriageIncident(triageIncident);

        Log.i("info", info.toString());
    }
}
