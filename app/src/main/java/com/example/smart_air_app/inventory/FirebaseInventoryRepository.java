package com.example.smart_air_app.inventory;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseInventoryRepository implements InventoryRepository {

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    String uid; // as child is logged in through parent we cannot use FirebaseAuth


    @Override
    public void saveInventory(List<Medicine> inventory, int index, SaveCallback callback) {
        if (uid == null) return;
        DatabaseReference ref = db.getReference().child("Inventory").child(uid).child(String.valueOf(index));

        ref.setValue(inventory.get(index))
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void fetchInventory(FetchCallback callback) {
        if (uid == null) return;
        DatabaseReference ref = db.getReference("Inventory").child(uid);

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

    @Override
    public void initInventory() {
        List<Medicine> medicine = new ArrayList<>();

        Medicine quickRelief = new Medicine();
        quickRelief.setType("Quick-Relief");
        quickRelief.setRemaining(200);
        quickRelief.setTotal(200);
        quickRelief.setLastPurchased("");
        quickRelief.setExpires("");
        quickRelief.setReportedBy("parent");

        Medicine controller = new Medicine();
        controller.setType("Controller");
        controller.setRemaining(200);
        controller.setTotal(200);
        controller.setLastPurchased("");
        controller.setExpires("");
        controller.setReportedBy("parent");

        medicine.add(quickRelief);
        medicine.add(controller);

        saveInventory(medicine, 0, new SaveCallback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onError(String e) {}
        });
        saveInventory(medicine, 1, new SaveCallback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onError(String e) {}
        });
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }
}
