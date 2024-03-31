package com.example.pruebasql;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.pruebasql.bbdd.parcelas.Parcela;
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
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.threeten.bp.format.DateTimeFormatter;
public class Server {
    private Usuario usuario;
    private String dnsActivo = "vaca.ddns.net";

    private String URL = "https://" + dnsActivo;
    private Context context; // Contexto para Volley

    String patternLocalDate = "yyyy-MM-dd";
    JsonDeserializer<LocalDate> deserializerLocalDate = new JsonDeserializer<LocalDate>() {
        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, com.google.gson.JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ofPattern(patternLocalDate));
        }
    };

    JsonSerializer<LocalDate> serializerLocalDate = new JsonSerializer<LocalDate>() {
        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DateTimeFormatter.ofPattern(patternLocalDate)));
        }
    };


    String pattern = "yyyy-MM-dd HH:mm:ss"; // Define el patrón del formato de fecha
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    JsonDeserializer<Date> deserializerDate = new JsonDeserializer<Date>() {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, com.google.gson.JsonDeserializationContext context) throws JsonParseException {
            try {
                return simpleDateFormat.parse(json.getAsJsonPrimitive().getAsString());
            } catch (ParseException e) {
                e.printStackTrace();
                return null; // O manejar de otra forma
            }
        }
    };

    JsonSerializer<Date> serializerDate = new JsonSerializer<Date>() {
        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? null : new JsonPrimitive(simpleDateFormat.format(src));
        }
    };

    Gson gson = new GsonBuilder()
            // Para que parsee bien los LocalDate.
            .registerTypeAdapter(LocalDate.class, deserializerLocalDate)
            .registerTypeAdapter(LocalDate.class, serializerLocalDate)
            .registerTypeAdapter(Date.class, deserializerDate)
            .registerTypeAdapter(Date.class, serializerDate)
            .create();


    public Server(Context context, Usuario usuario){
        this.context = context;
        this.usuario = usuario;
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

                        Usuario usuario = gson.fromJson(response, Usuario.class);
                        DataManager.getInstance().setUsuario(usuario);
                        Intent intent = new Intent(context, PrincipalActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
                        context.startActivity(intent);

                    } catch (Exception e) {
                        Toast.makeText(context, "Error al iniciar sesion", Toast.LENGTH_SHORT).show();
                    }
                    imprimirMensajeRespuesta(response);
                }else{
                    Toast.makeText(context, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void crearUsuario(String correo,String password, String nombre, String apellidos){
        String url = URL + "/usuarios";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Validamos que el response no esta vacío.
                // Por tanto, usuario y password ingresados existen -> servicio php nos está devolviendo la fila encontrada
                if(!response.isEmpty()){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int id = jsonObject.getInt("id");
                        String sesion_id = jsonObject.getString("sesion_id");
                        usuario.setid(id);
                        usuario.setSession_id(sesion_id);
                        DataManager.getInstance().setUsuario(usuario);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                    Intent intent = new Intent(context, PrincipalActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario cuando se inicia una actividad fuera de un contexto de actividad
                    context.startActivity(intent);
                    imprimirMensajeRespuesta(response);
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
            public byte[] getBody() throws AuthFailureError {
                String json = "";
                try {
                    usuario = new Usuario(nombre,apellidos,correo,hashPassword(password),0,"");
                    json = gson.toJson(usuario);

                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };

        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
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
        String url = URL + "/vacas";
        ArrayList<Vaca> listaVacas = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Vaca vaca = gson.fromJson(response, Vaca.class);
                        listaVacas.add(vaca);
                    }
                    listener.onResponse(listaVacas);
                } catch (Exception e) {
                    listener.onError(e.toString());
                }
                imprimirMensajeRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };

        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);

    }

    public void getVaca(int id_vaca, VacaResponseListener listener){
        // Configuración de la URI:
        String url = URL + "/vacas/" + id_vaca;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Vaca vaca = gson.fromJson(response, Vaca.class);
                    listener.onResponse(vaca);
                } catch (Exception e) {
                    listener.onError(e.toString());
                }
                imprimirMensajeRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void createVaca(Vaca vaca){
        String url = URL + "/vacas";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Validamos que el response no esta vacío.
                if(!response.isEmpty()){
                    imprimirMensajeRespuesta(response);
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
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(vaca);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };

        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void updateVaca(Vaca vaca){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas";
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    imprimirMensajeRespuesta(response);
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
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(vaca);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void deleteVaca(int numeroPendiente){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas/" + numeroPendiente;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    imprimirMensajeRespuesta(response);
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    /*********** FUNCIONES PARA ENFERMEDADES: ******/
    public void getEnfermedades(int id_vaca, EnfermedadesResponseListener listener){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/" +String.valueOf(id_vaca)+ "/enfermedades";
        ArrayList<Enfermedad> enfermedades = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Enfermedad enfermedad = gson.fromJson(response,Enfermedad.class);
                        enfermedades.add(enfermedad);
                    }
                    listener.onResponse(enfermedades);
                } catch (Exception e) {
                    listener.onError(e.toString());
                }
                imprimirMensajeRespuesta(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id());
                return headers;
            }
        };

        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void getEnfermedad(int id_vaca, EnfermedadResponseListener listener){
        // configurar la url para que devuelva las enfermedades
        String url = URL + "/vacas/" + id_vaca + "/enfermedades";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Enfermedad enfermedad = gson.fromJson(response,Enfermedad.class);
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id());
                return headers;
            }
        };

        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void addEnfermedad(Enfermedad enfermedad){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/enfermedades";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Enfermedad enfermedad = gson.fromJson(response, Enfermedad.class);
                usuario.addEnfermedad(enfermedad);
                imprimirMensajeRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Verificar si el error tiene una respuesta
                if(error.networkResponse != null) {
                    String responseBody;
                    try {
                        responseBody = new String(error.networkResponse.data, "utf-8");
                        // Aquí puedes convertir responseBody a un objeto JSON si esperas una respuesta en JSON
                        JSONObject jsonObject = new JSONObject(responseBody);
                        // Luego, extrae los detalles específicos del error del objeto JSON, por ejemplo:
                        String errorMessage = jsonObject.getString("message"); // Asume que el mensaje de error está en la clave "message"
                        Toast.makeText(context, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e) {
                        Log.e("VolleyError", "Error de codificación al convertir los datos a cadena");
                    } catch (JSONException e) {
                        Log.e("VolleyError", "Error al parsear la cadena JSON");
                    }
                } else {
                    // Aquí manejas errores que no incluyen una respuesta del servidor, como timeouts o no tener conexión
                    Toast.makeText(context, "Error de red. Intenta nuevamente", Toast.LENGTH_LONG).show();
                }
            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(enfermedad);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };

        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
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
                    imprimirMensajeRespuesta(response);
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
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(enfermedad);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void deleteEnfermedad(String enfermedad){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas/enfermedades";
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    imprimirMensajeRespuesta(response);
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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
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
                        Parto parto = gson.fromJson(response,Parto.class);
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };

        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }
    public void addParto(Parto parto){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/fechas_parto";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()){
                    Parto parto = gson.fromJson(response, Parto.class);
                    usuario.addParto(parto);
                    System.out.println("Se ha añadido con EXITO una enfermedad en la Base de Datos!");
                    Toast.makeText(context, "Se ha añadido con EXITO una enfermedad en la Base de Datos!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR. No se ha podido añadir la enfermedad a la Base de Datos!");
                Toast.makeText(context, "ERROR. No se ha podido añadir la enfermedad a la Base de Datos!", Toast.LENGTH_SHORT).show();            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(parto);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };

        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void updateParto(Parto parto){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/fechas_parto";

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    imprimirMensajeRespuesta(response);
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
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(parto);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void deleteParto(LocalDate fechaParto){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas/fechas_parto";
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    imprimirMensajeRespuesta(response);
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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
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
                    Pasto pasto = gson.fromJson(response,Pasto.class);
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };

        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }
    public void addDiasPasto(Pasto pasto){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/dias_pasto";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imprimirMensajeRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR. No se ha podido añadir los días de pasto a la Base de Datos!");
                Toast.makeText(context, "ERROR. No se ha podido añadir los días de pasto a la Base de Datos!", Toast.LENGTH_SHORT).show();}
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(pasto);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };

        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void updateDiasPasto(Pasto pasto){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/dias_pasto";

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    imprimirMensajeRespuesta(response);
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
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(pasto);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void deleteDiasPasto(int idVaca, LocalDate fechaPasto){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas/dias_pasto";
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    imprimirMensajeRespuesta(response);
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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
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
                    Leite leite = gson.fromJson(response, Leite.class);
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };

        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }
    public void addVolumenLeche(Leite leite){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/volumen_leche";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imprimirMensajeRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR. No se ha podido añadir el volumen de leche a la Base de Datos!");
                Toast.makeText(context, "ERROR. No se ha podido añadir el volumen de leche a la Base de Datos!", Toast.LENGTH_SHORT).show();}
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(leite);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };

        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void updateVolumenLeche(Leite leite){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/volumen_leche";

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    imprimirMensajeRespuesta(response);
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
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(leite);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void deleteVolumenLeche(Leite leite){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas/volumen_leche";
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    imprimirMensajeRespuesta(response);
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
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(leite);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    /************ FUNCIONES PARA PARCELAS: ********/

    public void addParcela(Parcela parcela){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/parcelas";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imprimirMensajeRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR. No se ha podido añadir la parcela");
                Toast.makeText(context, "ERROR. No se ha podido añadir la parcela", Toast.LENGTH_SHORT).show();}
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(parcela);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };

        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void updateParcela(Parcela parcela){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/parcelas";

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    imprimirMensajeRespuesta(response);
                }else{
                    // Mensaje: "Contraseñas incorrectas"
                    System.out.println("ERROR. No se ha podido modificar la nueva parcela: " + parcela.getNombre());
                    Toast.makeText(context, "ERROR. No se ha podido modificar la nueva parcela: " + parcela.getNombre(), Toast.LENGTH_SHORT).show();
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
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(parcela);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void deleteParcela(Parcela parcela){
        // Configuración de la URL del servidor apache
        String url = URL + "/parcelas/" + parcela.getId();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                imprimirMensajeRespuesta(response);
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
                return headers;
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);

    }

    private void imprimirMensajeRespuesta(String response){
        try {
            JSONObject obj = new JSONObject(response);
            String mensaje = obj.getString("mensaje");
            System.out.println(mensaje);
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            System.out.println("No hay mensaje de respuesta");
            Toast.makeText(context, "No hay mensaje de respuesta", Toast.LENGTH_SHORT).show();
        }
    }
}
