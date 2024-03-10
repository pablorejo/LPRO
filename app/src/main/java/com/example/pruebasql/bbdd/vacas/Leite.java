package com.example.pruebasql.bbdd.vacas;


import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Leite {
    private int idVacaLeite;
    private int  numeroPendiente;
    private double  litros;
    private Date fechaRecogida;

    public Leite(String json) throws Exception{
        // Convertir la respuesta String a un objeto JSONObject
        JSONObject jsonResponse = new JSONObject(json);
        this.setJson(jsonResponse);
    }

    private void setJson(JSONObject json) throws Exception{
        String idVacaLeite =  json.getString("idVacaLeite");
        String numeroPendiente =  json.getString("numeroPendiente");
        String litros = json.getString("litros");
        String fechaRecogidaString =  json.getString("fechaRecogida");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaRecogida = sdf.parse(fechaRecogidaString);


        // Crear una instancia de tu clase Usuario con los datos extraídos
        this.idVacaLeite = Integer.valueOf(idVacaLeite);
        this.numeroPendiente = Integer.valueOf(numeroPendiente);
        this.litros = Double.valueOf(litros);
        this.fechaRecogida = fechaRecogida;
    }

    public Map<String,String> getJson() {
        Map<String,String> parametros = new HashMap<>();

        parametros.put("idVacaLeite", String.valueOf(idVacaLeite));
        parametros.put("numeroPendiente", String.valueOf(numeroPendiente));
        parametros.put("litros", String.valueOf(litros));
        parametros.put("fechaRecogida", String.valueOf(fechaRecogida));

        return parametros;
    }

}
