package com.example.pruebasql;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pruebasql.bbdd.Usuario;

public class PrincipalActivity extends BarraSuperior {
    private Usuario usuario;
    TextView btnCowList, btnCowFinder, btnContact, btnAutonomization, btnCalendario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        configureToolbar(); //Para hacer que funcione el boton atras

        usuario = getIntent().getParcelableExtra("usuario");
        // Vinculamos variables con los controles del layout
        btnCowList=findViewById(R.id.idCowList);
        btnCowFinder=findViewById(R.id.idCowFinder);
        btnContact =findViewById(R.id.idContactar);
        btnAutonomization =findViewById(R.id.idAutomatizacion);
        btnCalendario = findViewById(R.id.idCalendario);


        btnCowList.setOnClickListener(view -> {
            iniciarActividad(CowList.class,usuario);
        });

        btnCowFinder.setOnClickListener(view -> {
            iniciarActividad(CowFinder.class,usuario);
        });

        btnContact.setOnClickListener(view -> {
            iniciarActividad(Contactar.class,usuario);
        });

        btnAutonomization.setOnClickListener(view -> {
            iniciarActividad(Automatizacion.class,usuario);
        });

        btnCalendario.setOnClickListener(view -> {
            iniciarActividad(Calendario.class,usuario);
        });
    }


}