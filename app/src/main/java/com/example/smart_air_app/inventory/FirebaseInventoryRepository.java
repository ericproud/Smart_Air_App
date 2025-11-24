package com.example.smart_air_app.inventory;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseInventoryRepository implements InventoryRepository{

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    @Override
    public void saveInventory(List<Medicine> inventory, int index, SaveCallback callback) {
        String childID = FirebaseAuth.getInstance().getUid();
//        String childID = "UYxYYabCYjaRUvbVEEYQ0gxKNQr2";

        DatabaseReference ref = db.getReference().child("Inventory").child(childID).child(String.valueOf(index));

        ref.setValue(inventory.get(index))
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void fetchInventory(FetchCallback callback) {
        String childID = FirebaseAuth.getInstance().getUid();
//        String childID = "UYxYYabCYjaRUvbVEEYQ0gxKNQr2";

        DatabaseReference ref = db.getReference("Inventory").child(childID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Medicine> result = new ArrayList<>();

                if (snapshot.child("0").exists()) {
                    Medicine m1 = snapshot.child("0").getValue(Medicine.class);
                    result.add(m1);
                } else {
                    result.add(null);
                }

                if (snapshot.child("1").exists()) {
                    Medicine m2 = snapshot.child("1").getValue(Medicine.class);
                    result.add(m2);
                } else {
                    result.add(null);
                }

                callback.onSuccess(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }
}
