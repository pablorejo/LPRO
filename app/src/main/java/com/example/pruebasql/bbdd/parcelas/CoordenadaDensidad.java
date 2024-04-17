package com.example.pruebasql.bbdd.parcelas;

import com.google.android.gms.maps.model.LatLng;

public class CoordenadaDensidad extends Coordenada{
    public double densidad;
    public boolean anomalia;
    public CoordenadaDensidad(LatLng punto) {
        super(punto);
    }
}
