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

import java.util.Calendar;

public class ViewHistoryScreen extends AppCompatActivity {
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

        Intent intent = getIntent();

        //symptom and trigger are the selected fields/options of symptom and trigger. "None" if none is selected
        String symptom = intent.getStringExtra("Symptom");
        String trigger = intent.getStringExtra("Trigger");

        //formatted as year-month-day ie March (3) 5th (5) 2025 (2025) would be "2025-3-5"
        //dates should be validated as well from "CheckinHistoryScreen.java"
        String startDate = intent.getStringExtra("Start");
        String endDate = intent.getStringExtra("End");

        //displays inputted fields
        Toast.makeText(this, "symptom: " + symptom + ", trigger: " + trigger
                + ", start: " + startDate + ", end: " + endDate, Toast.LENGTH_SHORT).show();

        Button backButton = findViewById(R.id.backButton);
        //exits if we click the back button
        backButton.setOnClickListener(v -> finish());

        //this array is exactly what's printed/displayed so you will need to format it however you want
        //would recommend you query the database, get all checkins between certain dates
        //then check each one if so add it to the ArrayList, then convert it to a string array
        String[] matches = {"if", "you", "are", "reading", "these", "messages", "someone", "hasn't", "done", "their", "story", "points"
        , "hopefully", "they", "get", "done", "soon", "or", "else", "heads", "will", "roll"};

        //handles the recycler view
        RecyclerView controllerSchedule = findViewById(R.id.historyMatches);
        controllerSchedule.setLayoutManager(new LinearLayoutManager(this));
        controllerSchedule.setAdapter(new CustomStringAdapter(matches));
    }

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
}