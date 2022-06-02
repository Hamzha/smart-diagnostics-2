package model;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;

public class MapPolygon {
    GoogleMap googleMap;
    ArrayList<Circle> circleList;
    ArrayList<com.google.android.gms.maps.model.Polygon> polygonList;
    boolean geoFenceVisibility;
    ArrayList<Marker> markers;
    ArrayList<Marker> events;
    boolean eventVisibility;

    public MapPolygon(GoogleMap googleMap, ArrayList<Circle> circleList, ArrayList<Polygon> polygonList, ArrayList<Marker> markers) {
        this.googleMap = googleMap;
        this.circleList = circleList;
        this.polygonList = polygonList;
        this.markers = markers;
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }

    public void setMarkers(ArrayList<Marker> markers) {
        this.markers = markers;
    }

    public MapPolygon(boolean geoFenceVisibility) {
        this.eventVisibility = true;
        this.geoFenceVisibility = geoFenceVisibility;
    }

    public boolean isEventVisibility() {
        return eventVisibility;
    }

    public void setEventVisibility(boolean eventVisibility) {
        this.eventVisibility = eventVisibility;
    }

    public MapPolygon() {

    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public ArrayList<Circle> getCircleList() {
        return circleList;
    }

    public void setCircleList(ArrayList<Circle> circleList) {
        this.circleList = circleList;
    }

    public ArrayList<Polygon> getPolygonList() {
        return polygonList;
    }

    public void setPolygonList(ArrayList<Polygon> polygonList) {
        this.polygonList = polygonList;
    }

    public boolean isGeoFenceVisibility() {
        return geoFenceVisibility;
    }

    public void setGeoFenceVisibility(boolean geoFenceVisibility) {
        this.geoFenceVisibility = geoFenceVisibility;
    }

    public ArrayList<Marker> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Marker> events) {
        this.events = events;
    }
}
