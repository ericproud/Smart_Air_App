package com.example.smart_air_app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StreaksAndBadges extends AppCompatActivity {
    /// /////////////I need two int here, one is login days, the other is the number of something el
    /// that can be bundeled for a badges ( i.e. number of t excersises done)
    int login_streak = 85;
    int exercise = 0;
    @Override
    protected void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaks_and_badges);

        ImageView rightbadgeImageView = findViewById(R.id.right);

        if(login_streak <= 0) {
            rightbadgeImageView.setImageResource(R.drawable.sam);
        } else if ( login_streak == 1) {
            rightbadgeImageView.setImageResource(R.drawable.tree);
        } else {
            rightbadgeImageView.setImageResource(R.drawable.forest);
        }

        rightbadgeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 3. Show the Toast message
                Toast.makeText(StreaksAndBadges.this,
                        "you have a " + login_streak + " day login streak" ,
                        Toast.LENGTH_SHORT).show();
            }
        });
        ImageView leftbadgeImageView = findViewById(R.id.left);

        if(exercise <= 0) {
            leftbadgeImageView.setImageResource(R.drawable.lungs);
        } else if ( exercise == 1) {
            leftbadgeImageView.setImageResource(R.drawable.lungs1);
        } else {
            leftbadgeImageView.setImageResource(R.drawable.lungs2);
        }

        leftbadgeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 3. Show the Toast message
                Toast.makeText(StreaksAndBadges.this,
                        "you have a " + exercise + " day exercise streak",
                        Toast.LENGTH_SHORT).show();
            }
        });
        TextView daysStreak = findViewById(R.id.streakNumber);
        daysStreak.setText( "You have " + login_streak + " days of login streak" +
                 "\n" + "and " + exercise + " days of controlled excersice" );



    }
}
