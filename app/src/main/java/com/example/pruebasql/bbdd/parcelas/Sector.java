package com.example.pruebasql.bbdd.parcelas;

import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.List;

public class Sector {
    public int id_sector;

    public int id_parcela;
    public int IdUsuario;
    public ArrayList<CoordenadasSector> coordenadasSector;

    public ArrayList<CoordenadasSector> ampliacion;

    public Sector(int id_parcela, int idUsuario) {
        this.id_parcela = id_parcela;
        IdUsuario = idUsuario;
    }

    public ArrayList<LatLng> getLatLong(){
        ArrayList<LatLng> puntos = new ArrayList<>();
        for (CoordenadasSector coordenada: coordenadasSector){
            puntos.add(new LatLng(coordenada.latitude,coordenada.longitude));
        }
        return puntos;
    }

    public List<LatLng> getPuntosLatLong(){
        ArrayList<LatLng> puntos = new ArrayList<>();
        for (CoordenadasSector coordenada: coordenadasSector){
            puntos.add(new LatLng(coordenada.latitude,coordenada.longitude));
        }
        return puntos;
    }
}
