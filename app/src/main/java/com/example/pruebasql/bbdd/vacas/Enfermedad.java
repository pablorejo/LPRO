package com.example.pruebasql.bbdd.vacas;

import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Enfermedad {
    private int id_enfermedad_vaca;
    private int Numero_pendiente;
    private String Medicamento;
    private String Enfermedad;
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    private String nota;
    private int periocidad_en_dias;
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Enfermedad(
            int id_enfermedad_vaca,
            int Numero_pendiente,
            String Medicamento,
            String Enfermedad,
            LocalDate fecha_inicio,
            LocalDate fecha_fin,
            int periodicidadEnDias,
            String nota
            ){
        this.id_enfermedad_vaca = id_enfermedad_vaca;
        this.Numero_pendiente = Numero_pendiente;
        this.Medicamento = Medicamento;
        this.Enfermedad = Enfermedad;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.periocidad_en_dias = periodicidadEnDias;
        this.nota = nota;
    }

    // Getters y Setters
    public LocalDate getFechaInicio(){
        return this.fecha_inicio;
    }

    public LocalDate getFechaFin(){
        return this.fecha_fin;
    }

    public int getNumero_pendiente() {return this.Numero_pendiente;}

    public String getEnfermedad() {return  this.Enfermedad;}
}
