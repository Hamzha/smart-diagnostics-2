package activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import model.Device;
import model.Position;
import util.BaseClass;
import util.Tools;

public class OnScreen extends BaseClass {
    protected Marker marker;
    protected Device device;
    protected Position position;
    protected GoogleMap mMap;
    private Context context;
    private static final String TAG = ">>>LiveVehicle";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_screen);
        context = this;
        langSelector(context);
        Toolbar(context, device.getName());
        device = getIntent().getParcelableExtra("Device");
        position = getIntent().getParcelableExtra("Position");
        setup();

//        assert mapFragment != null;
//        mapFragment.getMapAsync(googleMap -> {
//            mMap = Tools.configActivityMaps(googleMap);
//
//            MarkerOptions markerOptions = new MarkerOptions()
//                    .position(currentLatLng)
//                    .rotation((float) position.getCourse())
//                    .flat(true)
//                    .icon(getBitmapDescriptor(markerFile(device, position)));
//            marker = mMap.addMarker(markerOptions);
//        });
    }

    private void setup() {
        LatLng currentLatLng = new LatLng(position.getLatitude(), position.getLongitude());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) getDrawable(id);

            assert vectorDrawable != null;
            int h = vectorDrawable.getIntrinsicHeight();
            int w = vectorDrawable.getIntrinsicWidth();

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    }

    private int markerFile(Device device, Position position) {
        StringBuilder stringBuilder = new StringBuilder();

        if (device.getCategory() != null) {
            stringBuilder.append("ic_live_");
            stringBuilder.append(device.getCategory());
            stringBuilder.append("_");
            if (!device.getCategory().equals("tractor")) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("ic_live_");
                stringBuilder.append("car");
                stringBuilder.append("_");
            }
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("ic_live_");
            stringBuilder.append("car");
            stringBuilder.append("_");

        }
        if (position.getPositionAttributes().getIgnition() == null) {
            stringBuilder.append("online");
        } else if (position.getPositionAttributes().getIgnition())
            stringBuilder.append("online");
        else
            stringBuilder.append("offline");

        return getResources().getIdentifier(stringBuilder.toString(), "drawable", getPackageName());
    }

}