package activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.ui.IconGenerator;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;
import model.Device;
import model.MapPolygon;
import model.Position;
import util.BaseClass;
import util.Common;
import util.CommonConst;
import util.StaticFunction;
import util.StaticRequest;
import util.Tools;
import util.URLS;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class RoutePower extends BaseClass {

    Date startDate;
    Date endDate;
    Device device;
    List<Position> routeList = new ArrayList<>();
    List<LatLng> points = new ArrayList<>();
    PolylineOptions polylineOptions = new PolylineOptions();
    int mapType;
    String TAG = ">>>" + Route.class.getSimpleName();
    int timer = 15000;
    private IconGenerator iconGenerator;
    private model.Trip trip;
    Context context;
    private MapPolygon mapPolygon;
    boolean reverse = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_power);
        context = this;
        langSelector(context);
        runInit();
    }

    void runInit() {
        if (Common.isInternetConnected(context)) {
            Common.retryDialog(context, CommonConst.NO_INTERNET_CODE, bool -> {
                if (bool) {
                    runInit();
                } else {
                    finish();
                }
            });
        } else {
            init();
        }
    }

    private void init() {
        mapPolygon = new MapPolygon(false);

        mapType = ((SmartTracker) getApplication()).getMapType();
        startDate = (Date) getIntent().getSerializableExtra("TripStartTime");
        endDate = (Date) getIntent().getSerializableExtra("TripEndTime");
        device = getIntent().getParcelableExtra("Device");
        trip = getIntent().getParcelableExtra("Trip");
        Toolbar(context, getString(R.string.trip_route_power) + "/" + device.getName());
        getDevice(device);
        String string = getString(R.string.total_average_route) + " " + getIntent().getSerializableExtra("avgSpeed")
                + "\n"
                + getString(R.string.total_top_speed_route) + " " + getIntent().getSerializableExtra("topSpeed")
                + "\n"
                + getString(R.string.total_distance_route) + " " + getIntent().getSerializableExtra("distance")
                + "\n"
                + getString(R.string.total_duration_route) + " " + getIntent().getSerializableExtra("duration");

        ((TextView) findViewById(R.id.trip_detail_on_map)).setText(string);

        iconGenerator = new IconGenerator(getApplicationContext());

        AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner(context, startDate, endDate);
        asyncTaskRunner.execute();
        mapInit();

    }


    public void getDevice(Device device) {
        StaticRequest.fetchFromServer(URLS.DeviceById("id=" + device.getId()), context, timer, (response, statusCode) -> {

            if(response.contains("\"reverse\":\"true\"")){
                Common.logd(TAG,"!Exist");
                reverse = true;
            }else{
                Common.logd(TAG,"!No Exist");
            }

        });
    }


    private void mapInit() {
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.route_map);

        assert mapFragment != null;
        mapFragment.getMapAsync(googleMap -> {
            mapPolygon.setGoogleMap(Tools.configActivityMaps(googleMap)); ;
            mapPolygon.getGoogleMap().addPolyline(polylineOptions);
            mapPolygon.getGoogleMap().getUiSettings().setMyLocationButtonEnabled(true);
            mapPolygon.getGoogleMap().getUiSettings().setZoomControlsEnabled(false);
            mapPolygon.getGoogleMap().getUiSettings().setCompassEnabled(false);
            mapPolygon.getGoogleMap().getUiSettings().setMyLocationButtonEnabled(false);
            StaticFunction.setMapType(mapPolygon.getGoogleMap(), mapType);
        });
    }

    public void clickRouteMapSwitchAction(View view) {
        mapType = (mapType % 2) + 1;
        StaticFunction.setMapType(mapPolygon.getGoogleMap(), mapType);
    }

    public void clickRouteGeoFence(View view) {

        if (!mapPolygon.isGeoFenceVisibility()) {
            Common.logd(TAG, "false");
            StaticFunction.addGeoFencesRoute(this, ((SmartTracker) getApplication()).getGeoFences(), mapPolygon);
        } else {
            Common.logd(TAG, "true");
            for (int i = 0; i < mapPolygon.getPolygonList().size(); i++) {
                mapPolygon.getPolygonList().get(i).remove();
            }
            for (int i = 0; i < mapPolygon.getCircleList().size(); i++) {
                mapPolygon.getCircleList().get(i).remove();
            }

            for (int i = 0; i < mapPolygon.getMarkers().size(); i++) {
                mapPolygon.getMarkers().get(i).remove();
            }
            mapPolygon.setGeoFenceVisibility(!mapPolygon.isGeoFenceVisibility());

        }
    }

    protected void fetchRouteFromServer(Context context) {
//        sweetAlertDialog.show();
        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String[] date = Common.dateConversion(startDate, endDate);

        StaticRequest.fetchRouteFromServer(URLS.Route(device.getId(), date[0], date[1]), timer, context, (response, responseCode) -> {
            if (responseCode == CommonConst.ACTIVITY_MOVE_BACK) {
                finish();
            } else if (responseCode == CommonConst.ACTIVITY_RETRY_CODE) {
                timer = timer + 5000;
                runInit();
            } else if (responseCode == CommonConst.ACTIVITY_VOID) {

                Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();

                try {
                    Position[] routes = gson.fromJson(response, Position[].class);
                    drawAndAnimateCar(routes);
                } catch (Exception ex) {
                    Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);

                }
            }
        });
    }

    private void drawAndAnimateCar(Position[] routes) {

        Collections.addAll(routeList, routes);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        for (Position position : routes) {
            points.add(new LatLng(position.getLatitude(), position.getLongitude()));  //Points are LatLng list
            builder.include(new LatLng(position.getLatitude(), position.getLongitude()));
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mapPolygon.getGoogleMap().animateCamera(mCameraUpdate);

        String timeFormat = "HH:mm";
        String dateFormat = "MMM, dd, yyyy";

        @SuppressLint("SimpleDateFormat") MarkerOptions markerOptionsStart = new MarkerOptions()
                .title("")
                .position(points.get(0))
                //.flat(true)
                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(getString(R.string.start_point) + "\n" + new SimpleDateFormat(timeFormat).format(trip.getStartTime())
                        + " " + new SimpleDateFormat(dateFormat).format(trip.getStartTime()))))
                .anchor((float) 0.5, (float) 1.5);

        @SuppressLint("SimpleDateFormat") MarkerOptions markerOptionsEnd = new MarkerOptions()
                .title("")
                .position(points.get(points.size() - 1))
                //.flat(true)
                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(getString(R.string.end_point) + "\n" +
                        new SimpleDateFormat(timeFormat).format(trip.getEndTime()) + " " + new SimpleDateFormat(dateFormat).format(trip.getEndTime()))))
                .anchor((float) 0.5, (float) 1.5);

        mapPolygon.getGoogleMap().addMarker(markerOptionsStart);

        mapPolygon.getGoogleMap().addMarker(markerOptionsEnd);

        ArrayList<LatLng> latlngs = new ArrayList<>();
        for (Position route : routeList) {
            latlngs.add(new LatLng(route.getLatitude(), route.getLongitude())); //some latitude and logitude value

        }
        int pre = R.color.green_A700;
        int curr = R.color.red_A700;;

        polylineOptions = new PolylineOptions();

        if (device.getAttributes().getPowerHigh() == null)
            device.getAttributes().setPowerHigh("14.30");
        if (device.getAttributes().getPowerLower() == null)
            device.getAttributes().setPowerLower("13.26");
        if (device.getAttributes().getPowerMiddle() == null)
            device.getAttributes().setPowerMiddle("13.76");

        Common.logd(TAG,"!"+device.toString());
        Common.logd(TAG,"reverse : "+reverse);
        for (int i = 0; i < routes.length; i++) {
            if (routes[i].getPositionAttributes().getPower() != null) {
                if(!reverse){
                    if (routes[i].getPositionAttributes().getPower() <= Double.parseDouble(device.getAttributes().getPowerLower())) {
                        curr = R.color.green_A700;
                    } else if (routes[i].getPositionAttributes().getPower() <= Double.parseDouble(device.getAttributes().getPowerMiddle())) {
                        curr = R.color.yellow_A200;
                    } else if (routes[i].getPositionAttributes().getPower() <= Double.parseDouble(device.getAttributes().getPowerHigh())) {
                        curr = R.color.orange_500;
                    } else {
                        curr = R.color.red_A700;
                    }
                }
                else{
                    if(routes[i].getPositionAttributes().getPower() >= Double.parseDouble(device.getAttributes().getPowerLower())){
                        curr = R.color.green_A700;
                    }else if(routes[i].getPositionAttributes().getPower() < Double.parseDouble(device.getAttributes().getPowerLower()) &&
                            routes[i].getPositionAttributes().getPower() > Double.parseDouble(device.getAttributes().getPowerMiddle())){
                        curr = R.color.orange_500;
                    }else{
                        curr = R.color.red_A700;
                    }
                }
                if (curr == (pre)) {
                    polylineOptions.color(getResources().getColor(curr));
                    polylineOptions.width(10);
                    polylineOptions.startCap(new SquareCap());
                    polylineOptions.endCap(new SquareCap());
                    polylineOptions.jointType(ROUND);
                    polylineOptions.add(latlngs.get(i));
                    pre = curr;

                } else {
                    polylineOptions.width(10);
                    polylineOptions.startCap(new SquareCap());
                    polylineOptions.endCap(new SquareCap());
                    polylineOptions.jointType(ROUND);
                    polylineOptions.add(latlngs.get(i));

                    pre = curr;

                    mapPolygon.getGoogleMap().addPolyline(polylineOptions);
                    polylineOptions = new PolylineOptions();
                    polylineOptions.color(getResources().getColor(curr));
                    polylineOptions.width(10);
                    polylineOptions.startCap(new SquareCap());
                    polylineOptions.endCap(new SquareCap());
                    polylineOptions.jointType(ROUND);
                    polylineOptions.add(latlngs.get(i));

                }
            }
        }
        mapPolygon.getGoogleMap().addPolyline(polylineOptions);

        mapPolygon.getGoogleMap().addMarker(new MarkerOptions().position(points.get(0))
                //.flat(true)
        );
        mapPolygon.getGoogleMap().addMarker(new MarkerOptions().position(points.get(points.size() - 1))
                //.flat(true)
        );
    }


    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        SweetAlertDialog eDialog;
        Context activity;
        Date startDate, endDate;

        AsyncTaskRunner(Context context, Date startDate, Date endDate) {
            //        Context[] context;
            WeakReference<Context> activityReference = new WeakReference<>(context);
            activity = activityReference.get();
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            eDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
            eDialog.setTitle(getString(R.string.dialog_box_msg));
            eDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            fetchRouteFromServer(context);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            StaticFunction.dismissProgressDialog(eDialog);


        }

    }
}
