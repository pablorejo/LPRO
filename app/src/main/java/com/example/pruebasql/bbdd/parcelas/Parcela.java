package com.example.pruebasql.bbdd.parcelas;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

public class Parcela {

    private int id_parcela;
    private String nombre_parcela;
    private List<Coordenada> coordenadas;

    // Calcula el área del polígono en metros cuadrados

    public Parcela(List<Coordenada> coordenadas,String nombre_parcela) {
        this.coordenadas = coordenadas;
        this.nombre_parcela = nombre_parcela;
    }

    public String getNombre(){ return this.nombre_parcela;}
    public void setNombre(String nombre) { this.nombre_parcela = nombre;}

    // Getter para las coordenadas
    public List<Coordenada> getCoordenadas() {
        return coordenadas;
    }

    // Setter para las coordenadas
    public void setCoordenadas(List<Coordenada> coordenadas) {
        this.coordenadas = coordenadas;
        // Cada vez que se actualizan las coordenadas, recalcula el área
    }



    // Método para calcular el área de la parcela
    public double getArea() {
        if (coordenadas != null && !coordenadas.isEmpty()) {
            return SphericalUtil.computeArea(getPuntosLatLong());
        } else {
            return 0;
        }
    }

    public List<LatLng> getPuntosLatLong(){
        ArrayList<LatLng> puntos = new ArrayList<>();
        for (Coordenada coordenada: coordenadas){
            puntos.add(new LatLng(coordenada.latitude,coordenada.longitude));
        }
        return puntos;
    }

    public void setPuntosLatLong(List<LatLng> puntos){
        for (int k = 0; k < coordenadas.size(); k++){
            coordenadas.get(k).setPosition(puntos.get(k));
        }
    }

    // Método para añadir una coordenada a la lista
    public void addCoordenada(Coordenada coordenada) {
        this.coordenadas.add(coordenada);
        // No es necesario recalcular el área aquí, ya que se calcula en tiempo real en getArea()
    }

    public int getId(){ return this.id_parcela; }
}
