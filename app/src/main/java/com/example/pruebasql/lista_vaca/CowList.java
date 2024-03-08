package com.example.pruebasql.lista_vaca;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.R;
import com.example.pruebasql.Server;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.vacas.Vaca;

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

        Server server = new Server(this);
        server.getVacas(usuario.getId(), new VacaResponseListener() {
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
        });
    }


    protected void crearCowItem(String text){
        View newLayout = LayoutInflater.from(this).inflate(R.layout.list_cow, linearLayout, false);
        TextView textView = newLayout.findViewById(R.id.idTextViewCowItemList);

        textView.setText(text);

        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CowItem.class);
            intent.putExtra("nombre_vaca", text);
            startActivity(intent);
        });
        linearLayout.addView(newLayout);
    }
}