package com.example.smart_air_app.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.function.Consumer;

import controller_log.ControllerDatabase;

public class AdherenceCalculator {
    /*
    This method is the one you should use. just copy the following:
        String dateTemp = DateValidator.makeDateString(27, 11, 2025);
        //dateTemp is just what i used for the date itself, you can use whatever
        //ID is the child's id
        AdherenceCalculator.CalculateAdherence(ID, dateTemp, val -> {
            //val is the adherence schedule using the formula of medicine taken/medicine should have taken
            //assumes that if schedule is 0 then you are 100% adhering to schedule or 1.0
        });
     */
    public static void CalculateAdherence(String id, String date, Consumer<Double> onResult) {
        AdherenceResult result = new AdherenceResult();

        String currDate = DateValidator.getTodaysDate();

        //once completed return result.getResult() which happens to be what we calculate in the method below
        result.setCompletionListener(() -> {
            onResult.accept(result.getResult());
        });

        //call the calculator
        CalculateAdherence2(id, date, result);
    }

    //this calculates the adherence the method above is to get around asynch issues
    private static void CalculateAdherence2(String id, String date, AdherenceResult result) {
        //sum is what we want to take, amountTaken is how much we have taken
        int[] sum = {0};
        int[] amountTaken = {0};

        //loads the schedule (handles sum)
        ControllerDatabase.ControllerScheduleLoader(id, loaded_schedule -> {
            int tempAmount = 0;
            for (String toDo : loaded_schedule) {
                String[] temp = toDo.split(" ");
                try {
                    tempAmount = Integer.parseInt(temp[3]);
                    if (tempAmount > 0) {
                        sum[0] += tempAmount;
                    }
                } catch (NumberFormatException e) {
                }
            }

            //handles amountTaken or goes through controller usage after the schedule is loaded to avoid potential asynch issues
            FirebaseDatabase fdb = FirebaseDatabase.getInstance();

            DatabaseReference d_ref = fdb.getReference("ControllerLogs").child(id).child(date);

            //calculates amountTaken
            d_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot time_snapshot : snapshot.getChildren()) {
                        DataSnapshot amount_ref = time_snapshot.child("amountUsed");

                        if (amount_ref.exists()) {
                            Integer dose = amount_ref.getValue(Integer.class);
                            if (dose != null && dose > 0) {
                                amountTaken[0] += dose;
                            }
                        }
                    }

                    //sets the results from what we calculates assumes if no schedules medicine 100% adherence
                    if (sum[0] == 0) {
                        result.setResult(1.0);
                    }
                    else {
                        result.setResult(1.0 * amountTaken[0] / sum[0]);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        });
    }
}