package activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.smart.agriculture.solutions.vechicle.vehicletracker.BuildConfig;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieHandler;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import listener.LoginButtonListener;
import util.BaseClass;
import util.Common;
import util.CommonConst;
import util.SharedPrefConst;
import util.SharedPreferenceHelper;


public class Login extends BaseClass {

    Context context;
    String TAG = ">>>" + Login.class
            .getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        langSelector(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
//        versionCheck();
        CookieHandler.setDefault(((SmartTracker) getApplication()).getCookieManager());
        if (SharedPreferenceHelper.getSharedPreferenceString(getApplication(), SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.FIRST_TIME, "first").equals("first")) {
            String[] colors = {"English", "اُردُو"};

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("language/زبان");

            builder.setSingleChoiceItems(colors, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:
                            SharedPreferenceHelper.setSharedPreferenceBoolean(getApplication(), SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.LANG_TYPE, true);
                            break;
                        case 1:
                            SharedPreferenceHelper.setSharedPreferenceBoolean(getApplication(), SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.LANG_TYPE, false);
                            break;
                    }
                }
            });
            builder.setPositiveButton("OK", (dialog, which) -> {
                SharedPreferenceHelper.setSharedPreferenceString(getApplication(), SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.FIRST_TIME, "second");
                if (!SharedPreferenceHelper.getSharedPreferenceBoolean(getApplication(), SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.LANG_TYPE, true)) {

                    SharedPreferenceHelper.setSharedPreferenceBoolean(this, SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.LANG_TYPE, false);
                    Locale myLocale = new Locale("pk");
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.locale = myLocale;
                    res.updateConfiguration(conf, dm);
//                    setLanguage("pk");
                    Intent refresh = new Intent(context, Login.class);
                    startActivity(refresh);
                }

                init();
            });
            builder.show();

        } else {
            init();

        }
    }

    private void init() {
        Button loginBtn = findViewById(R.id.login_btn);

        boolean saveLogin = SharedPreferenceHelper.getSharedPreferenceBoolean(this, SharedPrefConst.OTHER_PREF_FILE, SharedPrefConst.LOGIN, false);
        loginBtn.setOnClickListener(new LoginButtonListener(this));
        if (saveLogin) {
            String emailStr = SharedPreferenceHelper.getSharedPreferenceString(context, SharedPrefConst.OTHER_PREF_FILE, SharedPrefConst.EMAIL, null);
            String passwordStr = SharedPreferenceHelper.getSharedPreferenceString(context, SharedPrefConst.OTHER_PREF_FILE, SharedPrefConst.PASSWORD, null);

            EditText email, password;
            email = findViewById(R.id.email);
            email.setText(emailStr);
            password = findViewById(R.id.password);
            password.setText(passwordStr);
            CheckBox loginSaveCheckBox = findViewById(R.id.remember_checkBox);
            loginSaveCheckBox.setChecked(true);
            loginBtn.performClick();
        }
    }


}

