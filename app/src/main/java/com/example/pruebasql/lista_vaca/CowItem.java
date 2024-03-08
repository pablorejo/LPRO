package com.example.pruebasql.lista_vaca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.R;

public class CowItem extends BarraSuperior {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_item);
        configureToolbar();

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre_vaca");

        TextView nombreVaca = findViewById(R.id.nombreVaca);
        nombreVaca.setText(nombre);
    }
}