package com.example.pruebasql.bbdd.parcelas;

import com.google.android.gms.maps.model.LatLng;

public class CoordenadasSector extends Coordenada{
    public int id_esquina;
    public int id_parcela;
    public int id_sector;

    public CoordenadasSector(LatLng punto) {
        super(punto);
    }

}
