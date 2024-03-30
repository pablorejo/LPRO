package com.example.pruebasql.bbdd.vacas;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Gps {

    private int id_vaca_gps;
    private int Numero_pendiente;
    private double longitud;
    private double latitud;
    private Date fecha;

    // Getter y Setter para id_vaca_gps
    public int getId_vaca_gps() {
        return id_vaca_gps;
    }

    public void setId_vaca_gps(int id_vaca_gps) {
        this.id_vaca_gps = id_vaca_gps;
    }

    // Getter y Setter para Numero_pendiente
    public int getNumero_pendiente() {
        return Numero_pendiente;
    }

    public void setNumero_pendiente(int Numero_pendiente) {
        this.Numero_pendiente = Numero_pendiente;
    }

    // Getter y Setter para longitud
    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    // Getter y Setter para latitud
    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    // Getter y Setter para fecha
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public LatLng getLatLng(){
        return new LatLng(latitud,longitud);
    }
}

