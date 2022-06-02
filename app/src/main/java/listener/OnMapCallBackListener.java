package listener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import activity.Event;
import activity.Fuel;
import activity.Immobilizer;
import activity.LiveVehicle;
import activity.MainActivity;
import activity.Maintenance;
import activity.SplashScreen;
import activity.Summary;
import activity.Trip;
import adaptor.CustomInfoWindowAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import model.Device;
import model.DialogBox;
import model.GeoFence;
import model.Position;
import util.Common;
import util.CommonConst;
import util.StaticFunction;
import util.Tools;

import static util.StaticFunction.zoomingLocation;

public class OnMapCallBackListener implements OnMapReadyCallback {
    private MainActivity context;
    private Map<Long, Device> devices;
    private Map<Long, Position> positions;
    private Map<Long, GeoFence> geoFences;
    private int mapType;
    private String TAG = ">>>" + OnMapCallBackListener.class.getSimpleName();

    private Double lat, lng;
    private GoogleMap mMap;
    private ArrayList<Marker> markersIcon;
    private ArrayList<Marker> markersTitle;
    private AlertDialog dialogBox;

    public OnMapCallBackListener(MainActivity context1) {
        this.context = context1;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMapReady(GoogleMap googleMap) {
        if (init()) {

            mMap = Tools.configActivityMaps(googleMap);
            markersIcon = StaticFunction.addMarkersIcon(positions, devices, mMap, context);
            markersTitle = StaticFunction.addMarkersTitle(positions, devices, mMap, context);
//            mMap = StaticFunction.addGeoFences(context, mMap, geoFences);
            final ArrayList<Marker> markers1 = markersIcon;
            mMap.setOnMapLoadedCallback(() -> {
                if (markers1.size() < 2) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markers1.get(0).getPosition(), 19));
                } else mMap.animateCamera(zoomingLocation(markers1, context));
                if (Common.checkAndAskLocationPermission(context)) {
                    mMap.setMyLocationEnabled(true);
                }
                if ((lat != null) || (lng != null)) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
                }
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(context);
                mMap.setInfoWindowAdapter(adapter);
                mMap = StaticFunction.setMapType(mMap, mapType);
                mMap.setOnMarkerClickListener(marker -> {
                    if (marker != null) {
                        Device device = devices.get((long) marker.getZIndex());
                        for (Map.Entry<Long, Position> entry : positions.entrySet()) {
                            if (device != null) {
                                if (entry.getValue().getDeviceId() == device.getId()) {
                                    break;
                                }
                            } else {
                                Common.logd(TAG, "NUll VALUE");
                            }
                        }
                        if (device == null) {
                            Common.logd(TAG, "device null");
                        } else {
                            if (dialogBox == null) {
                                dialogBox = showCustomDialogMain(device, context);
                            } else if (!dialogBox.isShowing()) {
                                dialogBox = showCustomDialogMain(device, context);
                            } else {
                                Common.logd(TAG, "Already");
                            }
                        }
                        return true;
                    }
                    return false;
                });

            });
            context.setDataUpdate(30000, check -> {
                for (int i = 0; i < this.markersIcon.size(); i++) {
                    Marker marker = this.markersIcon.get(i);
                    marker.remove();
                    marker = this.markersTitle.get(i);
                    marker.remove();
                }
                markersIcon = StaticFunction.addMarkersIcon(positions, devices, mMap, context);
                markersTitle = StaticFunction.addMarkersTitle(positions, devices, mMap, context);
            });
        }
    }

    private boolean init() {
        positions = ((SmartTracker) context.getApplicationContext()).getPositions();
        if (positions == null || positions.size() == 0) {
            context.finish();
            Intent newIntent = new Intent(context, SplashScreen.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(newIntent);
            return false;
        } else {
            mapType = ((SmartTracker) context.getApplicationContext()).getMapType();
            devices = ((SmartTracker) context.getApplicationContext()).getDevices();

            geoFences = ((SmartTracker) context.getApplicationContext()).getGeoFences();

            lat = (Double) context.getIntent().getSerializableExtra(CommonConst.LATITUDE);
            lng = (Double) context.getIntent().getSerializableExtra(CommonConst.LONGITUDE);

        }
        return true;
    }

    public Map<Long, Device> getDevices() {
        return devices;
    }

    public void setDevices(Map<Long, Device> devices) {
        this.devices = devices;
    }

    public Map<Long, Position> getPositions() {
        return positions;
    }

    public void setPositions(Map<Long, Position> positions) {
        this.positions = positions;
    }

    public Map<Long, GeoFence> getGeoFences() {
        return geoFences;
    }

    public void setGeoFences(Map<Long, GeoFence> geoFences) {
        this.geoFences = geoFences;
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public ArrayList<Marker> getMarkers() {
        return markersIcon;
    }

    public void setMarkers(ArrayList<Marker> markers) {
        this.markersIcon = markers;
    }


    @SuppressLint("ResourceAsColor")
    private AlertDialog showCustomDialogMain(final Device device, Activity activity) {

        Position position = ((SmartTracker) activity.getApplicationContext()).getPositions().get(device.getId());
//
//        position = Common.setAddress(position, activity);

        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        dialogBuilder = new AlertDialog.Builder(activity);
        @SuppressLint("InflateParams") View layoutView = activity.getLayoutInflater().inflate(R.layout.options_card_view_compact_list, null);
        dialogBuilder.setView(layoutView);
        dialog = dialogBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        CircleImageView circleImageViewIcon = dialog.findViewById(R.id.marker_dialog_top_image);
        TextView address = dialog.findViewById(R.id.marker_dialog_top_content);

//        ((CircleImageView) dialog.findViewById(R.id.marker_dialog_top_image)).setImageResource(activity.getResources().getIdentifier(StaticFunction.markerFile(device, position), "drawable", activity.getPackageName()));
        ((TextView) dialog.findViewById(R.id.marker_dialog_top_title)).setText(device.getName());


//        if (position.getAddress(activity) == null)
//            ((TextView) dialog.findViewById(R.id.marker_dialog_top_content)).setText(activity.getString(R.string.location_not_available));
//        else
//            ((TextView) dialog.findViewById(R.id.marker_dialog_top_content)).setText((CharSequence) position.getAddress(activity));

        AVLoadingIndicatorView progressBar = dialog.findViewById(R.id.progress_login_dialog);

        AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner(activity, circleImageViewIcon, address, progressBar, position, device);
        asyncTaskRunner.execute();

        dialog.findViewById(R.id.dialog_bt_close).setOnClickListener(v -> dialog.dismiss());

        ((AppCompatButton) dialog.findViewById(R.id.follow_btn)).setText(device.getStatus().toUpperCase());

        int status_color = device.getStatus().toLowerCase().equals("online") ? R.color.green_600 : R.color.red_600;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.findViewById(R.id.follow_btn).setBackgroundTintList(activity.getResources().getColorStateList(status_color));
        } else {
            dialog.findViewById(R.id.follow_btn).setBackgroundColor(status_color);
        }
        ((AppCompatButton) dialog.findViewById(R.id.route_to_btn)).setOnClickListener(new RouteToButtonListener((Activity) activity, position));

        if (device.getAttributes().getFuel() == null || !device.getAttributes().getFuel().contains("true")) {
            dialog.findViewById(R.id.fuel_btn).setVisibility(View.GONE);
        }else
            dialog.findViewById(R.id.fuel_btn).setOnClickListener(view -> {
                if (Common.isInternetConnected(context)) {
                    Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
                } else {
                    StaticFunction.SingleChoiceDialogAndButtonAction(device, activity, Fuel.class);
//                    context.startActivity(new Intent(context, Fuel.class).putExtra("Device", device).putExtra("Name", device.getName()));
                }

            });
        (dialog.findViewById(R.id.maintenance_btn)).setOnClickListener(view -> {
            if (Common.isInternetConnected((Activity) activity)) {
                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
            } else {
                Common.logd(TAG,"maintenance_btn test");
//                StaticFunction.SelectSingleDays(context, device, Maintenance.class);
                                    context.startActivity(new Intent(context, Maintenance.class).putExtra("Device", device).putExtra("Name", device.getName()));

            }
        });

        (dialog.findViewById(R.id.trips_btn)).setOnClickListener(view -> {
            if (Common.isInternetConnected((Activity) activity)) {
                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
            } else {
                StaticFunction.SingleChoiceDialogAndButtonAction(device, activity, Trip.class);
            }
        });

        dialog.findViewById(R.id.mobilizer_btn).setOnClickListener(view -> {
            if (Common.isInternetConnected((Activity) activity)) {
                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
            } else
                activity.startActivity(new Intent(activity, Immobilizer.class).putExtra("Device", device).putExtra("Name", device.getName()));
        });

        dialog.findViewById(R.id.track_it_live_btn).setOnClickListener(view -> {
//            Log.d(TAG, "onClick: " + "live");
            if (Common.isInternetConnected((Activity) activity)) {
                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
            } else
                activity.startActivity(new Intent(activity, LiveVehicle.class).putExtra("Device", device));
        });

        dialog.findViewById(R.id.event_btn).setOnClickListener(view -> {
            if (Common.isInternetConnected((Activity) activity)) {
                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
            } else {
                StaticFunction.SingleChoiceDialogAndButtonAction(device, (Activity) activity, Event.class);

            }
        });

        dialog.findViewById(R.id.summary_btn).setOnClickListener(view -> {
            if (Common.isInternetConnected((Activity) activity)) {
                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
            } else {
//                newActivityAction(view, device, 1, activity, (Activity) activity, Summary.class);
                StaticFunction.SingleChoiceDialogAndButtonAction(device, activity, Summary.class);

            }
        });

        dialog.show();

        return dialog;
    }


    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunner extends AsyncTask<String, String, DialogBox> {
        private Position position;
        Context activity;
        CircleImageView circleImageViewIcon;
        TextView textViewAddress;
        AVLoadingIndicatorView progressBar;
        Device device;

        AsyncTaskRunner(Activity context, CircleImageView circleImageViewIcon, TextView address, AVLoadingIndicatorView progressBar, Position positions, Device device) {
            //        Context[] context;
            WeakReference<Activity> activityReference = new WeakReference<Activity>(context);
            activity = activityReference.get();
            this.circleImageViewIcon = circleImageViewIcon;
            this.textViewAddress = address;
            this.progressBar = progressBar;
            this.position = positions;
            this.device = device;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            circleImageViewIcon.setVisibility(View.INVISIBLE);
            textViewAddress.setVisibility(View.INVISIBLE);

        }

        @Override
        protected DialogBox doInBackground(String... strings) {
            position = Common.setAddress(position, activity);
            return new DialogBox(position, StaticFunction.markerFile(device, position));
        }

        @Override
        protected void onPostExecute(DialogBox s) {
            super.onPostExecute(s);
            if (s.getPosition().getAddress(activity) == null)
                textViewAddress.setText(activity.getString(R.string.location_not_available));
            else
                textViewAddress.setText((CharSequence) s.getPosition().getAddress(activity));
            circleImageViewIcon.setImageResource(activity.getResources().getIdentifier(s.getIconName(), "drawable", activity.getPackageName()));

            progressBar.setVisibility(View.GONE);
            circleImageViewIcon.setVisibility(View.VISIBLE);
            textViewAddress.setVisibility(View.VISIBLE);

        }
    }


}

