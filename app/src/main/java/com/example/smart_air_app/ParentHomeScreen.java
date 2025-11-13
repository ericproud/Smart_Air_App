package com.example.smart_air_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ParentHomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home_screen);

        LinearLayout container = findViewById(R.id.childButtonContainer);

        // Create a rounded button fully in Java
        for (int i=0; i< 4;i++) {
            int childId = i; // Placeholder
            Button button = new Button(this);
            button.setText("Child" + i + 1);
            button.setTextColor(Color.WHITE);

            // Create a rounded background
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(Color.parseColor("#673AB7")); // background color
            drawable.setCornerRadius(dpToPx(35)); // radius for rounding
            button.setBackground(drawable);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            button.setPadding(0, 0, 0, 0);

            // Layout params with size + margins
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(297), dpToPx(67)
            );
            params.setMargins(dpToPx(55), dpToPx(20), dpToPx(56), 0);
            button.setLayoutParams(params);

            // Make it clickable
            button.setClickable(true);
            button.setOnClickListener(v ->
                    Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show()
            );

            button.setOnClickListener(v -> {
                Intent intent = new Intent(ParentHomeScreen.this, childHomeScreen.class);

                intent.putExtra("childId", childId);

                startActivity(intent);
            });

            // Add button to container
            container.addView(button);
        }
    }

    // Helper: convert dp -> px
    private int dpToPx(int dp){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
}