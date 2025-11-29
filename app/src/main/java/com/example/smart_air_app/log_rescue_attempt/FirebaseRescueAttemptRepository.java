package com.example.smart_air_app.log_rescue_attempt;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseRescueAttemptRepository implements RescueAttemptRepository {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private String uid;
    @Override
    public void saveRescueAttempt(RescueAttempt attempt, RepoCallback callback) {
        String userId = uid;

        DatabaseReference ref = db.getReference().child("RescueAttempts").child(userId);

        ref.push().setValue(attempt)
                .addOnFailureListener(e -> callback.onError(e.getMessage()))
                .addOnSuccessListener(v -> callback.onSuccess());
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }
}
