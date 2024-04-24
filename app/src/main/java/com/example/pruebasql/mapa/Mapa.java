package com.example.pruebasql.mapa;

import androidx.annotation.NonNull;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.R;
import com.example.pruebasql.Server;
import com.example.pruebasql.bbdd.parcelas.CoordenadaParcela;
import com.example.pruebasql.bbdd.parcelas.Parcela;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pruebasql.bbdd.vacas.Vaca;
import com.example.pruebasql.listeners.ServerCallback;
import com.example.pruebasql.mapa.parcelas.ParcelaActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Mapa extends BarraSuperior implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private GoogleMap gMap;

    private Button btnEliminarParcela, btnañadirParcela, btnEditarCowFinder;

    private boolean añadirParcela = false;

    private EditText editTextNombreParcela;

    private List<Poligono> poligonos = new ArrayList<Poligono>();

    private Server server;

    private Vaca vaca;

    private int indexCamara = 0; // El indice de la parcela donde se va poner la camara, inicio 0;

    private Location lastKnownLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_finder);
        configureToolbar();

        String numeroPendienteString = getIntent().getStringExtra("numero_pendiente");
        System.out.println("El valor del numero pendiente: " + numeroPendienteString);
        if (numeroPendienteString != null && !numeroPendienteString.equals("")){
            vaca = usuario.getVacaByNumeroPendiente(Integer.parseInt(numeroPendienteString));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        server = new Server(this,usuario);

        btnEliminarParcela = findViewById(R.id.btnEliminarParcela);
        btnEliminarParcela.setVisibility(View.GONE);

        btnañadirParcela = findViewById(R.id.btnAñadirParcela);
        btnañadirParcela.setOnClickListener(v -> {
            añadirParcela();
        });

        editTextNombreParcela = findViewById(R.id.editTextNombreParcela);
        editTextNombreParcela.setEnabled(false);
        editTextNombreParcela.setVisibility(View.GONE);
        editTextNombreParcela.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextNombreParcela.setText(""); // Esto borrará el texto cuando el EditText gane el foco
                }
            }
        });



        btnEditarCowFinder = findViewById(R.id.btnEditarCowFinder);
        btnEditarCowFinder.setVisibility(View.GONE);

    }

    private void verParcelaAnterior(){
        if (indexCamara -1 < 0){
            indexCamara = poligonos.size() -1;
        }else{
            indexCamara --;
        }
        LatLng ubicacionCentro = Poligono.getPolygonCenterLatLng(poligonos.get(indexCamara).getPuntosLatLng());
        float nivelZoom = 17.0f; // Ajusta este valor para el nivel de zoom que necesitas
        // Centra el mapa en la ubicación deseada con el nivel de zoom especificado
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionCentro, nivelZoom));
    }

    private void verParcelaSiguiente(){
        if (indexCamara + 1 >  poligonos.size() -1){
            indexCamara = 0;
        }else{
            indexCamara ++;
        }
        LatLng ubicacionCentro = Poligono.getPolygonCenterLatLng(poligonos.get(indexCamara).getPuntosLatLng());
        float nivelZoom = 17.0f; // Ajusta este valor para el nivel de zoom que necesitas
        // Centra el mapa en la ubicación deseada con el nivel de zoom especificado
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionCentro, nivelZoom));
    }

    private void añadirParcela(){
        if (añadirParcela){ // Si añadir parcela está a true hay que guardar la parcela.
            añadirParcela= false;

            configurarBtnañadirParcela();

            // Añadimos la parcela al usuario, también hay que añadirla con el server.
            Parcela parcela = usuario.getUltimaParcela();
            parcela.setNombre(editTextNombreParcela.getText().toString());

            // Cordenadas de la parcela
            List<CoordenadaParcela> coordenadas = getLastPoligono().getCoordenadas();

            if (coordenadas.size()> 2){
                parcela.setCoordenadas(coordenadas);
                server.addParcela(parcela, new ServerCallback() {
                    @Override
                    public void onResponse(Object response) {
                        Parcela parcela1 = (Parcela) response;
                        usuario.parcelas.set(usuario.parcelas.size() - 1, parcela1);
                    }
                });
            }else{
                Toast.makeText(this, "Puntos insuficientes, minimo 3", Toast.LENGTH_SHORT).show();
                usuario.getParcelas().remove(parcela);
                deleteLastMarkers();
            }
            redrawPolygon();

            // Comprobar si hay cambios en otras parcelas.
            for (int k = 0; k<poligonos.size(); k++){
                Poligono poligono = poligonos.get(k);
                poligono.updateMarkers(añadirParcela,false);

                if(poligono.modificado){
                    server.updateParcela(poligono.parcela);
                    poligono.modificado = false;
                }
            }

            editTextNombreParcela.setEnabled(false);
            editTextNombreParcela.setVisibility(View.GONE);
            editTextNombreParcela.setText("Nombre parcela");

        }else{
            // Hacemos el edit del nombre visible
            editTextNombreParcela.setEnabled(true);
            editTextNombreParcela.setVisibility(View.VISIBLE);

            añadirParcela = true;
            List<CoordenadaParcela> coordenadas = new ArrayList<CoordenadaParcela>();
            usuario.addParcela(new Parcela(coordenadas,"Nombre parcela"));

            Poligono poligono = new Poligono(new ArrayList<Marker>(),gMap, new Parcela(new ArrayList<>(),""),new ArrayList<Marker>());
            poligonos.add(poligono);

            btnañadirParcela.setText("Guardar");
            redrawPolygon();
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        LatLng ubicacionCentro = new LatLng(0, 0);
        gMap.setOnMarkerClickListener(Mapa.this);


        // Solicitar permisos de ubicacion en caso de que no los tenga ya la aplicación.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;

        List<LatLng> puntos = new ArrayList<>();
        for (Poligono poligono: poligonos){
            puntos.addAll(poligono.getPuntosLatLng());
        }

        // Verificar permisos nuevamente antes de habilitar la ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            gMap.setMyLocationEnabled(true); // Habilita el botón de ubicación
            gMap.getUiSettings().setMyLocationButtonEnabled(true); // Muestra el botón de ubicación
            Location lastKnownLocation = locationManager.getLastKnownLocation(provider);


        }else{
            if (!poligonos.isEmpty()){
                ubicacionCentro = Poligono.getPolygonCenterLatLng(poligonos.get(indexCamara).getPuntosLatLng());
            }
        }

        // Define la ubicación con un objeto LatLng

        float nivelZoom = 17.0f; // Ajusta este valor para el nivel de zoom que necesitas

        // Centra el mapa en la ubicación deseada con el nivel de zoom especificado
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionCentro, nivelZoom));

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (añadirParcela){
                    Marker marker = gMap.addMarker(new MarkerOptions()
                            .position(point)
                            .draggable(true)); // Permite que el marcador sea desplazable
                    if (marker != null) {
                        getLastPoligono().marcadores.add(marker);
                    }
                    redrawPolygon();
                }
            }
        });

        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {}

            @Override
            public void onMarkerDrag(Marker marker) {}

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // Simplemente redibuja el polígono, las posiciones de los marcadores ya están actualizadas.
                redrawPolygon();
            }
        });

        redrawPolygon();
    }

    private void redrawPolygon() {
        gMap.clear(); // Limpia el mapa para eliminar polígonos y marcadores anteriores
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        gMap.getUiSettings().setScrollGesturesEnabled(true);


        // Creamos el mapa de calor con los datos gps de cada vaca


        int indiceParcela = 0;
        // Dibujamos los poligonos que nos hacen falta.
        for (Poligono poligono : poligonos) {
            poligono.dibujar(añadirParcela,indiceParcela,usuario,false);
            indiceParcela ++;
        }

        // Esto es para que al inicio se pongan los marcadores del usurio.
        if (poligonos.isEmpty() ){
            for (Parcela parcela: usuario.getParcelas()){
                Poligono poligono = new Poligono(new ArrayList<Marker>(), gMap, parcela,new ArrayList<Marker>());
                poligono.setMarcadoresByPoints(parcela.getPuntosLatLong(),añadirParcela);
                poligono.dibujar(añadirParcela,indiceParcela,usuario,false);
                poligonos.add(poligono);
            }
            float nivelZoom = 17.0f; // Ajusta este valor para el nivel de zoom que necesitas
            LatLng ubicacionCentro;

            if (!poligonos.isEmpty()){
                ubicacionCentro = Poligono.getPolygonCenterLatLng(poligonos.get(0).getPuntosLatLng());
            }else{
                if (lastKnownLocation != null){
                    ubicacionCentro = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());

                }else{
                    ubicacionCentro = new LatLng(0,0);
                }
            }
            // Centra el mapa en la ubicación deseada con el nivel de zoom especificado
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionCentro, nivelZoom));
            confSiguienteAnterior();
        }
    }

    private void confSiguienteAnterior(){
        if (poligonos.size()>1){
            btnEliminarParcela.setText("Parcela anterior");
            btnEliminarParcela.setOnClickListener(v -> {
                verParcelaAnterior();
            });
            btnEliminarParcela.setVisibility(View.VISIBLE);

            btnEditarCowFinder.setText("Parcela siguiente");
            btnEditarCowFinder.setOnClickListener(v -> {
                verParcelaSiguiente();
            });
            btnEditarCowFinder.setVisibility(View.VISIBLE);
        }
    }

    private Poligono getLastPoligono(){
        return this.poligonos.get(poligonos.size()-1);
    }

    private void deleteLastMarkers(){
        poligonos.remove(getLastPoligono());
    }

    private Poligono obtenerParcelaPorMarkerCentral(Marker marker){
        indexCamara = 0;
        for (Poligono poligono: poligonos){
            if (poligono.isMarkerInsidePolygon(marker.getPosition(),poligono.getPuntosLatLng())){
                return poligono;
            }
            indexCamara ++;
        }
        return null;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        marker.setTag(marker.getTitle());
        // Esta funcion la usamos par obtener el marcador que a sido pulsado y mostrar si queremos eliminar la parcela o no
        Poligono poligono = obtenerParcelaPorMarkerCentral(marker);
        if (poligono != null){
            editTextNombreParcela.setVisibility(View.VISIBLE);
            editTextNombreParcela.setText(poligono.parcela.getNombre());

            // Configuramos el boton para eliminar la parcela
            btnEliminarParcela.setText("Eliminar");
            btnEliminarParcela.setVisibility(View.VISIBLE);
            btnEliminarParcela.setOnClickListener(v -> {
                server.deleteParcela(poligono.parcela);
                usuario.getParcelas().remove(poligono.parcela);
                poligonos.remove(poligono);
                configurarBtnañadirParcela();
                redrawPolygon();
            });

            // Configuramos el boton para cancelar
            btnañadirParcela.setVisibility(View.VISIBLE);
            btnañadirParcela.setText("Cancelar");
            btnañadirParcela.setOnClickListener(v -> {
                confSiguienteAnterior();
                configurarBtnañadirParcela();
            });

            // Configuramos el boton para editar la parcela
            btnEditarCowFinder.setText("Editar");
            btnEditarCowFinder.setVisibility(View.VISIBLE);
            btnEditarCowFinder.setOnClickListener(v ->{
                Intent intent = new Intent(getApplicationContext(), ParcelaActivity.class);
                intent.putExtra("id_fecha",poligono.parcela.getId());
                if (vaca != null){
                    intent.putExtra("numero_pendiente",vaca.getNumeroPendiente());
                }
                miActivityResultLauncher.launch(intent);
            });

            return true;
        }else{
            return false;
        }
    }

    private void configurarBtnañadirParcela(){
        btnañadirParcela.setText("Añadir parcela");
        btnañadirParcela.setOnClickListener(v1 -> {
            añadirParcela();
        });
    }
}