package activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import model.Device;
import model.Position;
import util.BaseClass;
import util.Common;
import util.CommonConst;
import util.NetworkUtils;
import util.SharedPrefConst;
import util.SharedPreferenceHelper;
import util.StaticFunction;
import util.StaticRequest;
import util.URLConst;
import util.URLS;

public class Maintenance extends BaseClass {
    int timer = 15000;
    private SweetAlertDialog pDialog;
    private Device device;
    private Calendar date;
    private Calendar startDateEngineOil = null;
    private Calendar startDateFilter = null;

    private String TAG = ">>>!" + Maintenance.class.getSimpleName();
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);
        context = this;
        langSelector(context);
        runInit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    void runInit() {
        if (Common.isInternetConnected(context)) {
            Common.retryDialog(context, CommonConst.NO_INTERNET_CODE, bool -> {
                if (bool) {
                    runInit();
                } else {
                    finish();
                }
            });
        } else {
            init();
        }
    }

    private void init() {
        device = getIntent().getParcelableExtra("Device");
        Toolbar(context, getString(R.string.maintenance) + "/" + device.getName());

        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitle(getString(R.string.dialog_box_title));
        pDialog.setContentText(getString(R.string.dialog_box_msg));


        setTimeView();

    }

    private void setTimeView() {
        String response = SharedPreferenceHelper.getSharedPreferenceString(context, SharedPrefConst.RESPONSE_PREF_FILE, SharedPrefConst.RESPONSE, null);

        JSONObject jObject = null;
        try {

            jObject = new JSONObject(response);

            JSONObject jObject1 = new JSONObject(jObject.get("attributes").toString());

            if (!jObject1.isNull(device.getId() + "_engine_oil")) {
                setEngineOilTime(String.valueOf(jObject1.get(device.getId() + "_engine_oil")));
                Button btn = findViewById(R.id.btn_update_engine_oil);
                btn.callOnClick();
            } else {
                setEngineOilTime("Update Time");
            }

            if (!jObject1.isNull(device.getId() + "_filter")) {
                setFilterTime(String.valueOf(jObject1.get(device.getId() + "_filter")));
                Button btn = findViewById(R.id.btn_total_filter_hours_result);
                btn.callOnClick();
            } else {
                setFilterTime("Update Time");
            }


        } catch (Exception ignored) {

        }
    }

    private void setEngineOilTime(String lastTime) {
        Common.logd(TAG, "setTime" + lastTime);
        TextView textViewTime = findViewById(R.id.engine_last_oil_time);
        try {

            startDateEngineOil = Calendar.getInstance();
            startDateEngineOil.setTime(new Date(Long.parseLong(lastTime)));
        } catch (Exception ex) {
            Common.logd(TAG, ex.getMessage());
        }
        lastTime = DateFormat.format("HH:mm:ss MM/dd/yyyy", new Date(Long.parseLong(lastTime))).toString();
        textViewTime.setText(lastTime);

    }


    private void setFilterTime(String lastTime) {
        Common.logd(TAG, "setTime" + lastTime);
        TextView textViewTime = findViewById(R.id.filter_last_time);
        try {

            startDateFilter = Calendar.getInstance();
            startDateFilter.setTime(new Date(Long.parseLong(lastTime)));
        } catch (Exception ex) {
            Common.logd(TAG, ex.getMessage());
        }
        lastTime = DateFormat.format("HH:mm:ss MM/dd/yyyy", new Date(Long.parseLong(lastTime))).toString();
        textViewTime.setText(lastTime);

    }


    public void updateUser(String type, long time) {
        String response = SharedPreferenceHelper.getSharedPreferenceString(context, SharedPrefConst.RESPONSE_PREF_FILE, SharedPrefConst.RESPONSE, null);

        JSONObject jObject = null;
        try {

            Common.logd(TAG, "response : " + response);
            jObject = new JSONObject(response);

            JSONObject jObject1 = new JSONObject(jObject.get("attributes").toString());

            if (type.equals("engine_oil"))
                if (jObject1.isNull(device.getId() + "_engine_oil")) {
                    Common.logd(TAG, "NO Exists");
                    jObject1.put(device.getId() + "_engine_oil", time);
                } else {
                    Common.logd(TAG, "Exists");
                    jObject1.remove(device.getId() + "_engine_oil");
                    jObject1.put(device.getId() + "_engine_oil", time);

                }
            else if (jObject1.isNull(device.getId() + "_filter")) {
                Common.logd(TAG, "NO Exists");
                jObject1.put(device.getId() + "_filter", time);
            } else {
                Common.logd(TAG, "Exists");
                jObject1.remove(device.getId() + "_filter");
                jObject1.put(device.getId() + "_filter", time);

            }
            jObject.remove("attributes");
            jObject.put("attributes", jObject1);
            String url = URLConst.BASE_URL + URLConst.USER + jObject.get("id");
            NetworkUtils networkUtils = new NetworkUtils(context);
            networkUtils.putWithStringBody(url, 15000, jObject.toString(), (response1, statusCode) -> {

                Common.logd(TAG, statusCode + " : " + response1);
                SharedPreferenceHelper.setSharedPreferenceString(context, SharedPrefConst.RESPONSE_PREF_FILE, SharedPrefConst.RESPONSE, response1);
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void fetchRouteFromServer(Date startDate, Date endDate, String type) {
        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        Common.logd(TAG, startDate + "  <>  " + endDate);

        endDate.setTime(endDate.getTime() + 18000000);
        startDate.setTime(startDate.getTime() + 18000000);

        String[] date = Common.dateConversion(startDate, endDate);
        Common.logd(TAG, date[0] + "  <>  " + date[1] +" <> "+URLS.Route(device.getId(), date[0], date[1]));
        StaticRequest.fetchRouteFromServer(URLS.Route(device.getId(), date[0], date[1]), timer, context, pDialog, (response, responseCode) -> {
            if (responseCode == CommonConst.ACTIVITY_MOVE_BACK) {
                StaticFunction.dismissProgressDialog(pDialog);
                finish();
            } else if (responseCode == CommonConst.ACTIVITY_RETRY_CODE) {
                timer = timer + 5000;
                runInit();
            } else if (responseCode == CommonConst.ACTIVITY_VOID) {
                try {
                    Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();

                    Common.logd(TAG, "!!" + response);

                    Position[] routes = gson.fromJson(response, Position[].class);
//                    for (int i=0;i<routes.length; i++){
//                        Common.logd(TAG, routes[i].toString());
//                    }

                    Map<Long, Position> devicePosition = ((SmartTracker) context.getApplicationContext()).getPositions();
                    Position currentPosition = devicePosition.get(device.getId());
                    Position positionOld = routes[0];

                    Common.logd(TAG, "currentPosition : " + currentPosition.toString());
                    Common.logd(TAG, "positionOld : " + positionOld.toString());


                    double diff = currentPosition.getPositionAttributes().getTotalDistance() - positionOld.getPositionAttributes().getTotalDistance();
                    if (type.equals("engine")) {
                        TextView total_engine_distance_travel_result = findViewById(R.id.total_engine_distance_travel_result);
                        total_engine_distance_travel_result.setText((diff / 1000) + " KM");
                        TextView engineHours = findViewById(R.id.total_engine_hours_result);
                        double hours = currentPosition.getPositionAttributes().getHours() - positionOld.getPositionAttributes().getHours();
                        Common.logd(TAG, String.valueOf(hours));
                        hours = TimeUnit.MILLISECONDS.toHours((long) hours);

                        engineHours.setText(hours +" Hr.");
                        findViewById(R.id.total_engine_oil_summary).setVisibility(View.VISIBLE);
                    } else {
                        TextView total_filter_distance_travel_result = findViewById(R.id.total_filter_distance_travel_result);
                        total_filter_distance_travel_result.setText((diff / 1000) + " KM");

                        TextView engineHours = findViewById(R.id.total_filter_hours_result);
                        double hours = currentPosition.getPositionAttributes().getHours() - positionOld.getPositionAttributes().getHours();
//                        hours = (int) ((hours / (1000*60*60)) % 24);
                        hours = TimeUnit.MILLISECONDS.toHours((long) hours);

                        engineHours.setText(hours +" Hr.");
                        Common.logd(TAG, String.valueOf((int) ((hours / (1000*60*60)) % 24)));
                        findViewById(R.id.total_filter_hour_summary).setVisibility(View.VISIBLE);

                    }


                } catch (Exception ex) {
                    Common.logd(TAG, ex.getMessage() + ex.toString());
                    Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
                }
            }
        });
    }


    public int[] getIndex(String text, String wordToFind) {

        Pattern word = Pattern.compile(wordToFind);
        Matcher match = word.matcher(text);
        int start = -1;
        int end = -1;
        while (match.find()) {
            start = match.start();
            end = match.end() - 1;

        }
        int[] test = new int[2];
        test[0] = start;
        test[1] = end;
        return test;
    }

    public void update_last_change_time(View view) {
        showDateTimePicker();
    }


    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();

        new DatePickerDialog(context, R.style.DialogTheme, (view, year, monthOfYear, dayOfMonth) -> {
            date.set(year, monthOfYear, dayOfMonth);
            new TimePickerDialog(context, R.style.DialogTheme, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);

                Calendar startDate = Calendar.getInstance();
                startDate.setTime(date.getTime());
                updateUser("engine_oil", startDate.getTime().getTime());
                setEngineOilTime(String.valueOf(startDate.getTime().getTime()));
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    public void calculate_engine_oil(View view) {
        if (startDateEngineOil != null) {

            Calendar startTime = (Calendar) startDateEngineOil.clone();
            startTime.set(Calendar.HOUR_OF_DAY, 0);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);
            Common.logd(TAG,startTime.getTime()+"<>"+Calendar.getInstance().getTime());
            fetchRouteFromServer(startTime.getTime(), Calendar.getInstance().getTime(), "engine");
        }
    }

    public void update_last_change_time_filter(View view) {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();

        new DatePickerDialog(context, R.style.DialogTheme, (DatePicker view1, int year, int monthOfYear, int dayOfMonth) -> {
            date.set(year, monthOfYear, dayOfMonth);
            new TimePickerDialog(context, R.style.DialogTheme, (view2, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);

                Calendar startDate = Calendar.getInstance();
                startDate.setTime(date.getTime());
                updateUser("filter", startDate.getTime().getTime());
                setFilterTime(String.valueOf(startDate.getTime().getTime()));

            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    public void calculate_filter(View view) {
        if (startDateFilter != null) {


            Calendar startTime = (Calendar) startDateFilter.clone();
            startTime.set(Calendar.HOUR_OF_DAY, 0);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);
            fetchRouteFromServer(startTime.getTime(),  Calendar.getInstance().getTime(), "filter");
        }
    }
}
