package com.example.pruebasql.lista_vaca;

import com.example.pruebasql.bbdd.vacas.Vaca;

import java.util.ArrayList;

public interface VacaResponseListener {
    void onResponse(ArrayList<Vaca> listaVacas);
    void onError(String mensaje);
}
