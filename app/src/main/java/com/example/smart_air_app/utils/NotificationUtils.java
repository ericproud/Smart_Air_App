package com.example.smart_air_app.utils;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.smart_air_app.R;


public class NotificationUtils {

    private static final String CHANNEL_ID = "alerts_channel";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 101;

    /**
     * Call this before posting notifications.
     * It creates the channel if needed and requests permission on Android 13+.
     */
    public static void ensurePermissionAndChannel(Context context) {
        createNotificationChannel(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean permissionGranted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED;

            boolean notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled();

            if (!permissionGranted || !notificationsEnabled) {
                if (context instanceof Activity) {
                    ActivityCompat.requestPermissions(
                            (Activity) context,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            REQUEST_NOTIFICATION_PERMISSION
                    );
                } else {

                }
            }
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "App Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for database events");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel); // safe to call multiple times
        }
    }

    /**
     * Safely show a notification.
     */
    public static void show(Context context, String title, String message) {
        ensurePermissionAndChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.user)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    /**
     * Optional: check if notifications are disabled on this device.
     */
    public static boolean areNotificationsEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }
}
