package com.example.smart_air_app.log_rescue_attempt;

import android.util.Log;
import android.widget.Toast;

import java.util.EnumSet;
import java.util.List;

public class LogRescueAttemptPresenterImpl implements  LogRescueAttemptPresenter{
    private final LogRescueAttemptView view;
    private final RescueAttemptRepository repo;
    public LogRescueAttemptPresenterImpl(LogRescueAttemptView view, RescueAttemptRepository repo) {
        this.view = view;
        this.repo = repo;
    }
    @Override
    public void onSubmitPressed(double dosage, List<String>  triggers, List<String> symptoms,
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
        repo.saveRescueAttempt(info, new RescueAttemptRepository.RepoCallback() {
            @Override
            public void onSuccess() {
                System.out.println("Success");
                view.navigateToSuccessScreen();
            }

            @Override
            public void onError(String e) {
                System.out.println(e);
            }
        });
    }
}
