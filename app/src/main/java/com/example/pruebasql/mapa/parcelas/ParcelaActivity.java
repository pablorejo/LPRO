package com.example.pruebasql.mapa.parcelas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.R;
import com.example.pruebasql.bbdd.parcelas.Coordenada;
import com.example.pruebasql.bbdd.parcelas.CoordenadasSector;
import com.example.pruebasql.bbdd.parcelas.Parcela;
import com.example.pruebasql.bbdd.parcelas.Sector;
import com.example.pruebasql.listeners.ServerCallback;
import com.example.pruebasql.mapa.CowFinder;
import com.example.pruebasql.mapa.Poligono;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class ParcelaActivity extends BarraSuperior implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private Parcela parcela;
    private GoogleMap gMap;

    private EditText editTextNombreParcela,EditTextDiasParcela;

    private Button btnEditarParcela,btnAñadirSectorParcela;

    private boolean editarParcela = false;

    private boolean añadirSector = false;

    private boolean existeSector = false;

    private Poligono poligono;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcela);
        configureToolbar();

        int idParcela = getIntent().getIntExtra("id_fecha",0);
        for (Parcela parcela1: usuario.getParcelas()){
            if(parcela1.getId() == idParcela){
                parcela = parcela1;
                if (parcela.sector != null){
                    existeSector = true;
                }
                break;
            }
        }



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        editTextNombreParcela = findViewById(R.id.editTextNombreParcela);
        editTextNombreParcela.setText(parcela.getNombre());
        editTextNombreParcela.setEnabled(false);

        EditTextDiasParcela = findViewById(R.id.EditTextDiasParcela);
        EditTextDiasParcela.setEnabled(false);

        btnEditarParcela = findViewById(R.id.btnEditarParcela);
        btnAñadirSectorParcela = findViewById(R.id.btnAñadirSectorParcela);
        funtionConfBtnAñadirSector();

        btnEditarParcela.setOnClickListener(v -> {
            funtionEditarParcela();
        });
    }

    private void funtionEditarParcela(){
        editTextNombreParcela.setEnabled(true);
        editarParcela = true;
        redrawPolygon();
        btnAñadirSectorParcela.setText("Guardar");
        btnAñadirSectorParcela.setOnClickListener(v1 -> {
            funtionGuardarParcela();
        });

        btnEditarParcela.setText("Cancelar");
        btnEditarParcela.setOnClickListener(v1 -> {
            funtionCancelarParcela();
        });
    }

    private void funtionCancelarParcela(){
        editarParcela = false;
        Toast.makeText(this, "Cancelar parcela", Toast.LENGTH_SHORT).show();
        btnEditarParcela.setText("Editar");
        btnEditarParcela.setOnClickListener(v -> {funtionEditarParcela();});
        funtionConfBtnAñadirSector();
        redrawPolygon();
    }

    private void funtionGuardarParcela(){
        editarParcela = false;
        Toast.makeText(this, "Guardar parcela", Toast.LENGTH_SHORT).show();
        btnEditarParcela.setText("Editar");
        btnEditarParcela.setOnClickListener(v -> {funtionEditarParcela();});
        funtionConfBtnAñadirSector();
        redrawPolygon();
    }

    private void funtionConfBtnAñadirSector(){
        if(parcela.sector != null){ // Si existe se debe cambiar por Recomendar extensión
            btnAñadirSectorParcela.setText("Recomendar extension");
            btnAñadirSectorParcela.setOnClickListener(v -> {
                funtionRecomendarSector();
            });
        }else{
            btnAñadirSectorParcela.setText("Añadir sector");
            btnAñadirSectorParcela.setOnClickListener(v -> { // Si no existe sector se deberá llamar a la funcion añadirSector
                funtionAñadirSector();
            });
        }
    }

    private void funtionAñadirSector(){
        if (añadirSector){ // Si añadir parcela está a true hay que guardar la parcela.
            añadirSector= false;

            funtionConfBtnAñadirSector();
            if (existeSector){
                server.updateSector(parcela.sector);
            }else{
                server.addSector(parcela.sector);
                existeSector = true;
            }
            funtionConfBtnAñadirSector();

        }else{
            btnAñadirSectorParcela.setText("Guardar sector");

            añadirSector = true;
            ArrayList<CoordenadasSector> coordenadas = new ArrayList<CoordenadasSector>();
            parcela.sector = new Sector(parcela.getId(),usuario.getId());
            parcela.sector.coordenadasSector = coordenadas;
            redrawPolygon();
        }
    }

    private void funtionRecomendarSector(){
        server.recomendarSector(parcela.sector, new ServerCallback() {
            @Override
            public void onResponse(Object response) {
                ArrayList<CoordenadasSector> coordenadasRecomendadas = (ArrayList<CoordenadasSector>) response;
                poligono.marcadoresSector.clear();

                ArrayList<LatLng> puntosRecomendados = new ArrayList<>();
                for (CoordenadasSector coordenadaSector: coordenadasRecomendadas){
                    puntosRecomendados.add(new LatLng(coordenadaSector.latitude,coordenadaSector.longitude));
                }
                poligono.setMarcadoresSectorByPoints(puntosRecomendados ,añadirSector);
                redrawPolygon();
                ArrayList<CoordenadasSector> coordenadasSectorsAnterior = (ArrayList<CoordenadasSector>) parcela.sector.coordenadasSector.clone();

                btnAñadirSectorParcela.setText("Guardar");
                btnAñadirSectorParcela.setOnClickListener(v -> {
                    parcela.sector.coordenadasSector = coordenadasRecomendadas;

                    server.updateSector(parcela.sector);
                    funtionConfBtnAñadirSector();
                    redrawPolygon();

                    btnEditarParcela.setText("Editar parcela");
                    btnEditarParcela.setOnClickListener(v1 -> {
                        funtionEditarParcela();
                    });
                });

                btnEditarParcela.setText("Cancelar");
                btnEditarParcela.setOnClickListener(v -> {

                    btnEditarParcela.setText("Editar parcela");
                    btnEditarParcela.setOnClickListener(v1 -> {
                        funtionEditarParcela();
                    });
                    funtionConfBtnAñadirSector();
                    poligono.setMarcadoresSectorByPoints(parcela.sector.getLatLong() ,añadirSector);
                    redrawPolygon();
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;


        // Solicitar permisos de ubicacion en caso de que no los tenga ya la aplicación.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        poligono = new Poligono(new ArrayList<Marker>(), gMap, parcela,new ArrayList<Marker>());
        poligono.setMarcadoresByPoints(parcela.getPuntosLatLong(),editarParcela);
        if(parcela.sector != null){
            poligono.setMarcadoresSectorByPoints(parcela.sector.getPuntosLatLong(),añadirSector);
        }

        LatLng ubicacionCentro = poligono.getPolygonCenterLatLng(parcela.getPuntosLatLong());
        gMap.setOnMarkerClickListener(ParcelaActivity.this);

        float nivelZoom = 17.0f; // Ajusta este valor para el nivel de zoom que necesitas
        // Centra el mapa en la ubicación deseada con el nivel de zoom especificado
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionCentro, nivelZoom));

        redrawPolygon();


        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (añadirSector) {
                    Marker marker = gMap.addMarker(new MarkerOptions()
                            .position(point)
                            .draggable(true)); // Permite que el marcador sea desplazable
                    if (marker != null) {

                        poligono.parcela.sector.coordenadasSector.add(new CoordenadasSector(marker.getPosition()));
                        poligono.marcadoresSector.add(marker);
                    }
                    redrawPolygon();
                }else if (editarParcela){
                    Marker marker = gMap.addMarker(new MarkerOptions()
                            .position(point)
                            .draggable(true)); // Permite que el marcador sea desplazable
                    if (marker != null) {
                        poligono.marcadores.add(marker);
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
    }

    public void redrawPolygon(){
        // Pimpiamos el mapa
        gMap.clear(); // Limpia el mapa para eliminar polígonos y marcadores anteriores
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        gMap.getUiSettings().setScrollGesturesEnabled(true);


        poligono.dibujar(editarParcela,0,usuario,añadirSector);
    }
}