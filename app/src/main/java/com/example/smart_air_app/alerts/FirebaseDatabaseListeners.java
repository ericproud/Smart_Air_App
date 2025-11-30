package com.example.smart_air_app.alerts;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseDatabaseListeners {

    interface ChildrenCallback {
        void onLoaded(HashMap<String, String> children); // maps uid to name
    }

    private static FirebaseDatabaseListeners instance;
    private static final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private String parentId;
    private final List<EntryListeners<?>> entryListeners = List.of(
            new TriageListeners(db.getReference("TriageEntries")),
            new InventoryListeners(db.getReference("Inventory")),
            new RescueListeners(db.getReference("RescueAttempts")),
            new ControllerListeners(db.getReference("ControllerLogs"))

    );

    private FirebaseDatabaseListeners() {}

    public static FirebaseDatabaseListeners getInstance() {
        if (instance == null) {
            instance = new FirebaseDatabaseListeners();
        }
        return instance;
    }

    public void setContext(Context context) {
        entryListeners.forEach(l -> l.setContext(context));
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    private void loadChildren(ChildrenCallback callback) {

        db.getReference("Users")
                .child(parentId)
                .child("children")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        HashMap<String, String> result = new HashMap<>();

                        if (!snapshot.exists()) {
                            callback.onLoaded(result);
                            return;
                        }

                        // Count how many child IDs we need to load
                        final int totalChildren = (int) snapshot.getChildrenCount();
                        final AtomicInteger loadedCount = new AtomicInteger(0);

                        for (DataSnapshot child : snapshot.getChildren()) {

                            String childId = child.getKey();

                            db.getReference("Users")
                                    .child(childId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot childSnap) {

                                            String first = childSnap.child("firstName").getValue(String.class);
                                            String last = childSnap.child("lastName").getValue(String.class);

                                            String fullName = (first == null ? "" : first) + " " +
                                                    (last == null ? "" : last);

                                            result.put(childId, fullName.trim());

                                            // When all children are loaded, call callback
                                            if (loadedCount.incrementAndGet() == totalChildren) {
                                                callback.onLoaded(result);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {}
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    public void attachListeners() {
        loadChildren(map -> {
            map.forEach(this::attachListeners);
            System.out.println(map);
        });
    }

    /**
     * Attaches all 5 types of listeners to a single child
     * @param childId id of the child
     */
    public void attachListeners(String childId, String fullName) {
        entryListeners.forEach(entryListeners -> {
            entryListeners.installListener(childId, fullName);
        });
    }

    public void removeListeners() {
        entryListeners.forEach(EntryListeners::removeListeners);
    }
}
