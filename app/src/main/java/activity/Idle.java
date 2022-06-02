package activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lecho.lib.hellocharts.model.SliceValue;
import model.Device;
import model.Event;
import model.Position;
import util.BaseClass;
import util.Common;
import util.CommonConst;
import util.NetworkUtils;
import util.StaticFunction;
import util.URLS;

public class Idle extends BaseClass {

    List<SliceValue> pieData;
    int timer = 15000;
    BarChart barChart;
    private Device device;
    private ArrayList<String> stringArrayList;
    private String TAG = ">>>" + Idle.class.getSimpleName();
    TextView idleMin, normalMin;
    Context context;
    private ArrayList<model.Event> eventsArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idle);
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
        stringArrayList = new ArrayList<>();
        eventsArrayList = new ArrayList<>();
        Date startDate = (Date) getIntent().getSerializableExtra("TripStartTime");
        Date endDate = (Date) getIntent().getSerializableExtra("TripEndTime");
        device = getIntent().getParcelableExtra("Device");
        pieData = new ArrayList<>();
        barChart = findViewById(R.id.bar_chart);

        normalMin = findViewById(R.id.normal_min);
        idleMin = findViewById(R.id.idle_min);
        normalMin.setVisibility(View.GONE);
        idleMin.setVisibility(View.GONE);
        Toolbar(context, getString(R.string.idle_graph)+"/"+device.getName());

        AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner(context, startDate, endDate);
        asyncTaskRunner.execute();
    }

    @SuppressLint("DefaultLocale")
    private void fetchTripsFromServer(final Date startdate, Date enddate, SweetAlertDialog pDialog, Context context) {
        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String fromDate = dateFormat.format(startdate);
        String toDate = dateFormat.format(enddate);

        try {
            fromDate = URLEncoder.encode(fromDate, "UTF-8");
            toDate = URLEncoder.encode(toDate, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final NetworkUtils networkUtils = new NetworkUtils(context);
        Common.logd(TAG,URLS.Events(device.getId(), fromDate, toDate));
        networkUtils.get(URLS.Events(device.getId(), fromDate, toDate), timer, (response, statusCode) -> {
            if (Common.responseCheck(statusCode, context)) {
                Common.retryDialog(context, statusCode, bool -> {
                    if (bool) {
                        timer = timer + 5000;
                        runInit();
                    } else {
                        finish();
                    }
                });
            } else {
                Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();
                try {
                    Common.logd(TAG,response);
                    final model.Event[] events = gson.fromJson(response, model.Event[].class);
                    for (model.Event event : events) {
                        if (!(event.getType().equals("deviceMoving") || event.getType().equals("deviceStopped")
                                || event.getType().equals("deviceOffline") || event.getType().equals("deviceOnline"))) {
                            eventsArrayList.add(event);
                            if (event.getPositionId() != 0) {
                                Common.logd(TAG,"Final: "+event.toString());
                                stringArrayList.add(String.valueOf(event.getPositionId()));
                            }
                        }
                    }
                    StringBuilder ids = new StringBuilder();
                    for (int i = 0; i < stringArrayList.size(); i++) {

                        if (i == 0)
                            ids = new StringBuilder("id=" + stringArrayList.get(i));
                        else
                            ids.append("&id=").append(stringArrayList.get(i));
                    }


                    networkUtils.get(URLS.PositionIds(ids), timer, (response1, statusCode1) -> {
                        try {
                            if (Common.responseCheck(statusCode, context)) {
                                Common.retryDialog(context, statusCode, bool -> {
                                    if (bool) {
                                        timer = timer + 5000;
                                        runInit();
                                    } else {
                                        finish();
                                    }
                                });
                            } else {
                                final Position[] position;
                                position = gson.fromJson(response1, Position[].class);
                                for (Position position1 : position) {
                                    Common.logd(TAG,position1.toString());
                                    for (int i = 0; i < eventsArrayList.size(); i++) {
                                        if (eventsArrayList.get(i).getPositionId() == (position1.getId())) {
                                            eventsArrayList.get(i).setServerTime(position1.getDeviceTime());
                                            eventsArrayList.get(i).getAttributes().setIo251(position1.getPositionAttributes().getIo251());

                                            if (eventsArrayList.get(i).getServerTime().split("\\.").length == 2) {
                                                String tmp1 = eventsArrayList.get(i).getServerTime().split("\\.")[0];
                                                String[] tmp2 = tmp1.split("T");
                                                eventsArrayList.get(i).setServerTime(tmp2[0] + " " + tmp2[1]);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }

                            createDataPieChart(eventsArrayList, startdate, enddate, pDialog, context);

                        } catch (Exception ex) {
//                            ex.printStackTrace();
                            Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
                        }
                    });
                } catch (Exception e) {
                    Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);

                }
            }
        });
    }

    private void createDataPieChart(ArrayList<Event> eventsArrayList, Date startdate, Date enddate, SweetAlertDialog pDialog, Context context) {
//
        String[][] mainData = new String[eventsArrayList.size()][2];

        for (int i = 0; i < eventsArrayList.size(); i++) {
            Common.logd(TAG, "Event : "+eventsArrayList.get(i).toString());
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            long dateTime = 0;
            Common.logd(TAG,"Server Time"+eventsArrayList.get(i).getServerTime());
            try {
                dateTime = Objects.requireNonNull(format.parse(eventsArrayList.get(i).getServerTime())).getTime();
            } catch (ParseException e) {
                Common.logd(TAG,e.getLocalizedMessage());
                e.printStackTrace();
            }
            Date dateTest = new Date(dateTime);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTest);
            calendar.add(Calendar.HOUR, 5);
            Common.logd(TAG,"io251()  : "+eventsArrayList.get(i).getAttributes().getIo251());
            mainData[i][1] = String.valueOf(eventsArrayList.get(i).getAttributes().getIo251());
            mainData[i][0] = String.valueOf(dateTest.getTime() + 18000000);
        }

        Arrays.sort(mainData, (first, second) -> {
            // here you should usually check that first and second
            // a) are not null and b) have at least two items
            // updated after comments: comparing Double, not Strings
            // makes more sense, thanks Bart Kiers
            return Double.valueOf(second[0]).compareTo(
                    Double.valueOf(first[0])
            );
        });


        barChart(eventsArrayList.size(), mainData, startdate, enddate, pDialog, context);
    }

    private void barChart(int k, String[][] mainData, Date startDate, Date endDate, SweetAlertDialog pDialog, Context context) {
        int counter = 0;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        boolean idleStart = false;
        double time = 00;
        double diff = 0;
        for (int i = k - 1; i >= 0; i--) {
            if (!mainData[i][1].equals("null") && Double.parseDouble(mainData[i][1]) == 1.0) {
                if (!idleStart) {
                    idleStart = true;
                    time = Double.parseDouble(mainData[i][0]);
//                    try {
//                        Date date = new Date(Long.parseLong(mainData[i][0]));
//                        Common.logd(TAG,"Idle Enter : "+format.format(date));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Common.logd(TAG,"test test");
//                    }
                }
            } else if (!mainData[i][1].equals("null") && Double.parseDouble(mainData[i][1]) == 0.0) {
                if (idleStart) {
                    idleStart = false;
                    counter = counter + 1;
                    diff = diff + (Double.parseDouble(mainData[i][0]) - time);
                    time = Double.parseDouble(mainData[i][0]);
//                    try {
//                        Date date = new Date(Long.parseLong(mainData[i][0]));
//                        Common.logd(TAG,"Idle Exit : "+format.format(date));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Common.logd(TAG,"test test");
//                    }
                }
            }

            if (!mainData[i][1].equals("null") && Double.parseDouble(mainData[i][1]) == 0.0 && i == k - 1) {
                if (!idleStart) {
                    diff = Double.parseDouble(mainData[i][0]) - startDate.getTime();
//                    Common.logd(TAG, "IDle Exit : " + startDate.getTime() + " + " + Double.parseDouble(mainData[i][0]) + "false");
                }
            }
        }

//        if(idleStart){
//            diff = diff + (endDate.getTime() - time);
//        }


        double totalTime = endDate.getTime() - startDate.getTime();

        counter = counter * 300000;
        Common.logd(TAG, "Diff : "+TimeUnit.MILLISECONDS.toMinutes((long) diff));
        Common.logd(TAG, "Total : "+TimeUnit.MILLISECONDS.toMinutes((long) totalTime));

        totalTime = totalTime - (counter);
        totalTime = totalTime - diff;
        diff = diff + (counter);


        if (diff < 0)
            diff = -diff;

        if (totalTime < 0) {
            totalTime = 0;
        }
        List<BarEntry> totalTimeList = new ArrayList<>();
        totalTimeList.add(new BarEntry(0, (int) TimeUnit.MILLISECONDS.toMinutes((long) totalTime)));

        List<BarEntry> idleTimeList = new ArrayList<>();
        idleTimeList.add(new BarEntry(2, (int) TimeUnit.MILLISECONDS.toMinutes((long) diff)));


        BarDataSet set1 = new BarDataSet(totalTimeList, getString(R.string.normal));
        set1.setColor(Color.BLUE);

        BarDataSet set = new BarDataSet(idleTimeList, getString(R.string.idling));
        set.setColor(Color.RED);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set);

        BarData data = new BarData(dataSets);
        barChart.setVisibility(View.VISIBLE);
        barChart.setData(data);
        barChart.getXAxis().setDrawLabels(false);

        float barWidth = 0.46f;

        barChart.animateX(30000);
        barChart.animateY(30000);

        barChart.getBarData().setBarWidth(barWidth);
        barChart.invalidate();
        barChart.getChartBitmap();
        barChart.getDescription().setText("");
//        barChart.getAxisLeft().mAxisMinimum = 0;

        findViewById(R.id.idle_graph_text).setVisibility(View.GONE);
        normalMin.setVisibility(View.VISIBLE);
        normalMin.setText(getString(R.string.normal) + " : " + TimeUnit.MILLISECONDS.toMinutes((long) totalTime));
        idleMin.setVisibility(View.VISIBLE);
        idleMin.setText(getString(R.string.idling) + " : " + TimeUnit.MILLISECONDS.toMinutes((long) diff));

        StaticFunction.dismissProgressDialog(pDialog);
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        SweetAlertDialog eDialog;
        Context activity;
        Date startDate, endDate;

        AsyncTaskRunner(Context context, Date startDate, Date endDate) {
            //        Context[] context;
            WeakReference<Context> activityReference = new WeakReference<>(context);
            context = activityReference.get();
            this.activity = context;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            eDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
            eDialog.setTitle(getString(R.string.dialog_box_msg));
            eDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            fetchTripsFromServer(startDate, endDate, eDialog, activity);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }

}


