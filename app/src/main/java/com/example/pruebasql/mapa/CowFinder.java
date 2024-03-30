package com.example.pruebasql.mapa;

import androidx.annotation.NonNull;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.R;
import com.example.pruebasql.Server;
import com.example.pruebasql.bbdd.parcelas.Coordenada;
import com.example.pruebasql.bbdd.parcelas.Parcela;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pruebasql.bbdd.vacas.Vaca;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.schedulers.Timed;

public class CowFinder extends BarraSuperior implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private GoogleMap gMap;

    private Button btnañadirParcela, btnEliminarParcela, btnFiltrarNumeroPendienteMapa,btnFechaInicioFiltroMapa,btnFechaFinFiltroMapa;

    private boolean añadirParcela = false;

    private EditText editTextNombreParcela;

    private List<Poligono> poligonos = new ArrayList<Poligono>();

    private Server server;

    private Vaca vaca;
    private boolean[] elementosSeleccionados;
    private String[] numeros;

    private Date fechaInicio, fechaFin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_finder);
        configureToolbar();

        String numeroPendienteString = getIntent().getStringExtra("numero_pendiente");
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

        btnFiltrarNumeroPendienteMapa = findViewById(R.id.btnFiltrarNumeroPendienteMapa);

        numeros = usuario.getNumerosPendiente().toArray(new String[0]);
        elementosSeleccionados = new boolean[numeros.length];
        btnFiltrarNumeroPendienteMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogoBuilder = new AlertDialog.Builder(CowFinder.this);
                dialogoBuilder.setTitle("Selecciona números");
                dialogoBuilder.setMultiChoiceItems(numeros, elementosSeleccionados, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                        // Actualiza el estado del elemento seleccionado
                        elementosSeleccionados[i] = isChecked;
                    }
                });

                dialogoBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        redrawPolygon();
                    }
                });

                dialogoBuilder.setNegativeButton("Cancelar", null);

                AlertDialog dialogo = dialogoBuilder.create();
                dialogo.show();

            }
        });
        btnFechaInicioFiltroMapa = findViewById(R.id.btnFechaInicioFiltroMapa);
        btnFechaInicioFiltroMapa.setOnClickListener(v -> {
            openDialogFecha(false);
        });
        btnFechaFinFiltroMapa = findViewById(R.id.btnFechaFinFiltroMapa);
        btnFechaFinFiltroMapa.setOnClickListener(v -> {
            openDialogFecha(true);
        });
    }

    private void añadirParcela(){
        if (añadirParcela){ // Si añadir parcela está a true hay que guardar la parcela.
            añadirParcela= false;

            configurarBtnañadirParcela();

            // Añadimos la parcela al usuario, también hay que añadirla con el server.
            Parcela parcela = usuario.getUltimaParcela();
            parcela.setNombre(editTextNombreParcela.getText().toString());

            // Cordenadas de la parcela
            List<Coordenada> coordenadas = getLastPoligono().getCoordenadas();

            if (coordenadas.size()> 2){
                parcela.setCoordenadas(coordenadas);
                server.addParcela(parcela);
            }else{
                Toast.makeText(this, "Puntos insuficientes, minimo 3", Toast.LENGTH_SHORT).show();
                usuario.getParcelas().remove(parcela);
                deleteLastMarkers();
            }
            redrawPolygon();

            // Comprobar si hay cambios en otras parcelas.
            for (int k = 0; k<poligonos.size(); k++){
                Poligono poligono = poligonos.get(k);
                poligono.updateMarkers(añadirParcela);

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
            List<Coordenada> coordenadas = new ArrayList<Coordenada>();
            usuario.addParcela(new Parcela(coordenadas,"Nombre parcela"));

            Poligono poligono = new Poligono(new ArrayList<Marker>(),gMap, new Parcela(new ArrayList<>(),""));
            poligonos.add(poligono);

            btnañadirParcela.setText("Guardar");
            redrawPolygon();
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        LatLng ubicacionCentro = new LatLng(43.31195130632422, -8.416801609724955);
        gMap.setOnMarkerClickListener(CowFinder.this);


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

            gMap.setMyLocationEnabled(true); // Habilita el botón de ubicación
            gMap.getUiSettings().setMyLocationButtonEnabled(true); // Muestra el botón de ubicación
            Location lastKnownLocation = locationManager.getLastKnownLocation(provider);

            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();
                ubicacionCentro = new LatLng(latitude, longitude);
            }
        }else{
            List<LatLng> puntos = new ArrayList<>();
            for (Poligono poligono: poligonos){
                puntos.addAll(poligono.getPuntosLatLng());
            }
            if (poligonos.get(0) != null){
                ubicacionCentro = poligonos.get(0).getPolygonCenterLatLng(puntos);
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
        if (vaca != null){
            addHeatPointsVaca(vaca);
        } else{
            ArrayList<String> numerosSeleccionados = new ArrayList<>();
            for (int j = 0; j < elementosSeleccionados.length; j++) {
                if (elementosSeleccionados[j]) {
                    numerosSeleccionados.add(numeros[j]);
                }
            }
            if (numerosSeleccionados.size() != 0){
                addHeatPointsVaca(numerosSeleccionados);
            }else{
                addHeatPointsVaca(usuario.getNumerosPendiente());
            }
        }

        int indiceParcela = 0;
        // Dibujamos los poligonos que nos hacen falta.
        for (Poligono poligono : poligonos) {
            poligono.dibujar(añadirParcela,indiceParcela,usuario);
            indiceParcela ++;
        }

        // Esto es para que al inicio se pongan los marcadores del usurio.
        if (poligonos.isEmpty() ){
            for (Parcela parcela: usuario.getParcelas()){
                Poligono poligono = new Poligono(new ArrayList<Marker>(), gMap, parcela);
                poligono.setMarcadoresByPoints(parcela.getPuntosLatLong(),añadirParcela);
                poligono.dibujar(añadirParcela,indiceParcela,usuario);
                poligonos.add(poligono);
            }
        }
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

    private Poligono getLastPoligono(){
        return this.poligonos.get(poligonos.size()-1);
    }

    private void deleteLastMarkers(){
        poligonos.remove(getLastPoligono());
    }

    private Poligono obtenerParcelaPorMarkerCentral(Marker marker){

        for (Poligono poligono: poligonos){
            if (poligono.isMarkerInsidePolygon(marker.getPosition(),poligono.getPuntosLatLng())){
                return poligono;
            }
        }
        return null;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        marker.setTag(marker.getTitle());
        // Esta funcion la usamos par obtener el marcador que a sido pulsado y mostrar si queremos eliminar la parcela o no
        Poligono poligono = obtenerParcelaPorMarkerCentral(marker);
        if (poligono != null){
            btnEliminarParcela.setVisibility(View.VISIBLE);
            btnEliminarParcela.setOnClickListener(v -> {
                server.deleteParcela(poligono.parcela);
                usuario.getParcelas().remove(poligono.parcela);
                poligonos.remove(poligono);
                configurarBtnañadirParcela();
                redrawPolygon();
            });

            btnañadirParcela.setVisibility(View.VISIBLE);
            btnañadirParcela.setText("Cancelar");

            btnañadirParcela.setOnClickListener(v -> {
                configurarBtnañadirParcela();
            });

            return true;
        }else{
            return false;
        }
    }

    private void configurarBtnañadirParcela(){
        btnañadirParcela.setText("Añadir parcela");
        btnEliminarParcela.setVisibility(View.GONE);
        btnañadirParcela.setOnClickListener(v1 -> {
            añadirParcela();
        });
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
                                CowFinder.this,
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