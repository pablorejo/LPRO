package com.example.pruebasql.listeners;

import com.example.pruebasql.bbdd.vacas.Vaca;

import java.util.ArrayList;

public interface VacasResponseListener {
    void onResponse(ArrayList<Vaca> listaVacas);

    void onError(String mensaje);
}
