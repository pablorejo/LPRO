package com.example.pruebasql.bbdd;

import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class Mail {
    private String cuerpo;
    private String asunto = "Correo del área de Contacto";

    // Constructor:
    public Mail(EditText cuerpo){
        this.cuerpo = cuerpo.getText().toString();
    }

    // Métodos:
    public Map<String,String> getJson() {
        Map<String,String> parametros = new HashMap<>();

        parametros.put("asunto", asunto);
        parametros.put("cuerpo", cuerpo);

        return parametros;
    }


}