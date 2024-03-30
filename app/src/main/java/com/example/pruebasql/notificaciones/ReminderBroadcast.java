package com.example.pruebasql.notificaciones;

import android.app.Notification;
import android.app.NotificationChannel;
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

import com.example.pruebasql.R;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String tituloNotificacion = intent.getStringExtra("tituloNotificacion");
            String textoNotificacion = intent.getStringExtra("textoNotificacion");
            int idNotificacion = intent.getIntExtra("idNotificacion",1);

            String channelId = context.getString(R.string.channel_id);
            CharSequence channelName = context.getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.icono)
                    .setContentTitle(tituloNotificacion)
                    .setContentText(textoNotificacion)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            notificationManager.notify(idNotificacion, builder.build());
        }
    }
}
