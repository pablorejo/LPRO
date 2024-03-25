package com.example.pruebasql.bbdd;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

public class Parcela {

    private int id;
    private String nombre;
    private List<LatLng> coordenadas;

    // Calcula el área del polígono en metros cuadrados

    public Parcela(List<LatLng> coordenadas,String nombre) {
        this.coordenadas = coordenadas;
        this.nombre = nombre;
    }

    public String getNombre(){ return this.nombre;}
    public void setNombre(String nombre) { this.nombre = nombre;}

    // Getter para las coordenadas
    public List<LatLng> getCoordenadas() {
        return coordenadas;
    }

    // Setter para las coordenadas
    public void setCoordenadas(ArrayList<LatLng> coordenadas) {
        this.coordenadas = coordenadas;
        // Cada vez que se actualizan las coordenadas, recalcula el área
    }

    // Método para calcular el área de la parcela
    public double getArea() {
        if (coordenadas != null && !coordenadas.isEmpty()) {
            return SphericalUtil.computeArea(coordenadas);
        } else {
            return 0;
        }
    }

    // Método para añadir una coordenada a la lista
    public void addCoordenada(LatLng coordenada) {
        this.coordenadas.add(coordenada);
        // No es necesario recalcular el área aquí, ya que se calcula en tiempo real en getArea()
    }


    public int getId(){ return this.id; }
}
