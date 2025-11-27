package com.example.smart_air_app.log_rescue_attempt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
import com.example.smart_air_app.user_classes.Child;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class LogRescueAttemptActivity extends AppCompatActivity implements LogRescueAttemptView {

    private TextInputLayout dosageInput;
    private ChipGroup chipGroupTriggers;
    private TextView chipGroupTriggersError;
    private ChipGroup chipGroupSymptoms;
    private TextView chipGroupSymptomsError;
    private TextInputLayout peakFlowBeforeInput;
    private TextInputLayout peakFlowAfterInput;
    private MaterialButtonToggleGroup triageIncidentToggleButton;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_rescue_attempt);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RescueAttemptRepository repo = new FirebaseRescueAttemptRepository();
        LogRescueAttemptPresenter presenter = new LogRescueAttemptPresenterImpl(this, repo);


        dosageInput = findViewById(R.id.dosageInput);
        chipGroupTriggers = findViewById(R.id.chipGroupTriggers);
        chipGroupTriggersError = findViewById(R.id.chipGroupTriggersError);
        chipGroupSymptoms = findViewById(R.id.chipGroupSymptoms);
        chipGroupSymptomsError = findViewById(R.id.chipGroupSymptomsError);
        peakFlowBeforeInput = findViewById(R.id.peakFlowBeforeInput);
        peakFlowAfterInput = findViewById(R.id.peakFlowAfterInput);
        triageIncidentToggleButton = findViewById(R.id.triageIncidentToggleButton);
        Button submit = findViewById(R.id.submitRescueAttemptButton);
        toolbar = findViewById(R.id.materialToolbar);

        // default check no
        triageIncidentToggleButton.check(triageIncidentToggleButton.getChildAt(1).getId());

        if (getIntent().hasExtra("childUID")) { // child logged in through parent
            String childUID = getIntent().getStringExtra("childUID");
            String childName = getIntent().getStringExtra("childName");
            repo.setUid(childUID);

            toolbar.setNavigationOnClickListener(view -> {
                Intent intent = new Intent(LogRescueAttemptActivity.this, ParentChildHomeScreen.class);
                intent.putExtra("childUID", childUID);
                intent.putExtra("childName", childName);
                startActivity(intent);
            });

        } else {
            repo.setUid(FirebaseAuth.getInstance().getUid()); // child logged in normally
            toolbar.setNavigationOnClickListener(view -> {
                Intent intent = new Intent(LogRescueAttemptActivity.this, ChildHomeScreen.class);
                startActivity(intent);
            });

        }

        submit.setOnClickListener(button -> {
            var dosageText = dosageInput.getEditText().getText();
            var peakFlowBeforeText = peakFlowBeforeInput.getEditText().getText();
            var peakFlowAfterText = peakFlowAfterInput.getEditText().getText();

            double dosage = dosageText.isEmpty() ? -1 : Double.parseDouble(dosageText.toString());
            List<String> triggers = new ArrayList<>();
            List<String> symptoms = new ArrayList<>();
            double peakFlowBefore = peakFlowBeforeText.isEmpty() ? -1 : Double.parseDouble(peakFlowBeforeText.toString());
            double peakFlowAfter = peakFlowAfterText.isEmpty() ? -1 : Double.parseDouble(peakFlowAfterText.toString());
            boolean triageIncident;

            for (int i = 0; i < chipGroupTriggers.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupTriggers.getChildAt(i);
                if (chip.isChecked()) {
                    triggers.add(chip.getText().toString());
                }
            }

            for (int i = 0; i < chipGroupSymptoms.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupSymptoms.getChildAt(i);
                if (chip.isChecked()) {
                    symptoms.add(chip.getText().toString());
                }
            }

            int id = triageIncidentToggleButton.getCheckedButtonId();
            MaterialButton selectedButton = findViewById(id);
            triageIncident = selectedButton.getText().toString().equalsIgnoreCase("yes");

            presenter.onSubmitPressed(dosage, triggers, symptoms, peakFlowBefore, peakFlowAfter, triageIncident);
        });
    }

    @Override
    public void resetErrors() {
        chipGroupSymptomsError.setText("");
        chipGroupTriggersError.setText("");
        dosageInput.setError("");
        peakFlowBeforeInput.setError("");
        peakFlowAfterInput.setError("");
    }

    @Override
    public void showDosageError(String message) {
        dosageInput.setError(message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTriggersError(String message) {
        chipGroupTriggersError.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSymptomsError(String message) {
        chipGroupSymptomsError.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPeakFlowBeforeError(String message) {
        peakFlowBeforeInput.setError(message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPeakFlowAfterError(String message) {
        peakFlowAfterInput.setError(message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTriageIncidentError(String message) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void navigateToSuccessScreen() {
        startActivity(new Intent(LogRescueAttemptActivity.this, ChildHomeScreen.class));
    }
}
