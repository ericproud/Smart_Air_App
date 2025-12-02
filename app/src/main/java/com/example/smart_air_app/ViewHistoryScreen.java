package com.example.smart_air_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_air_app.utils.CheckInFields;
import com.example.smart_air_app.utils.HistoryAdapter;
import com.example.smart_air_app.utils.HistoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ViewHistoryScreen extends AppCompatActivity {
    private RecyclerView historyRecycler;
    private HistoryAdapter adptr;
    private ArrayList<CheckInFields> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_history_screen);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        historyRecycler = findViewById(R.id.historyMatches); //this is the id of the box thing
        historyRecycler.setLayoutManager(new LinearLayoutManager(this));  //it like lieanrLayour but for the card thingy

        dataList = new ArrayList<>();
        adptr = new HistoryAdapter(dataList);
        historyRecycler.setAdapter(adptr);




        Intent intent = getIntent();

        //symptom and trigger are the selected fields/options of symptom and trigger. "None" if none is selected
        String symptom = intent.getStringExtra("Symptom");
        String trigger = intent.getStringExtra("Trigger");
        String childUId = intent.getStringExtra("childUID");
        String childname = intent.getStringExtra("childName");
        //formatted as year-month-day ie March (3) 5th (5) 2025 (2025) would be "2025-3-5"  //// changed this now there is a zero infromt of the dat and month if they are one digit
        //dates should be validated as well from "CheckinHistoryScreen.java"
        String startDate = intent.getStringExtra("Start");
        String endDate = intent.getStringExtra("End");

        if (childUId == null) childUId = FirebaseAuth.getInstance().getUid();

        long startMillis = parseDateToMillis(startDate, true); //this function just changes the Sring of date to a long
        long endMillis = parseDateToMillis(endDate, false);



        //displays inputted fields
        Toast.makeText(this, "symptom: " + symptom + ", trigger: " + trigger
                + ", start: " + startDate + ", end: " + endDate, Toast.LENGTH_SHORT).show();

        Button backButton = findViewById(R.id.backButton);
        //exits if we click the back button
        backButton.setOnClickListener(v -> finish());




        //this array is exactly what's printed/displayed so you will need to format it however you want
        //would recommend you query the database, get all checkins between certain dates
        //then check each one if so add it to the ArrayList, then convert it to a string array
        /*               keeping this here if the groupies need this format for R6 they can tweak this here !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        String[] matches = {"if", "you", "are", "reading", "these", "messages", "someone", "hasn't", "done", "their", "story", "points"
        , "hopefully", "they", "get", "done", "soon", "or", "else", "heads", "will", "roll"};
        */
        //handles the recycler view
        /*   a function will to this part
        RecyclerView controllerSchedule = findViewById(R.id.historyMatches);
        controllerSchedule.setLayoutManager(new LinearLayoutManager(this));
        controllerSchedule.setAdapter(new CustomStringAdapter(matches));

         */
        /// //////////till this point we have the bottoms and read the thing from the previous page
        ReadFBAndFilter(childUId, startMillis, endMillis, symptom,  trigger);  ///this guy read the fire base makes eveything up from there
    }
    /*
    //like the adapter design pattern. this allows use to use a String[] on the recycleview which lists what the user needs to take
    //prints exactly what's in the medicine_to_use
    private static class CustomStringAdapter extends RecyclerView.Adapter<CustomStringAdapter.ViewHolder> {
        private String[] to_print;

        public CustomStringAdapter(String[] to_print) {
            this.to_print = to_print;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView text;

            ViewHolder(View itemView) {
                super(itemView);
                text = itemView.findViewById(android.R.id.text1);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView text = (TextView) LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(text);
        }

        @Override
        public void onBindViewHolder(ViewHolder obj, int pos) {
            obj.text.setText(to_print[pos]);
        }

        @Override
        public int getItemCount() {
            return to_print.length;
        }
    }

     */
    private void ReadFBAndFilter(String childUID, long start, long end, String symptom, String trigger) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("DailyCheckIns").child(childUID); //this goes to the kids history of the kid in firebase
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();  ///make sure the List is actully emplty, don't rlly need to but whatever
                int count = 0;

                for (DataSnapshot data : snapshot.getChildren()) {  /// reads everylog has been saved for the kid one by one
                    CheckInFields item = data.getValue(CheckInFields.class); ///I made it a filed
                    if (item == null || item.date == null) continue; /// just in case


                    if (item.date < start || item.date > end) continue;  ///it is between the two dates


                    if (symptom != null && !symptom.equals("None")) {
                        boolean match = false;
                        if (symptom.contains("Cough") && item.cough) match = true;
                        if (symptom.contains("Night") && item.nightWaking) match = true;
                        if (symptom.contains("Activity") && item.activityLimits) match = true;
                        if (!match) continue; //I jst need to make sure one of the thing user wanted is there
                    }


                    if (trigger != null && !trigger.equals("None")) {
                        if (item.triggers == null || !item.triggers.contains(trigger))
                            continue;
                    }

                    dataList.add(item);
                    count++;  ////I have found atleat one of something
                }
                adptr.notifyDataSetChanged();
                if (count == 0)
                    Toast.makeText(ViewHistoryScreen.this, "No records found.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private long parseDateToMillis(String dateStr, boolean isStart) {        /// //////this just gets the date string we made and make it a long so I can actually compare it
        if (dateStr == null || dateStr.equals("None")) return isStart ? 0L : Long.MAX_VALUE;  ///this mean the user didn't pick a date for this endpoint I either make it 0 or 9999999
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());   ///sdf can bassically pick up formats ( YYYY-MM-DD is a format)
            Date date = sdf.parse(dateStr);
            if (date == null) return 0;
            if (!isStart) return date.getTime() + (24 * 60 * 60 * 1000) - 1;  //the minus one is there b/c we are not putting the times in so after 0;0 after goes the next day
            return date.getTime();
        } catch (ParseException e) { return 0; }  //jst retrun zero so we dont crashout!
    }
}