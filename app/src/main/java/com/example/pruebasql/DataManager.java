package com.example.pruebasql;

import com.example.pruebasql.bbdd.Usuario;

public class DataManager {
    private static DataManager instance;
    private Usuario usuario; // Tu clase Usuario

    private DataManager() {}

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

