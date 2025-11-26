package controller_log;

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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.R;
import com.example.smart_air_app.VideoSBSInhallerUse;
import com.example.smart_air_app.utils.DateValidator;

import java.util.Calendar;

/*
TO DO:
use the get intent when the parent child home screen or whatever works properly
*/

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

        //Toast.makeText(this, getIntent().getStringExtra("childId"), Toast.LENGTH_SHORT).show();

        if (getIntent().getStringExtra("childId") == null) {
            Toast.makeText(this, "child id was null", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, getIntent().getStringExtra("childId"), Toast.LENGTH_SHORT).show();
        }

        String id = "rR8IM0i012OxkTxPNOoT1KkUpsJ2";

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

        String[] feeling = {"Better", "Same", "Worse"};

        int[] personal_best = {-1};

        helperPB(id, personal_best);

        ControllerLog inputs = new ControllerLog();
        inputs.setPreInput(-69);
        inputs.setPostInput(-69);
        inputs.setDoseInput(-69);

        String[] feeling_chosen = {""};
        String[] doseInput = {""};
        String[] preInput = {""};
        String[] postInput = {""};
        String[] date = {""};

        //calculate this to display the current zone of the user
        String[] currZone = {"N/A"};

        scheduleButton.setOnClickListener(v ->{
            Intent intent = new Intent(ControllerLoggingScreen.this, ControllerScheduleScreen.class);
            intent.putExtra("childId", id);
            startActivity(intent);
        });

        timeSelector.setOnClickListener(v->{
            showTimePopup(inputs);
        });

        dateSelector.setOnClickListener(v->{
            Calendar today = Calendar.getInstance();

            int day = today.get(today.DAY_OF_MONTH);
            int month = today.get(today.MONTH);
            int year = today.get(today.YEAR);

            DatePickerDialog dateSelection = new DatePickerDialog(
              this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        date[0] = DateValidator.makeDateString(selectedDay, selectedMonth + 1, selectedYear);
                        dateText.setText("Selected time: " + date[0]);
                        inputs.setDate(date[0]);
                    },
                    year, month, day
            );

            dateSelection.getDatePicker().setMaxDate(today.getTimeInMillis());
            dateSelection.show();
        });

        //using an adapter to use the spinner/add choices
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, feeling);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feelingSpinner.setAdapter(adapter);

        feelingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                feeling_chosen[0] = feeling[position];
                Toast.makeText(ControllerLoggingScreen.this, feeling_chosen[0], Toast.LENGTH_SHORT).show();
                inputs.setFeeling(feeling_chosen[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                feeling_chosen[0] = "";
                inputs.setFeeling(null);
            }
        });

        pbButton.setOnClickListener(v->{
            String pbText = pbInput.getText().toString().trim();

            int pbParsed = intParser(pbText);

            if (pbParsed != -69) {
                inputs.setPB(pbParsed);
                ControllerDatabase.personalBestLogger(id, inputs);
                if (pbParsed > personal_best[0]) {
                    personal_best[0] = pbParsed;
                    personalBest.setText("Current Personal Best: " + personal_best[0]);
                }
            }
        });

        techniqueHelperRedirect.setOnClickListener(v-> {
            Intent intent = new Intent(ControllerLoggingScreen.this, VideoSBSInhallerUse.class);
            startActivity(intent);
        });

        currentZone.setText("Today's Zone: " + currZone[0]);

        //if submit button is clicked validate amount input for dosage and if valid then log to database
        //also if the inputs for the optional fields are there, log that after validation
        submitButton.setOnClickListener(v->{
            doseInput[0] = doseAmount.getText().toString().trim();
            preInput[0] = preController.getText().toString().trim();
            postInput[0] = postController.getText().toString().trim();

            //intParser returns -69 as a signal that bad input was entered handle accordingly
            int doseInputAmount = intParser(doseInput[0]);
            int preInputAmount = intParser(preInput[0]);
            int postInputAmount = intParser(postInput[0]);

            if (doseInputAmount != -69) {
                inputs.setDoseInput(doseInputAmount);
            }

            if (preInputAmount != -69) {
                inputs.setPreInput(preInputAmount);
            }

            if (postInputAmount != -69) {
                inputs.setPostInput(postInputAmount);
                inputs.setZone(currZone[0]);
            }

            if (validSubmission(inputs)) {
                ControllerDatabase.logControllerDatabase(id, inputs);
                Toast.makeText(this, "" + inputs.getDoseInput(), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
            }
        });

        //exit page if back button is clicked
        backButton.setOnClickListener(v->{finish();});

        //when input the PEF calculate the currZone
        postController.setOnFocusChangeListener((v, focus) -> {
            if (!focus) {
                Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
                String text = postController.getText().toString().trim();
                int textNum = intParser(text);
                Toast.makeText(this, "" + textNum, Toast.LENGTH_SHORT).show();
                if (textNum != -69 && personal_best[0] != -1) {
                    double ratio = 1.0 * textNum / personal_best[0];
                    if (ratio >= 0.8) {
                        currZone[0] = "Green";
                    }
                    else if (ratio >= 0.5) {
                        currZone[0] = "Yellow";
                    }
                    else {
                        currZone[0] = "Red";
                    }
                    currentZone.setText("Today's Zone: " + currZone[0]);
                }
                else {
                    currentZone.setText("Today's Zone: N/A");
                }
            }
        });
    }

    private int intParser(String input) {
        try {
            int ans = Integer.parseInt(input);
            if (ans < 0) {
                //Toast.makeText(this, "negative input given", Toast.LENGTH_SHORT).show();
                return -69;
            }
            return ans;
        }
        catch (NumberFormatException e) {
            //Toast.makeText(this, "bad input given", Toast.LENGTH_SHORT).show();
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
                    if (selectedMinute == 0) {
                        time[0] = selectedHour + ":0" + selectedMinute;
                    }
                    else {
                        time[0] = selectedHour + ":" + selectedMinute;
                    }
                    timeChosen.setText("Selected Time: " + time[0]);
                    input.setTime(time[0]);
                    Toast.makeText(ControllerLoggingScreen.this, time[0], Toast.LENGTH_SHORT).show();
                }, hour, minute, true
                );
        dialog.show();
    }

    private boolean validSubmission(ControllerLog inputs) {
        if (inputs.getDate() == null) {
            Toast.makeText(this, "invalid date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (inputs.getTime() == null) {
            Toast.makeText(this, "invalid time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (inputs.getDoseInput() == -69) {
            Toast.makeText(this, "invalid dosage", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (inputs.getPostInput() == -69) {
            Toast.makeText(this, "invalid post PEF", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void helperPB(String id, int[] personal_best) {
        TextView personalBest = findViewById(R.id.currentBest);

        //this is used to display the current personal best and calculate zones
        ControllerDatabase.personalBestGetter(id, value -> {
            Toast.makeText(this, "AAAAAAAA " + value, Toast.LENGTH_SHORT).show();
            if (value > 0) {
                personal_best[0] = value;
                personalBest.setText("Current Personal Best: " + personal_best[0]);
            }
            else {
                personal_best[0] = -1;
                personalBest.setText("Current Personal Best: N/A");
            }
        });
    }
}