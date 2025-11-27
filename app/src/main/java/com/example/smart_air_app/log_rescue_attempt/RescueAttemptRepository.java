package com.example.smart_air_app.log_rescue_attempt;

import java.util.List;

public interface RescueAttemptRepository {
    interface SaveCallback {
        void onSuccess();
        void onError(String e);
    }

    interface FetchCallback {
        void onSuccess(List<RescueAttempt> attempts);
        void onError(String e);
    }
    void saveRescueAttempt(RescueAttempt attempt, SaveCallback callback);

    void fetchRescueAttempt(FetchCallback callback);
    void setUid(String uid);
}
