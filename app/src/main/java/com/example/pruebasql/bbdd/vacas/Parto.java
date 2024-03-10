package com.example.pruebasql.bbdd.vacas;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Parto {
    private int idVacaParto;
    private int numeroPendiente;
    private Date fechaParto;

    // Constructor
    public Parto(int id_vaca_parto, int numeroPendiente, Date fechaParto) {
        this.idVacaParto = id_vaca_parto;
        this.numeroPendiente = numeroPendiente;
        this.fechaParto = fechaParto;
    }

    public Parto(JSONObject json) throws Exception{
        String idVacaParto =  json.getString("idVacaParto");
        String numeroPendiente =  json.getString("numeroPendiente");
        String fechaPartoString = json.getString("fechaParto");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaParto = sdf.parse(fechaPartoString);

        this.idVacaParto = Integer.parseInt(idVacaParto);
        this.numeroPendiente = Integer.parseInt(numeroPendiente);
        this.fechaParto = fechaParto;
    }

    // Metodos
    public Map<String,String> getJson() {
        Map<String,String> parametros = new HashMap<>();

        parametros.put("idVacaParto", String.valueOf(idVacaParto));
        parametros.put("numeroPendiente", String.valueOf(numeroPendiente));
        parametros.put("fechaParto", String.valueOf(fechaParto));

        return parametros;
    }


    // Getters
    public int getIdVacaParto() {
        return idVacaParto;
    }

    public int getNumeroPendiente() {
        return numeroPendiente;
    }

    public Date getFechaParto() {
        return fechaParto;
    }

    // Setters
    public void setIdVacaParto(int idVacaParto) {
        this.idVacaParto = idVacaParto;
    }

    public void setNumeroPendiente(int numeroPendiente) {
        this.numeroPendiente = numeroPendiente;
    }

    public void setFechaParto(Date fechaParto) {
        this.fechaParto = fechaParto;
    }
}

