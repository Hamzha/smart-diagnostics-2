package model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Polygon {

    List<LatLng> latLngs;

    public Polygon(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    public List<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }
}
