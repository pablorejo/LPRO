package com.example.pruebasql.mapa;

import android.graphics.Color;

import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.parcelas.Coordenada;
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
    List<Marker> marcadores;
    PolygonOptions polygonOptions;
    Parcela parcela;
    GoogleMap gMap;

    boolean modificado = false;

    public Poligono(List<Marker> marcadores, GoogleMap gMap, Parcela parcela){
        this.marcadores = marcadores;
        this.polygonOptions = new PolygonOptions()
                .addAll(getPuntosLatLng())
                .strokeColor(Color.RED)
                .fillColor(Color.argb(0, 0, 0, 0));
        this.gMap = gMap;
        this.parcela = parcela;
    }

    public List<LatLng> getPuntosLatLng(){
        List<LatLng> puntos = new ArrayList<>();
        for (Marker marker: marcadores){
            puntos.add(marker.getPosition());
        }
        return  puntos;
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

    public LatLng getPolygonCenterLatLng(List<LatLng> points){

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

    public List<Coordenada> getCoordenadas(){
        List<Coordenada> coordenadas = new ArrayList<>();
        for (Marker marker: marcadores){
            coordenadas.add(new Coordenada(marker.getPosition()));
        }
        return  coordenadas;
    }

    public void updateMarkers(Boolean añadirParcela){
        List<LatLng> points = new ArrayList<LatLng>();
        List<Marker> updatedMarkers = new ArrayList<>(); // Lista temporal para guardar los nuevos marcadores
        for (Marker marker: marcadores){
            points.add(marker.getPosition());

            Marker newMarker = gMap.addMarker(new MarkerOptions().position(marker.getPosition()).draggable(true));
            updatedMarkers.add(newMarker); // Guarda el nuevo marcador en la lista temporal
            if (!añadirParcela){
                newMarker.setVisible(false);
            }
        }

        if (!marcadores.equals(updatedMarkers)) {modificado = true; }

        marcadores.clear();
        marcadores.addAll(updatedMarkers);
        if (añadirParcela){
            parcela.setPuntosLatLong(points);
        }
    }

    private PolygonOptions getPolygonOptions(){
        this.polygonOptions = new PolygonOptions()
                .addAll(getPuntosLatLng())
                .strokeColor(Color.RED)
                .fillColor(Color.argb(0, 0, 0, 0));
        return this.polygonOptions;
    }

    public void dibujar(boolean añadirParcela, int indiceParcela, Usuario usuario){
        updateMarkers(añadirParcela);
        if (getPuntosLatLng().size() > 2) {
            gMap.addPolygon(getPolygonOptions());
            gMap.addMarker(new MarkerOptions()
                    .position(getPolygonCenterLatLng(getPuntosLatLng()))
                    .title(usuario.getParcelas().get(indiceParcela).getNombre())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
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
