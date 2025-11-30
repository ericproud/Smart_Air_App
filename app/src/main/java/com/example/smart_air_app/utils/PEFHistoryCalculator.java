package com.example.smart_air_app.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import controller_log.ControllerDatabase;
import controller_log.PEFZones;
import controller_log.PEFZonesDatabase;

public class PEFHistoryCalculator {
    private static FirebaseDatabase fdb = FirebaseDatabase.getInstance();

    public static void CalculateAdherence(String id, String date, Consumer<int[][]> onResult) {
        PEFHistory result = new PEFHistory();

        //once completed return result.getResult() which happens to be what we calculate in the method below
        result.setCompletionListener(() -> {
            onResult.accept(result.getResult());
        });

        //call the calculator
        CalculateHistory2(id, date, result);
    }

    public static LocalDate dateParseHelp(String date) {
        String[] temp = date.trim().split(" ");

        int month = 0;
        int day = Integer.parseInt(temp[1]);
        int year = Integer.parseInt(temp[2]);

        if (temp[0].equals("JAN")) {
            month = 1;
        }
        else if (temp[0].equals("FEB")) {
            month = 2;
        }
        else if (temp[0].equals("MAR")) {
            month = 3;
        }
        else if (temp[0].equals("APR")) {
            month = 4;
        }
        else if (temp[0].equals("MAY")) {
            month = 5;
        }
        else if (temp[0].equals("JUN")) {
            month = 6;
        }
        else if (temp[0].equals("JUL")) {
            month = 7;
        }
        else if (temp[0].equals("AUG")) {
            month = 8;
        }
        else if (temp[0].equals("SEP")) {
            month = 9;
        }
        else if (temp[0].equals("OCT")) {
            month = 10;
        }
        else if (temp[0].equals("NOV")) {
            month = 11;
        }
        else if (temp[0].equals("DEC")) {
            month = 12;
        }

        return LocalDate.of(year, month, day);
    }

    public static int monthGetter(String key) {
        return Integer.parseInt(key.substring(4,6));
    }

    public static int yearGetter(String key) {
        return Integer.parseInt(key.substring(0, 4));
    }

    private static void CalculateHistory2(String id, String date, PEFHistory result) {
        String today = DateValidator.getTodaysDate();

        String startKey = PEFZonesDatabase.keyHelper(date);
        String endKey = PEFZonesDatabase.keyHelper(today);

        LocalDate startTime = dateParseHelp(date);
        LocalDate endTime = dateParseHelp(today);

        long months = ChronoUnit.MONTHS.between(startTime.withDayOfMonth(1), endTime.withDayOfMonth(1)) + 1;

        int[][] ans = new int[(int) months][3];

        int[] curr_month = {monthGetter(startKey)};
        int[] temp_month = {curr_month[0]};
        int[] curr_year = {yearGetter(startKey)};
        int[] temp_year = {curr_year[0]};

        Log.d("PEFHistory", date);
        Log.d("PEFHistory", "curr month = " + curr_month[0]);

        int[] counter = {0};

        //toProcess is all keys within the (inclusive) range of start date and the current date
        Query toProcess = fdb.getReference("PEFHistory").child(id)
                .orderByKey().startAt(startKey).endAt(endKey);

        toProcess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data_to_process : snapshot.getChildren()) {
                    temp_month[0] = monthGetter(data_to_process.getKey());
                    temp_year[0] = yearGetter(data_to_process.getKey());

                    Log.d("PEFHistory", "" + temp_month[0]);

                    if (temp_year[0] > curr_year[0] && curr_month[0] == temp_month[0]) {
                        counter[0] += 12;
                        curr_year[0] = temp_year[0];
                    }

                    if (temp_month[0] > curr_month[0]) {
                        counter[0] += temp_month[0] - curr_month[0];
                        Log.d("PEFHistory", "counter a " + counter[0]);
                    }
                    else if (temp_month[0] < curr_month[0]){
                        counter[0] += 12 - curr_month[0] + temp_month[0];
                        Log.d("PEFHistory", "counter b " + counter[0]);
                    }

                    Log.d("PEFHistory", "counter " + counter[0]);

                    curr_month[0] = temp_month[0];

                    String temp = data_to_process.getValue(String.class);

                    if (temp != null) {
                        if (temp.equals("Green")) {
                            ans[counter[0]][0]++;
                        }
                        else if (temp.equals("Yellow")) {
                            ans[counter[0]][1]++;
                        }
                        else if (temp.equals("Red")) {
                            ans[counter[0]][2]++;
                        }
                    }
                }

                for (int i = 0; i < ans.length; i++) {
                    for (int j = 0; j < ans[i].length; j++) {
                        Log.d("PEFHistory", "ans[" + i + "][" + j + "] = " + ans[i][j]);
                    }
                }

                result.setResult(ans);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}