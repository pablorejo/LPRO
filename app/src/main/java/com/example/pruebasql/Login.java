package com.example.pruebasql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;


public class Login extends AppCompatActivity {
    EditText edtUsuario, edtPassword;
    Button btnLogin, btnActivitySingUp;
    Server server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        server = new Server(this);

        // Vinculamos variables con los controles del layout
        edtUsuario=findViewById(R.id.edtNomeUsuario);
        edtPassword=findViewById(R.id.edtUsuario);
        btnLogin=findViewById(R.id.btnLogin);
        btnActivitySingUp=findViewById(R.id.btn_activity_singUp);

        // Evento click de nuestro botón
        btnLogin.setOnClickListener(v -> {

            /*
            String correo = edtUsuario.getText().toString();
            String pass =edtPassword.getText().toString();

            correo = "pablopiorejoiglesias@gmail.com";
            pass = "1234";


            server.validarUsuario(
                    correo,
                    pass
            );
            */
            Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
            startActivity(intent);

        });

        btnActivitySingUp.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SingUpActivity.class);
            startActivity(intent);
        });
    }
}