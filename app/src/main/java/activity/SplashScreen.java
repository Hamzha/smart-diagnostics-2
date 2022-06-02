package activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.smart.agriculture.solutions.vechicle.vehicletracker.BuildConfig;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import listener.SplashScreenListener;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import util.BaseClass;
import util.Common;

public class SplashScreen extends BaseClass implements EasyPermissions.PermissionCallbacks {

    ImageView logo;
    ImageView logoText;
    Animation fromBottom;
    Animation fromTop;
    ProgressBar progressBar;
    Activity context;
    String TAG = ">>>" + SplashScreen.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        permission();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void init() {
        logo = findViewById(R.id.logo);
        logoText = findViewById(R.id.logoText);
        context = this;
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom);
        fromTop = AnimationUtils.loadAnimation(this, R.anim.from_top);
        fromBottom.setDuration(1000);
        fromTop.setDuration(1000);
        logo.setAnimation(fromTop);
        logoText.setAnimation(fromBottom);

        fromBottom.setAnimationListener(new SplashScreenListener(this, progressBar));

        /********************Clears****************************/
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();

    }
//    public void init() {
//        context = this;
//        VersionChecker versionChecker = new VersionChecker();
//        try {
//            String appVersionName = BuildConfig.VERSION_NAME;
//            String mLatestVersionName = versionChecker.execute().get();
//            Toast.makeText(context, "Match : " + appVersionName + " : " + mLatestVersionName, Toast.LENGTH_LONG).show();
//
//            if (appVersionName.equals(mLatestVersionName)) {
//                Toast.makeText(context, "Match : " + appVersionName + " : " + mLatestVersionName, Toast.LENGTH_LONG).show();
////                    showAppUpdateDialog();
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//    }

    @AfterPermissionGranted(123)
    private void permission() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            init();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_msg),
                    123, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        init();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            init();
        }
    }



}
