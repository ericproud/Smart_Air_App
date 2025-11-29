package com.example.smart_air_app.log_rescue_attempt;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRescueAttemptRepository implements RescueAttemptRepository {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private String uid;
    @Override
    public void saveRescueAttempt(RescueAttempt attempt, SaveCallback callback) {
        String userId = uid;

        DatabaseReference ref = db.getReference().child("RescueAttempts").child(userId).push();

        ref.setValue(attempt)
                .addOnFailureListener(e -> callback.onError(e.getMessage()))
                .addOnSuccessListener(v -> {
                    ref.child("timestamp").setValue(ServerValue.TIMESTAMP)
                            .addOnSuccessListener(v2 -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                });
    }

    @Override
    public void fetchRescueAttempt(FetchCallback callback) {
        String userId = uid;

        DatabaseReference ref = db.getReference().child("RescueAttempts").child(userId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RescueAttempt> attempts = new ArrayList<>();

                if (!snapshot.exists()) { // missing RescueAttempts or userID key
                    callback.onSuccess(attempts);
                    return;
                }


                for (DataSnapshot child: snapshot.getChildren()) {
                    RescueAttempt attempt = child.getValue(RescueAttempt.class);
                    if (attempt != null) {
                        attempts.add(attempt);
                    }
                }

                callback.onSuccess(attempts);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }


    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }
}
