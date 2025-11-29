package com.example.smart_air_app.alerts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smart_air_app.utils.NotificationUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class TriageListeners extends EntryListeners<ChildEventListener> {
    public TriageListeners(DatabaseReference ref) {
        super(ref);
    }

    @Override
    public void installListener(String childUserId, String childName) {
        ChildEventListener l = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                NotificationUtils.show(context, "WARNING: Triage Escalation",
                        "Your child " + childName + " has escalated a triage.");
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
        listeners.add(l);
        ref.child(childUserId).addChildEventListener(l);
    }

    @Override
    public void removeListeners() {
        listeners.forEach(l -> ref.removeEventListener(l));
    }
}
