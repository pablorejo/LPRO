package com.example.pruebasql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.pruebasql.bbdd.Usuario;

public class BarraSuperior extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barra_superior);
    }

    protected void configureToolbar() {
        Button btnAtras = findViewById(R.id.Atras);
        if (btnAtras != null) {
            btnAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    public void iniciarActividad(Class<?> appCompatActivity, Usuario usuario){
        Intent intent = new Intent(getApplicationContext(), appCompatActivity);
        intent.putExtra("usuario", usuario);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
        startActivity(intent);
    }
}