package com.example.pruebasql.bbdd.parcelas;

import android.graphics.Color;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.maps.model.LatLng;

public class CoordenadaColor extends Coordenada{
    public int color;
    public CoordenadaColor(LatLng punto) {
        super(punto);
    }
}
