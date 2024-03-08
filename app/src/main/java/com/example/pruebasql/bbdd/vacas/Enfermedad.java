package com.example.pruebasql.bbdd.vacas;

import java.util.Date;

public class Enfermedad {
    private int idEnfermedadVaca;
    private int numeroPendiente;
    private String medicamento;
    private String enfermedad;
    private Date fechaInicio;
    private Date fechaFin;
    private int periodicidadEnDias;

    // Constructor sin parámetros
    public Enfermedad() {

    }

    // Constructor con parámetros
    public Enfermedad(int idEnfermedadVaca, int numeroPendiente, String medicamento, String enfermedad, Date fechaInicio, Date fechaFin, int periodicidadEnDias) {
        this.idEnfermedadVaca = idEnfermedadVaca;
        this.numeroPendiente = numeroPendiente;
        this.medicamento = medicamento;
        this.enfermedad = enfermedad;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.periodicidadEnDias = periodicidadEnDias;
    }

    // Getters y Setters
    public int getIdEnfermedadVaca() {
        return idEnfermedadVaca;
    }

    public void setIdEnfermedadVaca(int idEnfermedadVaca) {
        this.idEnfermedadVaca = idEnfermedadVaca;
    }

    public int getNumeroPendiente() {
        return numeroPendiente;
    }

    public void setNumeroPendiente(int numeroPendiente) {
        this.numeroPendiente = numeroPendiente;
    }

    public String getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public String getEnfermedad() {
        return enfermedad;
    }

    public void setEnfermedad(String enfermedad) {
        this.enfermedad = enfermedad;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getPeriodicidadEnDias() {
        return periodicidadEnDias;
    }

    public void setPeriodicidadEnDias(int periodicidadEnDias) {
        this.periodicidadEnDias = periodicidadEnDias;
    }
}
