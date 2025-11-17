package com.example.smart_air_app.utils;

import android.graphics.Color;
import android.widget.EditText;

public class FormHelperFunctions {

    public static boolean handleEmpty(EditText inputField) {
        String text = inputField.getText().toString().trim();
        CharSequence hint = inputField.getHint();
        String currentHint = (hint != null) ? hint.toString() : "";

        if (text.isEmpty()) {
            inputField.setText("");
            inputField.setHintTextColor(Color.RED);
            return true;
        }
        return false;
    }
}
