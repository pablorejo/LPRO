package com.example.pruebasql;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.vacas.Enfermedad;
import com.example.pruebasql.bbdd.vacas.Vaca;
import com.example.pruebasql.listeners.EnfermedadesResponseListener;
import com.example.pruebasql.listeners.VacaResponseListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Server {
    private Usuario user = null;
    private String dnsActivo = "vacayisus.ddns.net";

    private String URL = "https://" + dnsActivo;
    private Context context; // Contexto para Volley


    public Server(Context context){
        this.context = context;
    }

    public void validarUsuario(String usuario, String password){
        String url = URL + "/login"; // Asegúrate de usar tu URL correcta aquí
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Tu lógica de validación aquí...
                if(!response.isEmpty()){
                    try {
                        Usuario user = new Usuario(response);
                        Intent intent = new Intent(context, PrincipalActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
                        intent.putExtra("usuario", user);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(context, "Error al crear el usuario", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("usuario", usuario);
                try {
                    parametros.put("password", hashPassword(password));
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context); // Usa el contexto de la instancia
        requestQueue.add(stringRequest);
    }

    public void crearUsuario(String usuario,String password, String nombre, String apellidos){
        String url = URL + "/usuarios";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Validamos que el response no esta vacío.
                // Por tanto, usuario y password ingresados existen -> servicio php nos está devolviendo la fila encontrada
                if(!response.isEmpty()){
                    Intent intent = new Intent(context, PrincipalActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
                    context.startActivity(intent);
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("Algo ha salido mal");
                    Toast.makeText(context, "Algo ha salido mal", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Mensaje que capture y muestre el error (no recomendable para el usuario final)
                System.out.println( error.toString());
                Toast.makeText(context,error.toString() , Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Creamos una instancia con el nombre parametros
                Map<String,String> parametros = new HashMap<String,String>();
                // Ingresamos los datos a enviar al servicio PHP
                parametros.put("usuario", usuario);
                try {
                    parametros.put("password", hashPassword(password));
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                parametros.put("nombre", nombre);
                parametros.put("apellidos", apellidos);
                return parametros;
            }
        };

        // Creamos una instancia
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    // Método para cifrar (hash) una contraseña
    private static String hashPassword(String password) throws NoSuchAlgorithmException {
        // Crear una instancia de MessageDigest para SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Aplicar SHA-256 al password
        byte[] encodedhash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        // Convertir el hash en hexadecimal
        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }


    public void getVacas(int id_usuario, VacaResponseListener listener){
        String url = URL + "/usuarios/" + id_usuario  + "/vacas";
        ArrayList<Vaca> listaVacas = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        // Suponiendo que tienes un constructor adecuado en tu clase Vaca
                        Vaca vaca = new Vaca(jsonObject);
                        listaVacas.add(vaca);
                    }
                    listener.onResponse(listaVacas);
                } catch (Exception e) {
                    listener.onError(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    public void getEnfermedades(int id_vaca, EnfermedadesResponseListener listener){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/";
        ArrayList<Enfermedad> enfermedades = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        // Suponiendo que tienes un constructor adecuado en tu clase Vaca
                        Enfermedad enfermedad = new Enfermedad(jsonObject);
                        enfermedades.add(enfermedad);
                    }
                    listener.onResponse(enfermedades);
                } catch (Exception e) {
                    listener.onError(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    public void getEnfermedad(int id_enfermedad, EnfermedadesResponseListener listener){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/";
        ArrayList<Enfermedad> enfermedades = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Enfermedad enfermedad = new Enfermedad(response);
                    listener.onResponse(enfermedad);

                } catch (Exception e) {
                    listener.onError(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void setEnfermedad(Enfermedad enfermedad){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/";
        ArrayList<Enfermedad> enfermedades = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Todo salio bien", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Algo ha salido mal", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return enfermedad.getJson();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}
