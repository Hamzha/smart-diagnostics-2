package activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;
import com.wang.avi.AVLoadingIndicatorView;

import java.net.CookieHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import adaptor.VehicleSummaryAdaptor;
import callbackinterface.CommonInterface;
import model.Device;
import util.BaseClass;
import util.Common;
import util.CommonConst;
import util.NetworkUtils;
import util.StaticFunction;
import util.StaticRequest;
import util.URLS;

public class VehicleListSummary extends BaseClass {

    String TAG = ">>>" + VehicleListSummary.class.getSimpleName();
    int timer = 15000;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list_summary);
        context = this;
        langSelector(context);
        CookieHandler.setDefault(((SmartTracker) getApplication()).getCookieManager());
        Toolbar(this, getString(R.string.vehicle_list_summary));
        runInit();
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
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        avLoadingIndicatorView = findViewById(R.id.progress_summary);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);

        fetchDataFromServer();

    }

    private void fetchDataFromServer() {
        StaticRequest.getDailySummary(this, 15000, (response, statusCode) -> {
            if (Common.responseCheck(statusCode, context)) {
                Common.retryDialog(this, statusCode, bool -> {
                    if (bool) {
                        timer = timer + 5000;
                        runInit();
                    } else {
                        finish();
                    }
                });
            } else {
                runOnUiThread(() -> buildRecyclerView());
            }
        });
    }


    public void getDailySummary(Activity context, int timer, CommonInterface commonInterface) {

        Map<Long, Device> devices = ((SmartTracker) context.getApplication()).getDevices();
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

        String startDateStr = format.format(StaticFunction.calculateDateFromDays(1));
        for (Map.Entry<Long, Device> device : devices.entrySet()) {
            NetworkUtils networkUtils = new NetworkUtils(context);
            Common.logd(TAG, URLS.Trips(startDateStr, format.format(Calendar.getInstance().getTime()), device.getValue().getId()));

            networkUtils.getJSON(URLS.Trips(startDateStr, format.format(Calendar.getInstance().getTime()), device.getValue().getId()), timer, (response, statusCode) -> {
                if (statusCode == CommonConst.LATE_RESPONSE_CODE) {
                    commonInterface.commonCallBack(response, CommonConst.LATE_RESPONSE_CODE);
                } else if (statusCode == CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE) {
                    commonInterface.commonCallBack(response, CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE);
                } else {
                    try {
                        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                        Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();
                        model.Trip[] trips = gson.fromJson(response, model.Trip[].class);
                        for (int i = 0; i < trips.length; i++) {
                            Common.logd(TAG, device.getValue().getName() + " : " + trips[i].toString());
                        }

                        commonInterface.commonCallBack("Successfull", 200);
                    } catch (Exception ex) {
                        commonInterface.commonCallBack(context.getString(R.string.late_response), CommonConst.LATE_RESPONSE_CODE);
                    }
                }
            });
        }
    }

    public void buildRecyclerView() {

        RecyclerView mRecyclerView = findViewById(R.id.vehicle_summary_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        RecyclerView.Adapter mAdapter = new VehicleSummaryAdaptor(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        avLoadingIndicatorView.setVisibility(View.INVISIBLE);

    }



}
