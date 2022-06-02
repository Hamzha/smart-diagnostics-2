package activity;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import util.BaseClass;

public class  About extends BaseClass implements OnMapReadyCallback {
    Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        context = this;
        langSelector(context);
        init();
    }

    private void init() {
        Toolbar(this, getString(R.string.about));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.about_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng office = new LatLng(31.5070, 74.3361);
        googleMap.addMarker(new MarkerOptions().position(office)
                .title("Smart Agriculture Solutions"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(office, 12));
        googleMap.getUiSettings().setZoomControlsEnabled(false);
    }
}
