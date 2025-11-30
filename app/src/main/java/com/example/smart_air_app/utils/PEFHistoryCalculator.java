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

import controller_log.PEFZonesDatabase;

public class PEFHistoryCalculator {
    private static FirebaseDatabase fdb = FirebaseDatabase.getInstance();

    /*
        String datetemp = DateValidator.makeDateString(5, 10, 2025);
        PEFHistoryCalculator.CalculateAdherence(ID, datetemp, val -> {
            val is the 2d int array where 1st index is month relative to given month, 2nd index follows [green, yellow, red]
        });
    */
    public static void CalculateAdherence(String id, String date, Consumer<int[][]> onResult) {
        PEFHistory result = new PEFHistory();

        //once completed return result.getResult() which happens to be what we calculate in the method below
        result.setCompletionListener(() -> {
            onResult.accept(result.getResult());
        });

        //call the calculator
        CalculateHistory2(id, date, result);
    }

    //this function parses the date into a LocalDate
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

    //from the string yyyymmdd it returns mm as an integer or the month
    public static int monthGetter(String key) {
        return Integer.parseInt(key.substring(4,6));
    }

    //from the string yyyymmdd it returns yyyy as an integer or the year
    public static int yearGetter(String key) {
        return Integer.parseInt(key.substring(0, 4));
    }

    //actual logic of calculating the pef zone history
    private static void CalculateHistory2(String id, String date, PEFHistory result) {
        String today = DateValidator.getTodaysDate();

        //keys for the database query
        String startKey = PEFZonesDatabase.keyHelper(date);
        String endKey = PEFZonesDatabase.keyHelper(today);

        //Local date versions of the times
        LocalDate startTime = dateParseHelp(date);
        LocalDate endTime = dateParseHelp(today);

        //number of months between the start date and today's date
        long months = ChronoUnit.MONTHS.between(startTime.withDayOfMonth(1), endTime.withDayOfMonth(1)) + 1;

        //what val is when calling the calculate history method
        int[][] ans = new int[(int) months][3];

        //temporary values to track where to write in the array
        int[] curr_month = {monthGetter(startKey)};
        int[] temp_month = {curr_month[0]};
        int[] curr_year = {yearGetter(startKey)};
        int[] temp_year = {curr_year[0]};

        //index to which we are writing in ans[][]
        int[] counter = {0};

        //toProcess is all keys within the (inclusive) range of start date and the current date
        Query toProcess = fdb.getReference("PEFHistory").child(id)
                .orderByKey().startAt(startKey).endAt(endKey);

        toProcess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data_to_process : snapshot.getChildren()) {
                    //the year and month of the current snapshot we are looking at
                    temp_month[0] = monthGetter(data_to_process.getKey());
                    temp_year[0] = yearGetter(data_to_process.getKey());

                    //logic to handle counter

                    if (temp_year[0] > curr_year[0] && curr_month[0] <= temp_month[0]) {
                        //if the year we read is > then the last year we read and the month we read > last month read then
                        //we do 12 * difference of years and add to counter to skip over the months that aren't logged
                        counter[0] += 12 * temp_year[0] - curr_year[0];
                        curr_year[0] = temp_year[0];
                    }
                    else if (temp_year[0] > curr_year[0] && curr_month[0] > temp_month[0]) {
                        //if the year > last year read but month < last month read then we skipped a partial year and more years
                        //so if we skip 9 months, the difference of "whole years" is techniquely 0 but if we skip 21 months
                        //then we skipped 1 whole year hence the -1 at the end, the difference in months is handled below
                        counter[0] += 12 * (temp_year[0] - curr_year[0] - 1);
                        curr_year[0] = temp_year[0] - 1;
                    }

                    //here we know the year is within 1 because the above if else if statement corrects that

                    //if the month we read > last month read we need to skip some months
                    if (temp_month[0] > curr_month[0]) {
                        //skipping the number of months as the difference (if we go from month 10 to month 12 we skip 2 months)
                        counter[0] += temp_month[0] - curr_month[0];
                    }
                    else if (temp_month[0] < curr_month[0]){
                        //here lets say we started at month 5 and read in month 2, then we skipped 9 months or 12 - 5 + 2 = 9
                        counter[0] += 12 - curr_month[0] + temp_month[0];
                        curr_year[0] = temp_year[0];
                    }

                    //setting the last month read as the month we just read
                    curr_month[0] = temp_month[0];

                    //getting the actual associated value
                    String temp = data_to_process.getValue(String.class);

                    //here we log if it's a green yellow or red zone day, anything else we ignore
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


                result.setResult(ans);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}