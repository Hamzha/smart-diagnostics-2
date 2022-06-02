package activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import listener.OnMapCallBackListener;
import util.BaseClass;
import util.Common;
import util.CommonConst;
import util.SharedPrefConst;
import util.SharedPreferenceHelper;
import util.StaticFunction;
import util.StaticRequest;

import static util.StaticFunction.zoomingLocation;

public class MainActivity extends BaseClass implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    protected double latitude;
    protected double longitude;

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    private int mapType;
    private GoogleMap mMap;
    private ArrayList<Marker> markersIcon;
    private ArrayList<Marker> markersTitle;
    private String TAG = ">>>" + MainActivity.class.getSimpleName();
    private OnMapCallBackListener onMapCallBackListener;
    private RapidFloatingActionHelper rfabHelper;
    private boolean clickableVehicle = true;
    private int checkLocation;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        setNetworkStateReceiver(context);
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

    @Override
    public void onResume() {
        langSelector(this);
        clickableVehicle = true;
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SmartTracker.getRefWatcher(this).watch(this);

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.quit_msg));
        alertDialogBuilder.setPositiveButton(getString(R.string.quit_option_yes),
                (arg0, arg1) -> super.onBackPressed());
        alertDialogBuilder.setNegativeButton(getString(R.string.quit_option_no), (dialogInterface, i) -> {
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    private void init() {
        this.checkLocation = 2;
        ((FloatingActionButton) findViewById(R.id.focus_btn)).setImageResource(R.drawable.ic_my_location_black_24dp);
        mapType = ((SmartTracker) getApplication()).getMapType();
        onMapCallBackListener = new OnMapCallBackListener(this);
        mMap = onMapCallBackListener.getmMap();
        setrfaButton();

        CookieHandler.setDefault(((SmartTracker) getApplication()).getCookieManager());

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Common.checkAndAskLocationPermission(this)) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 500, 2, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 2, locationListener);
        }

        initMap();
    }

    private void initMap() {
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(onMapCallBackListener);
    }

    private void setrfaButton() {

        RapidFloatingActionButton rfaBtn = findViewById(R.id.main_floating_btn);
        RapidFloatingActionLayout rfaLayout = findViewById(R.id.main_floating_layout);
        rfaBtn.setPressedColor(Color.parseColor("#00FFFFFF"));
        rfaLayout.setFrameAlpha(0.2f);
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();

        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.trfa_button_vehicle_list))
                .setResId(R.drawable.ic_format_list_bulleted_black_24dp)
                .setIconNormalColor(Color.parseColor("#1976D2"))
                .setIconPressedColor(Color.parseColor("#1976D2"))
                .setLabelColor(Color.parseColor("#1976D2"))
                .setWrapper(2)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.trfa_button_summary_list))
                .setResId(R.drawable.ic_format_list_bulleted_black_24dp)
                .setIconNormalColor(Color.parseColor("#1976D2"))
                .setIconPressedColor(Color.parseColor("#1976D2"))
                .setLabelColor(Color.parseColor("#1976D2"))
                .setWrapper(2)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.trfa_button_geo_fence))
                .setResId(R.drawable.ic_format_polygon_black_24dp)
                .setIconNormalColor(Color.parseColor("#1976D2"))
                .setIconPressedColor(Color.parseColor("#1976D2"))
                .setLabelColor(Color.parseColor("#1976D2"))
                .setWrapper(3)
        );

        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.trfa_button_about))
                .setResId(R.drawable.ic_information_icon)
                .setIconNormalColor(Color.parseColor("#1976D2"))
                .setIconPressedColor(Color.parseColor("#1976D2"))
                .setLabelColor(Color.parseColor("#1976D2"))
                .setWrapper(3)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.trfa_button_change_map))
                .setResId(R.drawable.map)
                .setIconNormalColor(Color.parseColor("#1976D2"))
                .setIconPressedColor(Color.parseColor("#1976D2"))
                .setLabelColor(Color.parseColor("#1976D2"))
                .setWrapper(3)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.trfa_button_change_language))
                .setResId(R.drawable.ic_iconfinder_ic_language_48px_352479)
                .setIconNormalColor(Color.parseColor("#1976D2"))
                .setIconPressedColor(Color.parseColor("#1976D2"))
                .setLabelColor(Color.parseColor("#1976D2"))
                .setWrapper(3)
        );
        rfaContent
                .setItems(items)
                .setIconShadowColor(Color.parseColor("#00ffffff"));

        rfabHelper = new RapidFloatingActionHelper(
                this,
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();

    }

    public void vehiclesListAction(View view) {
        if (clickableVehicle) {
            startActivity(new Intent(MainActivity.this, VehicleListCompact.class));
            clickableVehicle = false;
        }
    }

    public void logoutButton(View view) {

        if (clickableVehicle) {
            logoutBtnClickAction();
            clickableVehicle = false;
        }
    }

    Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    public int getCheckLocation() {
        return checkLocation;
    }

    public void setCheckLocation(int checkLocation) {
        this.checkLocation = checkLocation;
    }

    public OnMapCallBackListener getOnMapCallBackListener() {
        return onMapCallBackListener;
    }

    public void setOnMapCallBackListener(OnMapCallBackListener onMapCallBackListener) {
        this.onMapCallBackListener = onMapCallBackListener;
    }

    public void clickMapSwitchAction() {
        mMap = onMapCallBackListener.getmMap();
        mapType = (mapType % 2) + 1;
        mMap = StaticFunction.setMapType(mMap, mapType);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void clickLocationAction(View view) {
//        try {
        mMap = onMapCallBackListener.getmMap();
        markersIcon = onMapCallBackListener.getMarkers();
        FloatingActionButton floatingActionButton = findViewById(R.id.focus_btn);


        if (checkLocation == 1) {
            checkLocation = 2;
            if (markersIcon.size() < 2) {
                mMap.animateCamera(zoomingLocation(markersIcon,this));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markersIcon.get(0).getPosition(), 15));
            } else mMap.animateCamera(zoomingLocation(markersIcon,this));

            floatingActionButton.setImageResource(R.drawable.ic_my_location_black_24dp);

        } else {

            final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean checkPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

            checkLocation = 1;

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 500, 2, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 2, locationListener);

            floatingActionButton.setImageResource(R.drawable.ic_two_cars_in_line);
            Common.logd(TAG, latitude + "  " + longitude);

            StaticFunction.getLastKnownLocation(mMap, latitude, longitude);
        }
    }

    public void aboutBtnClickAction() {
        startActivity(new Intent(MainActivity.this, About.class));
    }

    public void logoutBtnClickAction() {
        if (!Common.isInternetConnected(this)) {
            ((SmartTracker) getApplication()).getWebSocket().close(1000, "Logout");
            StaticRequest.destroySession(this);

        } else {
            Common.failPopup(getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), this);
        }
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        rfabHelper.toggleContent();

        if (position == 0) {
            if (clickableVehicle) {
//                startActivity(new Intent(this, VehicleList.class));
                startActivity(new Intent(MainActivity.this, VehicleListCompactNew.class));
                clickableVehicle = false;
            }

        } else if (position == 1) {
            if (clickableVehicle) {
                startActivity(new Intent(this, VehicleListSummary.class));
                clickableVehicle = false;
            }
        } else if (position == 2) {
            mMap = onMapCallBackListener.getmMap();
            mMap.clear();
            markersIcon = StaticFunction.addMarkersIcon(((SmartTracker) getApplication()).getPositions(), ((SmartTracker) getApplication()).getDevices(), mMap, this);
            markersTitle = StaticFunction.addMarkersTitle(((SmartTracker) getApplication()).getPositions(), ((SmartTracker) getApplication()).getDevices(), mMap, this);
            mMap = StaticFunction.addGeoFences(this, mMap, ((SmartTracker) getApplication()).getGeoFences());
        } else if (position == 3) {
            if (clickableVehicle) {
                aboutBtnClickAction();
                clickableVehicle = false;
            }
        } else if (position == 4) {
            clickMapSwitchAction();
        } else if (position == 5) {
            if (SharedPreferenceHelper.getSharedPreferenceBoolean(this, SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.LANG_TYPE, true)) {
//                    Locale locale = new Locale("pk");
//                    Common.logd(TAG,locale.getCountry());
//                    setLanguage("pk");
//                    Common.logd(TAG, getCurrentLocale(this).getDisplayLanguage()+"pk");
                SharedPreferenceHelper.setSharedPreferenceBoolean(this, SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.LANG_TYPE, false);
                Locale myLocale = new Locale("pk");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);

                Log.d(TAG, "onRFACItemLabelClick: NEW"+ getResources().getString(R.string.ignition_on_txt));

//                    setLanguage("pk");
                Intent refresh = new Intent(this, MainActivity.class);
                refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(refresh);
                MainActivity.this.finish();


            } else {
                SharedPreferenceHelper.setSharedPreferenceBoolean(this, SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.LANG_TYPE, true);
                Locale myLocale = new Locale("en");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
//                setLanguage("en");
                Intent refresh = new Intent(this, MainActivity.class);
                refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(refresh);
                MainActivity.this.finish();
            }
        }
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {

        rfabHelper.toggleContent();
        if (position == 0) {
            if (clickableVehicle) {
                startActivity(new Intent(MainActivity.this, VehicleListCompactNew.class));
                clickableVehicle = false;
            }

        } else if (position == 1) {
            if (clickableVehicle) {
                startActivity(new Intent(this, VehicleListSummary.class));
                clickableVehicle = false;
            }
        } else if (position == 2) {
            mMap = onMapCallBackListener.getmMap();
            mMap.clear();
            markersIcon = StaticFunction.addMarkersIcon(((SmartTracker) getApplication()).getPositions(), ((SmartTracker) getApplication()).getDevices(), mMap, this);
            markersTitle = StaticFunction.addMarkersTitle(((SmartTracker) getApplication()).getPositions(), ((SmartTracker) getApplication()).getDevices(), mMap, this);
            mMap = StaticFunction.addGeoFences(this, mMap, ((SmartTracker) getApplication()).getGeoFences());

        } else if (position == 3) {
            if (clickableVehicle) {
                startActivity(new Intent(this, About.class));
                clickableVehicle = false;
            }
        } else if (position == 4) {
            clickMapSwitchAction();
        } else if (position == 5) {
            if (SharedPreferenceHelper.getSharedPreferenceBoolean(this, SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.LANG_TYPE, true)) {

                SharedPreferenceHelper.setSharedPreferenceBoolean(this, SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.LANG_TYPE, false);
                Locale myLocale = new Locale("pk");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
                Intent refresh = new Intent(this, MainActivity.class);
                refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(refresh);
                MainActivity.this.finish();

            } else {
                SharedPreferenceHelper.setSharedPreferenceBoolean(this, SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.LANG_TYPE, true);
                Locale myLocale = new Locale("en");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
                Intent refresh = new Intent(this, MainActivity.class);
                refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(refresh);
                MainActivity.this.finish();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}
