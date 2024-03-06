package com.example.pruebasql;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.pruebasql.bbdd.Usuario;

public class PrincipalActivity extends BarraSuperior {

    TextView btnCowList, btnCowFinder, btnContact, btnAutonomization;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        configureToolbar(); //Para hacer que funcione el boton atras

        Usuario usuario = getIntent().getParcelableExtra("usuario");
        // Vinculamos variables con los controles del layout
        btnCowList=findViewById(R.id.idCowList);
        btnCowFinder=findViewById(R.id.idCowFinder);
        btnContact =findViewById(R.id.idContactar);
        btnAutonomization =findViewById(R.id.idAutomatizacion);


        btnCowList.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CowList.class);
            intent.putExtra("usuario", usuario);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
            startActivity(intent);
        });

        btnCowFinder.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CowFinder.class);
            intent.putExtra("usuario", usuario);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
            startActivity(intent);
        });

        btnContact.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Contactar.class);
            intent.putExtra("usuario", usuario);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
            startActivity(intent);
        });

        btnAutonomization.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Automatizacion.class);
            intent.putExtra("usuario", usuario);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
            startActivity(intent);
        });
    }
}