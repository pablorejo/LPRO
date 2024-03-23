package com.example.pruebasql.bbdd.vacas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public class Vaca {
    private int Numero_pendiente;
    private LocalDate Fecha_nacimiento;

    private ArrayList<Enfermedad> enfermedades;

    private ArrayList<Parto> partos;

    private String nota;

    private ArrayList<Leite> leiteHistorico;

    private int idUsuarioMadre;
    private int idNumeroPendienteMadre;

    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Constructor con parámetros

    public Vaca(
            int numeroPendiente,
            LocalDate fechaNacimiento,
            String nota,
            int idUsuarioMadre,
            int idNumeroPendienteMadre)
    {
        this.Numero_pendiente = numeroPendiente;
        this.Fecha_nacimiento = fechaNacimiento;
        this.nota = nota;
        this.idUsuarioMadre = idUsuarioMadre;
        this.idNumeroPendienteMadre = idNumeroPendienteMadre;
    }

    // Getters y Setters
    public int getNumeroPendiente() {
        return Numero_pendiente;
    }

    public void setNumeroPendiente(int numeroPendiente) {
        this.Numero_pendiente = numeroPendiente;
    }

    public LocalDate getFechaNacimiento() {
        return Fecha_nacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.Fecha_nacimiento = fechaNacimiento;
    }

    //Arrays
    public void addEnfermedad(Enfermedad enfermedad){
        this.enfermedades.add(enfermedad);
    }

    public void setEnfermedades(ArrayList<Enfermedad> enfermedades){
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

    public int getIdNumeroPendienteMadre(){ return this.idNumeroPendienteMadre; }

    public String getNota() {return this.nota;}

    public void setNota(String nota){ this.nota = nota;}
}

