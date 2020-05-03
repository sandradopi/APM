package com.example.findmyrhythm.Model.Utils;

import android.util.Patterns;
import android.widget.EditText;

public class GenericUtils {

    public static boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() <= 0;
    }

    public static boolean isValidEmail(EditText email) {
        return Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
    }
}
