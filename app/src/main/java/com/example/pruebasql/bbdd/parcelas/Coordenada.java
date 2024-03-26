package com.example.pruebasql.bbdd.parcelas;

import com.google.android.gms.maps.model.LatLng;

public class Coordenada{
    public int id_esquina;
    public int id_parcela;
    public double latitude;
    public double longitude;

    public Coordenada(int id_esquina, int id_parcela, LatLng punto){
        this.id_esquina =id_esquina;
        this.id_parcela = id_parcela;
        this.setPosition(punto);
    }

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
