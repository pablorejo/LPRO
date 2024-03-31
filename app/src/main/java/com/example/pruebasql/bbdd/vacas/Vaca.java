package com.example.pruebasql.bbdd.vacas;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public class Vaca {
    private int Numero_pendiente;
    private LocalDate Fecha_nacimiento;

    private ArrayList<Enfermedad> enfermedades;

    private ArrayList<Parto> partos;

    private String nota;

    private ArrayList<Leite> leiteHistorico;

    private ArrayList<Gps> datosGps;

    private int idNumeroPendienteMadre;

    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Constructor con parámetros

    public Vaca(
            int numeroPendiente,
            LocalDate fechaNacimiento,
            String nota,
            int idNumeroPendienteMadre)
    {
        this.Numero_pendiente = numeroPendiente;
        this.Fecha_nacimiento = fechaNacimiento;
        this.nota = nota;
        this.idNumeroPendienteMadre = idNumeroPendienteMadre;
    }

    // Getters y Setters
    public int getNumeroPendiente() {
        return Numero_pendiente;
    }

    public void setNumeroPendiente(int numeroPendiente) {
        this.Numero_pendiente = numeroPendiente;
    }

    public LocalDate getFechaNacimiento() {
        return Fecha_nacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.Fecha_nacimiento = fechaNacimiento;
    }

    //Arrays
    public void addEnfermedad(Enfermedad enfermedad){
        this.enfermedades.add(enfermedad);
    }

    public void setEnfermedades(ArrayList<Enfermedad> enfermedades){
        this.enfermedades = enfermedades;
    }

    public ArrayList<Enfermedad> getEnfermedades(){
        return this.enfermedades;
    }

    public void addParto(Parto parto){
        this.partos.add(parto);
    }
    public void setPartos(ArrayList<Parto> partos){
        this.partos = partos;
    }

    public ArrayList<Parto> getPartos(){
        return this.partos;
    }

    public void addLeite(Leite leite){
        this.leiteHistorico.add(leite);
    }

    public void setLeite(ArrayList<Leite> leiteHistorico){
        this.leiteHistorico = leiteHistorico;
    }

    public ArrayList<Leite> getLeite(){
        return this.leiteHistorico;
    }


    public String getNota() {return this.nota;}

    public void setNota(String nota){ this.nota = nota;}

    public List<LatLng> getCordenadasGps(){
        List<LatLng> puntos = new ArrayList<LatLng>();
        if (datosGps != null){
            for (Gps gps: datosGps) {
                LatLng cordenada = new LatLng(gps.getLatitud(),gps.getLongitud());
                puntos.add(cordenada);
            }
        }
        return puntos;
    }

    public int getIdNumeroPendienteMadre(){ return  this.idNumeroPendienteMadre;}

    public void setIdNumeroPendienteMadre(int idNumeroPendienteMadre) {
        this.idNumeroPendienteMadre = idNumeroPendienteMadre;
    }

    public List<LatLng> getDatosGpsByFechaInicioYFechaFin(Date fechaInicio, Date fechaFin){
        List<LatLng> datosGpsCoinciden = new ArrayList<>();
        if (fechaInicio == null && fechaFin == null){
            return getCordenadasGps();
        }else{
            for (Gps gps: datosGps){
                if (fechaFin == null){
                    if (!gps.getFecha().before(fechaInicio)){
                        datosGpsCoinciden.add(gps.getLatLng());
                    }
                } else if (fechaInicio == null) {
                    if (!gps.getFecha().after(fechaFin)){
                        datosGpsCoinciden.add(gps.getLatLng());
                    }
                }else if(!gps.getFecha().before(fechaInicio) && !gps.getFecha().after(fechaFin)){
                    datosGpsCoinciden.add(gps.getLatLng());
                }
            }
            return datosGpsCoinciden;
        }
    }

    public void updateEnfermedad(Enfermedad enfermedad){
        int k = 0;
        for (Enfermedad enfermedad1: enfermedades){
            if (enfermedad1.getId_enfermedad_vaca() == enfermedad.getId_enfermedad_vaca()){
                enfermedades.set(k,enfermedad);
                break;
            }
            k++;
        }
    }

    public void updateParto(Parto parto){
        int k = 0;
        for (Parto parto1: partos){
            if (parto1.getId_vaca_parto() == parto.getId_vaca_parto()){
                partos.set(k,parto);
                break;
            }
            k++;
        }
    }
}

