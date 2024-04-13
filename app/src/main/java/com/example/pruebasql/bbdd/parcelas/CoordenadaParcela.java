package com.example.pruebasql.bbdd.parcelas;

import com.google.android.gms.maps.model.LatLng;

public class CoordenadaParcela extends Coordenada{

    public int id_esquina;
    public int id_parcela;

    public Sector sector;

    public CoordenadaParcela(LatLng punto) {
        super(punto);
    }

}
