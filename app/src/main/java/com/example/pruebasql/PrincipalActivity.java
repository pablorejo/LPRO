package com.example.pruebasql;


import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.pruebasql.automatizacion.Automatizacion;
import com.example.pruebasql.bbdd.vacas.Enfermedad;
import com.example.pruebasql.bbdd.vacas.Vaca;
import com.example.pruebasql.calendario.Calendario;
import com.example.pruebasql.mapa.Mapa;
import com.example.pruebasql.lista_vaca.CowList;
import com.example.pruebasql.notificaciones.Notificacion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import android.Manifest;

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
            iniciarActividad(Mapa.class);
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

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {

            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isComplete()){
                    String token = task.getResult();
                    Log.d("MyFirebaseMessagingService", "Token: " + token);
                }
            }
        });
    }
}