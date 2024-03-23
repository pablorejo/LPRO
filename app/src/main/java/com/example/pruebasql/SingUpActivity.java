package com.example.pruebasql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;


public class SingUpActivity extends AppCompatActivity {

    EditText edtUsuario, edtPassword,edtNombre,edtApellidos;
    Button btnLogin,btnActivityLogIn;
    Server server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        server = new Server(this,null);

        edtUsuario=findViewById(R.id.edtUsuario);
        edtPassword=findViewById(R.id.edtPassword2);
        edtNombre=findViewById(R.id.edtNomeUsuario);
        edtApellidos=findViewById(R.id.edtApellidos);

        btnLogin=findViewById(R.id.btnLogin);
        btnActivityLogIn=findViewById(R.id.btn_activity_logIn);

        btnLogin.setOnClickListener(v -> {
            server.crearUsuario(
                    edtUsuario.getText().toString(),
                    edtPassword.getText().toString(),
                    edtNombre.getText().toString(),
                    edtApellidos.getText().toString()
                    );
        });

        btnActivityLogIn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        });
    }
}