package com.example.pruebasql.bbdd.vacas;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Pasto {
    private int idVaca;
    private int numeroPendiente;
    private Date fechaPasto;

    // Constructor
    public Pasto(int idVaca, int numeroPendiente, Date fechaPasto) {
        this.idVaca = idVaca;
        this.numeroPendiente = numeroPendiente;
        this.fechaPasto = fechaPasto;
    }

    public Pasto(String json) throws Exception{
        // Convertir la respuesta String a un objeto JSONObject
        JSONObject jsonResponse = new JSONObject(json);
        this.setJson(jsonResponse);
    }
    private void setJson(JSONObject json) throws Exception{
        String idVaca =  json.getString("idVaca");
        String numeroPendiente =  json.getString("numeroPendiente");
        String fechaPastoString =  json.getString("fechaPasto");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaPasto = sdf.parse(fechaPastoString);

        // Crear una instancia de tu clase Usuario con los datos extraídos
        this.idVaca = Integer.valueOf(idVaca);
        this.numeroPendiente = Integer.valueOf(numeroPendiente);
        this.fechaPasto = fechaPasto;
    }

    // Metodos
    public Map<String,String> getJson() {
        Map<String,String> parametros = new HashMap<>();

        parametros.put("idVaca", String.valueOf(idVaca));
        parametros.put("numeroPendiente", String.valueOf(numeroPendiente));
        parametros.put("fechaPasto", String.valueOf(fechaPasto));

        return parametros;
    }


    // Getters
    public int getIdVaca() {
        return idVaca;
    }

    public int getNumeroPendiente() {
        return numeroPendiente;
    }

    public Date getFechaPasto() {
        return fechaPasto;
    }

    // Setters
    public void setIdVaca(int idVaca) {
        this.idVaca = idVaca;
    }

    public void setNumeroPendiente(int numeroPendiente) {
        this.numeroPendiente = numeroPendiente;
    }

    public void setFechaParto(Date fechaPasto) {
        this.fechaPasto = fechaPasto;
    }
}

