package com.example.smart_air_app.controller_log;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import com.example.smart_air_app.R;
import com.example.smart_air_app.VideoSBSInhallerUse;
import com.example.smart_air_app.utils.DateValidator;

import java.util.Calendar;

public class ControllerLoggingScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controller_logging_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),(v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final String ID = getIntent().getStringExtra("childUID");

        Button scheduleButton = findViewById(R.id.controllerUseScheduleButton);
        Button timeSelector = findViewById(R.id.timeButton);
        Button dateSelector = findViewById(R.id.selectDateButton);
        TextView doseAmount = findViewById(R.id.amountInputText);
        Spinner feelingSpinner = findViewById(R.id.feelingSpinner);
        Button pbButton = findViewById(R.id.setPBButton);
        TextView pbInput = findViewById(R.id.personalBestInput);
        Button techniqueHelperRedirect = findViewById(R.id.techniqueHelperButton);
        TextView preController = findViewById(R.id.preControllerInput);
        TextView postController = findViewById(R.id.postControllerInput);
        TextView currentZone = findViewById(R.id.currentZoneText);
        TextView dateText = findViewById(R.id.selectedDateText);
        Button submitButton = findViewById(R.id.submitButton);
        Button backButton = findViewById(R.id.backButton);
        TextView personalBest = findViewById(R.id.currentBest);
        TextView breathShortnessText = findViewById(R.id.shortnessBreathInput);

        String[] feeling = {"Better", "Same", "Worse"};

        //setting default values
        ControllerLog inputs = new ControllerLog();
        inputs.setPreInput(-69);
        inputs.setPostInput(-69);
        inputs.setDoseInput(-69);
        inputs.setBreathShortness(-69);

        //handles the pb, pef etc.
        PEFZones zone = new PEFZones();
        helperPB(ID, zone);

        String[] feeling_chosen = {""};
        String[] doseInput = {""};
        String[] preInput = {""};
        String[] postInput = {""};
        String[] date = {""};
        String[] breathShortness = {""};

        //leaves to the controller schedule screen
        scheduleButton.setOnClickListener(v ->{
            Intent intent = new Intent(ControllerLoggingScreen.this, ControllerScheduleScreen.class);
            intent.putExtra("childUID", ID);
            startActivity(intent);
        });

        //choose the time of controller use
        timeSelector.setOnClickListener(v->{
            showTimePopup(inputs);
        });

        //choose the date of controller use
        dateSelector.setOnClickListener(v->{
            Calendar today = Calendar.getInstance();

            //this is today's date and is the default date
            int day = today.get(today.DAY_OF_MONTH);
            int month = today.get(today.MONTH);
            int year = today.get(today.YEAR);

            DatePickerDialog dateSelection = new DatePickerDialog(
              this, (view, selectedYear, selectedMonth, selectedDay) -> {
                        //save the date chosen in inputs and in the dateText textview
                        date[0] = DateValidator.makeDateString(selectedDay, selectedMonth + 1, selectedYear);
                        dateText.setText("Selected time: " + date[0]);
                        //if there is an error remove it
                        dateText.setError(null);
                        inputs.setDate(date[0]);
                    },
                    year, month, day
            );

            //you can't really take medicine in the future and log it now so set max
            dateSelection.getDatePicker().setMaxDate(today.getTimeInMillis());

            //show the calender
            dateSelection.show();
        });

        //using an adapter to use the spinner/add choices
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, feeling);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feelingSpinner.setAdapter(adapter);

        //handles chosen feelings
        feelingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //handles and saves chosen feeling
                feeling_chosen[0] = feeling[position];
                inputs.setFeeling(feeling_chosen[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //if they manage to not choose anything there is no feeling
                feeling_chosen[0] = "";
                inputs.setFeeling(null);
            }
        });

        //this sets the pb
        pbButton.setOnClickListener(v->{
            //get pb text
            String pbText = pbInput.getText().toString().trim();

            //try and parse it
            int pbParsed = intParser(pbText);

            //-69 is the error code selected so if we don't get it then we know its a valid integer
            if (pbParsed != -69) {
                //only set the new pb if it's greater than the old pb
                if (pbParsed > zone.getPB()) {
                    personalBest.setText("Current Personal Best: " + pbParsed);
                }
                //set the pb, update the database and text
                zone.setPB(pbParsed);
                PEFZonesDatabase.savePEFZones(ID, zone);
                currentZone.setText("Today's Zone: " + zone.calculateZone());
            }
        });

        //redirects to technique helper page
        techniqueHelperRedirect.setOnClickListener(v-> {
            Intent intent = new Intent(ControllerLoggingScreen.this, VideoSBSInhallerUse.class);
            startActivity(intent);
        });

        //if submit button is clicked validate amount input for dosage and if valid then log to database
        //also if the inputs for the optional fields are there, log that after validation
        submitButton.setOnClickListener(v->{
            doseInput[0] = doseAmount.getText().toString().trim();
            preInput[0] = preController.getText().toString().trim();
            postInput[0] = postController.getText().toString().trim();
            breathShortness[0] = breathShortnessText.getText().toString().trim();

            //intParser returns -69 as a signal that bad input was entered handle accordingly
            int doseInputAmount = intParser(doseInput[0]);
            int preInputAmount = intParser(preInput[0]);
            int postInputAmount = intParser(postInput[0]);
            int breathShortnessInput = intParser(breathShortness[0]);

            //if the optional fields are not error codes then set input to store those values
            if (doseInputAmount != -69) {
                inputs.setDoseInput(doseInputAmount);
            }

            if (preInputAmount != -69) {
                inputs.setPreInput(preInputAmount);
            }

            if (breathShortnessInput != -69) {
                inputs.setBreathShortness(breathShortnessInput);
            }

            if (postInputAmount != -69) {
                inputs.setPostInput(postInputAmount);

                if (DateValidator.getTodaysDate().equals(inputs.getDate())) {
                    zone.setHighest_pef(postInputAmount);
                }
            }

            //valid submission handles if all required fields are filled in with valid inputs
            if (validSubmission(inputs)) {
                //save the controller
                ControllerDatabase.logControllerDatabase(ID, inputs);

                //if the pef logged is higher than the old pef set it
                zone.setHighest_pef(postInputAmount);

                //update feelings and pef
                PEFZonesDatabase.savePEFZones(ID, zone);

                //recalculate zone
                currentZone.setText("Today's Zone: " + zone.calculateZone());
            }
        });

        //exit page if back button is clicked
        backButton.setOnClickListener(v->{finish();});
    }

    //this is the integer parser
    private int intParser(String input) {
        try {
            int ans = Integer.parseInt(input);
            //if the value is a negative integer return error code -69
            if (ans < 0) {
                return -69;
            }
            //this means it was a non negative integer so valid
            return ans;
        }
        catch (NumberFormatException e) {
            //if the value was not an integer return error code -69
            return -69;
        }
    }

    //method to get the popup to choose time
    private void showTimePopup(ControllerLog input) {
        Calendar c = Calendar.getInstance();
        //set default time or start time to the current time
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        final String[] time = new String[1];
        TextView timeChosen = findViewById(R.id.selectedTimeText);

        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    //these lines are what we do with the selected time, the rest of this code is the constructor
                    if (selectedMinute < 10) {
                        time[0] = selectedHour + ":0" + selectedMinute;
                    }
                    else {
                        time[0] = selectedHour + ":" + selectedMinute;
                    }
                    timeChosen.setText("Selected Time: " + time[0]);
                    timeChosen.setError(null);
                    input.setTime(time[0]);
                }, hour, minute, true
                );
        dialog.show();
    }

    //if the submission is valid return true
    private boolean validSubmission(ControllerLog inputs) {
        TextView dateText = findViewById(R.id.selectedDateText);
        TextView timeChosen = findViewById(R.id.selectedTimeText);
        TextView doseAmount = findViewById(R.id.amountInputText);
        TextView breathShortnessText = findViewById(R.id.shortnessBreathInput);

        //we return this
        boolean ans = true;

        //if no date is chosen show an error
        if (inputs.getDate() == null) {
            dateText.setError("This field is required");
            dateText.requestFocus();
            ans = false;
        }

        //if the time is not chosen show an error
        if (inputs.getTime() == null) {
            timeChosen.setError("This field is required");
            timeChosen.requestFocus();
            ans = false;
        }

        //if the dosage amount is not given show an error
        if (inputs.getDoseInput() == -69) {
            doseAmount.setError("This field is required");
            doseAmount.requestFocus();
            ans = false;
        }

        //if breath shortness isn't given show an error
        if (inputs.getBreathShortness() == -69) {
            breathShortnessText.setError("This field is required");
            breathShortnessText.requestFocus();
            ans = false;
        }

        return ans;
    }

    //update pb and zone if a new pb is given
    private void helperPB(String id, PEFZones pefZone) {
        TextView personalBest = findViewById(R.id.currentBest);
        TextView currentZone = findViewById(R.id.currentZoneText);

        //loading in the pef zone object which contains the pb which is what we want
        PEFZonesDatabase.loadPEFZones(id, (pb, pef, date) -> {
            pefZone.initializePEF(pb, pef, date);

            if (pb > 0) {
                personalBest.setText("Current Personal Best: " + pb);
            }
            else {
                personalBest.setText("Current Personal Best: N/A");
            }

            currentZone.setText("Today's Zone: " + pefZone.calculateZone());
        });
    }
}