package util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import activity.Immobilizer;
import activity.Login;
import activity.MainActivity;
import callbackinterface.CommonInterface;
import cn.pedant.SweetAlert.SweetAlertDialog;
import model.Device;
import model.GeoFence;
import model.Group;
import model.Position;
import model.Trip;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class StaticRequest {

    private static String TAG = ">>>" + StaticRequest.class.getSimpleName();
    public static boolean internetConnectivity = true;

    public static void getSetDevices(final Context context, final int timer, final CommonInterface commonInterface) {
        final String URL = URLS.Devices();

        NetworkUtils networkUtils = new NetworkUtils(context);
        networkUtils.get(URL, timer, (response, statusCode) -> {
            if (statusCode == CommonConst.LATE_RESPONSE_CODE) {
                commonInterface.commonCallBack(response, CommonConst.LATE_RESPONSE_CODE);
            } else if (statusCode == CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE) {
                commonInterface.commonCallBack(response, CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE);
            } else {
                try {
                    Common.logd(TAG, response);
                    Device[] devices = new Gson().fromJson(response, Device[].class);



                    ((SmartTracker) context.getApplicationContext()).setDevices(devices);
                    getSetPosition(context, timer, commonInterface);
                } catch (Exception ex) {
                    ex.getStackTrace();
                    commonInterface.commonCallBack(context.getString(R.string.late_response), CommonConst.LATE_RESPONSE_CODE);
                }
            }
        });
    }

    private static void getSetPosition(final Context context, final int timer, final CommonInterface commonInterface) {
        final String URL = URLS.Position();
        NetworkUtils networkUtils = new NetworkUtils(context);
        networkUtils.get(URL, timer, (response, statusCode) -> {
            if (statusCode == CommonConst.LATE_RESPONSE_CODE) {
                commonInterface.commonCallBack(response, CommonConst.LATE_RESPONSE_CODE);
            } else if (statusCode == CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE) {
                commonInterface.commonCallBack(response, CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE);
            } else {
                try {

                    Common.logd(TAG,"position  : "+ response);

                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
                    Position[] positions = gson.fromJson(response, Position[].class);
                    ((SmartTracker) context.getApplicationContext()).setPositions(positions);
                    getSetGeoFences(context, timer, commonInterface);
                } catch (Exception ex) {
                    commonInterface.commonCallBack(context.getString(R.string.late_response), CommonConst.LATE_RESPONSE_CODE);

                }
            }
        });
    }

    private static void getSetGeoFences(final Context context, int timer, final CommonInterface commonInterface) {
        NetworkUtils networkUtils = new NetworkUtils(context);
        networkUtils.get(URLS.GeoFence(), timer, (response, statusCode) -> {
            if (statusCode == CommonConst.LATE_RESPONSE_CODE) {
                commonInterface.commonCallBack(response, CommonConst.LATE_RESPONSE_CODE);
            } else if (statusCode == CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE) {
                commonInterface.commonCallBack(response, CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE);
            } else {
                try {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                    GeoFence[] geoFences = gson.fromJson(response, GeoFence[].class);
                    ((SmartTracker) context.getApplicationContext()).setGeoFences(geoFences);
                    getSetGroup(context, timer, commonInterface);
                } catch (Exception ex) {
                    commonInterface.commonCallBack(context.getString(R.string.late_response), CommonConst.LATE_RESPONSE_CODE);
                }
            }
        });
    }

    private static void getSetGroup(final Context context, int timer, final CommonInterface commonInterface) {
        NetworkUtils networkUtils = new NetworkUtils(context);
        networkUtils.get(URLS.Group(), timer, (response, statusCode) -> {
            if (statusCode == CommonConst.LATE_RESPONSE_CODE) {
                commonInterface.commonCallBack(response, CommonConst.LATE_RESPONSE_CODE);
            } else if (statusCode == CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE) {
                commonInterface.commonCallBack(response, CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE);
            } else {
                try {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                    Group[] group = gson.fromJson(response, Group[].class);
                    ((SmartTracker) context.getApplicationContext()).setGroup(group);
                    commonInterface.commonCallBack(response, statusCode);
                } catch (Exception ex) {
                    commonInterface.commonCallBack(context.getString(R.string.late_response), CommonConst.LATE_RESPONSE_CODE);
                }
            }
        });
    }

    public static void getDailySummary(Activity context, int timer, CommonInterface commonInterface) {
        Map<Long, Device> devices = ((SmartTracker) context.getApplication()).getDevices();


        String[] dates = Common.dateConversion(StaticFunction.calculateDateFromDays(1), StaticFunction.calculateDateFromDays(0));

        for (Map.Entry<Long, Device> device : devices.entrySet()) {
            NetworkUtils networkUtils = new NetworkUtils(context);
            networkUtils.getJSON(URLS.Trips(dates[0], dates[1], device.getValue().getId()), timer, (response, statusCode) -> {
                if (statusCode == CommonConst.LATE_RESPONSE_CODE) {
                    commonInterface.commonCallBack(response, CommonConst.LATE_RESPONSE_CODE);
                } else if (statusCode == CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE) {
                    commonInterface.commonCallBack(response, CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE);
                } else {
                    try {
                        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                        Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();
                        model.Trip[] trips = gson.fromJson(response, model.Trip[].class);
                        makeSummaryReports(device.getValue().getId(), StaticFunction.createTripList(trips), context);
                        commonInterface.commonCallBack("Successfull", 200);
                    } catch (Exception ex) {
                        commonInterface.commonCallBack(context.getString(R.string.late_response), CommonConst.LATE_RESPONSE_CODE);
                    }
                }
            });
        }
    }

    private static void makeSummaryReports(long id, ArrayList<Trip> tripList, Context contaxt) {
        double totalDistance = 0;
        double totalAvgSpeed = 0;
        double maxSpeed = 0;
        int duration = 0;
        int totalTrips;
        for (int i = 0; i < tripList.size(); i++) {
            totalDistance = tripList.get(i).getDistance() + totalDistance;
            totalAvgSpeed = totalAvgSpeed + tripList.get(i).getAverageSpeed();
            duration = duration + tripList.get(i).getDuration();
            if (maxSpeed < tripList.get(i).getMaxSpeed()) {
                maxSpeed = tripList.get(i).getMaxSpeed();
            }
        }

        totalDistance = totalDistance / 1000;
        totalAvgSpeed = totalAvgSpeed * 1.852;
        maxSpeed = maxSpeed * 1.852;

        totalTrips = tripList.size();
        totalAvgSpeed = Math.round(totalAvgSpeed / tripList.size() * 100.0) / 100.0;
        maxSpeed = Math.round(maxSpeed * 100.0) / 100.0;

        totalDistance = Math.round(totalDistance * 100.0) / 100.0;

        double minutes = TimeUnit.MILLISECONDS.toMinutes(duration);

        Map<Long, Double> getTotalAvgSpeed = ((SmartTracker) contaxt.getApplicationContext()).getTotalAvgSpeed();
        Map<Long, Integer> getTotalTrips = ((SmartTracker) contaxt.getApplicationContext()).getTotalTrips();
        Map<Long, Double> getTotalDistance = ((SmartTracker) contaxt.getApplicationContext()).getTotalDistance();
        Map<Long, Double> getMaxSpeed = ((SmartTracker) contaxt.getApplicationContext()).getMaxSpeed();
        Map<Long, Double> getDuration = ((SmartTracker) contaxt.getApplicationContext()).getDuration();

        getTotalAvgSpeed.put(id, totalAvgSpeed);
        getTotalTrips.put(id, totalTrips);
        getTotalDistance.put(id, totalDistance);
        getMaxSpeed.put(id, maxSpeed);
        getDuration.put(id, minutes);

        ((SmartTracker) contaxt.getApplicationContext()).setDuration(getDuration);
        ((SmartTracker) contaxt.getApplicationContext()).setTotalAvgSpeed(getTotalAvgSpeed);
        ((SmartTracker) contaxt.getApplicationContext()).setTotalDistance(getTotalDistance);
        ((SmartTracker) contaxt.getApplicationContext()).setTotalTrips(getTotalTrips);
        ((SmartTracker) contaxt.getApplicationContext()).setMaxSpeed(getMaxSpeed);
    }

    public static WebSocket webSocket(Activity activity) {
        CookieManager cookieManager = ((SmartTracker) activity.getApplication()).getCookieManager();
        Common.logd(TAG, ((SmartTracker) activity.getApplicationContext()).getCookieManager().getCookieStore().toString());
        CookieStore cookieJar = ((SmartTracker) activity.getApplicationContext()).getCookieManager().getCookieStore();

        OkHttpClient client = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).cookieJar(new JavaNetCookieJar(cookieManager)).build();

        Request request = new Request.Builder()
                .url(URLConst.WEB_SOCKET)
                .build();

        EchoWebSocketListener listener = new EchoWebSocketListener(activity);
        WebSocket text = client.newWebSocket(request, listener);
        Common.logd(TAG, String.valueOf(text.request().body()));
        return text;
    }

    private static void updatedData(String response, Activity activity) {
        Map<Long, Device> mainDevice = ((SmartTracker) activity.getApplication()).getDevices();
        Map<Long, Position> mainPosition = ((SmartTracker) activity.getApplication()).getPositions();
        try {
            if (response.contains("devices")) {
                JSONObject d = new JSONObject(response);
                Device[] device = new Gson().fromJson(d.get("devices").toString(), Device[].class);
                for (Device device1 : device) {
                    mainDevice.put(device1.getId(), device1);
                }
                ((SmartTracker) activity.getApplication()).setDevices(mainDevice);
            } else if (response.contains("positions")) {
                JSONObject p = new JSONObject(response);
                Position[] positions = new Gson().fromJson(p.get("positions").toString(), Position[].class);

                for (Position position : positions) {
                    mainPosition.put(position.getDeviceId(), position);
                }
                ((SmartTracker) activity.getApplicationContext()).setPositions(mainPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Immobilizer Activity
     */
    @SuppressLint("SetTextI18n")
    public static void immobilizerButtonAction(Immobilizer immobilizer, List<Pair<String, JSONObject>> customCommands, long deviceId, String operationStr, int timer, Activity context, TextView park_lock_status) {
        String msg;
        if (operationStr.equals("on")) {
            msg = immobilizer.getString(R.string.enabled_permission);
            operationStr = "off";
        } else {
            msg = immobilizer.getString(R.string.disabled_permission);
            operationStr = "on";
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(immobilizer);
        alertDialogBuilder.setMessage(msg);
        String finalOperationStr = operationStr;
        alertDialogBuilder.setCancelable(false).setNegativeButton("Go",
                (dialog, id) -> {

                    Map<Long, Position> positionTmp = ((SmartTracker) context.getApplicationContext()).getPositions();

                    if (Objects.requireNonNull(positionTmp.get(deviceId)).getSpeed() * 1.852 > 40 && finalOperationStr.equals("off")) {
                        final SweetAlertDialog eDialog = new SweetAlertDialog(immobilizer, SweetAlertDialog.ERROR_TYPE);
                        eDialog.setContentText(immobilizer.getString(R.string.spedd_is_over_40_kph));
                        eDialog.show();
//                    }if (Objects.requireNonNull(positionTmp.get(deviceId)).getPositionAttributes().getRssi() <= 2 && finalOperationStr.equals("off")) {
//                        final SweetAlertDialog eDialog = new SweetAlertDialog(immobilizer, SweetAlertDialog.ERROR_TYPE);
//                        eDialog.setContentText(immobilizer.getString(R.string.spedd_is_over_40_kph));
//                        eDialog.show();
                    }else {
                        final SweetAlertDialog pDialog = new SweetAlertDialog(immobilizer, SweetAlertDialog.PROGRESS_TYPE);
                        final SweetAlertDialog sDialog = new SweetAlertDialog(immobilizer, SweetAlertDialog.SUCCESS_TYPE);
                        sDialog.setTitle(context.getString(R.string.success));

                        if (Common.isInternetConnected(immobilizer)) {
                            Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), immobilizer);

                        } else {

                            for (final Pair<String, JSONObject> command : customCommands) {
                                if (command.first.equals(finalOperationStr)) {
                                    NetworkUtils networkUtils = new NetworkUtils(immobilizer);
                                    String URL = URLS.Command(deviceId);
                                    pDialog.setTitle(context.getString(R.string.dialog_box_title));
                                    pDialog.setContentText(context.getString(R.string.dialog_box_msg));
                                    pDialog.show();
                                    networkUtils.postWithStringBody(URL, timer, command.second.toString(), (response, statusCode) -> {
                                        if (Common.responseCheck(statusCode, context)) {
                                            StaticFunction.dismissProgressDialog(pDialog);
                                            Common.failPopup(context.getString(R.string.no_immobilizer_response), context.getString(R.string.no_immobilizer_response), context);
                                            return;
                                        } else {
                                            if (pDialog != null && pDialog.isShowing()) {
                                                pDialog.dismissWithAnimation();
                                            }
                                            if (!finalOperationStr.equals("on")) {

                                                SharedPreferenceHelper.setSharedPreferenceString(context, SharedPrefConst.IMMOBILIZER_PREF_FILE, String.valueOf(deviceId), CommonConst.IMMOBILIZER_ENABLED);
                                                park_lock_status.setText(R.string.park_lock_locked);
                                                park_lock_status.setBackground(context.getResources().getDrawable(R.color.red_700));
                                                Map<Long, Position> position = ((SmartTracker) context.getApplicationContext()).getPositions();
                                                Objects.requireNonNull(position.get(deviceId)).getPositionAttributes().setout1(true);
                                                ((SmartTracker) context.getApplicationContext()).setPositions(position);
                                                Toast.makeText(immobilizer, immobilizer.getString(R.string.immobilizer_enabled), Toast.LENGTH_LONG).show();
                                                sDialog.setContentText(immobilizer.getString(R.string.immobilizer_enabled));
                                                sDialog.show();

                                            } else {
                                                SharedPreferenceHelper.setSharedPreferenceString(context, SharedPrefConst.IMMOBILIZER_PREF_FILE, String.valueOf(deviceId), CommonConst.IMMOBILIZER_DISABLED);
                                                park_lock_status.setText(R.string.park_lock_unlock);
                                                park_lock_status.setBackground(context.getResources().getDrawable(R.color.green_700));
                                                Map<Long, Position> position = ((SmartTracker) context.getApplicationContext()).getPositions();
                                                Objects.requireNonNull(position.get(deviceId)).getPositionAttributes().setout1(false);
                                                ((SmartTracker) context.getApplicationContext()).setPositions(position);
                                                sDialog.setContentText(immobilizer.getString(R.string.immobilizer_disabled));
                                                sDialog.show();
                                                Toast.makeText(immobilizer, immobilizer.getString(R.string.immobilizer_disabled), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                })
                .setPositiveButton("Cancel",
                        (dialog, id) -> {
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static void fetchFromServer(String URL, Context context, int timer, CommonInterface commonInterface) {
        NetworkUtils networkUtils = new NetworkUtils(context);
        networkUtils.get(URL, timer, (response, statusCode) -> {
            if (Common.responseCheck(statusCode, context)) {
                Common.retryDialog(context, statusCode, bool -> {
                    if (bool) {
                        commonInterface.commonCallBack(null, CommonConst.ACTIVITY_RETRY_CODE);
                    } else {
                        commonInterface.commonCallBack(null, CommonConst.ACTIVITY_MOVE_BACK);
                    }
                });
            } else {
                commonInterface.commonCallBack(response, CommonConst.SUCCESS_RESPONSE_CODE);
            }
        });
    }

    public static void fetchRouteFromServer(String URL, int timer, Context activity, SweetAlertDialog pDialog, CommonInterface commonInterface) {
        pDialog.show();
        NetworkUtils networkUtils = new NetworkUtils(activity.getApplicationContext());
        networkUtils.get(URL, timer, (response, statusCode) -> {


            pDialog.dismiss();
            if (Common.responseCheck(statusCode, activity)) {
                Common.retryDialog(activity, statusCode, bool -> {
                    if (bool) {
                        commonInterface.commonCallBack(null, CommonConst.ACTIVITY_RETRY_CODE);
                    } else {
                        commonInterface.commonCallBack(null, CommonConst.ACTIVITY_MOVE_BACK);
                    }
                });
            } else {
                commonInterface.commonCallBack(response, CommonConst.ACTIVITY_VOID);
            }
        });
    }

    public static void fetchRouteFromServer(String URL, int timer, Context activity, CommonInterface commonInterface) {
        NetworkUtils networkUtils = new NetworkUtils(activity.getApplicationContext());
        networkUtils.get(URL, timer, (response, statusCode) -> {
            if (Common.responseCheck(statusCode, activity)) {
                Common.retryDialog(activity, statusCode, bool -> {
                    if (bool) {
                        commonInterface.commonCallBack(null, CommonConst.ACTIVITY_RETRY_CODE);
                    } else {
                        commonInterface.commonCallBack(null, CommonConst.ACTIVITY_MOVE_BACK);
                    }
                });
            } else {
                commonInterface.commonCallBack(response, CommonConst.ACTIVITY_VOID);
            }
        });
    }


    public static void sendRegistrationTokenToServer(Context context, String newToken) {
        String response = SharedPreferenceHelper.getSharedPreferenceString(context, SharedPrefConst.RESPONSE_PREF_FILE, SharedPrefConst.RESPONSE, null);
        JSONObject jObject;
        try {
            jObject = new JSONObject(response);
            JSONObject jObject1 = new JSONObject(jObject.get("attributes").toString());
            assert newToken != null;
            response = response.replace(jObject1.get("notificationTokens").toString(), newToken);
            SharedPreferenceHelper.setSharedPreferenceString(context, SharedPrefConst.RESPONSE_PREF_FILE, SharedPrefConst.RESPONSE, response);
            String url = URLConst.BASE_URL + URLConst.USER + jObject.get("id");
            Common.logd(TAG, url);
            NetworkUtils networkUtils = new NetworkUtils(context);
            Common.logd(TAG, response);
            Common.logd(TAG, jObject.toString());
            networkUtils.putWithStringBody(url, 15000, response, (response1, statusCode) -> {
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private static void sendRegistrationTokenToServerDirect(Context context, String s) {
//        String url = URLConst.BASE_URL + URLConst.SESSION + URLConst.TOKEN + "'" + s + "'";
//        NetworkUtils networkUtils = new NetworkUtils(context);
//        networkUtils.putToken(url, 15000, (response, statusCode) -> {
//        });
//    }

    private static final class EchoWebSocketListener extends WebSocketListener {
        Activity activity;

        EchoWebSocketListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            activity.runOnUiThread(() -> updatedData(text, activity));
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            Log.e("onClosing", reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.e("onClosed", reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            if (!Common.isInternetConnected(activity)) {
                ((SmartTracker) activity.getApplicationContext()).setWebSocket(webSocket(activity));
            } else {
                internetConnectivity = false;
                Common.logd(TAG, "Socket Failed to Open");
            }
        }
    }


    public static void destroySession(MainActivity context) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitle(context.getString(R.string.dialog_box_title));
        pDialog.setContentText(context.getString(R.string.dialog_box_msg));
        pDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            StaticRequest.sendRegistrationTokenToServer(context, "__");
            StaticRequest.sendRegistrationTokenToServer(context, "__");
            final NetworkUtils[] networkUtils = {new NetworkUtils(context)};
            final String[] URL = {URLConst.BASE_URL + URLConst.SESSION};
            networkUtils[0].delete(URL[0], 15000, (response, statusCode) -> {
                if (statusCode == CommonConst.LATE_RESPONSE_CODE) {
                    Common.failPopup(context.getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
                } else if (statusCode == CommonConst.REQUEST_ERROR_CODE)
                    Common.failPopup(context.getString(R.string.late_response), String.valueOf(CommonConst.REQUEST_ERROR_CODE), context);
                else {
                    SharedPreferenceHelper.clearSharedPreference(context, SharedPrefConst.OTHER_PREF_FILE);
                    SharedPreferenceHelper.clearSharedPreference(context, SharedPrefConst.RESPONSE_PREF_FILE);
                    clearCookies(context);
                    StaticFunction.dismissProgressDialog(pDialog);
                    context.unregisterReceiver(context.getNetworkStateReceiver());
                    androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage(context.getString(R.string.logout_msg));
                    alertDialogBuilder.setPositiveButton(context.getString(R.string.OK),
                            (arg0, arg1) -> {
                                context.finish();
                                context.startActivity(new Intent(context, Login.class));
                            });

                    androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            });
        }, 2000);
    }

    private static void clearCookies(Context context) {
        ((SmartTracker) context.getApplicationContext()).getCookieManager().getCookieStore().removeAll();
    }



}
