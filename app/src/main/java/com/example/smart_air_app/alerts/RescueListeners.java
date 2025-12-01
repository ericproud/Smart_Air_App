package com.example.smart_air_app.alerts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smart_air_app.utils.NotificationUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class RescueListeners extends EntryListeners<ChildEventListener>{
    public RescueListeners(DatabaseReference ref) {
        super(ref);
    }

    @Override
    public void installListener(String childUserId, String childName) {
        ChildEventListener l = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                snapshot.getRef() // refers to newly added rescue attempt
                        .getParent() // refers to userid
                        .orderByChild("timestamp")
                        .limitToLast(3)
                        .get()
                        .addOnSuccessListener(timestampSnapshot -> {
                            List<Long> timestamps = new ArrayList<>();
                            timestampSnapshot.getChildren().forEach(child -> {
                                Long ts = child.child("timestamp").getValue(Long.class);
                                if (ts != null) timestamps.add(ts);
                            });

                            if (timestamps.size() == 3) { // if less than 3 entries do not alert
                                long now = System.currentTimeMillis();
                                long oldest = timestamps.get(0);
                                long newest = timestamps.get(2);

                                if (newest - oldest <= 1000 * 60 * 60 * 3) { // 3hr in ms
                                    NotificationUtils.show(context, "Rescue Alert",
                                            childName + ": 3 rescue attempts in 3 hours!");
                                }
                            }
                        });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        listeners.add(new EntryListener<>(childUserId, l));
        ref.child(childUserId).addChildEventListener(l);
    }

    @Override
    public void removeListeners() {
        listeners.forEach(l -> ref.child(l.childUserId).removeEventListener(l.listener));
    }
}
