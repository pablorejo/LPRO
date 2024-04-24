package com.example.pruebasql.mapa;

import android.graphics.Color;

import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.parcelas.Coordenada;
import com.example.pruebasql.bbdd.parcelas.CoordenadaParcela;
import com.example.pruebasql.bbdd.parcelas.Parcela;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class Poligono {
    public List<Marker> marcadores;

    public List<Marker> marcadoresSector;
    public PolygonOptions polygonOptions;
    public Parcela parcela;
    public GoogleMap gMap;

    public PolygonOptions polygonOptionsSector;

    public boolean modificado = false;

    public Poligono(List<Marker> marcadores, GoogleMap gMap, Parcela parcela, List<Marker> marcadoresSector){
        this.marcadores = marcadores;
        this.marcadoresSector = marcadoresSector;
        this.polygonOptions = new PolygonOptions()
                .addAll(getPuntosLatLng())
                .strokeColor(Color.RED)
                .fillColor(Color.argb(0, 0, 0, 0));

        if (parcela.sector != null){
            this.polygonOptionsSector = new PolygonOptions()
                    .addAll(parcela.sector.getLatLong())
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.argb(1, 0, 0, 0));
        }
        this.gMap = gMap;
        this.parcela = parcela;
    }

    public List<LatLng> getPuntosLatLng(){
        List<LatLng> puntos = new ArrayList<>();
        for (Marker marker: marcadores){
            puntos.add(marker.getPosition());
        }
        return puntos;
    }

    public List<LatLng> getPuntosLatLngSector(){
        List<LatLng> puntos = new ArrayList<>();
        for (Marker marker: marcadoresSector){
            puntos.add(marker.getPosition());
        }
        return puntos;
    }

    public void setMarcadoresByPoints(List<LatLng> points, boolean añadirParcela){
        for (LatLng point : points) {
            Marker newMarker = gMap.addMarker(
                    new MarkerOptions()
                            .position(point)
                            .draggable(true));
            if (!añadirParcela){
                newMarker.setVisible(false);

            }
            marcadores.add(newMarker);
        }
    }

    public void setMarcadoresSectorByPoints(List<LatLng> points, boolean añadirSector) {
        marcadoresSector = new ArrayList<Marker>();
        for (LatLng point : points) {
            Marker newMarker = gMap.addMarker(
                    new MarkerOptions()
                            .position(point)
                            .draggable(true));
            if (!añadirSector) {
                newMarker.setVisible(false);
            }
            marcadoresSector.add(newMarker);
        }
        setPolygonOptionsSector();
    }

    public void setPolygonOptionsSector(){
        this.polygonOptionsSector = new PolygonOptions()
            .addAll(getPuntosLatLngSector())
            .strokeColor(Color.BLUE)
            .fillColor(Color.argb(1, 0, 0, 0));
    }

    /**
     * Obtiene el punto central e base a una lista de puntos
     * @param points: lista de puntos
     * @return Devuelve un LatLng donde esta el centro
     */
    public static LatLng getPolygonCenterLatLng(List<LatLng> points){

        double latitude = 0;
        double longitude = 0;
        int count = points.size();

        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }

        return new LatLng(latitude / count, longitude / count);
    }

    public LatLng getPolygonCenterMarkers(List<Marker> points){

        double latitude = 0;
        double longitude = 0;
        int count = points.size();

        for (Marker point : points) {
            latitude += point.getPosition().latitude;
            longitude += point.getPosition().longitude;
        }

        return new LatLng(latitude / count, longitude / count);
    }

    public List<CoordenadaParcela> getCoordenadas(){
        List<CoordenadaParcela> coordenadas = new ArrayList<>();
        for (Marker marker: marcadores){
            coordenadas.add(new CoordenadaParcela(marker.getPosition()));
        }
        return  coordenadas;
    }

    public void updateMarkers(Boolean añadirParcela, boolean añadirSector){
        List<LatLng> points = new ArrayList<LatLng>();
        List<Marker> updatedMarkers = new ArrayList<>(); // Lista temporal para guardar los nuevos marcadores
        for (Marker marker: marcadores){
            points.add(marker.getPosition());

            Marker newMarker = gMap.addMarker(
                    new MarkerOptions()
                            .position(marker.getPosition())
                            .draggable(true)
                            .visible(false));

            updatedMarkers.add(newMarker); // Guarda el nuevo marcador en la lista temporal
            if (añadirParcela && !añadirSector){
                newMarker.setVisible(true);
            }
        }

        if (!marcadores.equals(updatedMarkers)) {modificado = true; }

        marcadores.clear();
        marcadores.addAll(updatedMarkers);
        if (añadirParcela){
            parcela.setPuntosLatLong(points);
        }


        ///////////////////////////////////////////////////////////////
        ////////////////// Seccion marcadores sector //////////////////
        ///////////////////////////////////////////////////////////////
        points = new ArrayList<LatLng>();
        updatedMarkers = new ArrayList<>(); // Lista temporal para guardar los nuevos marcadores
        for (Marker marker: marcadoresSector){
            points.add(marker.getPosition());

            Marker newMarker = gMap.addMarker(
                    new MarkerOptions()
                            .position(marker.getPosition())
                            .draggable(true)
                            .visible(false));

            updatedMarkers.add(newMarker); // Guarda el nuevo marcador en la lista temporal
            if (añadirSector){
                newMarker.setVisible(true);
            }
        }

        if (!marcadoresSector.equals(updatedMarkers)) {modificado = true; }

        marcadoresSector.clear();
        marcadoresSector.addAll(updatedMarkers);
        if (añadirSector){
            parcela.setPuntosLatLongSector(points);
        }
    }

    private PolygonOptions getPolygonOptions(){
        this.polygonOptions = new PolygonOptions()
                .addAll(getPuntosLatLng())
                .strokeColor(Color.RED)
                .fillColor(Color.argb(0, 0, 0, 0));
        return this.polygonOptions;
    }

    public void dibujar(boolean añadirParcela, int indiceParcela, Usuario usuario, boolean añadirSector){
        gMap.clear();
        updateMarkers(añadirParcela,añadirSector);
        if (getPuntosLatLng().size() > 2) {
            setPolygonOptionsSector();
            gMap.addPolygon(getPolygonOptions());
            gMap.addMarker(new MarkerOptions()
                    .position(getPolygonCenterLatLng(getPuntosLatLng()))
                    .title(usuario.getParcelas().get(indiceParcela).getNombre())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            if (polygonOptionsSector != null && polygonOptionsSector.getPoints().size() > 2){
                gMap.addPolygon(polygonOptionsSector);
            }
        }
    }

    public boolean isMarkerInsidePolygon(LatLng point, List<LatLng> polygon) {
        int intersectCount = 0;
        for (int j = 0; j < polygon.size() - 1; j++) {
            if (rayCastIntersect(point, polygon.get(j), polygon.get(j + 1))) {
                intersectCount++;
            }
        }

        return ((intersectCount % 2) == 1); // Impar = dentro, Par = fuera
    }

    private boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = tap.latitude;
        double pX = tap.longitude;

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY) || (aX < pX && bX < pX)) {
            return false; // El rayo no puede intersectar el segmento
        }

        double m = (aY - bY) / (aX - bX);
        double bee = (-aX) * m + aY;
        double x = (pY - bee) / m;

        return x > pX;
    }
}
