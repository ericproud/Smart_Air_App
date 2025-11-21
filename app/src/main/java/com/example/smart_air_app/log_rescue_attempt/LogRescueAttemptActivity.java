package com.example.smart_air_app.log_rescue_attempt;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_air_app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.EnumSet;

public class LogRescueAttemptActivity extends AppCompatActivity implements LogRescueAttemptView {

    private TextInputLayout dosageInput;
    private ChipGroup chipGroupTriggers;
    private TextView chipGroupTriggersError;
    private ChipGroup chipGroupSymptoms;
    private TextView chipGroupSymptomsError;
    private TextInputLayout peakFlowBeforeInput;
    private TextInputLayout peakFlowAfterInput;
    private MaterialButtonToggleGroup triageIncidentToggleButton;

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

        LogRescueAttemptPresenter presenter = new LogRescueAttemptPresenterImpl(this);

        dosageInput = findViewById(R.id.dosageInput);
        chipGroupTriggers = findViewById(R.id.chipGroupTriggers);
        chipGroupTriggersError = findViewById(R.id.chipGroupTriggersError);
        chipGroupSymptoms = findViewById(R.id.chipGroupSymptoms);
        chipGroupSymptomsError = findViewById(R.id.chipGroupSymptomsError);
        peakFlowBeforeInput = findViewById(R.id.peakFlowBeforeInput);
        peakFlowAfterInput = findViewById(R.id.peakFlowAfterInput);
        triageIncidentToggleButton = findViewById(R.id.triageIncidentToggleButton);
        Button submit = findViewById(R.id.submitRescueAttemptButton);

        // default check no
        triageIncidentToggleButton.check(triageIncidentToggleButton.getChildAt(1).getId());

        bindChipsToEnums();

        submit.setOnClickListener(button -> {
            var dosageText = dosageInput.getEditText().getText();
            var peakFlowBeforeText = peakFlowBeforeInput.getEditText().getText();
            var peakFlowAfterText = peakFlowAfterInput.getEditText().getText();

            double dosage = dosageText.isEmpty() ? -1 : Double.parseDouble(dosageText.toString());
            EnumSet<Trigger> triggers = EnumSet.noneOf(Trigger.class);
            EnumSet<Symptom> symptoms = EnumSet.noneOf(Symptom.class);
            double peakFlowBefore = peakFlowBeforeText.isEmpty() ? -1 : Double.parseDouble(peakFlowBeforeText.toString());
            double peakFlowAfter = peakFlowAfterText.isEmpty() ? -1 : Double.parseDouble(peakFlowAfterText.toString());
            boolean triageIncident;

            for (int i = 0; i < chipGroupTriggers.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupTriggers.getChildAt(i);
                if (chip.isChecked()) {
                    triggers.add((Trigger) chip.getTag());
                }
            }

            for (int i = 0; i < chipGroupSymptoms.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupSymptoms.getChildAt(i);
                if (chip.isChecked()) {
                    symptoms.add((Symptom) chip.getTag());
                }
            }

            int id = triageIncidentToggleButton.getCheckedButtonId();
            MaterialButton selectedButton = findViewById(id);
            triageIncident = selectedButton.getText().toString().equalsIgnoreCase("yes");

            presenter.onSubmitPressed(dosage, triggers, symptoms, peakFlowBefore, peakFlowAfter, triageIncident);
        });
    }

    private void bindChipsToEnums() {
        // Triggers
        chipGroupTriggers.findViewById(R.id.chipExercise).setTag(Trigger.EXERCISE);
        chipGroupTriggers.findViewById(R.id.chipPollen).setTag(Trigger.POLLEN);
        chipGroupTriggers.findViewById(R.id.chipColdAir).setTag(Trigger.COLD_AIR);
        chipGroupTriggers.findViewById(R.id.chipDust).setTag(Trigger.DUST);
        chipGroupTriggers.findViewById(R.id.chipSmoke).setTag(Trigger.SMOKE);
        chipGroupTriggers.findViewById(R.id.chipStress).setTag(Trigger.STRESS);
        chipGroupTriggers.findViewById(R.id.chipOtherTrigger).setTag(Trigger.OTHER);

        // Symptoms
        chipGroupSymptoms.findViewById(R.id.chipShortness).setTag(Symptom.SHORTNESS_OF_BREATH);
        chipGroupSymptoms.findViewById(R.id.chipChestTightness).setTag(Symptom.CHEST_TIGHTNESS);
        chipGroupSymptoms.findViewById(R.id.chipChestPain).setTag(Symptom.CHEST_PAIN);
        chipGroupSymptoms.findViewById(R.id.chipWheezing).setTag(Symptom.WHEEZING);
        chipGroupSymptoms.findViewById(R.id.chipTroubleSleeping).setTag(Symptom.TROUBLE_SLEEPING);
        chipGroupSymptoms.findViewById(R.id.chipCoughing).setTag(Symptom.COUGHING);
        chipGroupSymptoms.findViewById(R.id.chipOtherSymptom).setTag(Symptom.OTHER);
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

    }
}
