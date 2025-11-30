package com.example.smart_air_app;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_air_app.triage.TriageEntry;

public class VideoSBSInhallerUse extends AppCompatActivity {
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(  savedInstanceState );
        setContentView( R.layout.activity_video_step_by_step_rescure_inhaller_use);

        VideoView vid =  findViewById(R.id.videoView);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.controler_vid;
        Uri uri = Uri.parse(path);
        vid.setVideoURI(uri);

        MediaController controller = new MediaController(this);
        vid.setMediaController(controller);

        TextView daysStreak = findViewById(R.id.subTit);
        daysStreak.setText( "Look at the dose counter and make sure it is not zero\n" +
                "Prime the Inhaler before the first time use \n" +
                "Shake for 10 seconds\n" +
                "Sit or Stand up Straight\n" +
                "Breathe out\n" +
                "Put the inhaler mouthpiece in mouth\n" +
                "Press Down and breathe in deep and steady for 3-5 seconds\n" +
                "Hold your breath for up to 10 seconds\n" +
                "Breathe out slowly \n" +
                "Wait one minute and repeat\n" +
                "\n" +
                "Rinse your mouth with water and spit out.\n" +
                "\n" +
                "Replace the cap and put in a Dry cool place.\n" +
                "\n" +
                "Make sure to clean the inhaler based on package instructions\n" + "\n" +
                "If you have any trouble ask the Doctor or Pharmacist for help.\n" );


        TextView timerText = findViewById(R.id.emergencyTimer);

        long timeLeftInMillis = getIntent().getLongExtra("TIMER_REMAINING", 600000);


        // 10 mins = 600000ms
        CountDownTimer emergencyTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long msUntilFinished) {


                long seconds = msUntilFinished / 1000;
                long minutes = seconds / 60;
                long remainingSeconds = seconds % 60;

                String formatted = String.format("%02d:%02d", minutes, remainingSeconds);
                timerText.setText(formatted);
            }

            @Override
            public void onFinish() {
                timerText.setText("00:00");
                timeRanOut(null);
            }
        };

        emergencyTimer.start();


    }
    public void timeRanOut(View view) {

        startActivity(new Intent(VideoSBSInhallerUse.this, EmergencyScreen.class));

    }

}

