package com.smart.agriculture.solutions.vechicle.vehicletracker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import util.Common;

@Deprecated
public class BroadCastClass extends BroadcastReceiver {
//    String TAG = ">>>" + BroadCastClass.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent arg1) {
        boolean isConnected = Common.isInternetConnected((Activity) context);   //return true if not connected
        if (isConnected) {
//            Common.logd(TAG, "Internet Connection");
        } else {
            Toast.makeText(context, "Internet Connected Lost", Toast.LENGTH_LONG).show();
        }
    }
}