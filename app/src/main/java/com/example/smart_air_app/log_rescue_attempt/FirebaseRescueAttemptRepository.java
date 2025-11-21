package com.example.smart_air_app.log_rescue_attempt;

import com.example.smart_air_app.session.SessionManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseRescueAttemptRepository implements RescueAttemptRepository {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    @Override
    public void saveRescueAttempt(RescueAttempt attempt, RepoCallback callback) {
        String userId = SessionManager.getInstance().getUserId();

        DatabaseReference ref = db.getReference().child("RescueAttempts").child(userId);

        ref.push().setValue(attempt)
                .addOnFailureListener(e -> callback.onError(e.getMessage()))
                .addOnSuccessListener(v -> callback.onSuccess());
    }
}
