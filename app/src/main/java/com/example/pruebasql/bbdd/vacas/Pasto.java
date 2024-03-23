package com.example.pruebasql.bbdd.vacas;

import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Pasto {
    private int id_vaca_pasto;
    private int Numero_pendiente;
    private int dias_de_pasto;
    private LocalDate mes_de_pastore;
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Constructor
    public Pasto(int idVacaPasto,
                 int numeroPendiente,
                 int dias_de_pasto,
                 LocalDate fechaPasto) {
        this.id_vaca_pasto = idVacaPasto;
        this.Numero_pendiente = numeroPendiente;
        this.mes_de_pastore = fechaPasto;
        this.dias_de_pasto = dias_de_pasto;
    }

    // Metodos
    // Getters
    public int getIdVacaPasto() {
        return id_vaca_pasto;
    }

    public int getNumeroPendiente() {
        return Numero_pendiente;
    }

    public LocalDate getMesDePastore() {
        return mes_de_pastore;
    }

    // Setters
    public void setIdVaca(int idVacaPasto) {
        this.id_vaca_pasto = idVacaPasto;
    }

    public void setNumeroPendiente(int numeroPendiente) {
        this.Numero_pendiente = numeroPendiente;
    }

    public void setMesDePastore(LocalDate MesDePastore) {
        this.mes_de_pastore = MesDePastore;
    }
}

