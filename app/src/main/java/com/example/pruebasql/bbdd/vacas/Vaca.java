package com.example.pruebasql.bbdd.vacas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONObject;

public class Vaca {
    private int numeroPendiente;
    private int idUsuario;
    private Date fechaNacimiento;

    private ArrayList<Enfermedad> enfermedades;

    private ArrayList<Parto> partos;

    private ArrayList<Leite> leiteHistorico;

    // Constructor con parámetros
    public Vaca(int numeroPendiente, Date fechaNacimiento){
        this.numeroPendiente = numeroPendiente;
        this.fechaNacimiento = fechaNacimiento;
    }
    public Vaca(int numeroPendiente, int idUsuario, Date fechaNacimiento) {
        this.setVaca(numeroPendiente, idUsuario,  fechaNacimiento);
    }

    public Vaca(String json) throws Exception{
        // Convertir la respuesta String a un objeto JSONObject
        JSONObject jsonResponse = new JSONObject(json);

        this.setJson(jsonResponse);
    }

    public Vaca(JSONObject json) throws Exception{
        this.setJson(json);
    }

    private void setJson(JSONObject json) throws Exception{
        // Convertir la respuesta String a un objeto JSONObject

        // Extraer los datos del usuario del objeto JSON
        String numeroPendiente = json.getString("Numero_pendiente");
        String idUsuario = json.getString("IdUsuario");
        String fechaNacimiento = json.getString("Fecha_nacimiento");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = sdf.parse(fechaNacimiento);

        // Crear una instancia de tu clase Usuario con los datos extraídos
        this.setVaca(Integer.parseInt(numeroPendiente), Integer.parseInt(idUsuario),  fecha);
    }


    private void setVaca(int numeroPendiente, int idUsuario, Date fechaNacimiento){
        this.numeroPendiente = numeroPendiente;
        this.idUsuario = idUsuario;
        this.fechaNacimiento = fechaNacimiento;
    }


    // Getters y Setters
    public int getNumeroPendiente() {
        return numeroPendiente;
    }

    public void setNumeroPendiente(int numeroPendiente) {
        this.numeroPendiente = numeroPendiente;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    //Arrays
    public void addEnfermedad(Enfermedad enfermedad){
        this.enfermedades.add(enfermedad);
    }

    public void setEnfermedad(ArrayList<Enfermedad> enfermedades){
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


}

