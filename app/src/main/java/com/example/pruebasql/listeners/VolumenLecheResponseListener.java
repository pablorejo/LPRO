package com.example.pruebasql.listeners;

import com.example.pruebasql.bbdd.vacas.Leite;

import java.util.ArrayList;

public interface VolumenLecheResponseListener {
    void onResponse(Leite leite);
    
    void onError(String mensaje);
}
