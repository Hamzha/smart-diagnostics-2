package com.smart.agriculture.solutions.vechicle.vehicletracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.multidex.MultiDexApplication;

import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.net.CookieManager;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.fabric.sdk.android.Fabric;
import model.Device;
import model.GeoFence;
import model.Group;
import model.Position;
import okhttp3.WebSocket;
import util.SharedPrefConst;

public class SmartTracker extends MultiDexApplication {

//    private RefWatcher refWatcher;

    int mapType = 2;
    Map<Long, Device> devices = new LinkedHashMap<>();
    Map<Long, Position> positions = new LinkedHashMap<>();
    Map<Long, GeoFence> geoFences = new LinkedHashMap<>();

    Map<Long, Double> totalDistance = new LinkedHashMap<>();
    Map<Long, Double> totalAvgSpeed = new LinkedHashMap<>();
    Map<Long, Double> maxSpeed = new LinkedHashMap<>();
    Map<Long, Double> duration = new LinkedHashMap<>();
    Map<Long, Integer> totalTrips = new LinkedHashMap<>();

    String TAG = ">>>" + SmartTracker.class.getSimpleName();
    CookieManager cookieManager = new CookieManager();
    WebSocket webSocket;
    Map<Long, Group> group = new LinkedHashMap<>();


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            SharedPreferences tokenPreferences;
            tokenPreferences = getSharedPreferences(SharedPrefConst.TOKEN_PRED_FILE, MODE_PRIVATE);

            SharedPreferences.Editor tokenPrefsEditor;
            tokenPrefsEditor = tokenPreferences.edit();
            tokenPrefsEditor.clear();

            tokenPrefsEditor.putString(SharedPrefConst.TOKEN_NOTIICATION, Objects.requireNonNull(task.getResult()).getToken());
            tokenPrefsEditor.apply();
        });


//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        refWatcher = LeakCanary.install(this);

    }


//    public static RefWatcher getRefWatcher(Context context) {
//        SmartTracker application = (SmartTracker) context.getApplicationContext();
//        return application.refWatcher;
//    }


    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public Map<Long, Device> getDevices() {
        return this.devices;
    }

    public void setDevices(Device[] devices) {

        this.devices = new LinkedHashMap<>();
        for (Device device1 : devices) {
            this.devices.put(device1.getId(), device1);
        }
        for (Map.Entry<Long, Device> device : this.devices.entrySet()) {
            String stringBuilder = "marker_" +
                    device.getValue().getCategory() +
                    "_" +
                    device.getValue().getStatus();

            device.getValue().setImageId(getResources().getIdentifier(stringBuilder, "drawable", getPackageName()));
        }
    }

    public void setDevices(Map<Long, Device> mainDevice) {
        devices = mainDevice;
    }

    public Map<Long, Position> getPositions() {
        return this.positions;
    }

    public void setPositions(Map<Long, Position> newPosition) {
        this.positions = newPosition;
    }

    public void setPositions(Position[] position) {
        this.positions = new LinkedHashMap<>();
        for (Position position1 : position) {
//            position1 = Common.setAddress(position1,context);
            this.positions.put(position1.getDeviceId(), position1);
        }
    }

    public Map<Long, GeoFence> getGeoFences() {
        return geoFences;
    }

    public void setGeoFences(GeoFence[] geofences) {
        this.geoFences = new LinkedHashMap<>();
        for (GeoFence geofence : geofences) {
            this.geoFences.put((long) geofence.getId(), geofence);
        }
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }

    public Map<Long, Group> getGroup() {
        return group;
    }

    public void setGroup(Group[] group) {
        this.group = new LinkedHashMap<>();
        for (Group group1 : group) {
            this.group.put((long) group1.getId(), group1);
        }
    }

    public Map<Long, Double> getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Map<Long, Double> totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Map<Long, Double> getTotalAvgSpeed() {
        return totalAvgSpeed;
    }

    public void setTotalAvgSpeed(Map<Long, Double> totalAvgSpeed) {
        this.totalAvgSpeed = totalAvgSpeed;
    }

    public Map<Long, Double> getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Map<Long, Double> maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Map<Long, Double> getDuration() {
        return duration;
    }

    public void setDuration(Map<Long, Double> duration) {
        this.duration = duration;
    }

    public Map<Long, Integer> getTotalTrips() {
        return totalTrips;
    }

    public void setTotalTrips(Map<Long, Integer> totalTrips) {
        this.totalTrips = totalTrips;
    }


    LocalizationApplicationDelegate localizationDelegate = new LocalizationApplicationDelegate(this);

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(localizationDelegate.attachBaseContext(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localizationDelegate.onConfigurationChanged(this);
    }

    @Override
    public Context getApplicationContext() {
        return localizationDelegate.getApplicationContext(super.getApplicationContext());
    }


}