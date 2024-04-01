package com.example.pruebasql;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pruebasql.bbdd.Mail;
import com.example.pruebasql.bbdd.Usuario;





public class Contactar extends BarraSuperior {
    Server server;
    EditText textoEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Usuario usuarioCookies = DataManager.getInstance().getUsuario();
        server = new Server(this,usuarioCookies);

        // Creamos el servicio para la conexión con la API de Google
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactar);
        configureToolbar();

        Button enviar = findViewById(R.id.enviar);
        textoEnviar = findViewById(R.id.textoEnviar);
        Usuario usuario = getIntent().getParcelableExtra("usuario");

        Mail mail = new Mail(textoEnviar);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    server.sendMail(mail);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        //enviar.setOnClickListener(view -> System.out.println(textoEnviar.getText().toString()));

    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar recursos
        textoEnviar = null;
    }
}