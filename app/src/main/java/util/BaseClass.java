package util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate;
import com.akexorcist.localizationactivity.core.OnLocaleChangedListener;
import com.smart.agriculture.solutions.vechicle.vehicletracker.NetworkStateReceiver;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import java.util.Locale;
import java.util.Objects;

import callbackinterface.ObservableInterface;

@SuppressLint("Registered")
public class BaseClass extends AppCompatActivity implements OnLocaleChangedListener, NetworkStateReceiver.NetworkStateReceiverListener {

    private LocalizationActivityDelegate localizationDelegate = new LocalizationActivityDelegate(this);
    private String TAG = ">>>" + BaseClass.class.getSimpleName();


    NetworkStateReceiver networkStateReceiver = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        localizationDelegate.addOnLocaleChangedListener(this);
        localizationDelegate.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
    }

    public void setNetworkStateReceiver(Context context) {
        networkStateReceiver = new NetworkStateReceiver(context);
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    public NetworkStateReceiver getNetworkStateReceiver() {
        return networkStateReceiver;
    }


    @Override
    public void onResume() {
        super.onResume();
        localizationDelegate.onResume(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(localizationDelegate.attachBaseContext(newBase));
    }

    @Override
    public Context getApplicationContext() {
        return localizationDelegate.getApplicationContext(super.getApplicationContext());
    }

    @Override
    public Resources getResources() {
        return localizationDelegate.getResources(super.getResources());
    }

    public final void setLanguage(String language) {
        Common.logd(TAG, "inner");
        localizationDelegate.setLanguage(this, language);
    }

    public final void setLanguage(Locale locale) {
        localizationDelegate.setLanguage(this, locale);
    }

    public final void setDefaultLanguage(String language) {
        localizationDelegate.setDefaultLanguage(language);
    }

    public final void setDefaultLanguage(Locale locale) {
        localizationDelegate.setDefaultLanguage(locale);
    }

    public final Locale getCurrentLanguage() {
        return localizationDelegate.getLanguage(this);
    }

    // Just override method locale change event
    @Override
    public void onBeforeLocaleChanged() {
    }

    @Override
    public void onAfterLocaleChanged() {
    }

    public void     Toolbar(Context context, String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    public void ToolbarnoNavigation(Context activity, String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_white_black_24dp);
        Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

    }


    public void setDataUpdate(int timer, ObservableInterface observableInterface) {
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                //call function
                observableInterface.callBackResponseDevice(true);
                ha.postDelayed(this, timer);
            }
        }, timer);
    }

    @Override
    public void onNetworkAvailable() {
        if (!StaticRequest.internetConnectivity) {
            ((SmartTracker) getApplicationContext()).setWebSocket(StaticRequest.webSocket(this));
            StaticRequest.internetConnectivity = true;
        }
    }

    @Override
    public void onNetworkUnavailable() {
        Common.logd(TAG, "No Internet");
    }

    public void langSelector(Context context) {
        if (SharedPreferenceHelper.getSharedPreferenceBoolean(context, SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.LANG_TYPE, true) == false) {
            Common.logd(">>>", "urdu " + getCurrentLanguage());
//                    Locale locale = new Locale("pk");
//                    Common.logd(TAG,locale.getCountry());
//                    setLanguage("pk");
//                    Common.logd(TAG, getCurrentLocale(this).getDisplayLanguage()+"pk");
            Locale myLocale = new Locale("pk");
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
//
//                    setLanguage("pk");
        }
    }
}
