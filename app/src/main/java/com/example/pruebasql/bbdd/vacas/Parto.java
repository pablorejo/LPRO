package com.example.pruebasql.bbdd.vacas;

import java.util.Date;

import java.util.Date;

public class Parto {
    private int id_vaca_parto;
    private int numeroPendiente;
    private Date fechaParto;

    // Constructor
    public Parto(int id_vaca_parto, int numeroPendiente, Date fechaParto) {
        this.id_vaca_parto = id_vaca_parto;
        this.numeroPendiente = numeroPendiente;
        this.fechaParto = fechaParto;
    }

    // Getters
    public int getId_vaca_parto() {
        return id_vaca_parto;
    }

    public int getNumeroPendiente() {
        return numeroPendiente;
    }

    public Date getFechaParto() {
        return fechaParto;
    }

    // Setters
    public void setId_vaca_parto(int id_vaca_parto) {
        this.id_vaca_parto = id_vaca_parto;
    }

    public void setNumeroPendiente(int numeroPendiente) {
        this.numeroPendiente = numeroPendiente;
    }

    public void setFechaParto(Date fechaParto) {
        this.fechaParto = fechaParto;
    }
}

