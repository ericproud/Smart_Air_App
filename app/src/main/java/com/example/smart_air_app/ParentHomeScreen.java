package com.example.smart_air_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ParentHomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home_screen);

        LinearLayout container = findViewById(R.id.childButtonContainer);

        Button addChildButton = findViewById(R.id.addChildButton);

        addChildButton.setOnClickListener(v -> {
            Intent intent = new Intent(ParentHomeScreen.this, AddChildScreen.class);
            startActivity(intent);
        });

        // Making placeholder buttons where the child accounts would go
        for (int i=0; i< 4;i++) {
            int childId = i;
            Button button = new Button(this);
            button.setText("Child" + i + 1);
            button.setTextColor(Color.WHITE);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(Color.parseColor("#673AB7"));
            drawable.setCornerRadius(dpToPx(35));
            button.setBackground(drawable);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            button.setPadding(0, 0, 0, 0);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(297), dpToPx(67)
            );
            params.setMargins(dpToPx(55), dpToPx(20), dpToPx(56), 0);
            button.setLayoutParams(params);

            button.setClickable(true);
            button.setOnClickListener(v ->
                    Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show()
            );

            button.setOnClickListener(v -> {
                Intent intent = new Intent(ParentHomeScreen.this, ChildHomeScreen.class);

                intent.putExtra("childId", childId);

                startActivity(intent);
            });

            container.addView(button);
        }
    }

    private int dpToPx(int dp){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
}