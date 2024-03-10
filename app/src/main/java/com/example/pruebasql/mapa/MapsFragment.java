package com.example.pruebasql.mapa;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.example.pruebasql.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends FragmentActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps); // Asegúrate de que esto coincida con tu layout
        // Obtener el SupportMapFragment y ser notificado cuando el mapa esté listo para ser usado.
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Obtener los datos para el mapa de calor
        List<LatLng> locations = getYourData();

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

    private void addHeatMap() {
        List<LatLng> list = getYourData(); // Implementa este método para obtener tus datos

        // Crea un heat map tile provider, pasando los datos
        mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();
        // Añade el overlay al mapa. Nota que esto va después de que el mapa está listo.
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    private List<LatLng> getYourData() {
        // Aquí deberías recuperar/crear tu lista de objetos LatLng
        List<LatLng> list = new ArrayList<>();
        list.add(new LatLng(-34, 151)); // Ejemplo de datos
        list.add(new LatLng(-35, 152)); // Añade tus propios datos aquí
        return list;
    }
}