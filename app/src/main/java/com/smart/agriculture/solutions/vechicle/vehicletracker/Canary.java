//package com.smart.agriculture.solutions.vechicle.vehicletracker;
//
//import android.app.Application;
//
//public class Canary extends Application {
//
//    public static Canary instance;
//    private RefWatcher refWatcher;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        instance = this;
//        refWatcher = LeakCanary.install(this);
//    }
//
//    public void mustDie(Object object) {
//        if (refWatcher != null) {
//            refWatcher.watch(object);
//        }
//    }
//}
