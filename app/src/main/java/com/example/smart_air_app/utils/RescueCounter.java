package com.example.smart_air_app.utils;

import androidx.annotation.NonNull;

import com.example.smart_air_app.log_rescue_attempt.RescueAttempt;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RescueCounter {
    public interface RescueCallback {
        void onCountReceived(int count);
    }
    public static void calcAttInMonth( String childUID, RescueCallback myCallback){

        long minMillies = System.currentTimeMillis() -  ((long) 30 * 24 * 60 * 60 * 1000);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("RescueAttempts").child(childUID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    RescueAttempt itm = data.getValue(RescueAttempt.class);

                    if(  itm != null && itm.getTimestamp() >= minMillies) counter++;
                }
                myCallback.onCountReceived(counter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
