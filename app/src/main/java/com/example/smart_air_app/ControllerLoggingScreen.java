package com.example.smart_air_app;

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

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

public class ControllerLoggingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controller_logging_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toast.makeText(this, "App started", Toast.LENGTH_SHORT).show();

        Button scheduleButton = findViewById(R.id.controllerUseScheduleButton);
        Button timeSelector = findViewById(R.id.timeButton);
        Spinner medicineSpinner = findViewById(R.id.medicineSpinner);
        TextView doseAmount = findViewById(R.id.amountInputText);
        Button techniqueHelperRedirect = findViewById(R.id.techniqueHelperButton);
        TextView preController = findViewById(R.id.preControllerInput);
        TextView postController = findViewById(R.id.postControllerInput);
        TextView personalBest = findViewById(R.id.currentBest);
        TextView personalBestInput = findViewById(R.id.personalBestInput);
        TextView currentZone = findViewById(R.id.currentZoneText);
        Button submitButton = findViewById(R.id.submitButton);
        Button backButton = findViewById(R.id.backButton);

        //this is used for the medicineSpinner get from database.
        String[] medicine_to_choose = {"a", "b", "c"};

        //get from the database
        //this is used to display the current personal best
        int personal_best = 67;

        //calculate this to display the current zone of the user
        String currZone = "if you are reading this someone needs to do this story point of calculating zones";

        scheduleButton.setOnClickListener(v ->{
            Intent intent = new Intent(ControllerLoggingScreen.this, ControllerScheduleScreen.class);
            startActivity(intent);
        });

        timeSelector.setOnClickListener(v->{
            //temp is the time chosen by showTimePopup
            String temp = showTimePopup();
            System.out.println(temp);
        });

        //using an adapter to use the spinner/add choices
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, medicine_to_choose);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicineSpinner.setAdapter(adapter);

        medicineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(medicine_to_choose[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        techniqueHelperRedirect.setOnClickListener(v-> {
            //redirect to the technique helper page once that's done
            System.out.println("redirect to technique helper");
        });

        personalBest.setText("Current Personal Best: " + personal_best);

        currentZone.setText("Today's Zone: " + currZone);

        //if submit button is clicked validate amount input for dosage and if valid then log to database
        //also if the inputs for the optional fields are there, log that after validation
        submitButton.setOnClickListener(v->{
            String doseInput = doseAmount.getText().toString().trim();
            String preInput = preController.getText().toString().trim();
            String postInput = postController.getText().toString().trim();
            String newPersonalBestInput = personalBestInput.getText().toString().trim();

            //intParser returns -69 as a signal that bad input was entered handle accordingly
            int doseInputAmount = intParser(doseInput);
            int preInputAmount = intParser(preInput);
            int postInputAmount = intParser(postInput);
            int newPersonalBestInputAmount = intParser(newPersonalBestInput);

            System.out.println(doseInputAmount + " " + preInputAmount + " " + postInputAmount + " " + newPersonalBestInputAmount);
        });

        //exit page if back button is clicked
        backButton.setOnClickListener(v->{finish();});
    }

    private int intParser(String input) {
        if (input.isEmpty()) {
            System.out.println("empty input detected");
        }

        try {
            int ans = Integer.parseInt(input);
            if (ans < 0) {
                System.out.println("negative input given");
                return -69;
            }
            return ans;
        }
        catch (NumberFormatException e) {
            System.out.println("bad input given");
            return -69;
        }
    }

    //method to get the popup to choose time
    private String showTimePopup() {
        Calendar c = Calendar.getInstance();
        //set default time or start time to the current time
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        final String[] time = new String[1];
        TextView timeChosen = findViewById(R.id.selectedTimeText);

        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    //these lines are what we do with the selected time, the rest of this code is the constructor
                    time[0] = selectedHour + ":" + selectedMinute;
                    timeChosen.setText("Selected Time: " + time[0]);
                }, hour, minute, true
                );
        dialog.show();

        return time[0];
    }
}