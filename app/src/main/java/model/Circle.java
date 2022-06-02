package model;

import com.google.android.gms.maps.model.LatLng;

public class Circle {

    private double radius;
    private LatLng latLng;

    public Circle(double radius, LatLng latLng) {
        this.radius = radius;
        this.latLng = latLng;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
