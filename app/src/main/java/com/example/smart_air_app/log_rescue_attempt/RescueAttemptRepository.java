package com.example.smart_air_app.log_rescue_attempt;

public interface RescueAttemptRepository {
    interface RepoCallback {
        void onSuccess();
        void onError(String e);
    }
    void saveRescueAttempt(RescueAttempt attempt, RepoCallback callback);
}
