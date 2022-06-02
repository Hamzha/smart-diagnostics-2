package activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import model.Device;
import model.Event;
import model.Trip;
import util.BaseClass;
import util.Common;
import util.CommonConst;
import util.StaticFunction;
import util.StaticRequest;
import util.URLS;

public class Eco extends BaseClass {

    Date startDate;
    Date endDate;
    model.Trip trip;
    int timer = 15000;
    BarChart barChart;
    String TAG = ">>>" + Eco.class.getSimpleName();
    private Device device;
    private TextView harsh_acceleration, harsh_breaking, harsh_cornering, over_speed, viwe_idle;
    private String timeFormat = "HH:mm";
    private long harshAcc, harshBreak, harshCorner, overSpeeding;
    private long idle;
    private PieChart pieChart;
    Context context;
    private TextView ecoTripDeatil;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco);
        context = this;
        langSelector(context);
//        Toolbar(this, getString(R.string.trip_behaviour));
        runInit();
    }

    void runInit() {
        if (Common.isInternetConnected(context)) {
            Common.retryDialog(this, CommonConst.NO_INTERNET_CODE, bool -> {
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
        barChart = findViewById(R.id.bar_chart_eco);

        startDate = (Date) getIntent().getSerializableExtra("TripStartTime");
        endDate = (Date) getIntent().getSerializableExtra("TripEndTime");
        device = getIntent().getParcelableExtra("Device");
        trip = getIntent().getParcelableExtra("Trip");
        pieChart = findViewById(R.id.pie_chart);
        Toolbar(this, getString(R.string.trip_behaviour) + "/" + device.getName());

        setDateEventHeader(startDate, endDate);

        harsh_cornering = findViewById(R.id.harsh_cornering);
        harsh_breaking = findViewById(R.id.harsh_breaking);
        harsh_acceleration = findViewById(R.id.harsh_acceleration);
        over_speed = findViewById(R.id.over_speed);
        viwe_idle = findViewById(R.id.idle);

        harshAcc = 0;
        harshBreak = 0;
        harshCorner = 0;
        overSpeeding = 0;
        idle = 0;
        AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner(context, startDate, endDate);
        asyncTaskRunner.execute();

    }

    @SuppressLint("SimpleDateFormat")
    protected void setDateEventHeader(Date fromDate, Date toDate) {

        ((TextView) findViewById(R.id.eco_start_date)).setText(new SimpleDateFormat(CommonConst.dataFormatWithMonth).format(fromDate));
        ((TextView) findViewById(R.id.eco_end_date)).setText(new SimpleDateFormat(CommonConst.dataFormatWithMonth).format(toDate));
    }

    protected void fetchRouteFromServer(Date startDate, Date endDate, SweetAlertDialog pDialog, Context context) {
        String[] date = Common.dateConversion(startDate, endDate);
        StaticRequest.fetchFromServer(URLS.Events(device.getId(), date[0], date[1]), this, timer, (response, statusCode) -> {
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
                try {
                    Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();
                    final model.Event[] events = gson.fromJson(response, model.Event[].class);
                    setAttributes(events, pDialog);
                } catch (Exception ex) {
                    Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
                }
            }
        });

    }

    private void setAttributes(Event[] events, SweetAlertDialog pDialog) {
        ArrayList<Event> eventIdleIgnition = new ArrayList<>();

        for (Event event : events) {

            if (event.getType().toLowerCase().contains("ignitionoff")) {
                eventIdleIgnition.add(event);
            }

            if (event.getAttributes().getAlarm() != null && event.getAttributes().getAlarm().toLowerCase().contains("idle")) {
                eventIdleIgnition.add(event);
            }

            if (event.getType().toLowerCase().contains("deviceoverspeed")) {
                overSpeeding = overSpeeding + 1;
            }

            if (event.getType().toLowerCase().contains("alarm")) {
                if (event.getAttributes().getAlarm().toLowerCase().contains("acc")) {
                    harshAcc = harshAcc + 1;
                } else if (event.getAttributes().getAlarm().toLowerCase().contains("brak")) {
                    harshBreak = harshBreak + 1;
                } else if (event.getAttributes().getAlarm().toLowerCase().contains("corner")) {
                    harshCorner = harshCorner + 1;
                }
            }

        }

        boolean idleStart = false;
        for (int i = 0; i < eventIdleIgnition.size(); i++) {
            if (eventIdleIgnition.get(i).getAttributes().getAlarm() != null && eventIdleIgnition.get(i).getAttributes().getAlarm().toLowerCase().contains("idle")) {
                if (idleStart == true) {
                    idle = idle + 1;
                    idleStart = false;
                } else
                    idleStart = true;
            } else {
                if (idleStart == true) {
                    idle = idle + 1;
                    idleStart = false;
                }
            }
            if (eventIdleIgnition.get(i).getType().toLowerCase().contains("ignitionoff")) {
                if (idleStart == true) {
                    idleStart = false;
                    idle = idle + 1;
                }
            }
        }

        setPieChart(pDialog);

        setBarChart();

        setTextView();
    }

    @SuppressLint("DefaultLocale")
    private void setPieChart(SweetAlertDialog pDialog) {

        List<PieEntry> pieChartEntries = new ArrayList<>();

        pieChartEntries.add(new PieEntry(20.0f, getString(R.string.pie_chart_cornering)));
        pieChartEntries.add(new PieEntry(20.0f, getString(R.string.pie_chart_acceleration)));
        pieChartEntries.add(new PieEntry(20.0f, getString(R.string.pie_chart_braking)));
        pieChartEntries.add(new PieEntry(20.0f, getString(R.string.pie_chart_speeding)));
        pieChartEntries.add(new PieEntry(20.0f, getString(R.string.pie_chart_idling)));

        PieDataSet dataSet = new PieDataSet(pieChartEntries, getString(R.string.pie_chart_title));
        dataSet.setDrawIcons(false);


        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();

        if (formulaImplement(harshCorner, trip) == 0) {
            colors.add(Color.rgb(0, 210, 0));
        } else if (formulaImplement(harshCorner, trip) < 10) {
            colors.add(Color.rgb(210, 210, 0));
        } else if (formulaImplement(harshCorner, trip) < 15) {
            colors.add(Color.rgb(210, 165, 0));
        } else if (formulaImplement(harshCorner, trip) < 20) {
            colors.add(Color.rgb(210, 0, 0));
        }

        if (formulaImplement(harshAcc, trip) == 0) {
            colors.add(Color.rgb(0, 210, 0));
        } else if (formulaImplement(harshAcc, trip) < 10) {
            colors.add(Color.rgb(210, 210, 0));
        } else if (formulaImplement(harshAcc, trip) < 15) {
            colors.add(Color.rgb(210, 165, 0));
        } else if (formulaImplement(harshAcc, trip) < 20) {
            colors.add(Color.rgb(210, 0, 0));
        }

        if (formulaImplement(harshBreak, trip) == 0) {
            colors.add(Color.rgb(0, 210, 0));
        } else if (formulaImplement(harshBreak, trip) < 10) {
            colors.add(Color.rgb(210, 210, 0));
        } else if (formulaImplement(harshBreak, trip) < 15) {
            colors.add(Color.rgb(210, 165, 0));
        } else if (formulaImplement(harshBreak, trip) < 20) {
            colors.add(Color.rgb(210, 0, 0));
        }


        if (formulaImplement(overSpeeding, trip) == 0) {
            colors.add(Color.rgb(0, 210, 0));
        } else if (formulaImplement(overSpeeding, trip) < 10) {
            colors.add(Color.rgb(210, 210, 0));
        } else if (formulaImplement(overSpeeding, trip) < 15) {
            colors.add(Color.rgb(210, 165, 0));
        } else if (formulaImplement(overSpeeding, trip) < 20) {
            colors.add(Color.rgb(210, 0, 0));
        }

        if (formulaImplement(idle, trip) == 0) {
            colors.add(Color.rgb(0, 210, 0));
        } else if (formulaImplement(idle, trip) < 10) {
            colors.add(Color.rgb(210, 210, 0));
        } else if (formulaImplement(idle, trip) < 15) {
            colors.add(Color.rgb(210, 165, 0));
        } else if (formulaImplement(idle, trip) < 20) {
            colors.add(Color.rgb(210, 0, 0));
        }


        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(0);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);

        double totalScore = formulaImplement(idle, trip) + formulaImplement(overSpeeding, trip) + formulaImplement(harshCorner, trip) + formulaImplement(harshAcc, trip) + formulaImplement(harshBreak, trip);

        pieChart.setCenterText(String.format("%.2f", 100 - totalScore) + "%");
        pieChart.setCenterTextSize(17);
        pieChart.highlightValues(null);

        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.invalidate();
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        StaticFunction.dismissProgressDialog(pDialog);
    }

    private void setBarChart() {
        List<BarEntry> totalTimeCornering = new ArrayList<>();
        totalTimeCornering.add(new BarEntry(1, harshCorner));

        BarDataSet set1 = new BarDataSet(totalTimeCornering, getString(R.string.pie_chart_cornering));
        set1.setColor(Color.BLUE);

        List<BarEntry> totalTimeBreaking = new ArrayList<>();
        totalTimeBreaking.add(new BarEntry(3, harshBreak));

        BarDataSet set2 = new BarDataSet(totalTimeBreaking, getString(R.string.pie_chart_braking));
        set2.setColor(Color.rgb(139, 0, 139));

        List<BarEntry> totalTimeAcc = new ArrayList<>();
        totalTimeAcc.add(new BarEntry(5, harshAcc));

        BarDataSet set3 = new BarDataSet(totalTimeAcc, getString(R.string.pie_chart_acceleration));
        set3.setColor(Color.YELLOW);

        List<BarEntry> totalTimeSpeeding = new ArrayList<>();
        totalTimeSpeeding.add(new BarEntry(7, overSpeeding));

        BarDataSet set4 = new BarDataSet(totalTimeSpeeding, getString(R.string.pie_chart_speeding));
        set4.setColor(Color.rgb(255, 165, 0));

        List<BarEntry> totalTimeIdling = new ArrayList<>();
        totalTimeIdling.add(new BarEntry(9, idle));

        BarDataSet set5 = new BarDataSet(totalTimeIdling, getString(R.string.pie_chart_idling));
        set5.setColor(Color.rgb(255, 0, 0));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);
        dataSets.add(set4);
        dataSets.add(set5);

        BarData data = new BarData(dataSets);
        barChart.setData(data);

        float barWidth = 0.46f;

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().mAxisMinimum = 0f;
        barChart.getAxisRight().setEnabled(false);

        barChart.animateX(3000);
        barChart.animateY(3000);

        barChart.getXAxis().setDrawLabels(false);
        barChart.getBarData().setBarWidth(barWidth);
        barChart.invalidate();
        barChart.getChartBitmap();
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setText("");

    }

    @SuppressLint("SetTextI18n")
    private void setTextView() {
        this.harsh_acceleration.setText(this.harshAcc + " times");
        this.over_speed.setText(this.overSpeeding + " times");
        this.harsh_breaking.setText(this.harshBreak + " times");
        this.harsh_cornering.setText(this.harshCorner + " times");
        this.viwe_idle.setText(this.idle + " times");
    }


    private double formulaImplement(double events, Trip trip) {
        if (events > trip.getDistance() / 1000) {
            events = trip.getDistance() / 1000;
        }
        double eco = (100 * events) / (trip.getDistance() / 1000);
        return eco % 20;
    }


    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        SweetAlertDialog eDialog;
        Context activity;
        Date startDate, endDate;

        AsyncTaskRunner(Context context, Date startDate, Date endDate) {
            //        Context[] context;
            WeakReference<Context> activityReference = new WeakReference<>(context);
            activity = activityReference.get();
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
            fetchRouteFromServer(startDate, endDate, eDialog, activity);

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
