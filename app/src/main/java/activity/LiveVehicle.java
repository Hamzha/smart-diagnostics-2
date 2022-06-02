package activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import model.Device;
import model.Position;
import util.BaseClass;
import util.Common;
import util.StaticFunction;
import util.Tools;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class LiveVehicle extends BaseClass {
    protected Marker marker;
    protected Device device;
    protected Position position;
    protected GoogleMap mMap;
    protected LatLng currentLatLng;
    private SweetAlertDialog pDialog;
    private NumberFormat numberFormat;
    private Context context;
    private TextView textViewLiveSpeed;
    private static final String TAG = ">>>LiveVehicle";
    private String fuel, can, obd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_vehicle);
        context = this;
        langSelector(context);
        initVars();
        updateData();
    }

    @SuppressLint("SetTextI18n")
    private void updateData() {
        setDataUpdate(1000, check -> {

            Map<Long, Position> positionsFinal = ((SmartTracker) getApplicationContext()).getPositions();
            Map<Long, Device> devicesFinal = ((SmartTracker) getApplicationContext()).getDevices();

            for (Map.Entry<Long, Device> entry : devicesFinal.entrySet()) {
                if (entry.getKey() == device.getId()) {
                    Common.logd(TAG, position.toString());
                    if (marker != null) {
                        marker.setIcon(getBitmapDescriptor(markerFile(entry.getValue(), positionsFinal.get(entry.getValue().getId()))));
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(getString(R.string.KM_per_hour_live)).append(numberFormat.format((float) (position.getSpeed() * 1.852))).append("Kph");

                        if (can != null && can.equals("true")) {
                            try {
                                stringBuilder.append("\nFuel: ").append(position.getPositionAttributes().getFuel()).append("L");
                                if (position.getPositionAttributes().getRpm() != null)
                                    stringBuilder.append("\nRPM: ").append(position.getPositionAttributes().getRpm());
                                if (position.getPositionAttributes().getThrottle() != null)
                                    stringBuilder.append("\nThrottle: ").append(position.getPositionAttributes().getThrottle());
                                if (position.getPositionAttributes().getDeviceTemp() != null)
                                    stringBuilder.append("\nTemperature: ").append(position.getPositionAttributes().getDeviceTemp());
                                if (position.getPositionAttributes().getObdSpeed() != null)
                                    stringBuilder.append("\nOBD Speed: ").append(position.getPositionAttributes().getObdSpeed()).append("Kph");
                            } catch (Exception e) {
                                e.getLocalizedMessage();
                            }
                        } else if (obd != null && obd.equals("true"))
                            try {
                                if (position.getPositionAttributes().getFuel() != 0)
                                    stringBuilder.append("\nFuel: ").append(position.getPositionAttributes().getFuel()).append("L");
                                if (position.getPositionAttributes().getRpm() != null)
                                    stringBuilder.append("\nRPM: ").append(position.getPositionAttributes().getRpm());
                                if (position.getPositionAttributes().getThrottle() != null)
                                    stringBuilder.append("\nThrottle: ").append(position.getPositionAttributes().getThrottle());
                                if (position.getPositionAttributes().getDeviceTemp() != null)
                                    stringBuilder.append("\nTemperature: ").append(position.getPositionAttributes().getDeviceTemp());
                                if (position.getPositionAttributes().getObdSpeed() != null)
                                    stringBuilder.append("\nOBD Speed: ").append(position.getPositionAttributes().getObdSpeed()).append("Kph");
                            } catch (Exception e) {
                                e.getLocalizedMessage();
                            }
                        else if (fuel != null && fuel.equals("true")) {
                            try {
//                                Common.logd(TAG, String.valueOf(position.getPositionAttributes().getFuel()));
                                stringBuilder.append("\nFuel: ").append(Math.round(position.getPositionAttributes().getFuel() * 100.0) / 100.0).append("L");
                            } catch (Exception e) {
                                e.getLocalizedMessage();
                            }
                        }
                        textViewLiveSpeed.setText(stringBuilder);
                    }
                }
            }

            for (Map.Entry<Long, Position> entry : positionsFinal.entrySet()) {
                if (entry.getKey() == device.getId()) {
                    if (marker != null) {
//                        marker1.setPosition(new LatLng(position.getLatitude(), position.getLongitude()));
                        marker.setPosition(new LatLng(position.getLatitude(), position.getLongitude()));
//                        marker1.setIcon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(getString(R.string.KM_per_hour_live) + " " + numberFormat.format(Math.round((float) (position.getSpeed() * 1.852))) + "km/h")));
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(getString(R.string.KM_per_hour_live)).append(numberFormat.format((float) (position.getSpeed() * 1.852))).append("Kph");
                        Common.logd(TAG, position.toString());

                        if (can != null && can.equals("true")) {
                            try {
                                stringBuilder.append("\nFuel: ").append(position.getPositionAttributes().getFuel()).append("L");
                                if (position.getPositionAttributes().getRpm() != null)
                                    stringBuilder.append("\nRPM: ").append(position.getPositionAttributes().getRpm());
                                if (position.getPositionAttributes().getThrottle() != null)
                                    stringBuilder.append("\nThrottle: ").append(position.getPositionAttributes().getThrottle());
                                if (position.getPositionAttributes().getDeviceTemp() != null)
                                    stringBuilder.append("\nTemperature: ").append(position.getPositionAttributes().getDeviceTemp());
                                if (position.getPositionAttributes().getObdSpeed() != null)
                                    stringBuilder.append("\nOBD Speed: ").append(position.getPositionAttributes().getObdSpeed()).append("Kph");
                            } catch (Exception e) {
                                e.getLocalizedMessage();
                            }
                        } else if (obd != null && obd.equals("true"))
                            try {
                                if (position.getPositionAttributes().getFuel() != 0)
                                    stringBuilder.append("\nFuel: ").append(position.getPositionAttributes().getFuel()).append("L");
                                if (position.getPositionAttributes().getRpm() != null)
                                    stringBuilder.append("\nRPM: ").append(position.getPositionAttributes().getRpm());
                                if (position.getPositionAttributes().getThrottle() != null)
                                    stringBuilder.append("\nThrottle: ").append(position.getPositionAttributes().getThrottle());
                                if (position.getPositionAttributes().getDeviceTemp() != null)
                                    stringBuilder.append("\nTemperature: ").append(position.getPositionAttributes().getDeviceTemp());
                                if (position.getPositionAttributes().getObdSpeed() != null)
                                    stringBuilder.append("\nOBD Speed: ").append(position.getPositionAttributes().getObdSpeed()).append("Kph");
                            } catch (Exception e) {
                                e.getLocalizedMessage();
                            }
                        else if (fuel != null && fuel.equals("true")) {
                            try {
//                                Common.logd(TAG, String.valueOf(position.getPositionAttributes().getFuel()));
                                stringBuilder.append("\nFuel: ").append(Math.round(position.getPositionAttributes().getFuel() * 100.0) / 100.0).append("L");
                            } catch (Exception e) {
                                e.getLocalizedMessage();

                            }
                        }

                        textViewLiveSpeed.setText(stringBuilder);

                        marker.setRotation((float) position.getCourse());
                        drawPolyline(position, entry.getValue());
                        position = entry.getValue();
                        currentLatLng = new LatLng(position.getLatitude(), position.getLongitude());
                        mMap.animateCamera(zoomingLocation(currentLatLng));
                    }
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void drawPolyline(Position oldPosition, Position newPosition) {
        List<LatLng> points = new ArrayList<>();
        points.add(new LatLng(oldPosition.getLatitude(), oldPosition.getLongitude()));
        points.add(new LatLng(newPosition.getLatitude(), newPosition.getLongitude()));
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(getResources().getColor(R.color.amber_700));
        polylineOptions.width(10);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(points);
        mMap.addPolyline(polylineOptions);
    }

    @SuppressLint("SetTextI18n")
    private void initMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.live_map);

        assert mapFragment != null;
        mapFragment.getMapAsync(googleMap -> {
            final GoogleMap googleMap1 = googleMap;
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                mMap = Tools.configActivityMaps(googleMap1);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(currentLatLng)
                        .rotation((float) position.getCourse())
                        .flat(true)
                        .icon(getBitmapDescriptor(markerFile(device, position)));

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(getString(R.string.KM_per_hour_live)).append(numberFormat.format((float) (position.getSpeed() * 1.852))).append("Kph");

                Common.logd(TAG, position.toString());

                if (can != null && can.equals("true")) {
                    try {
                        stringBuilder.append("\nFuel: ").append(position.getPositionAttributes().getFuel()).append("L");
                        if (position.getPositionAttributes().getRpm() != null)
                            stringBuilder.append("\nRPM: ").append(position.getPositionAttributes().getRpm());
                        if (position.getPositionAttributes().getThrottle() != null)
                            stringBuilder.append("\nThrottle: ").append(position.getPositionAttributes().getThrottle());
                        if (position.getPositionAttributes().getDeviceTemp() != null)
                            stringBuilder.append("\nTemperature: ").append(position.getPositionAttributes().getDeviceTemp());
                        if (position.getPositionAttributes().getObdSpeed() != null)
                            stringBuilder.append("\nOBD Speed: ").append(position.getPositionAttributes().getObdSpeed()).append("Kph");
                    } catch (Exception e) {
                        e.getLocalizedMessage();
                    }
                } else if (obd != null && obd.equals("true"))
                    try {
                        if (position.getPositionAttributes().getFuel() != 0)
                            stringBuilder.append("\nFuel: ").append(position.getPositionAttributes().getFuel()).append("L");
                        if (position.getPositionAttributes().getRpm() != null)
                            stringBuilder.append("\nRPM: ").append(position.getPositionAttributes().getRpm());
                        if (position.getPositionAttributes().getThrottle() != null)
                            stringBuilder.append("\nThrottle: ").append(position.getPositionAttributes().getThrottle());
                        if (position.getPositionAttributes().getDeviceTemp() != null)
                            stringBuilder.append("\nTemperature: ").append(position.getPositionAttributes().getDeviceTemp());
                        if (position.getPositionAttributes().getObdSpeed() != null)
                            stringBuilder.append("\nOBD Speed: ").append(position.getPositionAttributes().getObdSpeed()).append("Kph");
                    } catch (Exception e) {
                        e.getLocalizedMessage();
                    }
                else if (fuel != null && fuel.equals("true")) {
                    try {
//                        Common.logd(TAG, String.valueOf(position.getPositionAttributes().getFuel()));
                        stringBuilder.append("\nFuel: ").append(Math.round(position.getPositionAttributes().getFuel() * 100.0) / 100.0).append("L");
                    } catch (Exception e) {
                        e.getLocalizedMessage();

                    }
                }
                textViewLiveSpeed.setText(stringBuilder);
                marker = mMap.addMarker(markerOptions);
                mMap.moveCamera(zoomingLocation(currentLatLng));
                mMap.setOnMarkerClickListener(marker -> {
                    mMap.animateCamera(zoomingLocation(currentLatLng));
                    return true;
                });
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismissWithAnimation();
                }
            }, 3000);
        });
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

    private CameraUpdate zoomingLocation(LatLng latLng) {


//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        builder.include(latLng);
//        final int width = context.getResources().getDisplayMetrics().widthPixels;
//        final int height = context.getResources().getDisplayMetrics().heightPixels;
//        final int minMetric = Math.min(width, height);
//        final int padding = (int) (minMetric * 0.40); // offset from edges of the map in pixels
//        LatLngBounds bounds = builder.build();
//
//        return CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        return CameraUpdateFactory.newLatLngZoom(latLng, 14);
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

    private void initVars() {

        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitle(getString(R.string.dialog_box_title));
        pDialog.setContentText(getString(R.string.dialog_box_msg));
        pDialog.show();
        device = getIntent().getParcelableExtra("Device");
        Common.logd(">>>" + TAG, device.toString());
        Toolbar(context, getString(R.string.live_tacking) + "/" + device.getName());
        fuel = getIntent().getStringExtra("Fuel");
        can = getIntent().getStringExtra("can");
        obd = getIntent().getStringExtra("obd2");
        Common.logd(TAG, "Fuel: " + fuel + ", Can: " + can + ", Obd:" + obd);
        Map<Long, Position> positions = ((SmartTracker) getApplication()).getPositions();
        for (Map.Entry<Long, Position> entry : positions.entrySet()) {
            if (entry.getValue().getDeviceId() == device.getId()) {
                this.position = entry.getValue();
                currentLatLng = new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude());
                break;
            }
        }
        if (currentLatLng != null) {
            initMapFragment();
        }
        textViewLiveSpeed = findViewById(R.id.live_map_speed_text);
    }

    public void clickLiveMapSwitchAction(View view) {
        int mapType = ((SmartTracker) getApplication()).getMapType();
        mapType = (mapType % 2) + 1;
        mMap = StaticFunction.setMapType(mMap, mapType);
        ((SmartTracker) getApplication()).setMapType(mapType);
    }

    public void clickImmobilizer(View view) {
        this.startActivity(new Intent(context, Immobilizer.class).putExtra("Device", device).putExtra("Name", device.getName()));
    }


}