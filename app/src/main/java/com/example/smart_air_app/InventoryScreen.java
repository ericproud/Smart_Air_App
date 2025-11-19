package com.example.smart_air_app;

import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
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

        var quickReliefViewLayout = findViewById(R.id.quickReliefView);
        var quickReliefEditLayout = findViewById(R.id.quickReliefEdit);
        ImageView quickReliefEditButton = findViewById(R.id.quickReliefEditIcon);

        var controllerViewLayout = findViewById(R.id.controllerView);
        var controllerEditLayout = findViewById(R.id.controllerEdit);
        ImageView  controllerEditButton = findViewById(R.id.controllerEditIcon);

        final boolean[] isEditMode = {false, false};

        quickReliefEditButton.setOnClickListener(view -> {
            isEditMode[0] = !isEditMode[0];

            if (isEditMode[0]) {
                quickReliefViewLayout.setVisibility(View.GONE);
                quickReliefEditLayout.setVisibility(View.VISIBLE);
                quickReliefEditButton.setImageResource(R.drawable.save_24px);
            } else {
                quickReliefViewLayout.setVisibility(View.VISIBLE);
                quickReliefEditLayout.setVisibility(View.GONE);
                quickReliefEditButton.setImageResource(R.drawable.edit_24px);
            }
        });

        controllerEditButton.setOnClickListener(view -> {
            isEditMode[1] = !isEditMode[1];

            if (isEditMode[1]) {
                controllerViewLayout.setVisibility(View.GONE);
                controllerEditLayout.setVisibility(View.VISIBLE);
                controllerEditButton.setImageResource(R.drawable.save_24px);
            } else {
                controllerViewLayout.setVisibility(View.VISIBLE);
                controllerEditLayout.setVisibility(View.GONE);
                controllerEditButton.setImageResource(R.drawable.edit_24px);
            }
        });


        TextInputEditText quickReliefPurchased = findViewById(R.id.quickReliefPurchased);
        TextInputEditText quickReliefExpires = findViewById(R.id.quickReliefExpires);

        TextInputEditText controllerPurchased = findViewById(R.id.controllerPurchased);
        TextInputEditText controllerExpires = findViewById(R.id.controllerExpires);

        attachDatePicker(quickReliefPurchased);
        attachDatePicker(quickReliefExpires);
        attachDatePicker(controllerPurchased);
        attachDatePicker(controllerExpires);

    }

    private void attachDatePicker(TextInputEditText editText) {

        MaterialDatePicker.Builder<Long> builder =
                MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Date");

        // Pre-select existing date
        if (!editText.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                df.setTimeZone(TimeZone.getTimeZone("UTC"));

                Date parsed = df.parse(editText.getText().toString());
                if (parsed != null) {
                    builder.setSelection(parsed.getTime());
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        MaterialDatePicker<Long> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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

