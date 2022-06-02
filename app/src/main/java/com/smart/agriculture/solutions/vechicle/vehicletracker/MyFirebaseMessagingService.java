package com.smart.agriculture.solutions.vechicle.vehicletracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import activity.Login;
import util.Common;
import util.SharedPrefConst;
import util.SharedPreferenceHelper;
import util.StaticRequest;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = ">>>" + MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        String channelId = "Default";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.launching_icon_logo)
                .setContentIntent(pendingIntent)
                .setContentTitle(Objects.requireNonNull(remoteMessage.getNotification()).getTitle())
                .setAutoCancel(true)
                .setContentText(remoteMessage.getNotification().getBody());
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();

        manager.notify(0, builder.build());

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String response = SharedPreferenceHelper.getSharedPreferenceString(this, SharedPrefConst.RESPONSE_PREF_FILE, SharedPrefConst.RESPONSE, null);
        if (response != null)
            StaticRequest.sendRegistrationTokenToServer(this, s);

    }
    //    @Override
//    public void onNewToken(String s) {
//        super.onNewToken(s);
//        SharedPreferenceHelper.setSharedPreferenceString(this, SharedPrefConst.TOKEN_PRED_FILE, SharedPrefConst.TOKEN_NOTIICATION, s);
//
//        StaticRequest.sendRegistrationTokenToServer(this, s);
//    }
}
