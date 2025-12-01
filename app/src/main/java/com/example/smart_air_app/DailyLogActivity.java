package com.example.smart_air_app;

import android.os.Bundle;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_air_app.utils.CheckInFields;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DailyLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState);
        setContentView(R.layout.activity_daily_check_in_log);
        boolean isParent = true; //we assume the parent is logging in everytime
        String childUID = getIntent().getStringExtra("childUserId");  //if it the parent this is not null
        String childName = getIntent().getStringExtra("childName");
        if(childUID == null) {
            childUID = FirebaseAuth.getInstance().getUid(); //if the child is on the app
            isParent = false; //now its diffrent
        }
        String finalChildUI = childUID;
        boolean FinalisParent = isParent;
        findViewById(R.id.btnSubmitLog).setOnClickListener(v -> saveLog(finalChildUI, FinalisParent));
    }
    private void saveLog( String UID, boolean isParent) {
        CheckInFields entry = new CheckInFields();
        entry.date = System.currentTimeMillis();


        CheckBox chkNight = findViewById(R.id.chkNight);
        CheckBox chkActivity = findViewById(R.id.chkActivity);
        CheckBox chkCough = findViewById(R.id.chkCough);


        entry.nightWaking = chkNight.isChecked();
        entry.activityLimits = chkActivity.isChecked();
        entry.cough = chkCough.isChecked();

        com.google.android.material.chip.ChipGroup chips = findViewById(R.id.triggerChipGroup);  //the triggers
        ArrayList<String> trriggers_list = new ArrayList<String>();
        for(int i = 0; i < chips.getChildCount(); i++) {
            com.google.android.material.chip.Chip chip =
                    (com.google.android.material.chip.Chip) chips.getChildAt(i);
            if( chip.isChecked()) {
                trriggers_list.add( (String) chip.getText());
            }

        }
        entry.triggers = String.join(", ", trriggers_list);  // I have the triggers as a string now
        if (isParent) { entry.author = "Parent";
        } else { entry.author = "Child"; }

        FirebaseDatabase.getInstance().getReference("DailyCheckIns").child(UID).push().setValue(entry);

        finish();

    }

}
