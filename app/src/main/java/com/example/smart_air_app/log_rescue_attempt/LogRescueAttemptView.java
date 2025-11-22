package com.example.smart_air_app.log_rescue_attempt;

public interface LogRescueAttemptView {
    void showDosageError(String message);
    void showTriggersError(String message);
    void showSymptomsError(String message);
    void showPeakFlowBeforeError(String message);
    void showPeakFlowAfterError(String message);
    void showTriageIncidentError(String message);

    void resetErrors();

    void showLoading();
    void hideLoading();
    void navigateToSuccessScreen();
}
