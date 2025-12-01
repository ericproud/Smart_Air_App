package com.example.smart_air_app.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.example.smart_air_app.ParentHomeScreen;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AlertSender {
    static boolean flag;
    static String  parentUID;
    static int type;
    static String name;
    public static void addInhalerUse(String childUID){
        long currentTime = System.currentTimeMillis();   //what time is it?
        DatabaseReference inhalerUse = FirebaseDatabase.getInstance()
                .getReference("Alerting").child("Inhaler").child(childUID);
                inhalerUse.push().setValue(currentTime);
    }
    public static void inhalerUseCheck( String childUId){  //return true if we need to contact the parent
        long threeHoursAgo = System.currentTimeMillis() - (3 * 60 * 60 * 1000);
        DatabaseReference inhalerUse = FirebaseDatabase.getInstance().getReference("Alerting").
                child("Inhaler").child(childUId);
        com.google.firebase.database.Query query = inhalerUse.orderByValue().startAt(threeHoursAgo);
        flag = false; /// assume the kid is fine
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long count = snapshot.getChildrenCount();


                if (count >= 3) {
                    createAlerts(3, childUId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public static void createAlerts(int option, String childUId){  //don't feel like to make a enum type ;)
        //1 is start of triage 2 is emergency triage and 3 is inhaller
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(childUId);
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                parentUID = snapshot.child(parentUID).getValue(String.class);
                name = snapshot.child("firstName").getValue(String.class);

                DatabaseReference notifyRef = FirebaseDatabase.getInstance()
                        .getReference("Alerting")
                        .child("Notifications")
                        .child(parentUID);
                HashMap<String, Object> alertData = new HashMap<>();
                alertData.put("type", option);
                alertData.put("name", name);


                notifyRef.push().setValue(alertData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }
    public static void showAlert(String parentUID, Context context) {
        DatabaseReference alert = FirebaseDatabase.getInstance().getReference("Alerting").
                child("Notifications").child(parentUID);
        alert.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {

                     name = snapshot.child("name").getValue(String.class);
                     type = snapshot.child("type").getValue(Integer.class);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {    }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {  }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }

        });
        String msg;
        if (type == 1) msg = "your child" + name + "has started a Triage Session";
        else if ( type==2) { msg  =  "your child" + name + "has started a emergency Triage Session";
        } else msg  =  "your child" + name + "has used his/her inhaller more than 3 times in the past 3 hours";

        AlertDialog.Builder builder  = new AlertDialog.Builder(context).setTitle("Emergency")
                .setMessage(msg).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();

    }



}
