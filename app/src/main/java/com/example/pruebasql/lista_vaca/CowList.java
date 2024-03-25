package com.example.pruebasql.lista_vaca;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.DataManager;
import com.example.pruebasql.R;
import com.example.pruebasql.Server;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.vacas.Vaca;
import com.example.pruebasql.listeners.VacaResponseListener;
import com.example.pruebasql.listeners.VacasResponseListener;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;

public class CowList extends BarraSuperior {

    LinearLayout linearLayout;

    Button btnAñadirVaca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_list);

        linearLayout = findViewById(R.id.idLinearLayout);
        configureToolbar();
        Usuario usuario = DataManager.getInstance().getUsuario();

        for (Vaca vaca: usuario.getVacas()){
            crearCowItem(vaca);
        }

        btnAñadirVaca = findViewById(R.id.btnAñadirVaca);
        btnAñadirVaca.setOnClickListener(v -> {
            Vaca vaca = new Vaca(0, LocalDate.now(),"",0);
            usuario.addVaca(vaca);
            Intent intent = new Intent(getApplicationContext(), CowItem.class);
            intent.putExtra("numero_pendiente", String.valueOf(vaca.getNumeroPendiente()));
            startActivity(intent);
        });
    }


    protected void crearCowItem(Vaca vaca){
        View newLayout = LayoutInflater.from(this).inflate(R.layout.list_cow, linearLayout, false);
        TextView textViewNumeroPendiente = newLayout.findViewById(R.id.idTextViewCowItemList);

        String numeroPendiente = String.valueOf(vaca.getNumeroPendiente());
        textViewNumeroPendiente.setText(numeroPendiente);

        textViewNumeroPendiente.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CowItem.class);
            intent.putExtra("numero_pendiente", numeroPendiente);
            startActivity(intent);
        });
        linearLayout.addView(newLayout);
    }
}