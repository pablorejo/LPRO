package com.example.pruebasql;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;


import org.threeten.bp.format.DateTimeFormatter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
public class BarraSuperior extends AppCompatActivity {

    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barra_superior);
    }

    protected void configureToolbar() {
        Button btnAtras = findViewById(R.id.Atras);
        if (btnAtras != null) {
            btnAtras.setOnClickListener(v -> {
                finish();
            });
        }
    }



    public void iniciarActividad(Class<?> appCompatActivity){
        Intent intent = new Intent(getApplicationContext(), appCompatActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
        startActivity(intent);
    }


    public void openDialog(TextView text){
        Calendar calendario = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                text.setText(String.valueOf(year)+"/"+String.valueOf(month)+"/"+String.valueOf(dayOfMonth));
            }
        },calendario.get(Calendar.YEAR),calendario.get(Calendar.MONTH),calendario.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    public Notification getNotification(Context context, String content) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Notificación Programada");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.user_icon);
        return builder.build();
    }

    public void scheduleNotification(Context context, Notification notification, int notificationId, LocalDate localDate) {
        Intent intent = new Intent(context, ReminderBroadcast.class);
        intent.putExtra("notification-id", notificationId);
        intent.putExtra("notification", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);


        // Convertir LocalDate a ZonedDateTime asumiendo medianoche y la zona horaria del sistema
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());

        // Convertir ZonedDateTime a Date
        Date date = Date.from(zonedDateTime.toInstant());

        // Obtener una instancia de Calendar y establecer el tiempo con el objeto Date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Verificación: imprimir el resultado
        System.out.println(calendar.getTime());
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.SECOND, 2);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Para Android 12 y versiones superiores, verifica si la aplicación puede programar alarmas exactas
                if (alarmManager.canScheduleExactAlarms()) {
                    try {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent);
                    } catch (SecurityException e) {
                        showPermissionExplanationAndGuide();
                        // Manejar la excepción, por ejemplo, guiando al usuario para que habilite el permiso en la configuración
                    }
                } else {
                    showPermissionExplanationAndGuide();
                    // La aplicación no tiene permiso, guía al usuario para habilitar el permiso en la configuración del sistema
                }
            } else {
                // Para versiones anteriores de Android, programa la alarma directamente
                try {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent);
                } catch (SecurityException e) {
                    // Manejar la excepción si es necesario
                }
            }
        }

    }

    // Suponiendo que esto está dentro de una actividad o un contexto donde puedas mostrar un diálogo o lanzar una actividad
    private void showPermissionExplanationAndGuide() {
        new AlertDialog.Builder(this)
                .setTitle("Permiso necesario")
                .setMessage("Nuestra aplicación necesita el permiso para programar alarmas exactas para funcionar correctamente. Por favor, permite este permiso en la configuración del sistema.")
                .setPositiveButton("Abrir configuración", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Intent para abrir la pantalla de configuración específica para el permiso exacto de alarma
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


}