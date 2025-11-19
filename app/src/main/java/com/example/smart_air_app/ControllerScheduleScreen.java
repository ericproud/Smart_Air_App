package com.example.smart_air_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ControllerScheduleScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controller_schedule_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        //this is the medicine the user needs to take
        String[] medicine_to_use = {"A", "B", "C", "CHANGE LATER"};

        RecyclerView controllerSchedule = findViewById(R.id.controller_schedule);
        controllerSchedule.setLayoutManager(new LinearLayoutManager(this));
        controllerSchedule.setAdapter(new CustomStringAdapter(medicine_to_use));
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