package activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.maps.android.ui.IconGenerator;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import model.Device;
import model.Event;
import model.GeoFence;
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

public class Route extends BaseClass {
    private Device device;
    private List<model.Position> routeList = new ArrayList<>();
    private List<LatLng> points = new ArrayList<>();
    private PolylineOptions polylineOptions = new PolylineOptions();
    private int mapType;
    private IconGenerator iconGenerator;
    private Polyline blackPolyline, greyPolyLine;
    private int timer = 15000;
    private String TAG = ">>>" + Route.class.getSimpleName();
    private model.Trip trip;
    Context context;
    Map<Long, GeoFence> geoFences;
    private boolean defaultGeoFence = false;
    private MapPolygon mapPolygon;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
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
        Date startDate = (Date) getIntent().getSerializableExtra("TripStartTime");
        Date endDate = (Date) getIntent().getSerializableExtra("TripEndTime");
        device = getIntent().getParcelableExtra("Device");
        trip = getIntent().getParcelableExtra("Trip");
        Toolbar(this, getString(R.string.trip_route) + "/" + device.getName());
        geoFences = ((SmartTracker) getApplicationContext()).getGeoFences();

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

    public void clickRouteMapSwitchAction(View view) {
        mapType = (mapType % 2) + 1;
        StaticFunction.setMapType(mapPolygon.getGoogleMap(), mapType);
    }

    private void mapInit() {
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.route_map);

        assert mapFragment != null;
        mapFragment.getMapAsync(googleMap -> {
            mapPolygon.setGoogleMap(Tools.configActivityMaps(googleMap));
            mapPolygon.getGoogleMap().addPolyline(polylineOptions);
            mapPolygon.getGoogleMap().getUiSettings().setMyLocationButtonEnabled(true);
            mapPolygon.getGoogleMap().getUiSettings().setZoomControlsEnabled(false);
            mapPolygon.getGoogleMap().getUiSettings().setCompassEnabled(false);
            mapPolygon.getGoogleMap().getUiSettings().setMyLocationButtonEnabled(false);
            StaticFunction.setMapType(mapPolygon.getGoogleMap(), mapType);
        });
    }

    protected void fetchRouteFromServer(final Date startDate, Date endDate, Context context) {
        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String[] dates = Common.dateConversion(startDate, endDate);
        StaticRequest.fetchRouteFromServer(URLS.Route(device.getId(), dates[0], dates[1]), timer, context, (response, responseCode) -> {
            try {

                if (responseCode == CommonConst.ACTIVITY_RETRY_CODE) {
                    timer = timer + 5000;
                    runInit();
                } else if (responseCode == CommonConst.ACTIVITY_MOVE_BACK) {
                    finish();
                } else if (responseCode == CommonConst.ACTIVITY_VOID) {
                    Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();
                    model.Position[] routes = gson.fromJson(response, model.Position[].class);

                    StaticRequest.fetchFromServer(URLS.Events(device.getId(), dates[0], dates[1]), context, timer, (response1, statusCode) -> {
                        if (statusCode == CommonConst.SUCCESS_RESPONSE_CODE) {
                            try {
                                Gson gson1 = new GsonBuilder().setDateFormat(timeFormat).create();
                                final Event[] events = gson1.fromJson(response1, Event[].class);
                                drawAndAnimateCar(routes, events);
                            } catch (IllegalStateException | JsonSyntaxException exception) {
                                Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
                            }
                        } else if (statusCode == CommonConst.ACTIVITY_MOVE_BACK) {
                            finish();
                        } else if (statusCode == CommonConst.ACTIVITY_RETRY_CODE) {
                            timer = timer + 5000;
                            runInit();
                        }
                    });
                }
            } catch (Exception ex) {
                Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);

            }
        });
    }

    private void drawAndAnimateCar(Position[] routes, Event[] events) {
        Collections.addAll(routeList, routes);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        ArrayList<Marker> eventsList = new ArrayList<>();
        for (model.Position route : routeList) {
            points.add(new LatLng(route.getLatitude(), route.getLongitude()));  //Points are LatLng list
            builder.include(new LatLng(route.getLatitude(), route.getLongitude()));
            Common.logd(TAG, route.toString());
            for (model.Event event : events) {
                if (event.getPositionId() == route.getId()) {
                    if (!(event.getType().equals("deviceMoving") || event.getType().equals("deviceStopped")
                            || event.getType().equals("deviceOffline") || event.getType().equals("deviceOnline"))) {

                        if (event.getType().equals("deviceOverspeed")) {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .title(getString(R.string.over_speeding_txt_route))
                                    .position(new LatLng(route.getLatitude(), route.getLongitude()))
//                                    //.flat(true)
                                    .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIconsMaps("over_speeding", getApplicationContext())));
                            eventsList.add(mapPolygon.getGoogleMap().addMarker(markerOptions));
                        }
                        if (event.getType().equals("alarm")) {
                            if (event.getAttributes().getAlarm().toLowerCase().contains("brak")) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .title(getString(R.string.harsh_braking_txt_route))
                                        .position(new LatLng(route.getLatitude(), route.getLongitude()))
//                                        //.flat(true)
                                        .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIconsMaps("harsh_breaking", getApplicationContext())));
                                eventsList.add(mapPolygon.getGoogleMap().addMarker(markerOptions));
                            } else if (event.getAttributes().getAlarm().toLowerCase().contains("acce")) {

                                MarkerOptions markerOptions = new MarkerOptions()
                                        .title(getString(R.string.harsh_acceleration_txt_route))
                                        .position(new LatLng(route.getLatitude(), route.getLongitude()))
//                                        //.flat(true)
                                        .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIconsMaps("harsh_acceleration", getApplicationContext())));
                                eventsList.add(mapPolygon.getGoogleMap().addMarker(markerOptions));
                            } else if (event.getAttributes().getAlarm().toLowerCase().contains("corner")) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .title(getString(R.string.harsh_cornering_txt_route))
                                        .position(new LatLng(route.getLatitude(), route.getLongitude()))
//                                        //.flat(true)
                                        .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIconsMaps("harsh_cornering", getApplicationContext())));
                                eventsList.add(mapPolygon.getGoogleMap().addMarker(markerOptions));
                            } else if (event.getAttributes().getAlarm().toLowerCase().contains("idle")) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .title(getString(R.string.idle_txt_route))
                                        .position(new LatLng(route.getLatitude(), route.getLongitude()))
//                                        //.flat(true)
                                        .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIconsMaps("idling", getApplicationContext())));
                                eventsList.add(mapPolygon.getGoogleMap().addMarker(markerOptions));

                            }
                        }
                    }
                }
            }
        }

        mapPolygon.setEvents(eventsList);


        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mapPolygon.getGoogleMap().animateCamera(mCameraUpdate);

        polylineOptions = new PolylineOptions();
        polylineOptions.color(getResources().getColor(R.color.blue_grey_600));
        polylineOptions.width(10);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(points);
        greyPolyLine = mapPolygon.getGoogleMap().addPolyline(polylineOptions);

        PolylineOptions blackPolylineOptions = new PolylineOptions();
        blackPolylineOptions.width(15);
        blackPolylineOptions.color(getResources().getColor(R.color.blue_700));
        blackPolylineOptions.startCap(new SquareCap());
        blackPolylineOptions.endCap(new SquareCap());
        blackPolylineOptions.jointType(ROUND);
        blackPolyline = mapPolygon.getGoogleMap().addPolyline(blackPolylineOptions);


        String timeFormat = "HH:mm";
        String dateFormat = "MMM, dd, yyyy";


        @SuppressLint("SimpleDateFormat") MarkerOptions markerOptionsStart = new MarkerOptions()
                .title("")
                .position(points.get(0))
//                //.flat(true)
                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(getString(R.string.start_point) + "\n" + new SimpleDateFormat(timeFormat).format(trip.getStartTime())
                        + " " + new SimpleDateFormat(dateFormat).format(trip.getStartTime()))))
                .anchor((float) 0.5, (float) 1.5);

        @SuppressLint("SimpleDateFormat") MarkerOptions markerOptionsEnd = new MarkerOptions()
                .title("")
                .position(points.get(points.size() - 1))
//                //.flat(true)
                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(getString(R.string.end_point) + "\n" +
                        new SimpleDateFormat(timeFormat).format(trip.getEndTime()) + " " + new SimpleDateFormat(dateFormat).format(trip.getEndTime()))))
                .anchor((float) 0.5, (float) 1.5);

        mapPolygon.getGoogleMap().addMarker(markerOptionsStart);

        mapPolygon.getGoogleMap().addMarker(markerOptionsEnd);

        ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
        polylineAnimator.setDuration(4000);
        polylineAnimator.setRepeatCount(1000);
        polylineAnimator.setInterpolator(new LinearInterpolator());
        polylineAnimator.addUpdateListener(valueAnimator -> {
            List<LatLng> points = greyPolyLine.getPoints();
            int percentValue = (int) valueAnimator.getAnimatedValue();
            int size = points.size();
            int newPoints = (int) (size * (percentValue / 100.0f));
            List<LatLng> p = points.subList(0, newPoints);
            blackPolyline.setPoints(p);
        });

        polylineAnimator.start();

        mapPolygon.getGoogleMap().addMarker(new MarkerOptions().position(points.get(0))
//                //.flat(true)
        );
        mapPolygon.getGoogleMap().addMarker(new MarkerOptions().position(points.get(points.size() - 1))
//                //.flat(true)
        );


    }

    public void clickRouteGeoFence(View view) {

        ArrayList<Marker> events = mapPolygon.getEvents();

        if (!mapPolygon.isGeoFenceVisibility()) {
            Common.logd(TAG, "false");
            StaticFunction.addGeoFencesRoute(this, ((SmartTracker) getApplication()).getGeoFences(), mapPolygon);

        } else {
            for (int i = 0; i < events.size(); i++) {
                events.get(i).setVisible(false);
            }
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

    public void clickRouteEvents(View view) {
        ArrayList<Marker> eventList = mapPolygon.getEvents();
        if(!mapPolygon.isEventVisibility()){
            for(int i=0;i<eventList.size();i++){
                eventList.get(i).setVisible(true);
            }
            mapPolygon.setEventVisibility(true);
        }else{

            for(int i=0;i<eventList.size();i++){
                eventList.get(i).setVisible(false);
            }
            mapPolygon.setEventVisibility(false);
        }

    }


    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        SweetAlertDialog eDialog;
        Context activity;
        Date startDate, endDate;

        AsyncTaskRunner(Context context, Date startDate, Date endDate) {
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
            fetchRouteFromServer(startDate, endDate, context);
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
