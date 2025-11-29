package com.example.smart_air_app.utils;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;

import com.example.smart_air_app.StartScreen;
import com.google.firebase.auth.FirebaseAuth;

public class Logout {
    public static void logout(Activity activity) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(activity, StartScreen.class);
        activity.startActivity(intent);
    }
}
