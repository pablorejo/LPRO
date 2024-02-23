package com.example.pruebasql;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PrincipalActivity extends BarraSuperior {

    TextView btnCowList, btnCowFinder, btnContact, btnAutonomization;
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


        btnCowList.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CowList.class);
            startActivity(intent);
        });

        btnCowFinder.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CowFinder.class);
            startActivity(intent);
        });

        btnContact.setOnClickListener(view -> {
                Intent intent = new Intent(getApplicationContext(), Contactar.class);
                startActivity(intent);
        });

        btnAutonomization.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Automatizacion.class);
            startActivity(intent);
        });
    }
}