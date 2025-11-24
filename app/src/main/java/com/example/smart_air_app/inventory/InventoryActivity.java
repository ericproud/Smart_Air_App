package com.example.smart_air_app.inventory;

import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.R;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import java.util.Locale;

public class InventoryScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        TextView quickReliefName = findViewById(R.id.quickReliefName);
        TextView quickReliefRemaining = findViewById(R.id.quickReliefRemaining);
        TextView quickReliefTotal = findViewById(R.id.quickReliefTotal);
        TextView quickReliefPercentage = findViewById(R.id.quickReliefPercentage);
        LinearProgressIndicator quickReliefProgress = findViewById(R.id.quickReliefProgress);
        TextView quickReliefLastPurchased = findViewById(R.id.quickReliefLastPurchased);
        TextView quickReliefExpires = findViewById(R.id.quickReliefExpires);
        LinearLayout quickReliefReportedBy = findViewById(R.id.quickReliefReportedBy);

        TextInputEditText quickReliefNameInput = findViewById(R.id.quickReliefNameInput);
        TextInputEditText quickReliefRemainingInput = findViewById(R.id.quickReliefRemainingInput);
        TextInputEditText quickReliefTotalInput = findViewById(R.id.quickReliefTotalInput);
        TextInputEditText quickReliefLastPurchasedInput = findViewById(R.id.quickReliefLastPurchasedInput);
        TextInputEditText quickReliefExpiresInput = findViewById(R.id.quickReliefExpiresInput);
        MaterialButtonToggleGroup quickReliefReportedByToggle = findViewById(R.id.quickReliefReportedByToggle);


        var quickReliefViewLayout = findViewById(R.id.quickReliefView);
        var quickReliefEditLayout = findViewById(R.id.quickReliefEdit);
        ImageView quickReliefEditButton = findViewById(R.id.quickReliefEditIcon);

        var controllerViewLayout = findViewById(R.id.controllerView);
        var controllerEditLayout = findViewById(R.id.controllerEdit);
        ImageView  controllerEditButton = findViewById(R.id.controllerEditIcon);

        quickReliefEditButton.setOnClickListener(new View.OnClickListener() {
            boolean isEditMode = false;
            @Override
            public void onClick(View v) {
                isEditMode = !isEditMode;

                if (isEditMode) {
                    quickReliefViewLayout.setVisibility(View.GONE);
                    quickReliefEditLayout.setVisibility(View.VISIBLE);
                    quickReliefEditButton.setImageResource(R.drawable.save_24px);
                } else {
                    quickReliefViewLayout.setVisibility(View.VISIBLE);
                    quickReliefEditLayout.setVisibility(View.GONE);
                    quickReliefEditButton.setImageResource(R.drawable.edit_24px);
                }
            }
        });

        controllerEditButton.setOnClickListener(new View.OnClickListener() {
            boolean isEditMode = false;
            @Override
            public void onClick(View v) {
                isEditMode = !isEditMode;

                if (isEditMode) {
                    controllerViewLayout.setVisibility(View.GONE);
                    controllerEditLayout.setVisibility(View.VISIBLE);
                    controllerEditButton.setImageResource(R.drawable.save_24px);
                } else {
                    controllerViewLayout.setVisibility(View.VISIBLE);
                    controllerEditLayout.setVisibility(View.GONE);
                    controllerEditButton.setImageResource(R.drawable.edit_24px);
                }
            }
        });


        TextInputEditText controllerPurchasedInput = findViewById(R.id.controllerPurchasedInput);
        TextInputEditText controllerExpiresInput = findViewById(R.id.controllerExpiresInput);

        attachDatePicker(quickReliefLastPurchasedInput);
        attachDatePicker(quickReliefExpiresInput);
        attachDatePicker(controllerPurchasedInput);
        attachDatePicker(controllerExpiresInput);

    }

    private void attachDatePicker(TextInputEditText editText) {

        MaterialDatePicker.Builder<Long> builder =
                MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Date");

        // Pre-select existing date
        if (!editText.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
                df.setTimeZone(TimeZone.getTimeZone("UTC"));

                Date parsed = df.parse(editText.getText().toString());
                if (parsed != null) {
                    builder.setSelection(parsed.getTime());
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        MaterialDatePicker<Long> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            editText.setText(df.format(new Date(selection)));
        });

        datePicker.addOnNegativeButtonClickListener(dialog -> editText.clearFocus());
        datePicker.addOnDismissListener(dialog -> editText.clearFocus());

        editText.setOnClickListener(v ->
                datePicker.show(getSupportFragmentManager(), "DATE_PICKER_" + editText.getId())
        );
    }

}

