package com.example.smart_air_app;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

public class DailyCheckIn extends AppCompatActivity {

    String childUID;
    String childName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daily_check_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.materialToolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        startFloatingAnimation(findViewById(R.id.blob_1));
        startFloatingAnimation(findViewById(R.id.blob_2));


        childUID = getIntent().getStringExtra("childUID");  //if it the parent this is not null
        childName = getIntent().getStringExtra("childName");
        if(childUID == null) childUID = FirebaseAuth.getInstance().getUid(); //if the child is on the app


        View btmComplete = findViewById(R.id.BtmComplete); //this guy is for filling the check ins!
        String findChildUID = childUID; ///it is not "stable"
        String findChildName = childName; ///it is not "stable"
        btmComplete.setOnClickListener(v -> {
            Intent intent = new Intent(DailyCheckIn.this, DailyLogActivity.class);
            intent.putExtra("childUID", findChildUID);
            intent.putExtra("childName", findChildName);
            startActivity(intent);
        });

        View btmHistory = findViewById(R.id.BtnHistory);
        btmHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DailyCheckIn.this, CheckinHistoryScreen.class);
            intent.putExtra("childUID", findChildUID);
            intent.putExtra("childName", findChildName);
            startActivity(intent);
        });
    }

    private void startFloatingAnimation(View blob) {
        Random random = new Random();

        float maxMove = dpToPx(blob.getContext(), 30 + random.nextInt(30));

        ObjectAnimator animX = ObjectAnimator.ofFloat(
                blob,
                "translationX",
                -maxMove,
                maxMove
        );
        animX.setRepeatCount(ValueAnimator.INFINITE);
        animX.setRepeatMode(ValueAnimator.REVERSE);
        animX.setDuration(4000 + random.nextInt(3000)); // 4–7 seconds

        ObjectAnimator animY = ObjectAnimator.ofFloat(
                blob,
                "translationY",
                -maxMove,
                maxMove
        );
        animY.setRepeatCount(ValueAnimator.INFINITE);
        animY.setRepeatMode(ValueAnimator.REVERSE);
        animY.setDuration(5000 + random.nextInt(3000)); // 5–8 seconds

        ObjectAnimator scaleAnim = ObjectAnimator.ofFloat(
                blob,
                "scaleX",
                0.95f,
                1.05f
        );
        scaleAnim.setRepeatCount(ValueAnimator.INFINITE);
        scaleAnim.setRepeatMode(ValueAnimator.REVERSE);
        scaleAnim.setDuration(6000);

        ObjectAnimator scaleAnimY = ObjectAnimator.ofFloat(
                blob,
                "scaleY",
                0.95f,
                1.05f
        );
        scaleAnimY.setRepeatCount(ValueAnimator.INFINITE);
        scaleAnimY.setRepeatMode(ValueAnimator.REVERSE);
        scaleAnimY.setDuration(6000);

        animX.start();
        animY.start();
        scaleAnim.start();
        scaleAnimY.start();
    }

    private float dpToPx(Context ctx, float dp) {
        return dp * ctx.getResources().getDisplayMetrics().density;
    }


}