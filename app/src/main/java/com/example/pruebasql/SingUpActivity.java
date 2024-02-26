package com.example.pruebasql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SingUpActivity extends AppCompatActivity implements Gloval{

    String url = getURL();
    EditText edtUsuario, edtPassword,edtNombre,edtApellidos;
    Button btnLogin,btnActivityLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        edtUsuario=findViewById(R.id.edtUsuario);
        edtPassword=findViewById(R.id.edtPassword2);
        edtNombre=findViewById(R.id.edtNomeUsuario);
        edtApellidos=findViewById(R.id.edtApellidos);

        btnLogin=findViewById(R.id.btnLogin);
        btnActivityLogIn=findViewById(R.id.btn_activity_logIn);

        btnLogin.setOnClickListener(v -> {crearUsuario("https://"+url+"/api/crear_usuario.php");});

        btnActivityLogIn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        });
    }

    private void crearUsuario(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Validamos que el response no esta vacío.
                // Por tanto, usuario y password ingresados existen -> servicio php nos está devolviendo la fila encontrada
                if(!response.isEmpty()){
                    // Lanzamos la activida "PrincipalActivity" como respuesta a usuario y password existentes
                    Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
                    startActivity(intent);
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("Algo ha salido mal");
                    Toast.makeText(SingUpActivity.this, "Algo ha salido mal", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Mensaje que capture y muestre el error (no recomendable para el usuario final)
                System.out.println( error.toString());
                Toast.makeText(SingUpActivity.this,error.toString() , Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Creamos una instancia con el nombre parametros
                Map<String,String> parametros = new HashMap<String,String>();
                // Ingresamos los datos a enviar al servicio PHP
                parametros.put("usuario", edtUsuario.getText().toString());
                parametros.put("password", edtPassword.getText().toString());
                parametros.put("nombre", edtNombre.getText().toString());
                parametros.put("apellidos", edtApellidos.getText().toString());
                return parametros;
            }
        };

        // Creamos una instancia
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}