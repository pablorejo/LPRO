package com.example.pruebasql.listeners;

import com.example.pruebasql.bbdd.vacas.Enfermedad;

import java.util.ArrayList;

public interface EnfermedadResponseListener {
    void onResponse(Enfermedad enfermedad);


    void onError(String mensaje);
}
