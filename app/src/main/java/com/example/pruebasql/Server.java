package com.example.pruebasql;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pruebasql.bbdd.Mail;
import com.example.pruebasql.bbdd.parcelas.CoordenadaDensidad;
import com.example.pruebasql.bbdd.parcelas.CoordenadasSector;
import com.example.pruebasql.bbdd.parcelas.Parcela;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.parcelas.Sector;
import com.example.pruebasql.bbdd.vacas.Enfermedad;
import com.example.pruebasql.bbdd.vacas.Leite;
import com.example.pruebasql.bbdd.vacas.Parto;
import com.example.pruebasql.bbdd.vacas.Pasto;
import com.example.pruebasql.bbdd.vacas.Vaca;
import com.example.pruebasql.listeners.ServerCallback;
import com.example.pruebasql.listeners.DiasPastoResponseListener;
import com.example.pruebasql.listeners.EnfermedadResponseListener;
import com.example.pruebasql.listeners.EnfermedadesResponseListener;
import com.example.pruebasql.listeners.FechasPartoResponseListener;
import com.example.pruebasql.listeners.VacaResponseListener;
import com.example.pruebasql.listeners.VacasResponseListener;
import com.example.pruebasql.listeners.VolumenLecheResponseListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.reflect.TypeToken;
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
    private String dnsActivo = "vacayisus.ddns.net";
    //private String dnsActivo = "172.20.10.9";
    private String URL = "http://" + dnsActivo;

    private Context context; // Contexto para Volley

    private static String tipoSalida = "application/json; charset=utf-8";

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

    ////////////// UTILIDADES ///////////////////////
    public Map<String, String> configurarCookie() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", "PHPSESSID=" + usuario.getSesion_id() + "; Path=/");
        return headers;
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

        setTimeOut(stringRequest,4000);
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
                return tipoSalida;
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
                return configurarCookie();
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
                return tipoSalida;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return configurarCookie();
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
                return configurarCookie();
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    /*********** FUNCIONES PARA ENFERMEDADES: ******/
    public void addEnfermedad(Enfermedad enfermedad, ServerCallback callback){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/vacas/enfermedades";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Enfermedad enfermedad = gson.fromJson(response, Enfermedad.class);
                usuario.addEnfermedad(enfermedad);
                callback.onResponse(enfermedad);
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
                return tipoSalida;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return configurarCookie();
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
                return tipoSalida;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return configurarCookie();
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void deleteEnfermedad(Enfermedad enfermedad){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas/enfermedades/"+enfermedad.getId_enfermedad_vaca();
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
                return configurarCookie();
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }


    /************ FUNCIONES PARA PARTOS: *************/
    public void addParto(Parto parto, ServerCallback callback){
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
                    callback.onResponse(parto);
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
                return tipoSalida;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return configurarCookie();
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
                return tipoSalida;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return configurarCookie();
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void deleteParto(Parto parto){
        // Configuración de la URL del servidor apache
        String url = URL + "/vacas/fechas_parto/" + parto.getId_vaca_parto();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                return configurarCookie();
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }





    /************ FUNCIONES PARA PARCELAS: ********/
    public void addParcela(Parcela parcela,ServerCallback callback){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/parcelas";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imprimirMensajeRespuesta(response);
                callback.onResponse(response);
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
                return tipoSalida;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return configurarCookie();
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
                return tipoSalida;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return configurarCookie();
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
                return configurarCookie();
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);

    }


    /************ FUNCIONES PARA Sectores: ********/
    public void addSector(Sector sector,ServerCallback callback){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/sectores";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Sector sector1 = gson.fromJson(response, Sector.class);
                    callback.onResponse(sector1);
                }catch (Exception e){}

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
                String json = gson.toJson(sector);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return tipoSalida;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return configurarCookie();
            }
        };

        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void updateSector(Sector sector){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/sectores";

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imprimirMensajeRespuesta(response);
                Sector sector1 = gson.fromJson(response, Sector.class);
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
                String json = gson.toJson(sector);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return tipoSalida;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return configurarCookie();
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void deleteSector(Sector sector){
        // Configuración de la URL del servidor apache
        String url = URL + "/parcelas/" + sector.id_sector;
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
                return configurarCookie();
            }
        };
        // Creamos una instancia
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void recomendarSector(Sector sector,ServerCallback serverCallback){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/sectores/recomendar";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    if (!response.isEmpty()){
                        // Especificar el tipo correcto utilizando TypeToken
                        Type listaTipo = new TypeToken<ArrayList<LatLng>>(){}.getType();
                        ArrayList<LatLng> sector1 = gson.fromJson(response, listaTipo);
                        serverCallback.onResponse(sector1);
                    }
                }
                catch (Exception e){

                }
                finally {
                    imprimirMensajeRespuesta(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                String json = gson.toJson(sector);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return tipoSalida;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return configurarCookie();
            }
        };

        setTimeOut(stringRequest,4000);
        // Añadir la solicitud a la cola
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    // Funcion para imprimir mensajes del servidor ////////////////////////////////77
    private void imprimirMensajeRespuesta(String response){
        try {
            JSONObject obj = new JSONObject(response);
            String mensaje = obj.getString("mensaje");
            System.out.println(mensaje);
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            System.out.println("No hay mensaje de respuesta");
            Log.println(Log.INFO,"Mensaje", "No hay mensaje de respuesta");
        }
    }


    /// Contactar /////////////////////////////////////////////////////////////////
    public void sendMail(Mail mail){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/contacto";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Se ha enviado con EXITO el Mail");
                Toast.makeText(context, "La información se ha enviado correctamente. MUCHAS GRACIAS POR AYUDARNOS A MEJORAR!! :)", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR. No se ha podido enviar el Mail");
                Toast.makeText(context, "ERROR. No se ha podido enviar el Mail", Toast.LENGTH_SHORT).show();            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return mail.getJson();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void call(int numeroPendiente){
        // configurar la url para que devuelva las enfermedades
        String url = this.URL + "/llamada";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Se ha efectuado la llamada con EXITO.");
                Toast.makeText(context, "Se ha efectuado la llamada con EXITO.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR. No se ha podido efectuar la llamada.");
                Toast.makeText(context, "ERROR. No se ha podido efectuar la llamada.", Toast.LENGTH_SHORT).show();            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Creamos una instancia con el nombre parametros
                HashMap<String, String> parametros = new HashMap<>();
                // Ingresamos los datos a enviar al servicio PHP
                parametros.put("numeroPendiente", String.valueOf(numeroPendiente));
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    /***
     * Esta funcion obtiene los datos gps filtrados y reducidos
     * Aunque se llame get esta usa el metodo post para mandar los filtros
     * @param inicio
     * @param fin
     * @param listener
     */
    public void getGPS(Date inicio, Date fin, Integer[] numerosVacas, int id_parcela, String tipo,ServerCallback listener){
        String url = URL + "/gps";
        ArrayList<Vaca> listaVacas = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Type listaTipo = new TypeToken<ArrayList<LatLng>>(){}.getType();
                    ArrayList<LatLng> coordenadas = gson.fromJson(response, listaTipo);
                    if (coordenadas.isEmpty()){
                        Toast.makeText(context, "No hay datos para mostrar", Toast.LENGTH_SHORT).show();
                    }
                    listener.onResponse(coordenadas);

                } catch (Exception e) {
                }
                imprimirMensajeRespuesta(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imprimirError(error);
            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                Filtro filtro = new Filtro(inicio,fin,numerosVacas,id_parcela,tipo);
                String json = gson.toJson(filtro);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return tipoSalida;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return configurarCookie();
            }
        };

        setTimeOut(stringRequest,4000);
        RequestQueue requestQueue = MyApplication.getInstance().getRequestQueue();
        requestQueue.add(stringRequest);
    }

    private class Filtro{
        Date inicio;
        Date fin;
        Integer[] numerosVacas;
        String tipo;
        int id_parcela;

        public Filtro(Date inicio, Date fin, Integer[] numerosVacas,int id_parcela, String tipo) {
            this.inicio = inicio;
            this.fin = fin;
            this.numerosVacas = numerosVacas;
            this.id_parcela = id_parcela;
            this.tipo = tipo;
        }
    }

    private void setTimeOut(StringRequest stringRequest, int timeOut){
        // Configurar el RetryPolicy personalizado
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                timeOut,  // Tiempo de espera inicial en milisegundos (10 segundos)
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Número de reintentos
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Multiplicador de backoff
        ));
    }

    private void imprimirError(VolleyError error){
        System.out.println("ERROR. Error al intentar recomendar sector");
        Toast.makeText(context, "ERROR. Ah ocurrido un error " + error.getCause(), Toast.LENGTH_SHORT).show();
    }
}