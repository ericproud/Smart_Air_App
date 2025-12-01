package com.example.smart_air_app.controller_log;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_air_app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


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

        int[] selectedSchedule = {-1};

        int[] doseAmount = {-1};
        String[] time = {""};

        //this is the medicine the user needs to take
        List<String> schedule = new ArrayList<>();
        String id = getIntent().getStringExtra("childUID");

        //this recycle view displays the schedule
        RecyclerView controllerSchedule = findViewById(R.id.controller_schedule);
        CustomStringAdapter listAdapter = new CustomStringAdapter(schedule);
        controllerSchedule.setLayoutManager(new LinearLayoutManager(this));
        controllerSchedule.setAdapter(listAdapter);

        //this spinner is the event you wish to delete
        Spinner selectedTask = findViewById(R.id.scheduledUsage);
        ArrayAdapter<String> selectedAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, schedule
        );
        selectedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectedTask.setAdapter(selectedAdapter);

        helperSchedule(id, schedule, listAdapter, selectedAdapter);

        //handles what task/event was selected
        selectedTask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSchedule[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSchedule[0] = -1;
            }
        });

        //exits the page but saves the schedule one last time for safety
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            ControllerDatabase.ControllerScheduleSaver(id, schedule);
            finish();
        });

        //deletes the event from the arraylist and saves it to the database
        Button deleteSelected = findViewById(R.id.deleteButton);
        deleteSelected.setOnClickListener(v->{
            if (selectedSchedule[0] >= 0 && selectedSchedule[0] < schedule.size()) {
                schedule.remove(selectedSchedule[0]);
                selectedAdapter.notifyDataSetChanged();;
                listAdapter.notifyDataSetChanged();
                ControllerDatabase.ControllerScheduleSaver(id, schedule);
            }
        });

        //handles getting does input for creating a new event for schedule
        TextView doseInput = findViewById(R.id.doseAmountText);

        doseInput.setOnFocusChangeListener((v, focus) -> {
            if (!focus) {
                String input = doseInput.getText().toString().trim();
                int val = -1;
                try {
                    val = Integer.parseInt(input);
                    if (val <= 0) {
                        val = -1;
                    }
                }
                catch (NumberFormatException e) {
                    val = -1;
                }

                if (val != -1) {
                    doseAmount[0] = val;
                }
            }
        });

        //handles time scheduled for controller use
        Button timeButton = findViewById(R.id.timeButton);
        TextView timeText = findViewById(R.id.timeText);

        timeButton.setOnClickListener(v->{
                Calendar c = Calendar.getInstance();
                //set default time or start time to the current time
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(this,
                        (view, selectedHour, selectedMinute) -> {
                            //these lines are what we do with the selected time, the rest of this code is the constructor
                            if (selectedMinute < 10) {
                                time[0] = selectedHour + ":0" + selectedMinute;
                            }
                            else {
                                time[0] = selectedHour + ":" + selectedMinute;
                            }
                            timeText.setText("Selected Time: " + time[0]);
                        }, hour, minute, true
                );

                dialog.show();
        });

        //adds event to schedule
        Button addToSchedule = findViewById(R.id.addButton);

        addToSchedule.setOnClickListener(v->{
            if (!time[0].equals("") && doseAmount[0] != -1) {
                String entry = "Time: " + time[0] + " Amount: " + doseAmount[0];
                if (schedule.contains(entry)) {
                    //can change this if you want duplicate entries ie 4 puffs and 2 puffs at the same time
                    //current assumption is no duplicate events
                }
                else {
                    //adds it to the schedule, updates the screen and saves to the database
                    schedule.add(entry);
                    selectedAdapter.notifyDataSetChanged();;
                    listAdapter.notifyDataSetChanged();
                    ControllerDatabase.ControllerScheduleSaver(id, schedule);
                }
            }
        });
    }

    //prints exactly what's in the medicine_to_use
    private static class CustomStringAdapter extends RecyclerView.Adapter<CustomStringAdapter.ViewHolder> {
        //to_print is what we want to print hence the name
        private List<String> to_print;

        public CustomStringAdapter(List<String> to_print) {
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
            //create the textviews that will display whatever we want to display
            TextView text = (TextView) LayoutInflater.from(parent.getContext()).inflate(
                    android.R.layout.simple_list_item_1,
                    parent,
                    false);

            return new ViewHolder(text);
        }

        //setting the text to the corresponding to_print item (position pos)
        @Override
        public void onBindViewHolder(ViewHolder obj, int pos) {
            obj.text.setText(to_print.get(pos));
        }

        //how many items we want to print
        @Override
        public int getItemCount() {
            return to_print.size();
        }
    }

    //loads in the schedule from the database
    private void helperSchedule(String id, List<String> schedule, CustomStringAdapter stringAdapter, ArrayAdapter<String> arrayAdapter) {
        //this is used to load in the List<String> of when to take medicine and how much from the databes
        ControllerDatabase.ControllerScheduleLoader(id, loaded_schedule ->{
            for (String toDo : loaded_schedule) {
                schedule.add(toDo);
            }

            //as the schedule is changed, update the array and string adapter to
            //update the spinner and recyleview of a schedule
            stringAdapter.notifyDataSetChanged();;
            arrayAdapter.notifyDataSetChanged();
        });
    }
}