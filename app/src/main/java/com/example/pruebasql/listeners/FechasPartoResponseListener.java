package com.example.pruebasql.listeners;

import com.example.pruebasql.bbdd.vacas.Parto;

import java.util.ArrayList;

public interface FechasPartoResponseListener {
    void onResponse(ArrayList<Parto> partos);


    void onError(String mensaje);
}
