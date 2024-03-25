package com.example.pruebasql;


import android.app.AlarmManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.example.pruebasql.automatizacion.Automatizacion;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.calendario.Calendario;
import com.example.pruebasql.mapa.CowFinder;
import com.example.pruebasql.lista_vaca.CowList;

import java.time.LocalDate;
import java.util.Locale;

public class PrincipalActivity extends BarraSuperior {
    private Usuario usuario;
    TextView btnCowList, btnCowFinder, btnContact, btnAutonomization, btnCalendario;
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
        // Obtener una instancia de AlarmManager del sistema
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        /*
        if (alarmManager != null) {
            // Verificar si la aplicación tiene permiso para programar alarmas exactas
            boolean hasPermission = alarmManager.canScheduleExactAlarms();

            if (hasPermission) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            } else {
                // La aplicación no tiene permiso, manejar esta situación adecuadamente
                // Por ejemplo, puedes pedir al usuario que vaya a la configuración del sistema y otorgue el permiso.
            }
        }*/


        LocalDate date = LocalDate.now();

        Notification notification = getNotification(this, "¡Es hora de tu evento!");
        int notificationId = 1; // Identificador único para cada notificación
        scheduleNotification(this, notification, notificationId, date);

    }
}