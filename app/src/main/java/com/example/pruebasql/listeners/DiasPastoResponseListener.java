package com.example.pruebasql.listeners;

import com.example.pruebasql.bbdd.vacas.Pasto;

public interface DiasPastoResponseListener {
    void onResponse(Pasto pasto);
    
    void onError(String mensaje);
}
