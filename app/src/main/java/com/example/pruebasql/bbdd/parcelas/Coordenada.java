package com.example.pruebasql.bbdd.parcelas;

import com.google.android.gms.maps.model.LatLng;

public class Coordenada{

    public double latitude;
    public double longitude;


    public Coordenada(LatLng punto){
        this.setPosition(punto);
    }

    public LatLng getPosition(){
        return new LatLng(latitude,longitude);
    }

    public void setPosition(LatLng punto){
        this.longitude = punto.longitude;
        this.latitude = punto.latitude;
    }
}
