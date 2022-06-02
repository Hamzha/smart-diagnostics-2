package activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;
import com.wang.avi.AVLoadingIndicatorView;

import java.net.CookieHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import adaptor.TripAdaptor;
import model.Device;
import model.GeoFence;
import util.BaseClass;
import util.Common;
import util.CommonConst;
import util.StaticFunction;
import util.StaticRequest;
import util.URLS;

public class    Trip extends BaseClass {
    String TAG = ">>>" + Trip.class.getSimpleName();
    int timer = 15000;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private Device device;
    private Date[] startDate = {null};
    private Date[] endDate = {null};
    private RecyclerView.LayoutManager mLayoutManager;
    Context context;
    Map<Long, GeoFence> geoFences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        CookieHandler.setDefault(((SmartTracker) getApplication()).getCookieManager());
        context = this;
        langSelector(context);
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
        avLoadingIndicatorView = findViewById(R.id.progress_trips);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(context);
        device = getIntent().getParcelableExtra("Device");
        Toolbar(context, getString(R.string.Trip) + "/" + device.getName());

        startDate[0] = (Date) getIntent().getSerializableExtra("StartDate");
        endDate[0] = (Date) getIntent().getSerializableExtra("EndDate");
        initFetch(startDate[0], endDate[0]);
    }

    protected void initFetch(Date startDate, Date endDate) {
        setDateTripHeader(startDate, endDate);
        fetchTripsFromServer(startDate, endDate);

    }

    private void fetchTripsFromServer(Date startDate, Date endDate) {
        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String[] dates = Common.dateConversion(startDate, endDate);
        StaticRequest.fetchFromServer(URLS.Trips(dates[0], dates[1], device.getId()), context, timer, (response, statusCode) -> {
            if (statusCode == CommonConst.SUCCESS_RESPONSE_CODE) {
                Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();
                try {
                    model.Trip[] trips = gson.fromJson(response, model.Trip[].class);
                    buildRecyclerView(StaticFunction.createTripList(trips));
                } catch (Exception e) {
                    Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
                }
            } else if (statusCode == CommonConst.ACTIVITY_MOVE_BACK) {
                finish();
            } else if (statusCode == CommonConst.ACTIVITY_RETRY_CODE) {
                timer = timer + 5000;
                runInit();
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    protected void setDateTripHeader(Date fromDate, Date toDate) {
        ((TextView) findViewById(R.id.trip_activity_start_date)).setText(new SimpleDateFormat(CommonConst.dataFormatWithMonth).format(fromDate));
        ((TextView) findViewById(R.id.trip_activity_end_date)).setText(new SimpleDateFormat(CommonConst.dataFormatWithMonth).format(toDate));
    }


    public void buildRecyclerView(ArrayList<model.Trip> tripList) {
        if (tripList.size() == 0) {
            avLoadingIndicatorView.setVisibility(View.INVISIBLE);
            findViewById(R.id.no_trips).setVisibility(View.VISIBLE);
        } else {
            RecyclerView mRecyclerView = findViewById(R.id.trips_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(context);
            Map<Long, Device> devices = ((SmartTracker) getApplication()).getDevices();
            RecyclerView.Adapter mAdapter = new TripAdaptor(tripList, devices, context);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
            avLoadingIndicatorView.setVisibility(View.INVISIBLE);
        }
    }

}
