package com.example.pruebasql;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Actualización para API 33+
        Notification notification = intent.getParcelableExtra("notification", Notification.class);
        int id = intent.getIntExtra("notification-id", 0);
        if (notificationManager != null) {
            notificationManager.notify(id, notification);
        }
    }
}
