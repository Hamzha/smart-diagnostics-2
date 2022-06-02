package activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import model.Device;
import model.Event;
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

public class SummaryTrip extends BaseClass {

    int timer = 15000;
    private List<model.Position> routeList = new ArrayList<>();
    private int mapType;
    private IconGenerator iconGenerator;
    private Polyline blackPolyline, greyPolyLine;
    private Device device;
    private Date[] startDate = {null};
    private Date[] endDate = {null};
    private PolylineOptions polylineOptions = new PolylineOptions();
    ArrayList<Marker> markers;
    private MapPolygon mapPolygon;

    String TAG = ">>>" + SummaryTrip.class.getSimpleName();

    TextView summaryTotalDuration;
    private long duration = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_trip);

        CookieHandler.setDefault(((SmartTracker) getApplication()).getCookieManager());

        runInit();
    }


    void runInit() {
        if (Common.isInternetConnected(this)) {
            Common.retryDialog(this, CommonConst.NO_INTERNET_CODE, bool -> {
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
        iconGenerator = new IconGenerator(getApplicationContext());
        device = getIntent().getParcelableExtra("Device");
        startDate[0] = (Date) getIntent().getSerializableExtra("StartDate");
        endDate[0] = (Date) getIntent().getSerializableExtra("EndDate");
        Toolbar(SummaryTrip.this, getString(R.string.Trip) + "/" + device.getName());

        summaryTotalDuration = findViewById(R.id.summary_trip_detail_on_map);

        markers = new ArrayList<>();
        AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner(this, startDate[0], endDate[0]);
        asyncTaskRunner.execute();
        mapInit();
        inti();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void inti() {
        this.duration = (long) getIntent().getSerializableExtra("duration");
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) this.duration);
        long hours = TimeUnit.MILLISECONDS.toHours((long) this.duration);
        minutes = minutes - (hours * 60);

        String string = getString(R.string.total_trips_txt) + " " + getIntent().getSerializableExtra("size")
                + "\n"
                + getString(R.string.total_average_route) + " " + getIntent().getSerializableExtra("avg")
                + "\n"
                + getString(R.string.total_top_speed_route) + " " + getIntent().getSerializableExtra("max")
                + "\n"
                + getString(R.string.total_distance_route) + " " + getIntent().getSerializableExtra("dis")
                + "\n"
                + getString(R.string.total_duration_route) + " " + hours + " " + getString(R.string.hours) + "," + minutes + " " + getString(R.string.minutes);

        summaryTotalDuration.setText(string);


    }


    protected void initFetch(Date startDate, Date endDate, SweetAlertDialog eDialog) {
        fetchTripsFromServer(startDate, endDate, eDialog);
    }

    private void fetchTripsFromServer(Date startDate, Date endDate, SweetAlertDialog eDialog) {
        fetchRouteFromServerTrip(startDate, endDate, eDialog);
//        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
//        String[] dates = Common.dateConversion(startDate, endDate);
//        StaticRequest.fetchFromServer(URLS.Trips(dates[0], dates[1], device.getId()), this, timer, (response, statusCode) -> {
//            if (statusCode == CommonConst.ACTIVITY_RETRY_CODE) {
//                timer = timer + 5000;
//                runInit();
//            } else if (statusCode == CommonConst.ACTIVITY_MOVE_BACK) {
//                finish();
//            } else if (statusCode == CommonConst.SUCCESS_RESPONSE_CODE) {
//                Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();
//                try {
//                    model.Trip[] trips = gson.fromJson(response, model.Trip[].class);
//                    for (Trip trip : trips) {
//                        if (trip.getDistance() / 1000 <= +2000) {
//                            if (trip.getDistance() < 0) {
//                                trip.setDistance(trip.getEndOdometer());
//                            }
//                        }
//                        fetchRouteFromServerTrip(trip.getStartTime(), trip.getEndTime(), trip, eDialog);
//                    }
//                } catch (Exception rx) {
//                    Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), this);
//                }
//            }
//        });
    }

    protected void fetchRouteFromServerTrip(final Date startDate, Date endDate, SweetAlertDialog eDialog) {
        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String[] dates = Common.dateConversion(startDate, endDate);

        StaticRequest.fetchRouteFromServer(URLS.Route(device.getId(), dates[0], dates[1]), timer, this, (response, responseCode) -> {
            try {

                if (responseCode == CommonConst.ACTIVITY_RETRY_CODE) {
                    timer = timer + 5000;
                    runInit();
                } else if (responseCode == CommonConst.ACTIVITY_MOVE_BACK) {
                    finish();
                } else if (responseCode == CommonConst.ACTIVITY_VOID) {
                    Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();
                    model.Position[] routes = gson.fromJson(response, model.Position[].class);
                    StaticRequest.fetchFromServer(URLS.Events(device.getId(), dates[0], dates[1]), this, timer, (response1, statusCode) -> {
                        if (statusCode == CommonConst.SUCCESS_RESPONSE_CODE) {
                            try {
                                Gson gson1 = new GsonBuilder().setDateFormat(timeFormat).create();
                                final model.Event[] events = gson1.fromJson(response1, model.Event[].class);
                                drawAndAnimateCar(routes, events, eDialog);

                            } catch (IllegalStateException | JsonSyntaxException exception) {
                                Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), this);
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
                Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), this);

            }
        });
    }


    private void drawAndAnimateCar(Position[] routes, Event[] events, SweetAlertDialog eDialog) {
        Collections.addAll(routeList, routes);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<LatLng> points = new ArrayList<>();
        for (model.Position route : routeList) {
            points.add(new LatLng(route.getLatitude(), route.getLongitude()));  //Points are LatLng list
            builder.include(new LatLng(route.getLatitude(), route.getLongitude()));

            for (model.Event event : events) {
                if (event.getPositionId() == route.getId()) {
                    if (!(event.getType().equals("deviceMoving") || event.getType().equals("deviceStopped")
                            || event.getType().equals("deviceOffline") || event.getType().equals("deviceOnline"))) {
                        if (event.getType().toLowerCase().contains("ignitionon")) {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .title(getString(R.string.ignition_on_txt))
                                    .position(new LatLng(route.getLatitude(), route.getLongitude()))
                                    //.flat(true)
                                    .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIconsMapsLowWidth("ignition_on", getApplicationContext())));
                            markers.add(mapPolygon.getGoogleMap().addMarker(markerOptions));
                        }
                        if (event.getType().toLowerCase().contains("ignitionoff")) {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(new LatLng(route.getLatitude(), route.getLongitude()))
                                    //.flat(true)
                                    .title(getString(R.string.ignition_off_txt))
                                    .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIconsMapsLowWidth("ignition_off", getApplicationContext())));
                            markers.add(mapPolygon.getGoogleMap().addMarker(markerOptions));
                        }
                        Common.logd(TAG, event.getType());
                        if (event.getType().equals("deviceOverspeed")) {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .title(getString(R.string.over_speeding_txt_route))
                                    .position(new LatLng(route.getLatitude(), route.getLongitude()))
                                    //.flat(true)
                                    .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIconsMaps("over_speeding", getApplicationContext())));
                            markers.add(mapPolygon.getGoogleMap().addMarker(markerOptions));
                        }
                        if (event.getType().equals("alarm")) {
                            if (event.getAttributes().getAlarm().toLowerCase().contains("brak")) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .title(getString(R.string.harsh_braking_txt_route))
                                        .position(new LatLng(route.getLatitude(), route.getLongitude()))
                                        //.flat(true)
                                        .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIconsMaps("harsh_breaking", getApplicationContext())));
                                markers.add(mapPolygon.getGoogleMap().addMarker(markerOptions));
                            } else if (event.getAttributes().getAlarm().toLowerCase().contains("acce")) {

                                MarkerOptions markerOptions = new MarkerOptions()
                                        .title(getString(R.string.harsh_acceleration_txt_route))
                                        .position(new LatLng(route.getLatitude(), route.getLongitude()))
                                        //.flat(true)
                                        .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIconsMaps("harsh_acceleration", getApplicationContext())));
                                markers.add(mapPolygon.getGoogleMap().addMarker(markerOptions));
                            } else if (event.getAttributes().getAlarm().toLowerCase().contains("corner")) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .title(getString(R.string.harsh_cornering_txt_route))
                                        .position(new LatLng(route.getLatitude(), route.getLongitude()))
                                        //.flat(true)
                                        .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIconsMaps("harsh_cornering", getApplicationContext())));
                                markers.add(mapPolygon.getGoogleMap().addMarker(markerOptions));
                            } else if (event.getAttributes().getAlarm().toLowerCase().contains("idle")) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .title(getString(R.string.idle_txt_route))
                                        .position(new LatLng(route.getLatitude(), route.getLongitude()))
                                        //.flat(true)
                                        .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIconsMaps("idling", getApplicationContext())));
                                markers.add(mapPolygon.getGoogleMap().addMarker(markerOptions));

                            }
                        }
                    }
                }
            }
        }

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


        ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
        polylineAnimator.setDuration(4000);
        polylineAnimator.setRepeatCount(1000);
        polylineAnimator.setInterpolator(new LinearInterpolator());
        polylineAnimator.addUpdateListener(valueAnimator -> {
            List<LatLng> pointstmp = greyPolyLine.getPoints();
            int percentValue = (int) valueAnimator.getAnimatedValue();
            int size = pointstmp.size();
            int newPoints = (int) (size * (percentValue / 100.0f));
            List<LatLng> p = pointstmp.subList(0, newPoints);
            blackPolyline.setPoints(p);
        });

        polylineAnimator.start();

        StaticFunction.dismissProgressDialog(eDialog);


    }

    private void mapInit() {
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.summary_route_map);

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

    public void clickSummaryRouteMapSwitchAction(View view) {
        mapType = (mapType % 2) + 1;
        StaticFunction.setMapType(mapPolygon.getGoogleMap(), mapType);
    }

    public void clickMarkerSwitchAction(View view) {
        if (markers.size() != 0) {
            if (markers.get(0).isVisible())
                for (int i = 0; i < markers.size(); i++) {
                    markers.get(i).setVisible(false);
                }
            else
                for (int i = 0; i < markers.size(); i++) {
                    markers.get(i).setVisible(true);
                }
        }
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




    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        SweetAlertDialog eDialog;
        SummaryTrip activity;
        Date startDate, endDate;

        AsyncTaskRunner(SummaryTrip context, Date startDate, Date endDate) {
            WeakReference<SummaryTrip> activityReference = new WeakReference<>(context);
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
            initFetch(startDate, endDate, eDialog);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}
