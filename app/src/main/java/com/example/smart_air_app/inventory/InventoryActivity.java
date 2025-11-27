package com.example.smart_air_app.inventory;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.ChildHomeScreen;
import com.example.smart_air_app.ParentChildHomeScreen;
import com.example.smart_air_app.R;
import com.example.smart_air_app.log_rescue_attempt.LogRescueAttemptActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import java.util.Locale;

public class InventoryActivity extends AppCompatActivity implements InventoryView {
    private TextView quickReliefRemaining;
    private TextView quickReliefTotal;
    private TextView quickReliefPercentage;
    private LinearProgressIndicator quickReliefProgress;
    private TextView quickReliefLastPurchased;
    private TextView quickReliefExpires;
    private LinearLayout quickReliefReportedBy;

    private TextInputEditText quickReliefRemainingInput;
    private TextInputEditText quickReliefTotalInput;
    private TextInputEditText quickReliefLastPurchasedInput;
    private TextInputEditText quickReliefExpiresInput;
    private MaterialButtonToggleGroup quickReliefReportedByToggle;

    private TextView controllerRemaining;
    private TextView controllerTotal;
    private TextView controllerPercentage;
    private LinearProgressIndicator controllerProgress;
    private TextView controllerLastPurchased;
    private TextView controllerExpires;
    private LinearLayout controllerReportedBy;

    private TextInputEditText controllerRemainingInput;
    private TextInputEditText controllerTotalInput;
    private TextInputEditText controllerLastPurchasedInput;
    private TextInputEditText controllerExpiresInput;
    private MaterialButtonToggleGroup controllerReportedByToggle;

    private LinearLayout quickReliefEditLayout;
    private LinearLayout quickReliefViewLayout;
    private ImageView quickReliefEditButton;
    private ImageView quickReliefSaveButton;

    private LinearLayout controllerViewLayout;
    private LinearLayout controllerEditLayout;
    private ImageView controllerEditButton;
    private ImageView controllerSaveButton;

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

        quickReliefRemaining = findViewById(R.id.quickReliefRemaining);
        quickReliefTotal = findViewById(R.id.quickReliefTotal);
        quickReliefPercentage = findViewById(R.id.quickReliefPercentage);
        quickReliefProgress = findViewById(R.id.quickReliefProgress);
        quickReliefLastPurchased = findViewById(R.id.quickReliefLastPurchased);
        quickReliefExpires = findViewById(R.id.quickReliefExpires);
        quickReliefReportedBy = findViewById(R.id.quickReliefReportedBy);

        quickReliefRemainingInput = findViewById(R.id.quickReliefRemainingInput);
        quickReliefTotalInput = findViewById(R.id.quickReliefTotalInput);
        quickReliefLastPurchasedInput = findViewById(R.id.quickReliefLastPurchasedInput);
        quickReliefExpiresInput = findViewById(R.id.quickReliefExpiresInput);
        quickReliefReportedByToggle = findViewById(R.id.quickReliefReportedByToggle);

        controllerRemaining = findViewById(R.id.controllerRemaining);
        controllerTotal = findViewById(R.id.controllerTotal);
        controllerPercentage = findViewById(R.id.controllerPercentage);
        controllerProgress = findViewById(R.id.controllerProgress);
        controllerLastPurchased = findViewById(R.id.controllerLastPurchased);
        controllerExpires = findViewById(R.id.controllerExpires);
        controllerReportedBy = findViewById(R.id.controllerReportedBy);

        controllerRemainingInput = findViewById(R.id.controllerRemainingInput);
        controllerTotalInput = findViewById(R.id.controllerTotalInput);
        controllerLastPurchasedInput = findViewById(R.id.controllerLastPurchasedInput);
        controllerExpiresInput = findViewById(R.id.controllerExpiresInput);
        controllerReportedByToggle = findViewById(R.id.controllerReportedByToggle);

        quickReliefViewLayout = findViewById(R.id.quickReliefView);
        quickReliefEditLayout = findViewById(R.id.quickReliefEdit);
        quickReliefEditButton = findViewById(R.id.quickReliefEditIcon);
        quickReliefSaveButton = findViewById(R.id.quickReliefSaveIcon);

        controllerViewLayout = findViewById(R.id.controllerView);
        controllerEditLayout = findViewById(R.id.controllerEdit);
        controllerEditButton = findViewById(R.id.controllerEditIcon);
        controllerSaveButton = findViewById(R.id.controllerSaveIcon);

        String childUID = getIntent().getStringExtra("childUID");
        String childName = getIntent().getStringExtra("childName");
        InventoryPresenter presenter = new InventoryPresenterImpl(childUID, this, new FirebaseInventoryRepository());

        quickReliefEditButton.setOnClickListener(v -> presenter.onEditToggle(0));
        quickReliefSaveButton.setOnClickListener(v -> {
            Medicine m = collectEditMedicine(0);
            presenter.onSave(0, m);
        });

        controllerEditButton.setOnClickListener(v -> presenter.onEditToggle(1));
        controllerSaveButton.setOnClickListener(v -> {
            Medicine m = collectEditMedicine(1);
            presenter.onSave(1, m);
        });


        MaterialToolbar toolbar = findViewById(R.id.materialToolbar);
        toolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(InventoryActivity.this, ParentChildHomeScreen.class);
            intent.putExtra("childUID", childUID);
            intent.putExtra("childName", childName);
            startActivity(intent);
        });


        attachDatePicker(quickReliefLastPurchasedInput);
        attachDatePicker(quickReliefExpiresInput);
        attachDatePicker(controllerLastPurchasedInput);
        attachDatePicker(controllerExpiresInput);

    }

    private void attachDatePicker(TextInputEditText editText) {

        MaterialDatePicker.Builder<Long> builder =
                MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Date");

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

    @Override
    public void showEditMode(int index, Medicine model) {
        if (index == 0) {
            populateEditFieldsQuick(model);
            quickReliefViewLayout.setVisibility(View.GONE);
            quickReliefEditLayout.setVisibility(View.VISIBLE);
            quickReliefEditButton.setImageResource(R.drawable.outline_close_24);
            quickReliefSaveButton.setVisibility(View.VISIBLE);
        } else {
            populateEditFieldsController(model);
            controllerViewLayout.setVisibility(View.GONE);
            controllerEditLayout.setVisibility(View.VISIBLE);
            controllerEditButton.setImageResource(R.drawable.outline_close_24);
            controllerSaveButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showViewMode(int index, Medicine model) {
        if (index == 0) {
            populateDisplayFieldsQuick(model);
            quickReliefEditLayout.setVisibility(View.GONE);
            quickReliefViewLayout.setVisibility(View.VISIBLE);
            quickReliefEditButton.setImageResource(R.drawable.edit_24px);
            quickReliefSaveButton.setVisibility(View.GONE);
        } else {
            populateDisplayFieldsController(model);
            controllerEditLayout.setVisibility(View.GONE);
            controllerViewLayout.setVisibility(View.VISIBLE);
            controllerEditButton.setImageResource(R.drawable.edit_24px);
            controllerSaveButton.setVisibility(View.GONE);
        }
    }

    public void clearErrors(int index) {
        TextInputEditText remaining = index == 0 ? quickReliefRemainingInput : controllerRemainingInput;
        TextInputEditText total = index == 0 ? quickReliefTotalInput : controllerTotalInput;

        remaining.setError("");
        total.setError("");
    }

    @Override
    public void showRemainingError(int index, String message) {
        TextInputEditText remaining = index == 0 ? quickReliefRemainingInput : controllerRemainingInput;
        remaining.setError(message);
        showError(message);
    }

    @Override
    public void showTotalError(int index, String message) {
        TextInputEditText total = index == 0 ? quickReliefTotalInput : controllerTotalInput;
        total.setError(message);
        showError(message);
    }

    @Override
    public void showLastPurchasedError(int index, String message) {
        TextInputEditText lastPurchased = index == 0 ? quickReliefLastPurchasedInput : controllerLastPurchasedInput;
        lastPurchased.setError(message);
        showError(message);
    }

    @Override
    public void showExpiresError(int index, String message) {
        TextInputEditText expires = index == 0 ? quickReliefExpiresInput : controllerExpiresInput;
        expires.setError(message);
        showError(message);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void populateDisplayFieldsQuick(Medicine m) {
        quickReliefRemaining.setText(String.valueOf(m.getRemaining()));
        quickReliefTotal.setText(String.valueOf(m.getTotal()));

        if (m.getTotal() > 0) {
            int pct = (int) (m.getRemaining() / (double) m.getTotal() * 100);
            quickReliefPercentage.setText(pct + "% available");
            quickReliefProgress.setProgress(pct, true);
        } else {
            quickReliefPercentage.setText("-");
            quickReliefProgress.setProgress(0, true);
        }
        quickReliefLastPurchased.setText(m.getLastPurchased());
        quickReliefExpires.setText(m.getExpires());

        TextView parentText = (TextView) quickReliefReportedBy.getChildAt(1);
        TextView childText = (TextView) quickReliefReportedBy.getChildAt(3);
        if (m.getReportedBy().equalsIgnoreCase(parentText.getTag().toString())) {
            parentText.setTextColor(getColor(R.color.md_theme_onPrimaryContainer));
            childText.setTextColor(getColor(R.color.md_theme_outlineVariant));
        } else {
            childText.setTextColor(getColor(R.color.md_theme_onPrimaryContainer));
            parentText.setTextColor(getColor(R.color.md_theme_outlineVariant));
        }
    }

    private void populateEditFieldsQuick(Medicine m) {
        quickReliefRemainingInput.setText(String.valueOf(m.getRemaining()));
        quickReliefTotalInput.setText(String.valueOf(m.getTotal()));
        quickReliefLastPurchasedInput.setText(m.getLastPurchased());
        quickReliefExpiresInput.setText(m.getExpires());

        boolean parent = m.getReportedBy().equalsIgnoreCase("parent");
        int id = parent ? quickReliefReportedByToggle.getChildAt(0).getId()
                : quickReliefReportedByToggle.getChildAt(1).getId();
        quickReliefReportedByToggle.check(id);
    }

    private void populateDisplayFieldsController(Medicine m) {
        controllerRemaining.setText(String.valueOf(m.getRemaining()));
        controllerTotal.setText(String.valueOf(m.getTotal()));

        if (m.getTotal() > 0) {
            int pct = (int) (m.getRemaining() / (double) m.getTotal() * 100);
            controllerPercentage.setText(pct + "% available");
            controllerProgress.setProgress(pct, true);
        } else {
            controllerPercentage.setText("-");
            controllerProgress.setProgress(0, true);
        }
        controllerLastPurchased.setText(m.getLastPurchased());
        controllerExpires.setText(m.getExpires());

        TextView parentText = (TextView) controllerReportedBy.getChildAt(1);
        TextView childText = (TextView) controllerReportedBy.getChildAt(3);
        if (m.getReportedBy().equalsIgnoreCase(parentText.getTag().toString())) {
            parentText.setTextColor(getColor(R.color.md_theme_onSecondaryContainer));
            childText.setTextColor(getColor(R.color.md_theme_outlineVariant));
        } else {
            childText.setTextColor(getColor(R.color.md_theme_onSecondaryContainer));
            parentText.setTextColor(getColor(R.color.md_theme_onSecondaryContainer));
        }
    }

    private void populateEditFieldsController(Medicine m) {
        controllerRemainingInput.setText(String.valueOf(m.getRemaining()));
        controllerTotalInput.setText(String.valueOf(m.getTotal()));
        controllerLastPurchasedInput.setText(m.getLastPurchased());
        controllerExpiresInput.setText(m.getExpires());

        boolean parent = m.getReportedBy().equalsIgnoreCase("parent");
        int id = parent ? controllerReportedByToggle.getChildAt(0).getId()
                : controllerReportedByToggle.getChildAt(1).getId();
        controllerReportedByToggle.check(id);
    }

    private Medicine collectEditMedicine(int index) {
        Medicine m = new Medicine();
        try {
            if (index == 0) {
                m.setType("Quick-Relief");
                m.setRemaining(safeParseInt(safeGetText(quickReliefRemainingInput), 0));
                m.setTotal(safeParseInt(safeGetText(quickReliefTotalInput), 0));
                m.setLastPurchased(safeGetText(quickReliefLastPurchasedInput));
                m.setExpires(safeGetText(quickReliefExpiresInput));
                m.setReportedBy(getReportedByFromToggle(quickReliefReportedByToggle));
            } else {
                m.setType("Controller");
                m.setRemaining(safeParseInt(safeGetText(controllerRemainingInput), 0));
                m.setTotal(safeParseInt(safeGetText(controllerTotalInput), 0));
                m.setLastPurchased(safeGetText(controllerLastPurchasedInput));
                m.setExpires(safeGetText(controllerExpiresInput));
                m.setReportedBy(getReportedByFromToggle(controllerReportedByToggle));
            }
        } catch (Exception e) {
            return null;
        }
        return m;
    }

    private String safeGetText(TextInputEditText input) {
        CharSequence cs = input.getText();
        return cs == null ? "" : cs.toString().trim();
    }

    private int safeParseInt(String s, int fallback) {
        try {
            if (s == null || s.isEmpty()) return fallback;
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private String getReportedByFromToggle(MaterialButtonToggleGroup toggle) {
        int checked = toggle.getCheckedButtonId();
        View checkedChild = findViewById(checked);
        if (checkedChild == null) return "";

        return checkedChild.getTag().toString();
    }
}

