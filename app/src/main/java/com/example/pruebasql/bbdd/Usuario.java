package com.example.pruebasql.bbdd;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.pruebasql.bbdd.vacas.Parto;
import com.example.pruebasql.bbdd.vacas.Vaca;

import org.json.JSONObject;

import java.util.ArrayList;

public class Usuario implements Parcelable {
    private String nombre;
    private String apellidos;
    private String correo;
    private int id;
    private ArrayList<Vaca> vacas;

    // Constructor
    public Usuario(String nombre, String apellidos, String correo, int id) {
        this.setUsuario(nombre,apellidos,correo,id);
    }

    public Usuario(String json) throws Exception{
        // Convertir la respuesta String a un objeto JSONObject
        JSONObject jsonResponse = new JSONObject(json);

        // Extraer los datos del usuario del objeto JSON
        String nombre = jsonResponse.getString("nombre");
        String apellidos = jsonResponse.getString("apellidos");
        String correo = jsonResponse.getString("correo");
        int id = jsonResponse.getInt("id"); // Asegúrate de que 'id' es un entero en tu JSON

        // Crear una instancia de tu clase Usuario con los datos extraídos
        this.setUsuario(nombre, apellidos,  correo,id);
    }

    private void setUsuario(String nombre, String apellidos, String correo, int id){
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.id = id;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Vaca> getVacas() {
        return vacas;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVacas(ArrayList<Vaca> vacas) {
        this.vacas = vacas;
    }



    // Métodos de Parcelable
    protected Usuario(Parcel in) {
        nombre = in.readString();
        apellidos = in.readString();
        correo = in.readString();
        id = in.readInt();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeString(apellidos);
        parcel.writeString(correo);
        parcel.writeInt(id);
    }
}
