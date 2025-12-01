package com.example.smart_air_app.controller_log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PEFZonesDatabase {
    private static FirebaseDatabase fdb = FirebaseDatabase.getInstance();

    public interface PBCallBack {
        void onResult(int pb, int pef, String date);
    }

    //loads the zones info from the database
    public static void loadPEFZones(String id, PBCallBack callBack) {
        DatabaseReference ref = fdb.getReference("Users").child(id).child("Zones");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //loads raw data or null from database
                Integer pb = snapshot.child("pb").getValue(Integer.class);
                Integer pef = snapshot.child("pef").getValue(Integer.class);
                String date = snapshot.child("date").getValue(String.class);

                //dummy values to ensure we don't pass through a null pointer
                int pbToSend = -1;
                int pefToSend = -1;
                String dateToSend = "";

                //if the pb has been logged set it
                if (pb != null) {
                    pbToSend = pb;
                }

                //if the pef has been logged set it
                if (pef != null) {
                    pefToSend = pef;
                }

                //if the date has been logged set it
                if (date != null) {
                    dateToSend = date;
                }

                callBack.onResult(pbToSend, pefToSend, dateToSend);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static String keyHelper(String date) {
        String year = "";
        String month = "";
        String day = "";

        String[] temp = date.split(" ");

        year = temp[2];
        day = temp[1];

        if (temp[0].equals("JAN")) {
            month = "01";
        }
        else if (temp[0].equals("FEB")) {
            month = "02";
        }
        else if (temp[0].equals("MAR")) {
            month = "03";
        }
        else if (temp[0].equals("APR")) {
            month = "04";
        }
        else if (temp[0].equals("MAY")) {
            month = "05";
        }
        else if (temp[0].equals("JUN")) {
            month = "06";
        }
        else if (temp[0].equals("JUL")) {
            month = "07";
        }
        else if (temp[0].equals("AUG")) {
            month = "08";
        }
        else if (temp[0].equals("SEP")) {
            month = "09";
        }
        else if (temp[0].equals("OCT")) {
            month = "10";
        }
        else if (temp[0].equals("NOV")) {
            month = "11";
        }
        else if (temp[0].equals("DEC")) {
            month = "12";
        }

        return year + month + day;
    }

    //save to database
    public static void savePEFZones(String id, PEFZones info) {
        DatabaseReference ref = fdb.getReference("Users").child(id).child("Zones");

        ref.child("pb").setValue(info.getPB());
        ref.child("pef").setValue(info.getHighest_pef());
        ref.child("date").setValue(info.getDate());

        String key = keyHelper(info.getDate());

        DatabaseReference pef_history_ref = fdb.getReference("PEFHistory").child(id).child(key);

        pef_history_ref.setValue(info.calculateZone());
    }
}
