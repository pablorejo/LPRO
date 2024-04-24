package com.example.pruebasql.mapa.parcelas;

import android.Manifest;
import android.app.AlertDialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.R;
import com.example.pruebasql.bbdd.parcelas.CoordenadaDensidad;
import com.example.pruebasql.bbdd.parcelas.CoordenadasSector;
import com.example.pruebasql.bbdd.parcelas.Parcela;
import com.example.pruebasql.bbdd.parcelas.Sector;
import com.example.pruebasql.bbdd.vacas.Gps;
import com.example.pruebasql.bbdd.vacas.Vaca;
import com.example.pruebasql.listeners.ServerCallback;
import com.example.pruebasql.mapa.Poligono;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.common.reflect.TypeToken;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ParcelaActivity extends BarraSuperior implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private Parcela parcela;
    private GoogleMap gMap;

    private EditText editTextNombreParcela,EditTextDiasParcela;

    private Button btnEditarParcela,btnAñadirSectorParcela;

    private Button btnFiltrarNumeroPendienteMapa,btnFechaInicioFiltroMapa,btnFechaFinFiltroMapa, btnLlamada;
    private Button btneliminarSector,btnEditarSector;
    private Button btnTipoMapa;

    private boolean editarParcela = false;

    private boolean añadirSector = false;

    private boolean existeSector = false;

    private Poligono poligono;

    private boolean[] elementosSeleccionados;
    private String[] numeros;
    private Integer[] seleccionadosArray;
    private Date fechaInicio, fechaFin;

    private Vaca vaca;

    private boolean changes = true;

    private boolean ponerMapaCalorBol = true;

    private String tipo = Gps.NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcela);
        configureToolbar();

        String numeroPendienteString = getIntent().getStringExtra("numero_pendiente");
        System.out.println("El valor del numero pendiente: " + numeroPendienteString);
        if (numeroPendienteString != null && !numeroPendienteString.equals("")){
            vaca = usuario.getVacaByNumeroPendiente(Integer.parseInt(numeroPendienteString));
        }


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

        //EditTextDiasParcela = findViewById(R.id.EditTextDiasParcela);
        //EditTextDiasParcela.setEnabled(false);

        btnEditarParcela = findViewById(R.id.btnEditarParcela);
        btnAñadirSectorParcela = findViewById(R.id.btnAñadirSectorParcela);
        funtionConfBtnAñadirSector();

        btnEditarParcela.setOnClickListener(v -> {
            funtionEditarParcela();
        });

        btnFiltrarNumeroPendienteMapa = findViewById(R.id.btnFiltrarNumeroPendienteMapa);

        numeros = usuario.getNumerosPendiente().toArray(new String[0]);
        elementosSeleccionados = new boolean[numeros.length];
        btnFiltrarNumeroPendienteMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogoBuilder = new AlertDialog.Builder(ParcelaActivity.this);
                dialogoBuilder.setTitle("Selecciona números");

                // Array que guarda el estado de los elementos seleccionados
                final boolean[] elementosSeleccionados = new boolean[numeros.length];
                // Lista para almacenar los números seleccionados
                final List<Integer> numerosSeleccionados = new ArrayList<>();

                dialogoBuilder.setMultiChoiceItems(numeros, elementosSeleccionados, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index, boolean isChecked) {
                        elementosSeleccionados[index] = isChecked;
                        // Si está marcado, añadir a la lista, si no, remover
                        if (isChecked) {
                            numerosSeleccionados.add(Integer.valueOf(numeros[index]));
                        } else {
                            numerosSeleccionados.remove(Integer.valueOf(numeros[index]));
                        }
                    }
                });

                dialogoBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Aquí puedes procesar o usar la lista de números seleccionados
                        changes = true;
                        seleccionadosArray = numerosSeleccionados.toArray(new Integer[0]);
                        redrawPolygon();
                    }
                });

                dialogoBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changes = false;
                    }
                });

                AlertDialog dialogo = dialogoBuilder.create();
                dialogo.show();
            }
        });

        btnFechaInicioFiltroMapa = findViewById(R.id.btnFechaInicioFiltroMapa);
        btnFechaInicioFiltroMapa.setOnClickListener(v -> {
            changes = true;
            openDialogFecha(false);
        });
        btnFechaFinFiltroMapa = findViewById(R.id.btnFechaFinFiltroMapa);
        btnFechaFinFiltroMapa.setOnClickListener(v -> {
            changes = true;
            openDialogFecha(true);
        });

        btnLlamada = findViewById(R.id.btnLlamada);
        btnLlamada.setOnClickListener(v -> {
            //server.call(numeroPendiente);
            server.call(34);
        });

        btneliminarSector = findViewById(R.id.btneliminarSector);
        btnEditarSector = findViewById(R.id.btnEditarSector);
        btneliminarSector.setOnClickListener(v -> {
            eliminarSector();
        });
        if (parcela.sector == null){
            btneliminarSector.setVisibility(View.GONE);
            btnEditarSector.setVisibility(View.GONE);
        }

        btnTipoMapa = findViewById(R.id.btnTipoMapa);
        btnTipoMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {Gps.NORMAL, Gps.PASTANDO, Gps.CAMINANDO, Gps.DESCANSANDO};
                AlertDialog.Builder builder = new AlertDialog.Builder(ParcelaActivity.this);
                builder.setTitle("Elige una opción");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tipo = items[which];
                        changes = true;
                        redrawPolygon();
                    }
                });
                builder.show();
            }
        });
    }

    private void editarSector(){

        //server.updateSector(parcela.sector);
    }

    private void eliminarSector(){
        server.deleteSector(parcela.sector);
        parcela.sector = null;
        redrawPolygon();
        btneliminarSector.setVisibility(View.GONE);
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
        server.updateParcela(parcela);
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

            if (existeSector){
                server.updateSector(parcela.sector);
            }else{
                server.addSector(parcela.sector, new ServerCallback() {
                    @Override
                    public void onResponse(Object response) {
                        parcela.sector = (Sector) response;
                    }
                });
                existeSector = true;
            }
            funtionConfBtnAñadirSector();

        }else{
            añadirSector = true;
            ArrayList<CoordenadasSector> coordenadas = new ArrayList<CoordenadasSector>();
            parcela.sector = new Sector(parcela.getId(),usuario.getId());
            parcela.sector.coordenadasSector = coordenadas;
            redrawPolygon();
            btnAñadirSectorParcela.setText("Guardar sector");
            //funtionConfBtnAñadirSector();
        }
        redrawPolygon();
    }

    private void funtionRecomendarSector(){
        server.recomendarSector(parcela.sector, new ServerCallback() {
            @Override
            public void onResponse(Object response) {
                ArrayList<LatLng> puntosRecomendados = (ArrayList<LatLng>) response;
                poligono.marcadoresSector.clear();

                ponerMapaCalorBol = false;
                poligono.setMarcadoresSectorByPoints(puntosRecomendados ,añadirSector);
                redrawPolygon();
                ArrayList<CoordenadasSector> coordenadasSectorsAnterior = (ArrayList<CoordenadasSector>) parcela.sector.coordenadasSector.clone();

                btnAñadirSectorParcela.setText("Guardar");
                btnAñadirSectorParcela.setOnClickListener(v -> {
                    ponerMapaCalorBol = true;
                    ArrayList<CoordenadasSector> coordenadasRecomendadas = new ArrayList<>();
                    for (LatLng latLng : puntosRecomendados){
                        coordenadasRecomendadas.add(new CoordenadasSector(latLng));
                    }

                    parcela.sector.coordenadasSector = coordenadasRecomendadas;

                    server.updateSector(parcela.sector);
                    redrawPolygon();

                    btnEditarParcela.setText("Editar parcela");
                    btnEditarParcela.setOnClickListener(v1 -> {
                        funtionEditarParcela();
                    });
                    funtionConfBtnAñadirSector();
                });

                btnEditarParcela.setText("Cancelar");
                btnEditarParcela.setOnClickListener(v -> {
                    ponerMapaCalorBol = true;
                    btnEditarParcela.setText("Editar parcela");
                    btnEditarParcela.setOnClickListener(v1 -> {
                        funtionEditarParcela();
                    });
                    funtionConfBtnAñadirSector();
                    poligono.setMarcadoresSectorByPoints(parcela.sector.getLatLong() ,añadirSector);
                    redrawPolygon();
                });
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

        LatLng ubicacionCentro = Poligono.getPolygonCenterLatLng(parcela.getPuntosLatLong());
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

    private void addHeatPointsVaca(ArrayList<String> numerosPendienteVaca){
        for (String numeroPendiente: numerosPendienteVaca){
            Vaca vaca1 = usuario.getVacaByNumeroPendiente(Integer.valueOf(numeroPendiente));
            addHeatPointsVaca(vaca1);
        }
    }

    /**
     * Añade los puntos al heatmap de las ubicaciones de una vaca en concreto
     * @param vaca: Clase vaca
     */
    private void addHeatPointsVaca(Vaca vaca){
        List<LatLng> locations = vaca.getDatosGpsByFechaInicioYFechaFin(fechaInicio,fechaFin);
        // Verificar que la lista no esté vacía
        if (!locations.isEmpty()) {
            // Crear el proveedor del mapa de calor con los datos
            HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                    .data(locations)
                    .build();

            // Añadir el overlay del mapa de calor al Google Map
            TileOverlay overlay = gMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
        }
    }

    private void addHeatPointsCoordenadasDensidad(ArrayList<LatLng> coordenadaDensidads){
        // Verificar que la lista no esté vacía
        if (!coordenadaDensidads.isEmpty()) {
            // Crear el proveedor del mapa de calor con los datos
            HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                    .data(coordenadaDensidads)
                    .build();

            // Añadir el overlay del mapa de calor al Google Map
            TileOverlay overlay = gMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
        }
    }

    private void ponerMapaCalor(){
        server.getGPS(fechaInicio, fechaFin, seleccionadosArray, parcela.getId(), tipo, new ServerCallback() {
            @Override
            public void onResponse(Object response) {
                ArrayList<LatLng> coordenadas = (ArrayList<LatLng>) response;
                addHeatPointsCoordenadasDensidad(coordenadas);
                changes = false;
            }
        });
    }

    public void redrawPolygon(){
        // Pimpiamos el mapa
        gMap.clear(); // Limpia el mapa para eliminar polígonos y marcadores anteriores
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        gMap.getUiSettings().setScrollGesturesEnabled(true);

        if (changes && ponerMapaCalorBol){
            ponerMapaCalor();
        }

        //if (vaca != null){
        //    addHeatPointsVaca(vaca);
        //} else{

        /*ArrayList<String> numerosSeleccionados = new ArrayList<>();
        for (int j = 0; j < elementosSeleccionados.length; j++) {
            if (elementosSeleccionados[j]) {
                numerosSeleccionados.add(numeros[j]);
            }
        }
        if (numerosSeleccionados.size() != 0){
            addHeatPointsVaca(numerosSeleccionados);
        }else{
            addHeatPointsVaca(usuario.getNumerosPendiente());
        }*/


        if (parcela.sector == null){
            poligono.polygonOptionsSector = null;
        }
        poligono.dibujar(editarParcela,0,usuario,añadirSector);
    }

    /**
     * Lo que hace la función es mostrar un widget para especificar el día y la hora y lo guarda en las variables privadas de fechaFin y fechaInicio
     * @param boolFechaFin: Si está a true guarda la fecha fin si está a false guarda la fecha inicio.
     */
    public void openDialogFecha(boolean boolFechaFin){
        final Calendar calendario = Calendar.getInstance();
        if (boolFechaFin && fechaFin!= null){
            calendario.setTime(fechaFin);
        }else if(fechaInicio!= null){
            calendario.setTime(fechaInicio);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendario.set(Calendar.YEAR, year);
                        calendario.set(Calendar.MONTH, monthOfYear);
                        calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Ahora que tienes la fecha, abre el TimePickerDialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                ParcelaActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        calendario.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendario.set(Calendar.MINUTE, minute);

                                        // Aquí tienes la fecha y la hora en el objeto calendario
                                        if (boolFechaFin){
                                            fechaFin = calendario.getTime();
                                        }else{
                                            fechaInicio = calendario.getTime();
                                        }
                                        // Ahora puedes actualizar el mapa o lo que necesites hacer con la fecha y hora
                                        redrawPolygon();
                                    }
                                },
                                calendario.get(Calendar.HOUR_OF_DAY),
                                calendario.get(Calendar.MINUTE),
                                true // Modo 24 horas
                        );
                        timePickerDialog.show();
                    }
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}