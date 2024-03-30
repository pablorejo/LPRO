package com.example.pruebasql;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.pruebasql.automatizacion.Automatizacion;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.vacas.Enfermedad;
import com.example.pruebasql.bbdd.vacas.Vaca;
import com.example.pruebasql.calendario.Calendario;
import com.example.pruebasql.mapa.CowFinder;
import com.example.pruebasql.lista_vaca.CowList;

import java.util.Calendar;
import java.util.Locale;
import android.Manifest;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.threeten.bp.LocalDate;

public class PrincipalActivity extends BarraSuperior {
    TextView btnCowList, btnCowFinder, btnContact, btnAutonomization, btnCalendario;

    private NotificationManager notificationManager;

    private int notificationId = 1;

    private NotificationCompat.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        configureToolbar(); //Para hacer que funcione el boton atras

        // Vinculamos variables con los controles del layout
        btnCowList=findViewById(R.id.idCowList);
        btnCowFinder=findViewById(R.id.idCowFinder);
        btnContact =findViewById(R.id.idContactar);
        btnAutonomization =findViewById(R.id.idAutomatizacion);
        btnCalendario = findViewById(R.id.idCalendario);


        btnCowList.setOnClickListener(view -> {
            iniciarActividad(CowList.class);
        });

        btnCowFinder.setOnClickListener(view -> {
            iniciarActividad(CowFinder.class);
        });

        btnContact.setOnClickListener(view -> {
            iniciarActividad(Contactar.class);
        });

        btnAutonomization.setOnClickListener(view -> {
            iniciarActividad(Automatizacion.class);
        });

        btnCalendario.setOnClickListener(view -> {
            iniciarActividad(Calendario.class);
        });

        // Comprobamos que nos dejan hacer notificaciones, esto se hace una vez que se inicia sesion.
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, POST_NOTIFICATIONS_REQUEST_CODE);
        }


        int id_notificacion = 1;
        for (Vaca vaca: usuario.getVacas()){
            for (Enfermedad enfermedad: vaca.getEnfermedades()){
                for (LocalDate tomarMedicina: enfermedad.getFechasTomarMedicina()){

                    Notificacion notificacion = new Notificacion(
                            tomarMedicina,
                            "Tomar medicina",
                            "La vaca " + vaca.getNumeroPendiente() + "tiene que tomar " + enfermedad.getMedicamento(),
                            id_notificacion);
                    
                    crearNotificacion(notificacion);
                    id_notificacion = id_notificacion + 1;
                }
            }
        }
    }
}