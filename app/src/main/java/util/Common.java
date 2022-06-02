package util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import callbackinterface.DialogBoxInterface;
import cn.pedant.SweetAlert.SweetAlertDialog;
import model.Attribute;
import model.Position;

public class Common {
    static String TAG = ">>>" + Common.class.getSimpleName();

    public static boolean isInternetConnected(Context context) {    //return true if not connected
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork == null ||
                !activeNetwork.isConnectedOrConnecting();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkAndAskLocationPermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
            int[] graanted = {0};
            onRequestPermissionsResult(permission, graanted, context);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkAndAskStoragePermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
//            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, CommonConst.REQUEST_CODE_ASK_PERMISSIONS);
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            int[] graanted = {0};
            onRequestPermissionsResult(permission, graanted, context);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void onRequestPermissionsResult(String[] permissions, int[] grantResults, Activity activity) {
        if (CommonConst.REQUEST_CODE_ASK_PERMISSIONS == 123) {
            // for each permission check if the user granted/denied them
            // you may want to group the rationale in a single dialog,
            // this is just an example
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = activity.shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        activity.startActivityForResult(intent, 123);
                        // user also CHECKED "never ask again"
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                    } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, CommonConst.REQUEST_CODE_ASK_PERMISSIONS);

                        // user did NOT check "never ask again"
                        // this is a good place to explain the user
                        // why you need the permission and ask if he wants
                        // to accept it (the rationale)
                    }
                }
            }
        }
    }

    public static boolean checkInternetConnect(Activity context) {
        if (Common.isInternetConnected(context)) {
            Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
            return false;
        }
        return true;
    }

    public static void failPopup(String msg, String responseCode, Context context) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(context.getString(R.string.error));
        pDialog.setContentText(msg);
        pDialog.showCancelButton(true);
        pDialog.setCancelable(true);
        pDialog.setOnShowListener(dialogInterface -> {
            SweetAlertDialog sweetAlertDialog = (SweetAlertDialog) dialogInterface;
            TextView text = (TextView) sweetAlertDialog.findViewById(R.id.content_text);
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            text.setSingleLine(false);
            text.setMaxLines(10);
            text.setLines(6);
        });
        if (!((Activity) context).isFinishing()) {
            pDialog.show();
        }
    }

    public static void successPopup(String msg, Activity context) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText(context.getString(R.string.OK));
        pDialog.setContentText(msg);
        pDialog.showCancelButton(true);
        pDialog.setCancelable(true);
        pDialog.setOnShowListener(dialogInterface -> {
            SweetAlertDialog sweetAlertDialog = (SweetAlertDialog) dialogInterface;
            TextView text = (TextView) sweetAlertDialog.findViewById(R.id.content_text);
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            text.setSingleLine(false);
            text.setMaxLines(10);
            text.setLines(6);
        });
        pDialog.show();
    }

    public static void JSONReplace(JSONObject obj, String keyMain, String valueMain, String
            newValue) throws Exception {
        // We need to know keys of Jsonobject
        JSONObject json = new JSONObject();
        Iterator iterator = obj.keys();
        String key;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            // if object is just string we change value in key
            if ((obj.optJSONArray(key) == null) && (obj.optJSONObject(key) == null)) {
                if ((key.equals(keyMain)) && (obj.get(key).toString().equals(valueMain))) {
                    // put new value
                    obj.put(key, newValue);
                    return;
                }
            }

            // if it's jsonobject
            if (obj.optJSONObject(key) != null) {
                JSONReplace(obj.getJSONObject(key), keyMain, valueMain, newValue);
            }

            // if it's jsonarray
            if (obj.optJSONArray(key) != null) {
                JSONArray jArray = obj.getJSONArray(key);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONReplace(jArray.getJSONObject(i), keyMain, valueMain, newValue);
                }
            }
        }
    }

    public static void showToast(Activity context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void logd(String TAG, String msg) {
        Log.d(TAG, msg);
    }

    public static void retryDialog(Context context, int statusCode, DialogBoxInterface interfece) {
        String msg = null;
        if (statusCode == CommonConst.NO_INTERNET_CODE) {
            msg = context.getString(R.string.no_internet_response);
        } else if (statusCode == CommonConst.REQUEST_ERROR_CODE) {
            msg = context.getString(R.string.late_response);
        } else if (statusCode == CommonConst.LATE_RESPONSE_CODE) {
            msg = context.getString(R.string.late_response);
        }
        msg = msg + context.getString(R.string.want_to_retry);

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    interfece.callBack(true);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    interfece.callBack(false);
                    break;
            }
        };

        if (!((Activity) context).isFinishing()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage(msg).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }

    public static boolean responseCheck(int statusCode, Context context) {
        if (statusCode == CommonConst.LATE_RESPONSE_CODE) {
            return true;
        } else if (statusCode == CommonConst.REQUEST_ERROR_CODE) {
            return true;
        } else return statusCode == CommonConst.NO_INTERNET_CODE;
    }

    public static Position[] getPosition(String response) {
        StringBuilder json = new StringBuilder();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Position[] positions;
        positions = gson.fromJson(response, Position[].class);
        return positions;
    }

    public static Attribute[] getAttributes(String response) {
        StringBuilder json = new StringBuilder();

        json.append("[");
        Attribute[] attributes;

        try {
            JSONArray jArr = new JSONArray(response);
            for (int count = 0; count < jArr.length(); count++) {
                JSONObject obj = jArr.getJSONObject(count);
                String attributesTmp = obj.getString("attributes");
                json.append(attributesTmp).append(",");
            }
            json.append("]");
            attributes = new Gson().fromJson(json.toString(), Attribute[].class);
            return attributes;
        } catch (Exception ignored) {
        }
        return null;
    }

    public static boolean checkInternetAvailability(Activity context) {
        final boolean[] chk = new boolean[1];
        if (Common.isInternetConnected(context)) {
            Common.retryDialog(context, CommonConst.NO_INTERNET_CODE, bool -> chk[0] = bool);
        } else {
            chk[0] = true;
        }
        return chk[0];
    }

    public static String[] dateConversion(Date startDate, Date endDate) {

        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String fromDate = dateFormat.format(startDate);
        String toDate = dateFormat.format(endDate);

        String[] date = new String[2];

        try {
            date[0] = URLEncoder.encode(fromDate, "UTF-8");
            date[1] = URLEncoder.encode(toDate, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static boolean checkPermission(Activity splashScreen) {
        return ContextCompat.checkSelfPermission(splashScreen, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(splashScreen, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    public static Position setAddress(Position position, Context context) {
        boolean langFlag = SharedPreferenceHelper.getSharedPreferenceBoolean(context, SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.LANG_TYPE, false);
        Geocoder geocoder;
        if (langFlag) {
            geocoder = new Geocoder(context, new Locale("en"));
        } else {
            geocoder = new Geocoder(context, new Locale("ur"));
        }
        if (position.getLatitude() < 90 && position.getLatitude() > -90 && position.getLongitude() < 180 && position.getLongitude() > -180) {
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(position.getLatitude(), position.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if ((addresses != null ? addresses.size() : 0) > 0) {
                Common.logd(TAG, String.valueOf(addresses.get(0).getAddressLine(0)));
                position.setAddress(addresses.get(0).getAddressLine(0));
            }
        }
        return position;
    }

    public static Timestamp getTimestamp(String timestampInString) {
            Date date = new Date(Long.parseLong(timestampInString));
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            String formatted = format.format(date);
            Timestamp timeStamp = Timestamp.valueOf(formatted);
            return timeStamp;

    }
}
