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
import com.example.pruebasql.bbdd.vacas.Leite;
import com.example.pruebasql.bbdd.vacas.Parto;
import com.example.pruebasql.bbdd.vacas.Pasto;
import com.example.pruebasql.bbdd.vacas.Vaca;
import com.example.pruebasql.listeners.DiasPastoResponseListener;
import com.example.pruebasql.listeners.EnfermedadResponseListener;
import com.example.pruebasql.listeners.EnfermedadesResponseListener;
import com.example.pruebasql.listeners.FechasPartoResponseListener;
import com.example.pruebasql.listeners.VacaResponseListener;
import com.example.pruebasql.listeners.VacasResponseListener;
import com.example.pruebasql.listeners.VolumenLecheResponseListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
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

    // FUNCIONES PARA USUARIOS:
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




    /************ FUNCIONES PARA USUARIOS: *******/

    /*********** FUNCIONES PARA VACAS: **********/
    public void getVacas(VacasResponseListener listener){
        String url = URL + "/vacas/";
        ArrayList<Vaca> listaVacas = new ArrayList<>();

        // String url = URL + "/usuarios/" + id_usuario  + "/vacas";

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

    public void getVaca(int id_vaca, VacaResponseListener listener){
        // Configuración de la URI:
        String url = URL + "/vacas/" + id_vaca;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Vaca vaca = new Vaca(response);
                    listener.onResponse(vaca);
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

    public void createVaca(int numeroPendiente, Date fechaNacimiento){
        String url = URL + "/vacas";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Validamos que el response no esta vacío.
                if(!response.isEmpty()){
                    System.out.println("Se ha añadido con EXITO una vaca nueva a la Base de Datos!");
                    Toast.makeText(context, "Se ha añadido con EXITO una vaca nueva a la Base de Datos!", Toast.LENGTH_SHORT).show();
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("ERROR. No se ha podido añadir la vaca a la Base de Datos!");
                    Toast.makeText(context, "ERROR. No se ha podido añadir la vaca a la Base de Datos!", Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> parametros = new HashMap<>();
                // Ingresamos los datos a enviar al servicio PHP
                Vaca vaca = new Vaca(numeroPendiente, fechaNacimiento);
                String numeroPendienteString = String.valueOf(numeroPendiente);
                String fechaNacimientoString = String.valueOf(fechaNacimiento);
                parametros.put("numeroPendiente", numeroPendienteString);
                parametros.put("fechaNacimiento", fechaNacimientoString);
                return parametros;
            }
        };

        // Creamos una instancia
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void updateVaca(int numeroPendiente, Date fechaNacimiento){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas";
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    System.out.println("Se ha modificado con EXITO la vaca en la Base de Datos!");
                    Toast.makeText(context, "Se ha modificado con EXITO la vaca en la Base de Datos!", Toast.LENGTH_SHORT).show();
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("ERROR. No se ha podido modificar la vaca a la Base de Datos!");
                    Toast.makeText(context, "ERROR. No se ha podido modificar la vaca a la Base de Datos!", Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> parametros = new HashMap<>();
                // Ingresamos los datos a enviar al servicio PHP
                Vaca vaca = new Vaca(numeroPendiente, fechaNacimiento);
                String numeroPendienteString = String.valueOf(numeroPendiente);
                String fechaNacimientoString = String.valueOf(fechaNacimiento);
                parametros.put("numeroPendiente", numeroPendienteString);
                parametros.put("fechaNacimiento", fechaNacimientoString);
                return parametros;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void deleteVaca(int numeroPendiente){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas";
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    System.out.println("Se ha eliminado con EXITO la vaca en la Base de Datos!");
                    Toast.makeText(context, "Se ha eliminado con EXITO la vaca en la Base de Datos!", Toast.LENGTH_SHORT).show();
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("ERROR. No se ha podido eliminar la vaca a la Base de Datos!");
                    Toast.makeText(context, "ERROR. No se ha podido eliminar la vaca a la Base de Datos!", Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> parametros = new HashMap<>();
                // Ingresamos los datos a enviar al servicio PHP
                String numeroPendienteString = String.valueOf(numeroPendiente);
                parametros.put("numeroPendiente", numeroPendienteString);
                return parametros;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    /*********** FUNCIONES PARA ENFERMEDADES: ******/
    public void getEnfermedades(int id_vaca, EnfermedadesResponseListener listener){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/enfermedades";
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

    public void getEnfermedad(int id_vaca, EnfermedadResponseListener listener){
        // configurar la url para que devuelva las enfermedades
        String url = URL + "/vacas/" + id_vaca + "/enfermedades";

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

    public void addEnfermedad(Enfermedad enfermedad){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/enfermedades";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Se ha añadido con EXITO una enfermedad en la Base de Datos!");
                Toast.makeText(context, "Se ha añadido con EXITO una enfermedad en la Base de Datos!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR. No se ha podido añadir la enfermedad a la Base de Datos!");
                Toast.makeText(context, "ERROR. No se ha podido añadir la enfermedad a la Base de Datos!", Toast.LENGTH_SHORT).show();            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return enfermedad.getJson();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void updateEnfermedad(Enfermedad enfermedad){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/enfermedades";
        ArrayList<Enfermedad> enfermedades = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    System.out.println("Se ha modificado con EXITO la vaca en la Base de Datos!");
                    Toast.makeText(context, "Se ha modificado con EXITO la vaca en la Base de Datos!", Toast.LENGTH_SHORT).show();
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("ERROR. No se ha podido modificar la vaca a la Base de Datos!");
                    Toast.makeText(context, "ERROR. No se ha podido modificar la vaca a la Base de Datos!", Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> parametros = new HashMap<>();
                // Ingresamos los datos a enviar al servicio PHP
                return enfermedad.getJson();
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void deleteEnfermedad(String enfermedad){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas/enfermedades";
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    System.out.println("Se ha eliminado con EXITO la enfermedad en la Base de Datos!");
                    Toast.makeText(context, "Se ha eliminado con EXITO la enfermedad en la Base de Datos!", Toast.LENGTH_SHORT).show();
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("ERROR. No se ha podido eliminar la enfermedad a la Base de Datos!");
                    Toast.makeText(context, "ERROR. No se ha podido eliminar la enfermedad a la Base de Datos!", Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> parametros = new HashMap<>();
                // Ingresamos los datos a enviar al servicio PHP
                parametros.put("enfermedad", enfermedad);
                return parametros;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    /************ FUNCIONES PARA PARTOS: *************/
    public void getFechasParto(int id_vaca, FechasPartoResponseListener listener){
        // configurar la url para que devuelva las fechas de los partos de una vaca
        String url = this.URL + "/vacas/" + id_vaca + "/fechas_parto";
        ArrayList<Parto> partos = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        // Suponiendo que tienes un constructor adecuado en tu clase Vaca
                        Parto parto = new Parto(jsonObject);
                        partos.add(parto);
                    }
                    listener.onResponse(partos);
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
    public void addFechaParto(Parto parto){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/fechas_parto";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Se ha añadido con EXITO una enfermedad en la Base de Datos!");
                Toast.makeText(context, "Se ha añadido con EXITO una enfermedad en la Base de Datos!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR. No se ha podido añadir la enfermedad a la Base de Datos!");
                Toast.makeText(context, "ERROR. No se ha podido añadir la enfermedad a la Base de Datos!", Toast.LENGTH_SHORT).show();            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return parto.getJson();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void updateFechaParto(Parto parto){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/fechas_parto";

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    System.out.println("Se ha modificado con EXITO la fecha de parto en la Base de Datos!");
                    Toast.makeText(context, "Se ha modificado con EXITO la fecha de parto en la Base de Datos!", Toast.LENGTH_SHORT).show();
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("ERROR. No se ha podido modificar la fecha de parto a la Base de Datos!");
                    Toast.makeText(context, "ERROR. No se ha podido modificar la fecha de parto a la Base de Datos!", Toast.LENGTH_SHORT).show();
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
                // Ingresamos los datos a enviar al servicio PHP
                return parto.getJson();
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void deleteFechaParto(Date fechaParto){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas/fechas_parto";
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    System.out.println("Se ha eliminado con EXITO la fecha del parto en la Base de Datos!");
                    Toast.makeText(context, "Se ha eliminado con EXITO la fecha del parto en la Base de Datos!", Toast.LENGTH_SHORT).show();
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("ERROR. No se ha podido eliminar la fecha del parto a la Base de Datos!");
                    Toast.makeText(context, "ERROR. No se ha podido eliminar la fecha del parto a la Base de Datos!", Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> parametros = new HashMap<>();
                // Ingresamos los datos a enviar al servicio PHP
                parametros.put("fechaParto", String.valueOf(fechaParto));
                return parametros;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    /************ FUNCIONES PARA PASTO: ********/
    public void getDiasPasto(int id_vaca, DiasPastoResponseListener listener){
        // configurar la url para que devuelva las fechas de los partos de una vaca
        String url = this.URL + "/vacas/" + id_vaca + "/dias_pasto";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Pasto pasto = new Pasto(response);
                    listener.onResponse(pasto);

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
    public void addDiasPasto(Pasto pasto){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/dias_pasto";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Se ha añadido con EXITO los días de pasto en la Base de Datos!");
                Toast.makeText(context, "Se ha añadido con EXITO los días de pasto en la Base de Datos!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR. No se ha podido añadir los días de pasto a la Base de Datos!");
                Toast.makeText(context, "ERROR. No se ha podido añadir los días de pasto a la Base de Datos!", Toast.LENGTH_SHORT).show();}
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return pasto.getJson();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void updateDiasPasto(Pasto pasto){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/dias_pasto";

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    System.out.println("Se ha modificado con EXITO los días de pasto en la Base de Datos!");
                    Toast.makeText(context, "Se ha modificado con EXITO los días de pasto en la Base de Datos!", Toast.LENGTH_SHORT).show();
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("ERROR. No se ha podido modificar los días de pasto a la Base de Datos!");
                    Toast.makeText(context, "ERROR. No se ha podido modificar los días de pasto en la Base de Datos!", Toast.LENGTH_SHORT).show();
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
                // Ingresamos los datos a enviar al servicio PHP
                return pasto.getJson();
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void deleteDiasPasto(int idVaca, Date fechaPasto){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas/dias_pasto";
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    System.out.println("Se ha eliminado con EXITO los días de pasto seleccionado de la Base de Datos!");
                    Toast.makeText(context, "Se ha eliminado con EXITO los días de pasto seleccionado la Base de Datos!", Toast.LENGTH_SHORT).show();
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("ERROR. No se ha podido eliminar los días de pasto de la Base de Datos!");
                    Toast.makeText(context, "ERROR. No se ha podido eliminar los días de pasto de la Base de Datos!", Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> parametros = new HashMap<>();
                // Ingresamos los datos a enviar al servicio PHP
                parametros.put("idVaca", String.valueOf(idVaca));
                parametros.put("fechaPasto", String.valueOf(fechaPasto));
                return parametros;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    /************ FUNCIONES PARA VOLUMEN DE LECHE: ********/
    public void getVolumenLeche(int id_vaca, VolumenLecheResponseListener listener){
        // configurar la url para que devuelva las fechas de los partos de una vaca
        String url = this.URL + "/vacas/" + id_vaca + "/volumen_leche";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Leite leite = new Leite(response);
                    listener.onResponse(leite);

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
    public void addVolumenLeche(Leite leite){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/volumen_leche";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Se ha añadido con EXITO el volumen de leche en la Base de Datos!");
                Toast.makeText(context, "Se ha añadido con EXITO el volumen de leche en la Base de Datos!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR. No se ha podido añadir el volumen de leche a la Base de Datos!");
                Toast.makeText(context, "ERROR. No se ha podido añadir el volumen de leche a la Base de Datos!", Toast.LENGTH_SHORT).show();}
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return leite.getJson();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void updateVolumenLeche(Leite leite){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/volumen_leche";

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    System.out.println("Se ha modificado con EXITO el volumen de leche en la Base de Datos!");
                    Toast.makeText(context, "Se ha modificado con EXITO el volumen de leche en la Base de Datos!", Toast.LENGTH_SHORT).show();
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("ERROR. No se ha podido modificar el volumen de leche a la Base de Datos!");
                    Toast.makeText(context, "ERROR. No se ha podido modificar el volumen de leche en la Base de Datos!", Toast.LENGTH_SHORT).show();
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
                // Ingresamos los datos a enviar al servicio PHP
                return leite.getJson();
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void deleteVolumenLeche(int idVaca, Date fechaRecogida){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas/volumen_leche";
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    System.out.println("Se ha eliminado con EXITO el volumen de leche seleccionado de la Base de Datos!");
                    Toast.makeText(context, "Se ha eliminado con EXITO el volumen de leche seleccionado la Base de Datos!", Toast.LENGTH_SHORT).show();
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("ERROR. No se ha podido eliminar el volumen de leche seleccionado la Base de Datos!");
                    Toast.makeText(context, "ERROR. No se ha podido eliminar el volumen de leche seleccionado la Base de Datos!", Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> parametros = new HashMap<>();
                // Ingresamos los datos a enviar al servicio PHP
                parametros.put("idVaca", String.valueOf(idVaca));
                parametros.put("fechaRecogida", String.valueOf(fechaRecogida));
                return parametros;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
