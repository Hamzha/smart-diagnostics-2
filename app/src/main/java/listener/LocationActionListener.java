package listener;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import java.util.ArrayList;

import activity.MainActivity;
import util.Common;
import util.StaticFunction;

import static android.content.Context.LOCATION_SERVICE;
import static util.CommonConst.REQUEST_CODE_ASK_PERMISSIONS;
import static util.StaticFunction.zoomingLocation;

public class LocationActionListener implements View.OnClickListener {
    private MainActivity context;

    public LocationActionListener(MainActivity context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        FloatingActionButton floatingActionButton = context.findViewById(R.id.focus_btn);
        int check = context.getCheckLocation();
        ArrayList<Marker> markers = context.getOnMapCallBackListener().getMarkers();
        GoogleMap mMap = context.getOnMapCallBackListener().getmMap();

        if (check == 1) {
            Toast.makeText(context, context.getString(R.string.bird_eye_view), Toast.LENGTH_LONG).show();
            context.setCheckLocation(2);
            if (markers.size() < 2) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 15));
            } else mMap.animateCamera(zoomingLocation(markers,context));
            if (Common.checkAndAskLocationPermission(context)) {
                mMap.setMyLocationEnabled(true);
            }
            floatingActionButton.setImageResource(R.drawable.ic_my_location_black_24dp);
        } else {
            Toast.makeText(context, context.getString(R.string.current_location), Toast.LENGTH_LONG).show();
            floatingActionButton.setImageResource(R.drawable.ic_people_black_24dp);
            context.setCheckLocation(1);
            if (Common.checkAndAskLocationPermission(context)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
                } else {
                    StaticFunction.getLastKnownLocation(context.getApplicationContext(), mMap);
                }
            }
        }
    }
}
