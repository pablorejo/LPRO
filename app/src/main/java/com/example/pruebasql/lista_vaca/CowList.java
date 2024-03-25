package com.example.pruebasql.lista_vaca;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
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
    private void filtrarVacas(String query) {
        ArrayList<Vaca> vacasFiltradas = new ArrayList<>();
        Usuario usuario = DataManager.getInstance().getUsuario();

        for (Vaca vaca : usuario.getVacas()) {
            if (String.valueOf(vaca.getNumeroPendiente()).toLowerCase().contains(query)) {
                vacasFiltradas.add(vaca);
            }
        }

        // Actualizar la lista de vacas mostradas en la pantalla con los resultados del filtrado
        mostrarTodasLasVacas(vacasFiltradas);
    }

    private void mostrarTodasLasVacas(ArrayList<Vaca> vacas) {
        // Limpiar la lista actual de vacas mostradas en la pantalla
        linearLayout.removeAllViews();

        // Mostrar todas las vacas en la lista
        for (Vaca vaca : vacas) {
            crearCowItem(vaca);
        }
    }
}