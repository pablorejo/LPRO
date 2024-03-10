package com.example.pruebasql.listeners;

import com.example.pruebasql.bbdd.vacas.Vaca;

import java.util.ArrayList;

public interface VacaResponseListener {
    void onResponse(Vaca vaca);

    void onError(String mensaje);
}
