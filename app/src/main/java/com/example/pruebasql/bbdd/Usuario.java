package com.example.pruebasql.bbdd;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.pruebasql.bbdd.vacas.Enfermedad;
import com.example.pruebasql.bbdd.vacas.Parto;
import com.example.pruebasql.bbdd.vacas.Vaca;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONObject;
import org.threeten.bp.format.DateTimeFormatter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Usuario {
    private String nombre;
    private String apellidos;
    private String correo;
    private int id;
    private String session_id;

    private ArrayList<Parcela> parcelas;

    private ArrayList<Vaca> vacas;
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Constructor
    public Usuario(String nombre, String apellidos, String correo, int id, String sesion_id) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.id = id;
        this.session_id = sesion_id;
    }

    public String getSesion_id() {
        return this.session_id;
    }
    public int getId() {
        return id;
    }

    public ArrayList<Vaca> getVacas() {
        return vacas;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVacas(ArrayList<Vaca> vacas) {
        this.vacas = vacas;
    }

    public Vaca getVacaByNumeroPendiente(int Numero_pendiente){
        for (Vaca vaca: vacas){
            if (vaca.getNumeroPendiente() == Numero_pendiente){
                return vaca;
            }
        }
        return null;
    }

    public void addParto(Parto parto){
        getVacaByNumeroPendiente(parto.getNumeroPendiente()).addParto(parto);
    }

    public void addEnfermedad(Enfermedad enfermedad){
        getVacaByNumeroPendiente(enfermedad.getNumero_pendiente()).addEnfermedad(enfermedad);
    }

    public Vaca getMadre(Vaca hija){
        return getVacaByNumeroPendiente(hija.getIdNumeroPendienteMadre());
    }

    public ArrayList<Vaca> getHijos(Vaca madre){
        ArrayList<Vaca> hijos = new ArrayList<Vaca>();
        for (Vaca vaca: getVacas()){
            if (vaca.getIdNumeroPendienteMadre() == madre.getNumeroPendiente()){
                hijos.add(vaca);
            }
        }
        return hijos;
    }

    public ArrayList<Parcela> getParcelas(){
        if (this.parcelas == null){
            this.parcelas = new ArrayList<Parcela>();
        }
        return this.parcelas;
    }
    public void setParcelas(ArrayList<Parcela> parcelas){ this.parcelas = parcelas;}

    public Parcela addParcela(Parcela parcela) {
        if (parcelas == null){
            parcelas = new ArrayList<Parcela>();
        }
        this.parcelas.add(parcela);
        return parcela;
    }

    public Parcela getUltimaParcela(){
        return this.parcelas.get(this.parcelas.size()-1);
    }
}
