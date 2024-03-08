package com.example.pruebasql.listeners;

import com.example.pruebasql.bbdd.vacas.Enfermedad;
import com.example.pruebasql.bbdd.vacas.Vaca;

import java.util.ArrayList;

public interface EnfermedadesResponseListener {
    void onResponse(ArrayList<Enfermedad> enfermedades);

    void onResponse(Enfermedad enfermedad);


    void onError(String mensaje);
}
