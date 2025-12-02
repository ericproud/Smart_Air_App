package com.example.smart_air_app.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import com.example.smart_air_app.controller_log.ControllerDatabase;

public class AdherenceCalculator {
    /*
    This method is the one you should use. just copy the following:
        String dateTemp = DateValidator.makeDateString(27, 11, 2025);
        //dateTemp is just what i used for the start date, you can use whatever
        //ID is the child's id
        AdherenceCalculator.CalculateAdherence(ID, dateTemp, val -> {
            //val is the adherence schedule using the formula of medicine taken/medicine should have taken
            //assumes that if schedule is 0 then you are 100% adhering to schedule or 1.0
        });

        this is the code itself:

        String dateTemp = DateValidator.makeDateString(30, 11, 2025);
        AdherenceCalculator.CalculateAdherence(ID, dateTemp, val -> {
            //do whatever with val
        });
     */
    public static void CalculateAdherence(String id, String date, Consumer<Double> onResult) {
        String startKey = ControllerDatabase.keyHelper(date, "0:00");
        String today = DateValidator.getTodaysDate();
        String endKey = ControllerDatabase.keyHelper(today, "23:59");

        AdherenceResult result = new AdherenceResult();

        //once completed return result.getResult() which happens to be what we calculate in the method below
        result.setCompletionListener(() -> {
            onResult.accept(result.getResult());
        });

        LocalDate start = dateParseHelp(date);
        LocalDate end = dateParseHelp(today);

        long days = ChronoUnit.DAYS.between(start, end) + 1;

        //call the calculator
        CalculateAdherence2(id, startKey, endKey, days, result);
    }

    //converts date from MMM DD YYYY to yyyymmdd for query usage
    public static LocalDate dateParseHelp(String date) {
        String[] temp = date.trim().split(" ");

        String monthStr = temp[0].toUpperCase();
        int month = 1;
        int day = Integer.parseInt(temp[1]);
        int year = Integer.parseInt(temp[2]);

        if (year < 100) {
            year += 2000;
        }

        if (monthStr.equals("JAN")) {
            month = 1;
        }
        else if (monthStr.equals("FEB")) {
            month = 2;
        }
        else if (monthStr.equals("MAR")) {
            month = 3;
        }
        else if (monthStr.equals("APR")) {
            month = 4;
        }
        else if (monthStr.equals("MAY")) {
            month = 5;
        }
        else if (monthStr.equals("JUN")) {
            month = 6;
        }
        else if (monthStr.equals("JUL")) {
            month = 7;
        }
        else if (monthStr.equals("AUG")) {
            month = 8;
        }
        else if (monthStr.equals("SEP")) {
            month = 9;
        }
        else if (monthStr.equals("OCT")) {
            month = 10;
        }
        else if (monthStr.equals("NOV")) {
            month = 11;
        }
        else if (monthStr.equals("DEC")) {
            month = 12;
        }

        return LocalDate.of(year, month, day);
    }

    //this calculates the adherence the method above is to get around asynch issues
    private static void CalculateAdherence2(String id, String startKey, String endKey, long days, AdherenceResult result) {
        //sum is what we want to take, amountTaken is how much we have taken
        int[] sum = {0};
        int[] amountTaken = {0};

        //loads the schedule (handles sum or how much the user should be taking)
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

            //d_ref query contains all logs within the date range
            Query d_ref = fdb.getReference("ControllerLogs").child(id)
                    .orderByKey().startAt(startKey).endAt(endKey);

            //calculates amountTaken
            d_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot toDo : snapshot.getChildren()) {
                        DataSnapshot amount_ref = toDo.child("amountUsed");

                        //if the amount is logged and non null then add it to what we have taken
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
                        if ((1.0 * amountTaken[0]) / (sum[0] * days) > 1.0) {
                            //if the result is above 100% its not right so set to 100%
                            result.setResult(1.0);
                        }
                        else {
                            //setting to dosage taken over period/dosage supposed to take over time
                            result.setResult((1.0 * amountTaken[0]) / (sum[0] * days));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        });
    }
}