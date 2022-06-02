package activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import model.Device;
import model.Trip;
import util.BaseClass;
import util.Common;
import util.CommonConst;
import util.StaticFunction;
import util.StaticRequest;
import util.URLS;

public class Summary extends BaseClass {
    TextView summaryTotalDuration;
    int timer = 15000;
    private Device device;
    private Date[] startDate = {null};
    private Date[] endDate = {null};
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private String TAG = ">>>" + Summary.class.getSimpleName();
    Context context;

    private double totalDistance = 0;
    private double totalAvgSpeed = 0;
    private double maxSpeed = 0;
    private long duration = 0;
    private int tripSize = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
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
        summaryTotalDuration = findViewById(R.id.summary_total_duration);
        device = getIntent().getParcelableExtra("Device");
        Toolbar(context, getString(R.string.summary) + "/" + device.getName());

        startDate[0] = (Date) getIntent().getSerializableExtra("StartDate");
        endDate[0] = (Date) getIntent().getSerializableExtra("EndDate");
        avLoadingIndicatorView = findViewById(R.id.progress_summary);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
        initFetch(startDate[0], endDate[0]);
    }

    private void initFetch(Date startDate, Date endDate) {
        setDateTripHeader(startDate, endDate);
        fetchTripsFromServer(startDate, endDate);
    }

    private void fetchTripsFromServer(Date startDate, Date endDate) {
        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String[] dates = Common.dateConversion(startDate, endDate);
        Common.logd(TAG, URLS.Trips(dates[0], dates[1], device.getId()));


        StaticRequest.fetchFromServer(URLS.Summary(device.getId(), dates[0], dates[1]), context, timer, (response, statusCode) -> {
            if (statusCode == CommonConst.ACTIVITY_RETRY_CODE) {
                timer = timer + 5000;
                runInit();
            } else if (statusCode == CommonConst.ACTIVITY_MOVE_BACK) {
                finish();
            } else if (statusCode == CommonConst.SUCCESS_RESPONSE_CODE) {

                try {
                    Common.logd(TAG, response);
                    Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();
//                    model.Summary summary = gson.fromJson(response, model.Summary.class);
//                    Common.logd(TAG, String.valueOf(summary.getDistance()));
                    JSONObject jsonObject = new JSONObject(response.replace("[", ""));

                    StaticRequest.fetchFromServer(URLS.Trips(dates[0], dates[1], device.getId()), context, timer, (responseTrip, statusCodeTrip) -> {
                        if (statusCodeTrip == CommonConst.ACTIVITY_RETRY_CODE) {
                            timer = timer + 5000;
                            runInit();
                        } else if (statusCodeTrip == CommonConst.ACTIVITY_MOVE_BACK) {
                            finish();
                        } else if (statusCodeTrip == CommonConst.SUCCESS_RESPONSE_CODE) {
                            try {
                                model.Trip[] trips = gson.fromJson(responseTrip, model.Trip[].class);
                                tripSize = trips.length;
                                buildView(StaticFunction.createTripList(trips), jsonObject.getDouble("distance"));
                            } catch (Exception rx) {
                                Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
                            }
                        }
                    });


                } catch (Exception rx) {
                    Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
                }
            }
        });

//        StaticRequest.fetchFromServer(URLS.Trips(dates[0], dates[1], device.getId()), context, timer, (response, statusCode) -> {
//            if (statusCode == CommonConst.ACTIVITY_RETRY_CODE) {
//                timer = timer + 5000;
//                runInit();
//            } else if (statusCode == CommonConst.ACTIVITY_MOVE_BACK) {
//                finish();
//            } else if (statusCode == CommonConst.SUCCESS_RESPONSE_CODE) {
//                Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();
//                try {
//                    model.Trip[] trips = gson.fromJson(response, model.Trip[].class);
//                    tripSize = trips.length;
//                    buildView(StaticFunction.createTripList(trips));
//                } catch (Exception rx) {
//                    Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
//                }
//            }
//        });

    }

    @SuppressLint("SetTextI18n")
    private void buildView(ArrayList<Trip> tripList, double distance) {
        avLoadingIndicatorView.setVisibility(View.INVISIBLE);


        for (int i = 0; i < tripList.size(); i++) {

//            if (tripList.get(i).getDistance() / 1000 < 0) {
//                totalDistance = -tripList.get(i).getDistance() + totalDistance;
//
//            } else
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
        distance = distance / 1000;

        totalAvgSpeed = Math.round(totalAvgSpeed / tripList.size() * 100.0) / 100.0;
        maxSpeed = Math.round(maxSpeed * 100.0) / 100.0;
        distance = Math.round(distance * 100.0) / 100.0;

        totalDistance = Math.round(totalDistance * 100.0) / 100.0;


        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        minutes = minutes - (hours * 60);

        summaryTotalDuration.setText(" " + hours + " " + getString(R.string.hours) + "," + minutes + " " + getString(R.string.minutes));
        findViewById(R.id.total_duration).setVisibility(View.VISIBLE);

        TextView summaryTripTotalDistance = findViewById(R.id.summary_trip_total_distance);
        summaryTripTotalDistance.setText(" " + totalDistance + " km");
        findViewById(R.id.total_trip_distance).setVisibility(View.VISIBLE);

        TextView summaryTotalDistance = findViewById(R.id.summary_total_distance);
        summaryTotalDistance.setText(" " + distance + " km");
        findViewById(R.id.total_distance).setVisibility(View.VISIBLE);

        TextView summaryMaxSpeed = findViewById(R.id.summary_max_speed);
        summaryMaxSpeed.setText(" " + maxSpeed + " km/h");
        findViewById(R.id.max_speed).setVisibility(View.VISIBLE);


        TextView summaryTotalTrips = findViewById(R.id.summary_total_trips);
        summaryTotalTrips.setText(" " + String.valueOf(tripList.size()));
        findViewById(R.id.total_trips).setVisibility(View.VISIBLE);

        TextView summaryAvgSpped = findViewById(R.id.summary_avg_speed);
        summaryAvgSpped.setText(" " + totalAvgSpeed + " km/h");
        findViewById(R.id.avg_speed).setVisibility(View.VISIBLE);

        avLoadingIndicatorView.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("SimpleDateFormat")
    private void setDateTripHeader(Date startDate, Date endDate) {
        ((TextView) findViewById(R.id.summary_start_date)).setText(new SimpleDateFormat(CommonConst.dataFormatWithMonth).format(startDate));
        ((TextView) findViewById(R.id.summary_end_date)).setText(new SimpleDateFormat(CommonConst.dataFormatWithMonth).format(endDate));
    }


    public void onMap(View view) {
        long diff = endDate[0].getTime() - startDate[0].getTime();
        if (tripSize == 0) {
            Toast.makeText(this, getString(R.string.no_trips_txt), Toast.LENGTH_LONG).show();
        } else if (diff < 108000000) {
            startActivity(new Intent(this, SummaryTrip.class).putExtra("Device", device)
                    .putExtra("StartDate", startDate[0])
                    .putExtra("EndDate", endDate[0])
                    .putExtra("avg", this.totalAvgSpeed)
                    .putExtra("max", this.maxSpeed)
                    .putExtra("dis", this.totalDistance)
                    .putExtra("duration", this.duration)
                    .putExtra("size", this.tripSize));

        } else {
            Toast.makeText(this, "Only for one day summary", Toast.LENGTH_LONG).show();

        }
    }
}
