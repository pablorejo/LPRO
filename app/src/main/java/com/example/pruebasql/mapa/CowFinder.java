package com.example.pruebasql.mapa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.R;
import com.example.pruebasql.Server;
import com.example.pruebasql.bbdd.Parcela;
import com.example.pruebasql.bbdd.Usuario;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.pruebasql.bbdd.vacas.Vaca;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CowFinder extends BarraSuperior implements OnMapReadyCallback{

    private GoogleMap mMap;

    private Button btnañadirParcela;

    private boolean añadirParcela = false;

    private EditText editTextNombreParcela;

    private List<List<Marker>> markersMarkers = new ArrayList<List<Marker>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_finder);
        configureToolbar();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        btnañadirParcela = findViewById(R.id.btnAñadirParcela);
        btnañadirParcela.setOnClickListener(v -> {
            if (añadirParcela){ // Si añadir parcela está a true hay que guardar la parcela.
                añadirParcela= false;
                btnañadirParcela.setText("Añadir parcela");

                // Añadimos la parcela al usuario, también hay que añadirla con el server.
                Parcela parcela = usuario.getUltimaParcela();
                parcela.setNombre(editTextNombreParcela.getText().toString());

                // Cordenadas de la parcela
                ArrayList<LatLng> coordenadas = new ArrayList<LatLng>();
                for (Marker marker: getLastMarkers() ){
                    coordenadas.add(marker.getPosition());
                }
                if (coordenadas.size()> 2){
                    parcela.setCoordenadas(coordenadas);

                    Server server = new Server(this,usuario);
                    server.addParcela(parcela);
                }else{
                    Toast.makeText(this, "Puntos insuficientes, minimo 3", Toast.LENGTH_SHORT).show();
                    usuario.getParcelas().remove(parcela);
                    deleteLastMarkers();
                }
                redrawPolygon();

                editTextNombreParcela.setEnabled(false);
                editTextNombreParcela.setVisibility(View.GONE);
                editTextNombreParcela.setText("Nombre parcela");

            }else{
                // Hacemos el edit del nombre visible
                editTextNombreParcela.setEnabled(true);
                editTextNombreParcela.setVisibility(View.VISIBLE);

                añadirParcela = true;
                List<LatLng> points = new ArrayList<LatLng>();
                usuario.addParcela(new Parcela(points,"Nombre parcela"));

                List<Marker> markers = new ArrayList<Marker>();
                markersMarkers.add(markers);

                btnañadirParcela.setText("Guardar");
                redrawPolygon();
            }
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
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng ubicacionCentro = new LatLng(43.31195130632422, -8.416801609724955);

        // Solicitar permisos de ubicacion en caso de que no los tenga ya la aplicación.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;

        // Verificar permisos nuevamente antes de habilitar la ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true); // Habilita el botón de ubicación
            mMap.getUiSettings().setMyLocationButtonEnabled(true); // Muestra el botón de ubicación
            Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();
                ubicacionCentro = new LatLng(latitude, longitude);
            }
        }

        // Define la ubicación con un objeto LatLng

        float nivelZoom = 17.0f; // Ajusta este valor para el nivel de zoom que necesitas

        // Centra el mapa en la ubicación deseada con el nivel de zoom especificado
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionCentro, nivelZoom));


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (añadirParcela){
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(point)
                            .draggable(true)); // Permite que el marcador sea desplazable
                    if (marker != null) {
                        getLastMarkers().add(marker);
                    }
                    redrawPolygon();
                }
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
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
        mMap.clear(); // Limpia el mapa para eliminar polígonos y marcadores anteriores
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


        // Creamos el mapa de calor con los datos gps de cada vaca
        for (Vaca vaca: usuario.getVacas() ){
            List<LatLng> locations = vaca.getCordenadasGps();
            // Verificar que la lista no esté vacía
            if (!locations.isEmpty()) {
                // Crear el proveedor del mapa de calor con los datos
                HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                        .data(locations)
                        .build();

                // Añadir el overlay del mapa de calor al Google Map
                TileOverlay overlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
            }
        }


        int indiceParcela = 0;
        for (List<Marker> markers : markersMarkers) {
            List<LatLng> points = new ArrayList<LatLng>();
            List<Marker> updatedMarkers = new ArrayList<>(); // Lista temporal para guardar los nuevos marcadores
            for (Marker marker: markers){
                points.add(marker.getPosition());

                Marker newMarker = mMap.addMarker(new MarkerOptions().position(marker.getPosition()).draggable(true));
                updatedMarkers.add(newMarker); // Guarda el nuevo marcador en la lista temporal
                if (!añadirParcela){
                    newMarker.setVisible(false);
                }
            }

            markers.clear();
            markers.addAll(updatedMarkers);

            if (points.size() > 2) {
                mMap.addPolygon(new PolygonOptions()
                        .addAll(points)
                        .strokeColor(Color.RED)
                        .fillColor(Color.argb(0, 0, 0, 0)));

                mMap.addMarker(new MarkerOptions()
                        .position(getPolygonCenter(markers))
                        .title(usuario.getParcelas().get(indiceParcela).getNombre())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }
            indiceParcela ++;
        }
    }

    private LatLng getPolygonCenter(List<Marker> markers) {

        List<LatLng> points = new ArrayList<LatLng>();
        for (Marker marker: markers){
            points.add(marker.getPosition());
        }

        double latitude = 0;
        double longitude = 0;
        int count = points.size();

        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }

        return new LatLng(latitude / count, longitude / count);
    }

    private List<Marker> getLastMarkers(){


        return this.markersMarkers.get(markersMarkers.size()-1);
    }

    private void deleteLastMarkers(){
        markersMarkers.remove(getLastMarkers());
    }

    private List<List<LatLng>> getPoints(){
        List<List<LatLng>> pointsPoints = new ArrayList<List<LatLng>>();
        for (List<Marker> markers : markersMarkers){
            List<LatLng> points = new ArrayList<LatLng>();
            for (Marker marker: markers){
                points.add(marker.getPosition());
            }
            pointsPoints.add(points);
        }
        return pointsPoints;
    }

    private class Marcadores {
        private List<Marker> marcadores;
        private Parcela parcela;

        public Marcadores(List<Marker> marcadores, Parcela parcela) {
            this.marcadores = marcadores;
            this.parcela = parcela;
        }

        // Getters y setters
        public List<Marker> getMarcadores() {
            return marcadores;
        }

        public void setMarcadores(List<Marker> marcadores) {
            this.marcadores = marcadores;
        }

        public Parcela getParcela() {
            return parcela;
        }

        public void setParcela(Parcela parcela) {
            this.parcela = parcela;
        }
    }
}