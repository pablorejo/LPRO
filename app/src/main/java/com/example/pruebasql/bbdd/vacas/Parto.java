package com.example.pruebasql.bbdd.vacas;

import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Parto {
    private int id_vaca_parto;
    private int Numero_pendiente;
    private LocalDate fecha_parto;

    private int idNumeroPendienteMadre;

    private String nota;
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Constructor
    public Parto(int id_vaca_parto, int numeroPendiente, LocalDate fechaParto, String nota) {
        this.id_vaca_parto = id_vaca_parto;
        this.Numero_pendiente = numeroPendiente;
        this.fecha_parto = fechaParto;
        this.nota = nota;
    }

    // Metodos
    public LocalDate getFechaParto(){
        return this.fecha_parto;
    }

    public int getNumeroPendiente(){ return this.Numero_pendiente; }

    public int getId_vaca_parto() {
        return id_vaca_parto;
    }

    public String getNota() {
        return nota;
    }
}

