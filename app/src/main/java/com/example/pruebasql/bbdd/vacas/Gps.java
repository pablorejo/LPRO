package com.example.pruebasql.bbdd.vacas;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Gps {

    public static final String NORMAL = "normal";
    public static final String PASTANDO = "pastando";
    public static final String CAMINANDO = "caminando";
    public static final String DESCANSANDO = "descansando";
    private int id_vaca_gps;
    private int Numero_pendiente;
    private double longitude;
    private double latitude;
    private Date fecha;
    private int id_parcela;

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
        return longitude;
    }

    public void setLongitud(double longitud) {
        this.longitude = longitud;
    }

    // Getter y Setter para latitud
    public double getLatitud() {
        return latitude;
    }

    public void setLatitud(double latitud) {
        this.latitude = latitud;
    }

    // Getter y Setter para fecha
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }
}

