package com.example.pruebasql.mapa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.R;
import com.example.pruebasql.bbdd.Usuario;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.common.collect.Maps;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CowFinder extends BarraSuperior implements OnMapReadyCallback{

    private GoogleMap mMap;

    List<Marker> markers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_finder);
        configureToolbar();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Define la ubicación con un objeto LatLng
        LatLng ubicacionCentro = new LatLng(43.31195130632422, -8.416801609724955); // Ejemplo: Buenos Aires, Argentina
        float nivelZoom = 17.0f; // Ajusta este valor para el nivel de zoom que necesitas


        // Centra el mapa en la ubicación deseada con el nivel de zoom especificado
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionCentro, nivelZoom));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .draggable(true)); // Permite que el marcador sea desplazable
                if (marker != null) {
                    markers.add(marker);
                }
                redrawPolygon();
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {}

            @Override
            public void onMarkerDrag(Marker marker) {}

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // Actualizamos el array de marcadores.

                for (int i = 0; i < markers.size(); i++){
                    if (markers.get(i).getId().equals(marker.getId())){
                        markers.get(i).setPosition(marker.getPosition());
                    }
                }
                redrawPolygon();
            }
        });
    }

    private List<LatLng> getYourData() {
        // Aquí deberías recuperar/crear tu lista de objetos LatLng
        List<LatLng> list = new ArrayList<>();
        list.add(new LatLng(43.31175851678619, -8.418459493325816));
        list.add(new LatLng(43.31164765757602, -8.41805161849009));
        list.add(new LatLng(43.311597732036496, -8.41626661611641));
        list.add(new LatLng(43.31193316579331, -8.416527059726878));
        list.add(new LatLng(43.31067584103942, -8.416431326485327));
        list.add(new LatLng(43.31061090648096, -8.417790435170705));
        list.add(new LatLng(43.31099032969088, -8.415775759833384));
        list.add(new LatLng(43.31069869536366, -8.416838605570206));
        list.add(new LatLng(43.31082378281837, -8.4158906673425));
        list.add(new LatLng(43.31160873971172, -8.416117176231438));
        list.add(new LatLng(43.3123327125249, -8.418677420084384));
        list.add(new LatLng(43.3123932262803, -8.416690091890604));
        list.add(new LatLng(43.312583723422186, -8.416823522595358));
        list.add(new LatLng(43.311696106334985, -8.416912019316852));
        list.add(new LatLng(43.31110027391736, -8.41667116531951));
        list.add(new LatLng(43.31107785633847, -8.416867063597172));
        list.add(new LatLng(43.310512151814, -8.416588635759624));
        list.add(new LatLng(43.31269057364387, -8.416067861351491));
        list.add(new LatLng(43.31155408344095, -8.417111180535835));
        list.add(new LatLng(43.31079235410967, -8.41592399120384));
        list.add(new LatLng(43.3106930956568, -8.416278652328396));
        list.add(new LatLng(43.31174017954888, -8.416485285612195));
        list.add(new LatLng(43.311140936709045, -8.415780517890383));
        list.add(new LatLng(43.311153921643296, -8.418003265253724));
        list.add(new LatLng(43.31156084392801, -8.417049149821379));
        list.add(new LatLng(43.31055884910463, -8.417125327624369));
        list.add(new LatLng(43.31255060833937, -8.41669326689292));
        list.add(new LatLng(43.31246948440388, -8.416570346162414));
        list.add(new LatLng(43.31237906979592, -8.417366287845281));
        list.add(new LatLng(43.31194846168894, -8.416930966289298));
        list.add(new LatLng(43.31199872270574, -8.417021442684728));
        list.add(new LatLng(43.31204930849728, -8.41696702992639));
        list.add(new LatLng(43.31101375774912, -8.41582418340402));
        list.add(new LatLng(43.31124349262044, -8.41689567672067));
        list.add(new LatLng(43.31148229706132, -8.415749161066998));
        list.add(new LatLng(43.31106958903103, -8.415783390908773));
        list.add(new LatLng(43.31158159968763, -8.416692983265646));
        list.add(new LatLng(43.31063347703429, -8.416154382818785));
        list.add(new LatLng(43.31235321554213, -8.418757956743356));
        list.add(new LatLng(43.31124594671412, -8.41759228193805));
        list.add(new LatLng(43.31115610396627, -8.416031228693425));
        list.add(new LatLng(43.31108916899425, -8.416866991707904));
        list.add(new LatLng(43.31195130632422, -8.416801609724955));
        list.add(new LatLng(43.31089039853586, -8.415641559121287));
        list.add(new LatLng(43.31099414107394, -8.415928880436033));
        list.add(new LatLng(43.31239956897884, -8.4163897996269));
        list.add(new LatLng(43.31205348642612, -8.416553519339288));
        list.add(new LatLng(43.31075221333162, -8.416905815566922));
        list.add(new LatLng(43.31144244370642, -8.416000624497801));
        list.add(new LatLng(43.31075380282665, -8.417347035481587));
        list.add(new LatLng(43.311864137036565, -8.415772790299046));
        list.add(new LatLng(43.31185909064828, -8.416168245814976));
        list.add(new LatLng(43.31188061248351, -8.416125182872227));
        list.add(new LatLng(43.31153231976745, -8.415900169234016));
        list.add(new LatLng(43.31230224470357, -8.418277648119993));
        list.add(new LatLng(43.311971049984926, -8.416744748058107));
        list.add(new LatLng(43.31156387408696, -8.417107210774487));
        list.add(new LatLng(43.31098726603104, -8.41614387186507));
        list.add(new LatLng(43.31167233456869, -8.415823720646237));
        list.add(new LatLng(43.31167059562125, -8.416920177182577));
        list.add(new LatLng(43.31154547740984, -8.416495015442559));
        list.add(new LatLng(43.31069513340855, -8.417504996431202));
        list.add(new LatLng(43.31243742852751, -8.415797780539169));
        list.add(new LatLng(43.31145223089769, -8.416392912118498));
        list.add(new LatLng(43.31147174374713, -8.415621359530132));
        list.add(new LatLng(43.31167908408217, -8.416378227631851));
        list.add(new LatLng(43.31209130528068, -8.416480740351211));
        list.add(new LatLng(43.31051705821914, -8.416894396508049));
        list.add(new LatLng(43.31203924215901, -8.416193844943423));
        list.add(new LatLng(43.311005852455295, -8.416238664188137));
        list.add(new LatLng(43.31141600143268, -8.416488648300033));
        list.add(new LatLng(43.31201050410895, -8.41603350367292));
        list.add(new LatLng(43.31140239181273, -8.41654780568092));
        list.add(new LatLng(43.31182041944552, -8.416285174292423));
        list.add(new LatLng(43.3104459009942, -8.41692592901043));
        list.add(new LatLng(43.31191642144496, -8.419114676761414));
        list.add(new LatLng(43.31055844493872, -8.415889645975131));
        list.add(new LatLng(43.31103014852497, -8.417338395079783));
        list.add(new LatLng(43.31080010713557, -8.417152459293773));
        list.add(new LatLng(43.31168305202605, -8.417593341580744));
        list.add(new LatLng(43.31141515024852, -8.415812484614273));
        list.add(new LatLng(43.310998325075005, -8.417575434579106));
        list.add(new LatLng(43.31218084405534, -8.415871398790927));
        list.add(new LatLng(43.3128585103503, -8.415933404029632));
        list.add(new LatLng(43.31083547106585, -8.416296083748785));
        list.add(new LatLng(43.31100524564, -8.415950336420849));
        list.add(new LatLng(43.31149784535726, -8.41651090139516));
        list.add(new LatLng(43.31123854262913, -8.416777049155016));
        list.add(new LatLng(43.31141154261754, -8.418150532439908));
        list.add(new LatLng(43.31236913343638, -8.416952828137678));
        list.add(new LatLng(43.31145584274704, -8.417733661423782));
        list.add(new LatLng(43.31125114479085, -8.416727902327597));
        list.add(new LatLng(43.310517782590274, -8.415759385272372));
        list.add(new LatLng(43.31090344106662, -8.418562623894127));
        list.add(new LatLng(43.31092018333883, -8.416480914812745));
        list.add(new LatLng(43.31061191702232, -8.418075649434094));
        list.add(new LatLng(43.31140002513376, -8.416444755983349));
        list.add(new LatLng(43.312674606085785, -8.416190706187907));
        list.add(new LatLng(43.31232170751926, -8.416736267768064));
        list.add(new LatLng(43.31138209054755, -8.417398202061468));

        return list;
    }

    private void redrawPolygon() {
        mMap.clear(); // Limpia el mapa para eliminar polígonos y marcadores anteriores
        List<LatLng> points = new ArrayList<>();
        for (Marker marker : markers) {
            points.add(marker.getPosition());
            mMap.addMarker(new MarkerOptions().position(marker.getPosition()).draggable(true)); // Re-añade los marcadores para asegurarte de que se muestren después de limpiar el mapa
        }

        if (points.size() > 2) {
            mMap.addPolygon(new PolygonOptions()
                    .addAll(points)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(128, 255, 0, 0)));
        }
    }
}