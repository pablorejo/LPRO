package com.example.pruebasql;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;


import com.example.pruebasql.bbdd.Usuario;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
public class BarraSuperior extends AppCompatActivity {

    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    protected Usuario usuario = DataManager.getInstance().getUsuario();
    private String pattern = "yyyy-MM-dd HH:mm:ss"; // Define el patrón del formato de fecha
    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    protected  ActivityResultLauncher<Intent> miActivityResultLauncher;

    protected static final int POST_NOTIFICATIONS_REQUEST_CODE = 1; // Definir un valor constante para el requestCode

    private boolean permisoNotificaciones = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barra_superior);
        miActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        actualizar(data);
                        // Maneja el resultado OK aquí
                    }
                }
            }
        );
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        super.finish();
    }

    protected void actualizar(Intent data){
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
                text.setText(String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(dayOfMonth));
            }
        },calendario.get(Calendar.YEAR),calendario.get(Calendar.MONTH),calendario.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    // Notificaciones //////////////////////////////////////

    /**
     * Esta funcion crea una notificacion programada para una fecha en concreto.
     * @param notificacion: clase notificicacion que contiene el titulo el texto la fecha y el id de la notificacion
     */
    public void crearNotificacion(Notificacion notificacion){

        if (permisoNotificaciones){
            // Paso 4
            Intent intent = new Intent(this, ReminderBroadcast.class);
            intent.putExtra("tituloNotificacion",notificacion.titulo);
            intent.putExtra("textoNotificacion",notificacion.texto);
            intent.putExtra("idNotificacion",notificacion.idNotificacion);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            // Establece el momento exacto para la notificación
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(notificacion.date);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    System.out.println("No tienes permisos para las notificaciones");
                }
            }
        }else{
            requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, POST_NOTIFICATIONS_REQUEST_CODE);
        }
    }

    /**
     * Esta funcion nos sirve para cancelar una notificacion.
     * @param notificacion
     */
    public void cancelarNotificacion(Notificacion notificacion){

        // Paso 4
        Intent intent = new Intent(this, ReminderBroadcast.class);
        intent.putExtra("tituloNotificacion",notificacion.titulo);
        intent.putExtra("textoNotificacion",notificacion.texto);
        intent.putExtra("idNotificacion",notificacion.idNotificacion);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == POST_NOTIFICATIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes enviar la notificación
                permisoNotificaciones = true;
            } else {
                // Permiso no concedido, maneja esta situación
            }
        }
    }

}