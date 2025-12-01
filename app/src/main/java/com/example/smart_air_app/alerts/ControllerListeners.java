package com.example.smart_air_app.alerts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smart_air_app.utils.NotificationUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class ControllerListeners extends EntryListeners<ChildEventListener> {
    public ControllerListeners(DatabaseReference ref) {
        super(ref);
    }

    @Override
    public void installListener(String childUserId, String childName) {
        ChildEventListener l = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String rating = snapshot.child("breathRating").getValue(String.class);
                if (rating != null && rating.equalsIgnoreCase("worse")) {
                    NotificationUtils.show(context, "Controller Worse After Dose",
                            String.format("Your child %s breathing has gotten worse after using a controller dose", childName));
                }
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
