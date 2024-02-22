package com.example.pruebasql;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Automatizacion extends BarraSuperior {
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatizacion);
        configureToolbar();
        linearLayout = findViewById(R.id.idLinearLayout);

        crearAutomatizadorItem("Puerta 1");
        crearAutomatizadorItem("Puerta 2");
        crearAutomatizadorItem("Puerta 3");
        crearAutomatizadorItem("Puerta 4");
    }

    protected void crearAutomatizadorItem(String text){
        View newLayout = LayoutInflater.from(this).inflate(R.layout.automatizacion_item, linearLayout, false);
        TextView textView = newLayout.findViewById(R.id.idTextAutomatizacionItemList);

        textView.setText(text);
        linearLayout.addView(newLayout);
    }
}