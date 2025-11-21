package com.example.smart_air_app.log_rescue_attempt;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseRescueAttemptRepository implements RescueAttemptRepository {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    @Override
    public void saveRescueAttempt(RescueAttempt attempt, RepoCallback callback) {
    }
}
