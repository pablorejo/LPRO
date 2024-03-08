package com.example.pruebasql.bbdd.vacas;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Enfermedad {
    private int idEnfermedadVaca;
    private int numeroPendiente;
    private String medicamento;
    private String enfermedad;
    private Date fechaInicio;
    private Date fechaFin;
    private int periodicidadEnDias;



    public Enfermedad(String json) throws Exception{
        // Convertir la respuesta String a un objeto JSONObject
        JSONObject jsonResponse = new JSONObject(json);
        this.setJson(jsonResponse);
    }

    public Enfermedad(JSONObject json) throws Exception{
        this.setJson(json);
    }

    private void setJson(JSONObject json) throws Exception{

        String idEnfermedadVaca =  json.getString("idEnfermedadVaca");
        String numeroPendiente =  json.getString("Numero_pendiente");
        String medicamento = json.getString("medicamento");
        String enfermedad =  json.getString("enfermedad");
        String strfechaInicio =  json.getString("fechaInicio");
        String strfechaFin =  json.getString("fechaFin");
        String periodicidadEnDias =  json.getString("periodicidadEnDias");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaInicio = sdf.parse(strfechaInicio);
        Date fechaFin = sdf.parse(strfechaFin);


        // Crear una instancia de tu clase Usuario con los datos extraídos
        this.setEnfermedad(
                Integer.parseInt(idEnfermedadVaca),
                Integer.parseInt(numeroPendiente),
                medicamento,
                enfermedad,
                fechaInicio,
                fechaFin,
                Integer.parseInt(periodicidadEnDias));
    }

    public Map<String,String> getJson() {
        Map<String,String> parametros = new HashMap<>();

        parametros.put("idEnfermedadVaca", String.valueOf(idEnfermedadVaca));
        parametros.put("Numero_pendiente", String.valueOf(numeroPendiente));
        parametros.put("medicamento", medicamento);
        parametros.put("enfermedad", enfermedad);
        parametros.put("fechaInicio", fechaFin.toString());
        parametros.put("fechaFin", fechaFin.toString());
        parametros.put("periodicidadEnDias", String.valueOf(periodicidadEnDias));

        return parametros;
    }

    private void setEnfermedad(
            int idEnfermedadVaca,
            int numeroPendiente,
            String medicamento,
            String enfermedad,
            Date fechaInicio,
            Date fechaFin,
            int periodicidadEnDias){
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
