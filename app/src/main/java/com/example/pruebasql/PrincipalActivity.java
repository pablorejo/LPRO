package com.example.pruebasql;


import android.os.Bundle;
import android.widget.TextView;

import com.example.pruebasql.automatizacion.Automatizacion;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.calendario.Calendario;
import com.example.pruebasql.mapa.CowFinder;
import com.example.pruebasql.lista_vaca.CowList;

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
    }
}