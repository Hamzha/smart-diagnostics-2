package activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import model.Device;
import model.Position;
import util.BaseClass;
import util.Common;
import util.CommonConst;
import util.StaticFunction;
import util.StaticRequest;
import util.URLS;

public class Extra extends BaseClass {
    int timer = 15000;
    private Device device;
    private List<Entry> dataSet;
    private SweetAlertDialog pDialog;
    private LineChart lineChart;
    Context context;
    String TAG = ">>>"+Extra.class.getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);
        context = this;
        langSelector(context);
        Toolbar(context, getString(R.string.speed_graph));
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

        Date startDate = (Date) getIntent().getSerializableExtra("TripStartTime");
        Date endDate = (Date) getIntent().getSerializableExtra("TripEndTime");
        device = getIntent().getParcelableExtra("Device");

        dataSet = new ArrayList<>();
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitle(getString(R.string.dialog_box_title));
        pDialog.setContentText(getString(R.string.dialog_box_msg));

        fetchRouteFromServer(startDate, endDate);
    }

    private void fetchRouteFromServer(Date startDate, Date endDate) {
        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String[] date = Common.dateConversion(startDate, endDate);
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
                    Position[] routes = gson.fromJson(response, Position[].class);
                    Common.logd(TAG,date[0]+ "  "+date[1]);
                    Common.logd(TAG,response);
                    for(int i=0;i<routes.length;i++){
                        Common.logd(TAG,routes[i].toString());
                    }

                    initLineChart();
                    setDataSet(routes);



                } catch (Exception ex) {
                    Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
                }
            }
        });
    }

    private void setDataSet(Position[] routes) {

        int count = 0;
        for (Position route : routes) {
            dataSet.add(new Entry(++count, (float) (route.getSpeed() * 1.852)));
        }


        LineDataSet set1 = new LineDataSet(dataSet, getString(R.string.speed_in_kph));

        set1.setDrawIcons(false);

        // draw dashed line
        set1.enableDashedLine(10f, 5f, 0f);

        // black lines and points
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);

        // line thickness and point size
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);

        // draw points as solid circles
        set1.setDrawCircleHole(false);

        // customize legend entry
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);

        // text size of values
        set1.setValueTextSize(9f);

        // draw selection line as dashed
        set1.enableDashedHighlightLine(10f, 5f, 0f);

        // set the filled area
        set1.setDrawFilled(true);
        set1.setFillFormatter((dataSet, dataProvider) -> lineChart.getAxisLeft().getAxisMinimum());

        // set color of filled area
        if (Utils.getSDKInt() >= 18) {
            // drawables only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade_red);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Color.BLACK);
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        // create a data object with the data sets
        LineData data = new LineData(dataSets);

        lineChart.setData(data);
        lineChart.getAxisLeft().mAxisMinimum = -10f;
        lineChart.animateX(3000);
        lineChart.animateY(3000);

        StaticFunction.dismissProgressDialog(pDialog);
        lineChart.animateXY(2000, 2000);
    }

    private void initLineChart() {
        lineChart = findViewById(R.id.graph_chart);

        lineChart.setBackgroundColor(Color.WHITE);

        lineChart.getDescription().setEnabled(false);

        lineChart.setTouchEnabled(true);

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
        lineChart.setDrawGridBackground(false);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = lineChart.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);
            xAxis.setDrawLabels(false);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = lineChart.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            lineChart.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

            // axis range
            yAxis.setAxisMaximum(150f);
            yAxis.setAxisMinimum(-50f);
        }
        LimitLine ll1;
        if (device.getCategory() == null ||device.getCategory().equals(CommonConst.TRACTOR))
            ll1 = new LimitLine(35f, getString(R.string.Km_H_35));
        else
            ll1 = new LimitLine(70f, getString(R.string.Km_H_70));

        ll1.setLineWidth(4f);
        ll1.setLineColor(R.color.blue_700);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        yAxis.setDrawLimitLinesBehindData(true);
        xAxis.setDrawLimitLinesBehindData(true);

        // add limit lines
        yAxis.addLimitLine(ll1);

    }
}
