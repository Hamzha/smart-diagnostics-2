package util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.applikeysolutions.cosmocalendar.dialog.CalendarDialog;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.settings.appearance.ConnectedDayIconPosition;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.ui.IconGenerator;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import model.Circle;
import model.Device;
import model.GeoFence;
import model.MapPolygon;
import model.Polygon;
import model.Position;

import static android.content.Context.LOCATION_SERVICE;
import static util.CommonConst.TRIP_DAYS;

public class StaticFunction {
    private static String TAG = ">>>" + StaticFunction.class.getSimpleName();

    public static ArrayList<Marker> addMarkersTitle(Map<Long, Position> positions, Map<Long, Device> devices, GoogleMap mMap, Context context) {
        ArrayList<Marker> markers = new ArrayList<>();

        for (Map.Entry<Long, Position> position : positions.entrySet()) {
            IconGenerator iconGenerator = new IconGenerator(context);
            Marker marker = mMap.addMarker(new MarkerOptions()
                            .title(Objects.requireNonNull(devices.get(position.getValue().getDeviceId())).getName())
                            .position(new LatLng(position.getValue().getLatitude(), position.getValue().getLongitude()))
                            .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(Objects.requireNonNull(devices.get(position.getValue().getDeviceId())).getName())))
                            .zIndex(position.getValue().getDeviceId())
                            .anchor((float) 0.42, (float) 1.8)
            );
            marker.setTag(Objects.requireNonNull(devices.get(position.getKey())).getName());
            markers.add(marker);
        }
        return markers;
    }

    public static ArrayList<Marker> addMarkersIcon(Map<Long, Position> positions, Map<Long, Device> devices, GoogleMap mMap, Context context) {
        ArrayList<Marker> markers = new ArrayList<>();
        for (Map.Entry<Long, Position> position : positions.entrySet()) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(position.getValue().getLatitude(), position.getValue().getLongitude()))
                            .zIndex(position.getValue().getDeviceId())
//                    .flat(false)
//                    .rotation(LocationUtils.sBearing)
                            .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIcons(StaticFunction.markerFile(Objects.requireNonNull(devices.get(position.getKey())), position.getValue()), context)))
            );
            marker.setTag(Objects.requireNonNull(devices.get(position.getKey())).getName());
            markers.add(marker);
        }
        return markers;
    }

    private static Bitmap resizeMapIcons(String iconName, Context context) {

        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        if (imageBitmap == null) {
            if (iconName.contains("online")) {
                iconName = "marker_default_online";
            } else {
                iconName = "marker_default_offline";
            }
            imageBitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));

        }
        return Bitmap.createScaledBitmap(imageBitmap, 100, 100, false);

    }

    public static Bitmap resizeMapIconsMaps(String iconName, Context context) {
//        int resID = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
//        Drawable drawable = context.getResources().getDrawable(resID);
//        Canvas canvas = new Canvas();
//        Bitmap bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
//        canvas.setBitmap(bitmap);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        drawable.draw(canvas);
//        return bitmap;
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, 90, 100, false);

    }

    public static Bitmap resizeMapIconsMapsLowWidth(String iconName, Context context) {
//        int resID = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
//        Drawable drawable = context.getResources().getDrawable(resID);
//        Canvas canvas = new Canvas();
//        Bitmap bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
//        canvas.setBitmap(bitmap);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        drawable.draw(canvas);
//        return bitmap;
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, 80, 100, false);

    }

    public static String markerFile(Device device, Position position) {
        String name;
        if (position.getPositionAttributes().getIgnition() == null) {
            name = "marker_" +
                    device.getCategory() +
                    "_" + "offline";
        } else if (position.getPositionAttributes().getIgnition())
            name = "marker_" +
                    device.getCategory() +
                    "_" + "online";
        else
            name = "marker_" +
                    device.getCategory() +
                    "_" + "offline";

        return name;
    }

//    public static String markerFile(Device device) {
//        return "marker_" +
//                device.getCategory() +
//                "_" +
//                (device.getStatus().equals("unknown") ? "offline" : device.getStatus());
//    }
//
//    public static String markerCommonFile(Device device) {
//
//        return "marker_" +
//                "default" +
//                "_" +
//                (device.getStatus().equals("unknown") ? "offline" : device.getStatus());
//
//    }

    public static GoogleMap addGeoFences(Context context, GoogleMap mMap, Map<Long, GeoFence> geofences) {
        boolean check = SharedPreferenceHelper.getSharedPreferenceBoolean(context, SharedPrefConst.POLYGON_PREF_FILE, SharedPrefConst.POLYGON_VISIBILITY, false);

        if (!check) {

            for (Map.Entry<Long, GeoFence> entry : geofences.entrySet()) {
                GeoFence geoFence = entry.getValue();
                Pair<Integer, Object> pair = decodeArea(geoFence.getArea(), context);
                Common.logd(TAG, geoFence.toString());
                final TextView textView = new TextView(context);
                textView.setText(geoFence.getName());
                textView.setTextSize(20);
                final Paint paintText = textView.getPaint();
                final Rect boundsText = new Rect();
                paintText.getTextBounds(geoFence.getName(), 0, textView.length(), boundsText);
                paintText.setTextAlign(Paint.Align.CENTER);
                final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
                        * 20, boundsText.height() + 20 * 5, conf);

                final Canvas canvasText = new Canvas(bmpText);
                paintText.setColor(Color.BLACK);
                @SuppressWarnings("IntegerDivisionInFloatingPointContext") float halfCanvas = (canvasText.getWidth() / 2);
                canvasText.drawText(geoFence.getName(), halfCanvas,
                        canvasText.getHeight() - 20 - boundsText.bottom, paintText);

                assert pair != null;
                switch (pair.first) {
                    case 1:
                        Polygon polygon = (Polygon) pair.second;
                        IconGenerator iconGenerator = new IconGenerator(context);
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        if (geoFence.getGeofenceAttributes().getColor() != null) {
                            String[] color = geoFence.getGeofenceAttributes().getColor().split("#");
                            PolygonOptions test = new PolygonOptions().addAll(polygon.getLatLngs()).fillColor(Color.parseColor("#37" + color[1]));
                            List<LatLng> list = test.getPoints();
                            for (int i = 0; i < list.size(); i++) {
                                builder.include(list.get(i));
                            }
                            LatLngBounds latLngBounds = builder.build();
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLngBounds.getCenter())
                                    //.icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                                    .anchor(0.5f, 1).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(geoFence.getName()))));
                            test.strokeColor(0);
                            mMap.addPolygon(test);

                        } else {
                            PolygonOptions test = new PolygonOptions().addAll(polygon.getLatLngs()).fillColor(context.getResources().getColor(R.color.transparent_geo_fence));
                            List<LatLng> list = test.getPoints();
                            for (int i = 0; i < list.size(); i++) {
                                builder.include(list.get(i));
                            }
                            LatLngBounds latLngBounds = builder.build();
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLngBounds.getCenter())
                                    //.icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                                    .anchor(0.5f, 1).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(geoFence.getName()))));
                            test.strokeColor(0);
                            mMap.addPolygon(test);
                        }
                        break;

                    case 2: //Circle Type
                        Circle circle = (Circle) pair.second;
                        iconGenerator = new IconGenerator(context);
                        mMap.addMarker(new MarkerOptions()
                                .position(circle.getLatLng())
                                //.icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                                .anchor(0.5f, 1).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(geoFence.getName()))));

                        mMap.addCircle(new CircleOptions()
                                .radius(circle.getRadius())
                                .center(circle.getLatLng())
                                .fillColor(context.getResources().getColor(R.color.transparent_geo_fence))
                        );
                        break;
                }

                if (decodeArea(geoFence.getArea(), context) == null) {

                    return mMap;
                }
            }
        }

        SharedPreferenceHelper.setSharedPreferenceBoolean(context, SharedPrefConst.POLYGON_PREF_FILE, SharedPrefConst.POLYGON_VISIBILITY, !check);
        return mMap;
    }

    public static MapPolygon addGeoFencesRoute(Context context, Map<Long, GeoFence> geofences, MapPolygon mapPolygon) {

        ArrayList<com.google.android.gms.maps.model.Polygon> polygonList = new ArrayList<>();
        ArrayList<com.google.android.gms.maps.model.Circle> circleList = new ArrayList<>();
        ArrayList<Marker> markers = new ArrayList<>();

        for (Map.Entry<Long, GeoFence> entry : geofences.entrySet()) {
            GeoFence geoFence = entry.getValue();
            Pair<Integer, Object> pair = decodeArea(geoFence.getArea(), context);
            Common.logd(TAG, geoFence.toString());
            final TextView textView = new TextView(context);
            textView.setText(geoFence.getName());
            textView.setTextSize(20);
            final Paint paintText = textView.getPaint();
            final Rect boundsText = new Rect();
            paintText.getTextBounds(geoFence.getName(), 0, textView.length(), boundsText);
            paintText.setTextAlign(Paint.Align.CENTER);
            final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
                    * 20, boundsText.height() + 20 * 5, conf);

            final Canvas canvasText = new Canvas(bmpText);
            paintText.setColor(Color.BLACK);
            @SuppressWarnings("IntegerDivisionInFloatingPointContext") float halfCanvas = (canvasText.getWidth() / 2);
            canvasText.drawText(geoFence.getName(), halfCanvas,
                    canvasText.getHeight() - 20 - boundsText.bottom, paintText);

            assert pair != null;
            switch (pair.first) {
                case 1:
                    Polygon polygon = (Polygon) pair.second;
                    IconGenerator iconGenerator = new IconGenerator(context);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();


                    if (geoFence.getGeofenceAttributes().getColor() != null) {
                        String[] color = geoFence.getGeofenceAttributes().getColor().split("#");
                        PolygonOptions test = new PolygonOptions().addAll(polygon.getLatLngs()).fillColor(Color.parseColor("#37" + color[1]));
                        List<LatLng> list = test.getPoints();
                        for (int i = 0; i < list.size(); i++) {
                            builder.include(list.get(i));
                        }
                        LatLngBounds latLngBounds = builder.build();
                        Marker marker = mapPolygon.getGoogleMap().addMarker(new MarkerOptions()
                                .position(latLngBounds.getCenter())
                                //.icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                                .anchor(0.5f, 1).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(geoFence.getName()))));
                        test.strokeColor(0);
                        markers.add(marker);
                        polygonList.add(mapPolygon.getGoogleMap().addPolygon(test));

                    } else {
                        PolygonOptions test = new PolygonOptions().addAll(polygon.getLatLngs()).fillColor(context.getResources().getColor(R.color.transparent_geo_fence));
                        List<LatLng> list = test.getPoints();
                        for (int i = 0; i < list.size(); i++) {
                            builder.include(list.get(i));
                        }
                        LatLngBounds latLngBounds = builder.build();
                        Marker marker = mapPolygon.getGoogleMap().addMarker(new MarkerOptions()
                                .position(latLngBounds.getCenter())
                                //.icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                                .anchor(0.5f, 1).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(geoFence.getName()))));
                        test.strokeColor(0);
                        markers.add(marker);

                        polygonList.add(mapPolygon.getGoogleMap().addPolygon(test));

                    }
                    break;

                case 2: //Circle Type
                    Circle circle = (Circle) pair.second;
                    iconGenerator = new IconGenerator(context);
                    Marker marker = mapPolygon.getGoogleMap().addMarker(new MarkerOptions()
                            .position(circle.getLatLng())
                            //.icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                            .anchor(0.5f, 1).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(geoFence.getName()))));
                    markers.add(marker);
                    circleList.add(mapPolygon.getGoogleMap().addCircle(new CircleOptions()
                            .radius(circle.getRadius())
                            .center(circle.getLatLng())
                            .fillColor(context.getResources().getColor(R.color.transparent_geo_fence))
                    ));
                    break;
            }
            if (decodeArea(geoFence.getArea(), context) == null) {
                mapPolygon.setCircleList(circleList);
                mapPolygon.setPolygonList(polygonList);
                mapPolygon.setGoogleMap(mapPolygon.getGoogleMap());
                mapPolygon.setMarkers(markers);
                return mapPolygon;
            }
        }

        mapPolygon.setGeoFenceVisibility(!mapPolygon.isGeoFenceVisibility());
        mapPolygon.setCircleList(circleList);
        mapPolygon.setPolygonList(polygonList);
        mapPolygon.setGoogleMap(mapPolygon.getGoogleMap());
        mapPolygon.setMarkers(markers);
        return mapPolygon;
    }

    private static Pair<Integer, Object> decodeArea(String area, Context context) {
        int areaType = 0;

        if (area.contains("polygon".toUpperCase())) {
            areaType = 1;
        } else if (area.contains("circle".toUpperCase())) areaType = 2;


        switch (areaType) {
            case 1:     ////////// Polygon
                final String regexPolygon = "\\(\\((.*?)\\)\\)";
                final Pattern patternPolygon = Pattern.compile(regexPolygon, Pattern.MULTILINE);
                Matcher matcher = patternPolygon.matcher(area);
                List<LatLng> latLngs = new LinkedList<>();

                if (matcher.find()) {

                    String latlnString = matcher.group(1);

                    String[] latlng = latlnString.split(",");

                    for (String ltln : latlng) {

                        String[] tmp = ltln.trim().split(" ");
                        double Lat = Double.valueOf(tmp[0]);
                        double Lon = Double.valueOf(tmp[1]);
                        latLngs.add(new LatLng(Lat, Lon));
                    }
                    return new Pair<>(areaType, new Polygon(latLngs));

                }
                break;
            case 2: //////////    Circle
                final String regexCircle = context.getString(R.string.regixCircle);
                final Pattern patternCircle = Pattern.compile(regexCircle, Pattern.MULTILINE);
                Matcher matcherCircle = patternCircle.matcher(area);

                if (matcherCircle.find()) {
                    String[] str = matcherCircle.group(1).split(",");

                    String[] LatLong = str[0].trim().split(" ");

                    double Lat = Double.valueOf(LatLong[0].trim());

                    double Lon = Double.valueOf(LatLong[1].trim());

                    double radius = Double.valueOf(str[1].trim());
                    return new Pair<>(areaType, new Circle(radius, new LatLng(Lat, Lon)));
                }
                break;
        }
        return null;
    }

    public static void dismissProgressDialog(SweetAlertDialog pDialog) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

//    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
//    public static AlertDialog showCustomDialogMain(final Device device, Activity activity) {
//
//        Position position = ((SmartTracker) activity.getApplicationContext()).getPositions().get(device.getId());
////
//        position = Common.setAddress(position, activity);
//
//        AlertDialog.Builder dialogBuilder;
//        AlertDialog dialog;
//        dialogBuilder = new AlertDialog.Builder(activity);
//        @SuppressLint("InflateParams") View layoutView = activity.getLayoutInflater().inflate(R.layout.options_card_view_compact_list, null);
//        dialogBuilder.setView(layoutView);
//        dialog = dialogBuilder.create();
//        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
//        dialog.show();
//
//        ((CircleImageView) dialog.findViewById(R.id.marker_dialog_top_image)).setImageResource(activity.getResources().getIdentifier(StaticFunction.markerFile(device, position), "drawable", activity.getPackageName()));
//        ((TextView) dialog.findViewById(R.id.marker_dialog_top_title)).setText(device.getName());
//
//        if (position.getAddress(activity) == null)
//            ((TextView) dialog.findViewById(R.id.marker_dialog_top_content)).setText(activity.getString(R.string.location_not_available));
//        else
//            ((TextView) dialog.findViewById(R.id.marker_dialog_top_content)).setText((CharSequence) position.getAddress(activity));
//
//        dialog.findViewById(R.id.dialog_bt_close).setOnClickListener(v -> dialog.dismiss());
//
//        ((AppCompatButton) dialog.findViewById(R.id.follow_btn)).setText(device.getStatus().toUpperCase());
//
//        int status_color = device.getStatus().toLowerCase().equals("online") ? R.color.green_600 : R.color.red_600;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            dialog.findViewById(R.id.follow_btn).setBackgroundTintList(activity.getResources().getColorStateList(status_color));
//        } else {
//            dialog.findViewById(R.id.follow_btn).setBackgroundColor(status_color);
//        }
//        ((AppCompatButton) dialog.findViewById(R.id.route_to_btn)).setOnClickListener(new RouteToButtonListener((Activity) activity, position));
//
//        (dialog.findViewById(R.id.trips_btn)).setOnClickListener(view -> {
//            if (Common.isInternetConnected((Activity) activity)) {
//                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
//            } else {
//                StaticFunction.SingleChoiceDialogAndButtonAction(device, activity, Trip.class);
//            }
//        });
//
//        dialog.findViewById(R.id.mobilizer_btn).setOnClickListener(view -> {
//            if (Common.isInternetConnected((Activity) activity)) {
//                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
//            } else
//                activity.startActivity(new Intent(activity, Immobilizer.class).putExtra("Device", device).putExtra("Name", device.getName()));
//        });
//
//        dialog.findViewById(R.id.track_it_live_btn).setOnClickListener(view -> {
////            Log.d(TAG, "onClick: " + "live");
//            if (Common.isInternetConnected((Activity) activity)) {
//                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
//            } else
//                activity.startActivity(new Intent(activity, LiveVehicle.class).putExtra("Device", device));
//        });
//
//        dialog.findViewById(R.id.event_btn).setOnClickListener(view -> {
//            if (Common.isInternetConnected((Activity) activity)) {
//                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
//            } else {
//                StaticFunction.SingleChoiceDialogAndButtonAction(device, (Activity) activity, Event.class);
//
//            }
//        });
//
//        dialog.findViewById(R.id.summary_btn).setOnClickListener(view -> {
//            if (Common.isInternetConnected((Activity) activity)) {
//                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
//            } else {
////                newActivityAction(view, device, 1, activity, (Activity) activity, Summary.class);
//                StaticFunction.SingleChoiceDialogAndButtonAction(device, activity, Summary.class);
//
//            }
//        });
//
//        dialog.show();
//
//        return dialog;
//    }

//    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
//    public static void showCustomDialog(final Device device, final Position position, Activity activity) {
//
//        AlertDialog.Builder dialogBuilder;
//        AlertDialog dialog;
//        dialogBuilder = new AlertDialog.Builder(activity);
//        @SuppressLint("InflateParams") View layoutView = activity.getLayoutInflater().inflate(R.layout.options_dialog, null);
//        dialogBuilder.setView(layoutView);
//        dialog = dialogBuilder.create();
//        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
//        dialog.show();
//
//        Double avgSpeed = getAvgSpeed(device.getId(), activity);
//        Double maxSpeed = getMaxSpeed(device.getId(), activity);
//        Double totalDuration = getDuration(device.getId(), activity);
//        int totalTrips = getTotalTrips(device.getId(), activity);
//        Double totalDistance = getTotalDistance(device.getId(), activity);
//
//        ((CircleImageView) dialog.findViewById(R.id.marker_dialog_top_image)).setImageResource(activity.getResources().getIdentifier(StaticFunction.markerFile(device, position), "drawable", activity.getPackageName()));
//        ((TextView) dialog.findViewById(R.id.marker_dialog_top_title)).setText(device.getName());
//        ((TextView) dialog.findViewById(R.id.summary_avg_speed_dialog)).setText(avgSpeed + " KPH");
//        ((TextView) dialog.findViewById(R.id.summary_max_speed_dialog)).setText(maxSpeed + " KPH");
//        ((TextView) dialog.findViewById(R.id.summary_total_duration_dialog)).setText(totalDuration + "mins");
//        ((TextView) dialog.findViewById(R.id.summary_total_trips_dialog)).setText(Double.toString(totalTrips));
//        ((TextView) dialog.findViewById(R.id.summary_total_distance_dialog)).setText(totalDistance + " KM");
//
//
//        if (position.getAddress(activity) == null)
//            ((TextView) dialog.findViewById(R.id.marker_dialog_top_content)).setText("Location: Not Available");
//        else
//            ((TextView) dialog.findViewById(R.id.marker_dialog_top_content)).setText("Location: " + position.getAddress(activity));
//
//        dialog.findViewById(R.id.dialog_bt_close).setOnClickListener(v -> dialog.dismiss());
//
//        ((AppCompatButton) dialog.findViewById(R.id.follow_btn)).setText(device.getStatus().toUpperCase());
//
//        int status_color = device.getStatus().toLowerCase().equals("online") ? R.color.green_600 : R.color.red_600;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            dialog.findViewById(R.id.follow_btn).setBackgroundTintList(activity.getResources().getColorStateList(status_color));
//        } else {
//            dialog.findViewById(R.id.follow_btn).setBackgroundColor(status_color);
//        }
//        dialog.findViewById(R.id.route_to_btn).setOnClickListener(new RouteToButtonListener((Activity) activity, position));
//
//        (dialog.findViewById(R.id.trips_btn)).setOnClickListener(view -> {
//            if (Common.isInternetConnected(activity)) {
//                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
//            } else {
//                StaticFunction.SingleChoiceDialogAndButtonAction(device, activity, Trip.class);
//            }
//        });
//
//        dialog.findViewById(R.id.mobilizer_btn).setOnClickListener(view -> {
//            if (Common.isInternetConnected(activity)) {
//                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
//            } else
//                activity.startActivity(new Intent(activity, Immobilizer.class).putExtra("Device", device).putExtra("Name", device.getName()));
//        });
//
//        dialog.findViewById(R.id.track_it_live_btn).setOnClickListener(view -> {
////            Log.d(TAG, "onClick: " + "live");
//            if (Common.isInternetConnected((Activity) activity)) {
//                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
//            } else
//                activity.startActivity(new Intent(activity, LiveVehicle.class).putExtra("Device", device));
//        });
//
//        dialog.findViewById(R.id.event_btn).setOnClickListener(view -> {
//            if (Common.isInternetConnected(activity)) {
//                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
//            } else {
//                StaticFunction.SingleChoiceDialogAndButtonAction(device, activity, Event.class);
//
//            }
//        });
//
//        dialog.findViewById(R.id.summary_btn).setOnClickListener(view -> {
//            if (Common.isInternetConnected(activity)) {
//                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
//            } else {
////                newActivityAction(view, device, 1, activity, (Activity) activity, Summary.class);
//                StaticFunction.SingleChoiceDialogAndButtonAction(device, activity, Summary.class);
//
//            }
//        });
//        dialog.show();
//    }

//    private static Double getTotalDistance(long id, Activity activity) {
//        Map<Long, Double> totalDistance = ((SmartTracker) activity.getApplicationContext()).getTotalDistance();
//        return totalDistance.get(id);
//
//    }

//    private static int getTotalTrips(long id, Activity activity) {
//        Map<Long, Integer> totalTrips = ((SmartTracker) activity.getApplicationContext()).getTotalTrips();
//        return totalTrips.get(id);
//
//    }

//    private static Double getDuration(long id, Activity activity) {
//        Map<Long, Double> getDuration = ((SmartTracker) activity.getApplicationContext()).getDuration();
//        return getDuration.get(id);
//
//    }
//
//    private static Double getMaxSpeed(long id, Activity activity) {
//        Map<Long, Double> maxSpeed = ((SmartTracker) activity.getApplicationContext()).getMaxSpeed();
//        return maxSpeed.get(id);
//    }
//
//    private static Double getAvgSpeed(long id, Activity activity) {
//        Map<Long, Double> totalAvgSpeed = ((SmartTracker) activity.getApplicationContext()).getTotalAvgSpeed();
//        return totalAvgSpeed.get(id);
//    }

    public static void getLastKnownLocation(GoogleMap mMap, double latitude, double longitude) {
        LatLng myLatLng = new LatLng(latitude, longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 15);
        mMap.animateCamera(cameraUpdate);
    }

    public static void getLastKnownLocation(Context context, GoogleMap mMap) {
        LocationManager mLocationManager;

        mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        assert bestLocation != null;
        LatLng myLatLng = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 15);
        mMap.animateCamera(cameraUpdate);
    }

    public static void SingleChoiceDialogAndButtonAction(final Device device, Activity context, Class<?> className) {

        final int[] single_choice_selected = new int[1];
        single_choice_selected[0] = TRIP_DAYS[0];
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.select_history));
        final String[] my = new String[TRIP_DAYS.length];
        for (int i = 0; i < TRIP_DAYS.length - 1; i++) {
            my[i] = +TRIP_DAYS[i] + " " + context.getString(R.string.detail);
        }
        if (className.getSimpleName().toLowerCase().contains("summary"))
            my[TRIP_DAYS.length - 1] = context.getString(R.string.custom_date_31);
        else
            my[TRIP_DAYS.length - 1] = context.getString(R.string.custom_date_10);
        builder.setSingleChoiceItems(my, 0, (dialogInterface, i) -> single_choice_selected[0] = TRIP_DAYS[i]);

        builder.setPositiveButton(R.string.OK, (dialogInterface, i) -> {
            if (single_choice_selected[0] == 60) {
                StaticFunction.SelectNewDays(context, device, className);
            } else
                activityCall(device, single_choice_selected[0], context, context, className);
        });
        builder.setNegativeButton(R.string.CANCEL, null);
        builder.show();
    }

    private static void activityCall(Device device, final int days, Context context, Context activity, Class<?> tripClass) {
        Date endDate = new Date();
        Date startDate = calculateDateFromDays(days);
        Common.logd(TAG, startDate + " : " + endDate);
        context.startActivity(new Intent(activity, tripClass)
                .putExtra("Device", device)
                .putExtra("StartDate", startDate)
                .putExtra("EndDate", endDate));
    }

    public static Date calculateDateFromDays(int days) {
        Date today = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -days);
        return cal.getTime();
    }


    @Deprecated
    public static List<Pair<Marker, Marker>> markersPair(Map<Long, Position> positions, Map<Long, Device> devices, GoogleMap mMap, OnMapReadyCallback context) {
        List<Pair<Marker, Marker>> markersPair = new ArrayList<>();

        for (Map.Entry<Long, Position> position : positions.entrySet()) {

            IconGenerator iconGenerator = new IconGenerator((Context) context);

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .title(Objects.requireNonNull(devices.get(position.getValue().getDeviceId())).getName())
                    .position(new LatLng(position.getValue().getLatitude(), position.getValue().getLongitude()))
                    //.flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(Objects.requireNonNull(devices.get(position.getValue().getDeviceId())).getName())))
                    .zIndex(position.getValue().getDeviceId())
                    .anchor((float) 0.5, (float) 1.5)
            );

            Marker marker2 = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(position.getValue().getLatitude(), position.getValue().getLongitude()))
                    //.flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIcons(StaticFunction.markerFile(Objects.requireNonNull(devices.get(position.getKey())), position.getValue()), (Context) context)))
            );

            markersPair.add(new Pair<>(marker, marker2));

        }
        return markersPair;
    }

    @Deprecated
    public static Map<Long, Pair<Marker, Marker>> markersMap(Map<Long, Position> positions, Map<Long, Device> devices, GoogleMap mMap, OnMapReadyCallback context) {
        @SuppressLint("UseSparseArrays") Map<Long, Pair<Marker, Marker>> markersMap = new HashMap<>();
        for (Map.Entry<Long, Position> position : positions.entrySet()) {

            IconGenerator iconGenerator = new IconGenerator((Context) context);

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .title(Objects.requireNonNull(devices.get(position.getValue().getDeviceId())).getName())
                    .position(new LatLng(position.getValue().getLatitude(), position.getValue().getLongitude()))
                    //.flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(Objects.requireNonNull(devices.get(position.getValue().getDeviceId())).getName())))
                    .zIndex(position.getValue().getDeviceId())
                    .anchor((float) 0.5, (float) 1.5)
            );

            Marker marker2 = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(position.getValue().getLatitude(), position.getValue().getLongitude()))
                    //.flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(StaticFunction.resizeMapIcons(StaticFunction.markerFile(Objects.requireNonNull(devices.get(position.getKey())), position.getValue()), (Context) context)))
            );

            markersMap.put(position.getKey(), new Pair<>(marker, marker2));

        }
        return markersMap;
    }

//    public static void destroySession(MainActivity context) {
//        StaticRequest.sendRegistrationTokenToServer(context, "__");
//        StaticRequest.sendRegistrationTokenToServer(context, "__");
//        final NetworkUtils[] networkUtils = {new NetworkUtils(context)};
//        final String[] URL = {URLConst.BASE_URL + URLConst.SESSION};
//        networkUtils[0].delete(URL[0], 15000, (response, statusCode) -> {
//            if (statusCode == CommonConst.LATE_RESPONSE_CODE) {
//                Common.failPopup(context.getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
//            } else if (statusCode == CommonConst.REQUEST_ERROR_CODE)
//                Common.failPopup(context.getString(R.string.late_response), String.valueOf(CommonConst.REQUEST_ERROR_CODE), context);
//            else {
//                SharedPreferenceHelper.clearSharedPreference(context, SharedPrefConst.OTHER_PREF_FILE);
//                SharedPreferenceHelper.clearSharedPreference(context, SharedPrefConst.RESPONSE_PREF_FILE);
//                clearCookies(context);
//                context.finish();
//                context.startActivity(new Intent(context, Login.class));
//            }
//        });
//    }
//
//    private static void clearCookies(Context context) {
//        ((SmartTracker) context.getApplicationContext()).getCookieManager().getCookieStore().removeAll();
//    }

    private static void SelectNewDays(Activity context, Device device, Class<?> className) {
        CalendarDialog calendarDialog;
        calendarDialog = new CalendarDialog(context);
        calendarDialog.show();
        calendarDialog.setSelectionType(SelectionType.RANGE);
        calendarDialog.setSelectedDayBackgroundStartColor(context.getResources().getColor(R.color.blue_grey_700));
        calendarDialog.setSelectedDayBackgroundEndColor(context.getResources().getColor(R.color.blue_grey_700));
        calendarDialog.setCalendarOrientation(LinearLayoutManager.VERTICAL);
        calendarDialog.setConnectedDayIconPosition(ConnectedDayIconPosition.TOP);
        calendarDialog.setTitle("Select date Range for Trips");

        calendarDialog.setOnDaysSelectionListener(selectedDays -> {
            if (selectedDays.size() >= 1) {
                //int range = (selectedDays.size() <= 1) ? selectedDays.size() : (selectedDays.size() - 1);
                Day startRange = selectedDays.get(0);

                Day endRange = (selectedDays.size() == 1) ? null : selectedDays.get(selectedDays.size() - 1);

                Date startDate = startRange.getCalendar().getTime();
                Date endDate = (endRange == null) ? getPreviousDate(startDate) : endRange.getCalendar().getTime();


                Common.logd(TAG, endDate.toString());
                Common.logd(TAG, startDate.toString());

//
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();


                calendar.setTime(endDate);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                endDate = calendar.getTime();

                Common.logd(TAG, endDate.toString());
                Common.logd(TAG, startDate.toString());


                long startDayMili = startDate.getTime();
                long endDayMili = endDate.getTime();

                int limit = 10;

                if (className.getSimpleName().toLowerCase().contains("summary"))
                    limit = 31;
                long days = TimeUnit.MILLISECONDS.toDays(endDayMili - startDayMili);
                if (days < 0) {
                    Common.failPopup(context.getString(R.string.select_atleast_two_days), String.valueOf(CommonConst.CALENDER_DAYS_ERROR), context);
                } else if (days >= limit) {
                    if (className.getSimpleName().toLowerCase().contains("summary"))
                        Common.failPopup(context.getString(R.string.days_limit_exceeds_30), String.valueOf(CommonConst.CALENDER_DAYS_ERROR), context);
                    else
                        Common.failPopup(context.getString(R.string.days_limit_exceeds_10), String.valueOf(CommonConst.CALENDER_DAYS_ERROR), context);

                } else {
                    Common.logd(TAG, startDate + "+" + endDate);
                    context.startActivity(new Intent(context, className).putExtra("Device", device).putExtra("StartDate", startDate).putExtra("EndDate", endDate));
                }
            } else {
                Toast.makeText(context, context.getString(R.string.select_atleast_two_days), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void SelectSingleDays (Activity context, Device device, Class<?> className) {
        CalendarDialog calendarDialog;
        calendarDialog = new CalendarDialog(context);
        calendarDialog.show();
        calendarDialog.setSelectionType(SelectionType.SINGLE);
        calendarDialog.setSelectedDayBackgroundStartColor(context.getResources().getColor(R.color.blue_grey_700));
        calendarDialog.setSelectedDayBackgroundEndColor(context.getResources().getColor(R.color.blue_grey_700));
        calendarDialog.setCalendarOrientation(LinearLayoutManager.VERTICAL);
        calendarDialog.setConnectedDayIconPosition(ConnectedDayIconPosition.TOP);
        calendarDialog.setTitle("Select date Range for Trips");

        calendarDialog.setOnDaysSelectionListener(selectedDays -> {
            if (selectedDays.size() >= 1) {
                //int range = (selectedDays.size() <= 1) ? selectedDays.size() : (selectedDays.size() - 1);
                Day startRange = selectedDays.get(0);

                Day endRange = (selectedDays.size() == 1) ? null : selectedDays.get(selectedDays.size() - 1);

                Date startDate = startRange.getCalendar().getTime();
                Date endDate = (endRange == null) ? getPreviousDate(startDate) : endRange.getCalendar().getTime();


                Common.logd(TAG, endDate.toString());
                Common.logd(TAG, startDate.toString());

//
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();


                calendar.setTime(endDate);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                endDate = calendar.getTime();

                Common.logd(TAG, endDate.toString());
                Common.logd(TAG, startDate.toString());


                long startDayMili = startDate.getTime();
                long endDayMili = endDate.getTime();

                int limit = 10;

                if (className.getSimpleName().toLowerCase().contains("summary"))
                    limit = 31;
                long days = TimeUnit.MILLISECONDS.toDays(endDayMili - startDayMili);
                if (days < 0) {
                    Common.failPopup(context.getString(R.string.select_atleast_two_days), String.valueOf(CommonConst.CALENDER_DAYS_ERROR), context);
                } else if (days >= limit) {
                    if (className.getSimpleName().toLowerCase().contains("summary"))
                        Common.failPopup(context.getString(R.string.days_limit_exceeds_30), String.valueOf(CommonConst.CALENDER_DAYS_ERROR), context);
                    else
                        Common.failPopup(context.getString(R.string.days_limit_exceeds_10), String.valueOf(CommonConst.CALENDER_DAYS_ERROR), context);

                } else {
                    Common.logd(TAG, startDate + "+" + endDate);
                    context.startActivity(new Intent(context, className).putExtra("Device", device).putExtra("StartDate", startDate).putExtra("EndDate", endDate));
                }
            } else {
                Toast.makeText(context, context.getString(R.string.select_atleast_two_days), Toast.LENGTH_LONG).show();
            }
        });
    }



    private static Date getPreviousDate(Date startDate) {
        Calendar tmp = Calendar.getInstance();
        tmp.setTime(startDate);
        tmp.set(Calendar.HOUR, 0);
        tmp.set(Calendar.MINUTE, 0);
        tmp.set(Calendar.SECOND, 0);
        tmp.add(Calendar.DATE, 1);
        return tmp.getTime();
    }

//    public static void newActivityAction(View view, Device device, final int days, Context context, Activity activity, Class<?> activityClass) {
//        Date endDate = new Date();
//        Date startDate = calculateDateFromDays(days);
//        context.startActivity(new Intent(activity, activityClass).putExtra("Device", device).putExtra("StartDate", startDate).putExtra("EndDate", endDate));
//    }

    public static CameraUpdate zoomingLocation(ArrayList<Marker> markers, Context context) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        final int width = context.getResources().getDisplayMetrics().widthPixels;
        final int height = context.getResources().getDisplayMetrics().heightPixels;
        final int minMetric = Math.min(width, height);
        final int padding = (int) (minMetric * 0.20); // offset from edges of the map in pixels
        LatLngBounds bounds = builder.build();

        return CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
    }

    public static GoogleMap setMapType(GoogleMap mMap, int mapType) {

        switch (mapType) {
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 4:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
        return mMap;
    }

//    public static model.Event[] finalFormatDate(model.Event[] finalEvents, int k) {
//        for (int i = 0; i < k; i++) {
//            if (finalEvents[i].getServerTime().split("\\.").length == 2) {
//                String tmp1 = finalEvents[i].getServerTime().split("\\.")[0];
//                String[] tmp2 = tmp1.split("T");
//                finalEvents[i].setServerTime(tmp2[0] + " " + tmp2[1]);
//            }
//        }
//        return finalEvents;
//    }

//    public static model.Attribute[] getAttributes(String response) {
//        Attribute[] attribute = new Attribute[0];
//        StringBuilder json = new StringBuilder();
//        try {
//            json.append("[");
//
//            JSONArray jArr = new JSONArray(response);
//            for (int count = 0; count < jArr.length(); count++) {
//                JSONObject obj = jArr.getJSONObject(count);
//                String attributes = obj.getString("attributes");
//                json.append(attributes).append(",");
//            }
//            json.append("]");
//            attribute = new Gson().fromJson(json.toString(), Attribute[].class);
//
//            for (int k = 0; k < attribute.length - 1; k++) {
//                attribute[k].setPower((float) Math.round(attribute[k].getPower() * 10d) / 10);
//                if (attribute[k].getPower() < 12)
//                    attribute[k].setPower(12);
//                else if (attribute[k].getPower() > 15)
//                    attribute[k].setPower(15);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return attribute;
//    }

    //    public static void updateMarkers(ArrayList<Marker> markers, Map<Long, Position> positions) {
//        for (int i = 0; i < markers.size(); i++) {
//            Marker marker = markers.get(i);
//
//            for (Map.Entry<Long, Position> entry : positions.entrySet()) {
//                if (marker.getZIndex() == entry.getValue().getDeviceId()) {
//                    marker.remove();
//                    LatLng latLng = new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude());
//                    marker.setPosition(latLng);
//                }
//            }
//        }
//        for (int i = 0; i < markers.size(); i++) {
//            Marker marker = markers.get(i);
//
//            for (Map.Entry<Long, Position> entry : positions.entrySet()) {
//                if (marker.getZIndex() == entry.getValue().getDeviceId()) {
//                    LatLng latLng = new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude());
//                    marker.setPosition(latLng);
//                }
//            }
//        }
//    }


//    public static double mean(double[] m) {
//        double sum = 0;
//        for (int i = 0; i < m.length; i++) {
//            sum += m[i];
//        }
//        return sum / m.length;
//    }
//
//    // the array double[] m MUST BE SORTED
//    public static double median(double[] m) {
//        Arrays.sort(m);
//        int middle = m.length / 2;
//        if (m.length % 2 == 1) {
//            return m[middle];
//        } else {
//            return (m[middle - 1] + m[middle]) / 2.0;
//        }
//    }
//
//    public static double mode(double[] a) {
//        double maxValue = 0, maxCount = 0;
//
//        for (double i1 : a) {
//            int count = 0;
//            for (double i : a) {
//                if (i == i1) ++count;
//            }
//            if (count > maxCount) {
//                maxCount = count;
//                maxValue = i1;
//            }
//        }
//
//        return maxValue;
//    }
//
//    public static double getMax(double[] inputArray) {
//        double maxValue = inputArray[0];
//        for (int i = 1; i < inputArray.length; i++) {
//            if (inputArray[i] > maxValue) {
//                maxValue = inputArray[i];
//            }
//        }
//        return maxValue;
//    }
//
//    public static double getMin(double[] inputArray) {
//        double minValue = inputArray[0];
//        for (int i = 1; i < inputArray.length; i++) {
//            if (inputArray[i] < minValue) {
//                minValue = inputArray[i];
//            }
//        }
//        return minValue;
//    }

//    public static String[] getDate(Date startDate, Date endDate) {
//        String[] date = new String[2];
//        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat);
//        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String fromDate = dateFormat.format(startDate);
//        String toDate = dateFormat.format(endDate);
//        try {
//            date[0] = URLEncoder.encode(fromDate, "UTF-8");
//            date[1] = URLEncoder.encode(toDate, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return date;
//    }


    public static ArrayList<model.Trip> createTripList(model.Trip[] trips) {
        ArrayList<model.Trip> tripsList = new ArrayList<>();
        for (int i = trips.length - 1; i >= 0; i--) {
            if (trips[i].getDistance() / 1000 <= +2000) {
//                if (trips[i].getDistance() < 0) {
//                    trips[i].setDistance(trips[i].getEndOdometer());
//                }
                Common.logd(TAG, trips[i].toString());
                model.Trip trip = trips[i];
                tripsList.add(trip);

            }
        }
        return tripsList;
    }

//    public static void requestPermission(Activity splashScreen) {
//        final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
//
//        ActivityCompat.requestPermissions(splashScreen,
//                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                MY_PERMISSIONS_REQUEST_LOCATION);
//
//    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
