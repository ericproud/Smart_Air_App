package com.example.smart_air_app.utils;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.smart_air_app.R;

public class NotificationUtils {
    private static final String CHANNEL_ID = "alerts_channel";
    private static boolean channelCreated = false;

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;
    private static final String PREFS_NAME = "notification_prefs";
    private static final String PREF_PERMISSION_REQUESTED = "permission_requested";

    public static void show(Context context, String title, String message) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.user)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public static void requestNotificationPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            // Only request permission once
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean requestedBefore = prefs.getBoolean(PREF_PERMISSION_REQUESTED, false);

            if (!requestedBefore && context instanceof Activity) {
                // Save that we requested
                prefs.edit().putBoolean(PREF_PERMISSION_REQUESTED, true).apply();

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }

    }

    private static void createNotificationChannel(Context context) {
        if (channelCreated) return;

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "App Alerts", NotificationManager.IMPORTANCE_HIGH);

        channel.setDescription("Notifications for database events");
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        channelCreated = true;
    }
}
