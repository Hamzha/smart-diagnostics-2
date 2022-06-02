package listener;


import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import org.json.JSONObject;

import activity.Login;
import activity.MainActivity;
import okhttp3.WebSocket;
import util.Common;
import util.CommonConst;
import util.NetworkUtils;
import util.SharedPrefConst;
import util.SharedPreferenceHelper;
import util.StaticRequest;
import util.URLS;

public class LoginButtonListener implements View.OnClickListener {
    private final Login login;
    private String TAG = ">>>" + LocationActionListener.class.getSimpleName();
    private WebSocket webSocket;

    public LoginButtonListener(Login login) {
        this.login = login;
    }

    @Override
    public void onClick(View view) {
        login.findViewById(R.id.login_btn).setVisibility(View.INVISIBLE);
        login.findViewById(R.id.progress_login).setVisibility(View.VISIBLE);

        if (Common.isInternetConnected(login)) {
            progressBarr();
            Common.failPopup(login.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), login);

            return;
        }

        final EditText email, password;
        email = login.findViewById(R.id.email);
        password = login.findViewById(R.id.password);

        if (TextUtils.isEmpty(email.getText().toString())) {
            email.requestFocus();
            email.setError(login.getString(R.string.invalid_input_response));
            progressBarr();
            return;
        } else if (TextUtils.isEmpty(password.getText().toString().trim())) {
            password.setError(login.getString(R.string.invalid_input_response));
            progressBarr();
            password.requestFocus();
            return;
        }
        NetworkUtils networkUtils = new NetworkUtils(this.login);
        Common.logd(TAG, URLS.Login(email.getText().toString().trim(), password.getText().toString().trim()));
        networkUtils.post(URLS.Login(email.getText().toString().trim(), password.getText().toString().trim()), 15000, (response, statusCode) -> {
            if (statusCode == CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE) {
                Common.failPopup(login.getString(R.string.invalid_credentials_response), String.valueOf(CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE), login);
                progressBarr();
            } else if (statusCode == CommonConst.LATE_RESPONSE_CODE) {
                Common.failPopup(login.getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), login);
                progressBarr();
            } else {
                JSONObject jObject;
                try {
                    jObject = new JSONObject(response);
                    Common.logd(TAG,jObject.getString("token"));
                    SharedPreferenceHelper.setSharedPreferenceString(login,SharedPrefConst.OTHER_PREF_FILE,SharedPrefConst.TOKEN_USER,jObject.getString("token"));
                } catch (Exception ex) {
                    Common.logd(TAG, "ERROR " + ex.getMessage());
                }
                SharedPreferenceHelper.setSharedPreferenceString(login, SharedPrefConst.RESPONSE_PREF_FILE, SharedPrefConst.RESPONSE, response);
                String token = SharedPreferenceHelper.getSharedPreferenceString(this.login, SharedPrefConst.TOKEN_PRED_FILE, SharedPrefConst.TOKEN_NOTIICATION, null);
                StaticRequest.sendRegistrationTokenToServer(this.login, token);
                loginSuccess();
            }
        });
    }

    private void loginSuccess() {
        CheckBox saveLoginCheckBox = login.findViewById(R.id.remember_checkBox);
        EditText email = login.findViewById(R.id.email);
        EditText password = login.findViewById(R.id.password);
        SharedPreferenceHelper.setSharedPreferenceBoolean(login, SharedPrefConst.POLYGON_PREF_FILE, SharedPrefConst.POLYGON_VISIBILITY, false);
        if (saveLoginCheckBox.isChecked()) {
            SharedPreferenceHelper.setSharedPreferenceBoolean(login, SharedPrefConst.OTHER_PREF_FILE, SharedPrefConst.LOGIN, true);
            SharedPreferenceHelper.setSharedPreferenceString(login, SharedPrefConst.OTHER_PREF_FILE, SharedPrefConst.EMAIL, email.getText().toString());
            SharedPreferenceHelper.setSharedPreferenceString(login, SharedPrefConst.OTHER_PREF_FILE, SharedPrefConst.PASSWORD, password.getText().toString());
        } else {
            SharedPreferenceHelper.clearSharedPreference(login, SharedPrefConst.OTHER_PREF_FILE);
        }

        StaticRequest.getSetDevices(login.getApplication(), 15000, (response1, statusCode) -> {
            if (statusCode == CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE) {
                Common.failPopup(login.getString(R.string.invalid_credentials_response), String.valueOf(CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE), login);
                progressBarr();
            } else if (statusCode == CommonConst.LATE_RESPONSE_CODE) {
                Common.failPopup(login.getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), login);
                progressBarr();
            } else {
                webSocket = StaticRequest.webSocket(login);
                ((SmartTracker) login.getApplicationContext()).setWebSocket(webSocket);
                login.startActivity(new Intent(login, MainActivity.class));
                login.finish();
            }
        });
    }

    private void progressBarr() {
        login.findViewById(R.id.login_btn).setVisibility(View.VISIBLE);
        login.findViewById(R.id.progress_login).setVisibility(View.INVISIBLE);
    }

}
