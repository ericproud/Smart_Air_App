package com.example.smart_air_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class CheckinHistoryScreen extends AppCompatActivity {
    private String symptomSelected = "None";
    private String triggerSelected = "None";
    private String startSelected = "None";
    private String endSelected = "None";
    private Calendar startDate = null;
    private Calendar endDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkin_history_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner symptomSpinner = findViewById(R.id.symptomSpinner);
        Spinner triggerSpinner = findViewById(R.id.triggerSpinner);
        Button startDateButton = findViewById(R.id.startDateButton);
        Button startDateResetButton = findViewById(R.id.startResetButton);
        Button endDateButton = findViewById(R.id.endDateButton);
        Button endDateResetButton = findViewById(R.id.endResetButton);
        TextView symptomLabel = findViewById(R.id.symptomLabel);
        TextView triggerLabel = findViewById(R.id.triggerLabel);
        TextView startDateLabel = findViewById(R.id.startDateLabel);
        TextView endDateLabel = findViewById(R.id.endDateLabel);
        Button backButton = findViewById(R.id.backButton);
        Button viewHistoryButton = findViewById(R.id.viewHistoryButton);

        //options for the symptoms spinner
        String[] symptoms = {"if", "you", "are", "seeing", "this", "the", "page", "isn't", "done"};
        //options for the trigger spinner
        String[] triggers = {"if", "you", "are", "seeing", "this", "the", "page", "isn't", "done"};

        //setting symptom spinner's options to symptoms array
        ArrayAdapter<String> symptomAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                symptoms
        );

        symptomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        symptomSpinner.setAdapter(symptomAdapter);

        //setting trigger spinner's options to triggers array
        ArrayAdapter<String> triggerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                triggers
        );

        triggerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        triggerSpinner.setAdapter(triggerAdapter);

        //handles selecting a symptom
        symptomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //handles when a user selects an option
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                symptomSelected = symptoms[position];
                symptomLabel.setText("Symptom Selected: " + symptomSelected);
            }

            //handles when a user doesn't select an option
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                symptomSelected = "None";
                symptomLabel.setText("Symptom Selected: " + symptomSelected);
            }
        });

        //handles selecting a trigger
        triggerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //handles what happens when a user chooses an option
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                triggerSelected = triggers[position];
                triggerLabel.setText("Trigger Selected: " + triggerSelected);
            }

            //handles what happens when a user doesn't choose an option
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                triggerSelected = "None";
                triggerLabel.setText("Trigger Selected: " + triggerSelected);
            }
        });

        startDateButton.setOnClickListener(v->{
            Calendar today = Calendar.getInstance();
            int day = today.get(today.DAY_OF_MONTH);
            int month = today.get(today.MONTH);
            int year = today.get(today.YEAR);

            //calender popup to select date
            DatePickerDialog dateSelection = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        startSelected = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        startDateLabel.setText("Start Date Selected: " + startSelected);
                        startDate = Calendar.getInstance();
                        startDate.set(selectedYear, selectedMonth, selectedDay);
                    },
                    year, month, day
            );

            //end date is selected, so we cannot choose the start as before the end date
            if (endDate != null) {
                dateSelection.getDatePicker().setMaxDate(endDate.getTimeInMillis());
            }
            dateSelection.show();
        });

        //resets the start date to "None"
        startDateResetButton.setOnClickListener(v->{
            startSelected = "None";
            startDateLabel.setText("Start Date Selected: " + startSelected);
            startDate = null;
        });

        endDateButton.setOnClickListener(v->{
            Calendar today = Calendar.getInstance();
            int day = today.get(today.DAY_OF_MONTH);
            int month = today.get(today.MONTH);
            int year = today.get(today.YEAR);

            //calender popup to select date
            DatePickerDialog dateSelection = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        endSelected = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        endDateLabel.setText("Start Date Selected: " + endSelected);
                        endDate = Calendar.getInstance();
                        endDate.set(selectedYear, selectedMonth, selectedDay);
                    },
                    year, month, day
            );

            //if the start date is selected we cannot choose the end date to be before the start date
            if (startDate != null) {
                dateSelection.getDatePicker().setMinDate(startDate.getTimeInMillis());
            }
            dateSelection.show();
        });

        //resets the end date to "None"
        endDateResetButton.setOnClickListener(v->{
            endSelected = "None";
            endDateLabel.setText("End Date Selected: " + endSelected);
            endDate = null;
        });

        //exits the page
        backButton.setOnClickListener(v->{
            finish();
        });

        viewHistoryButton.setOnClickListener(v->{
            //as symptoms and triggers are option, all we care about are 2 valid dates selected.
            if (!startSelected.equals("None") && !endSelected.equals("None")) {
                Intent intent = new Intent(CheckinHistoryScreen.this, ViewHistoryScreen.class);
                intent.putExtra("Symptom", symptomSelected);
                intent.putExtra("Trigger", triggerSelected);
                intent.putExtra("Start", startSelected);
                intent.putExtra("End", endSelected);
                startActivity(intent);
            }
        });
    }
}