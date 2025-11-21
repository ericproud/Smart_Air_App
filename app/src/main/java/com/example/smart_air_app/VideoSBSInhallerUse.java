package com.example.smart_air_app;


import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

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
    }
}
