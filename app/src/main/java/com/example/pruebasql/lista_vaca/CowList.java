package com.example.pruebasql.lista_vaca;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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

import java.util.ArrayList;


public class CowList extends BarraSuperior {

    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_list);

        linearLayout = findViewById(R.id.idLinearLayout);
        configureToolbar();
        Usuario usuario = getIntent().getParcelableExtra("usuario");

        // METODOS PARA VACAS:
        Server server = new Server(this);
        server.getVacas(new VacasResponseListener() {
            @Override
            public void onResponse(ArrayList<Vaca> listaVacas) {
                usuario.setVacas(listaVacas);
                for (Vaca vaca : listaVacas) {
                    crearCowItem(String.valueOf(vaca.getNumeroPendiente()));
                }
            }

            @Override
            public void onError(String mensaje) {
                System.out.println("ok");
            }
        });
        /*server.getVacas(usuario.getId(), new VacaResponseListener() {
            @Override
            public void onResponse(ArrayList<Vaca> listaVacas) {
                usuario.setVacas(listaVacas);
                for (Vaca vaca: listaVacas) {
                    crearCowItem(String.valueOf(vaca.getNumeroPendiente()));
                }
            }

            @Override
            public void onError(String mensaje) {
                System.out.println("ok");
            }
        });*/
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
    private void buscarVacaxNombre(String query) {
        Usuario usuario = getIntent().getParcelableExtra("usuario");
        linearLayout.removeAllViews(); // Limpia la lista actual de vacas

        for (Vaca vaca : usuario.getVacas()) {
            if (String.valueOf(vaca.getNumeroPendiente()).contains(query)) {
                crearCowItem(String.valueOf(vaca.getNumeroPendiente()));
            }
        }
    }

}