package com.example.pruebasql;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.pruebasql.bbdd.Usuario;

public class Contactar extends BarraSuperior {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactar);
        configureToolbar();

        Button enviar = findViewById(R.id.enviar);
        EditText textoEnviar = findViewById(R.id.textoEnviar);
        Usuario usuario = getIntent().getParcelableExtra("usuario");

        enviar.setOnClickListener(view -> System.out.println(textoEnviar.getText().toString()));

    }
}