package activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import model.Device;
import model.Event;
import model.Trip;
import util.BaseClass;
import util.Common;
import util.CommonConst;
import util.StaticFunction;
import util.StaticRequest;
import util.URLS;

@Deprecated
public class MonthlyReport extends BaseClass {
    private String TAG = ">>>" + MonthlyReport.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);
        Toolbar(this, "Events");
        runInit();
    }

    void runInit() {
        if (Common.isInternetConnected(this)) {
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

    void init() {
        getTrips(this, ((SmartTracker) getApplicationContext()).getDevices());
    }

    @SuppressLint("DefaultLocale")
    public void getTrips(Activity context, Map<Long, Device> device) {
        Date endDate = new Date();

        Date startDate = StaticFunction.calculateDateFromDays(30);
        final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String[] dates = Common.dateConversion(startDate, endDate);


        for (Map.Entry<Long, Device> entry : device.entrySet()) {

            StaticRequest.fetchFromServer(URLS.Trips(dates[0], dates[1], entry.getValue().getId()), context, 15000, (response, statusCode) -> {

                List<Trip> tripList = new ArrayList<>();
                BarChart chart = new BarChart(this);

                List<BarEntry> totalDurationList = new ArrayList<>();
                ArrayList<IBarDataSet> dataDurationSets = new ArrayList<>();

                if (statusCode == CommonConst.SUCCESS_RESPONSE_CODE) {
                    Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();
                    Trip[] trips = gson.fromJson(response, Trip[].class);
                    String tmp = null;

                    if (trips.length != 0) {
                        tmp = trips[0].getStartTime().toString().split(" ")[2];
                    }

                    for (Trip trip : trips) {
                        String day = trip.getStartTime().toString().split(" ")[2];
                        if (day.equals(tmp)) {
                            tripList.add(trip);
                            long time = trip.getStartTime().getTime();
                        } else {

                            double avg = 0;
                            double max = 0;
                            double totalDistance = 0;
                            double totalDuration = 0;
                            for (int i = 0; i < tripList.size(); i++) {
                                Common.logd(TAG, tripList.get(i).getStartTime() + "   " + tripList.get(i).getAverageSpeed() * 1.852 + "inner");
                                avg = avg + (tripList.get(i).getAverageSpeed() * 1.852);
                                max = max + (tripList.get(i).getMaxSpeed() * 1.852);
                                totalDistance = totalDistance + (tripList.get(i).getDistance() / 1000);
                                totalDuration = totalDuration + (tripList.get(i).getDuration());
                            }

                            long minutes = TimeUnit.MILLISECONDS.toMinutes((long) totalDuration);

                            totalDurationList.add(new BarEntry(0, minutes));

                            List<BarEntry> totalTimeList = new ArrayList<>();
                            totalTimeList.add(new BarEntry(0, minutes));
                            BarDataSet set1 = new BarDataSet(totalTimeList, tripList.get(0).getStartTime() + "");
                            set1.setColor(Color.BLUE);
                            dataDurationSets.add(set1);

                            BarData data = new BarData(dataDurationSets);
                            chart.setData(data);

                            chart.getBarData().setBarWidth(0.46f);
                            chart.invalidate();
                            chart.getChartBitmap();

                            tripList = new ArrayList<>();
                            tripList.add(trip);
                            Common.logd(TAG, trip.getStartTime() + trip.getDeviceName() + "change");
                            tmp = day;
                        }
                    }
                    double avg = 0;
                    double max = 0;
                    double totalDistance = 0;
                    double totalDuration = 0;
                    for (int i = 0; i < tripList.size(); i++) {
                        Common.logd(TAG, tripList.get(i).getStartTime() + "inner");
                        avg = avg + (tripList.get(i).getAverageSpeed() * 1.852);
                        max = max + (tripList.get(i).getMaxSpeed() * 1.852);
                        totalDistance = totalDistance + (tripList.get(i).getDistance() / 1000);
                        totalDuration = totalDuration + tripList.get(i).getDuration();
                    }
                    avg = Double.parseDouble(String.format("%.2f", (avg / tripList.size())));
                    max = Double.parseDouble(String.format("%.2f", max / tripList.size()));
                    totalDistance = Double.parseDouble(String.format("%.2f", totalDistance));

                    long minutes = TimeUnit.MILLISECONDS.toMinutes((long) totalDuration);
                    long hours = TimeUnit.MILLISECONDS.toHours((long) totalDuration);
                    minutes = minutes - (hours * 60);

                } else if (statusCode == CommonConst.ACTIVITY_MOVE_BACK) {
                    Common.logd(TAG, "ERROR");
                } else if (statusCode == CommonConst.ACTIVITY_RETRY_CODE) {
                    Common.logd(TAG, "ERROR");
                }
            });
//            StaticRequest.fetchFromServer(URLS.Events(entry.getValue().getId(), dates[0], dates[1]), context, 15000, (response, statusCode) -> {
//                ArrayList<String> stringArrayList = new ArrayList<>();
//
//                Gson gson = new GsonBuilder().setDateFormat(timeFormat).create();
//                final model.Event[] events = gson.fromJson(response, model.Event[].class);
//
//                for (model.Event event : events) {
//                    if (!(event.getType().equals("deviceMoving") || event.getType().equals("deviceStopped")
//                            || event.getType().equals("deviceOffline") || event.getType().equals("deviceOnline"))) {
//                        if (event.getPositionId() != 0) {
//                            stringArrayList.add(String.valueOf(event.getPositionId()));
//                        }
//                    }
//                }
//                StringBuilder ids = new StringBuilder();
//                for (int i = 0; i < stringArrayList.size(); i++) {
//                    if (i == 0)
//                        ids = new StringBuilder("id=" + stringArrayList.get(i));
//                    else
//                        ids.append("&id=").append(stringArrayList.get(i));
//                }
//                StaticRequest.fetchFromServer(URLS.Base(ids), context, 15000, (response1, statusCode1) -> {
//
//                    final Position[] position;
//                    position = gson.fromJson(response1, Position[].class);
//                    for (Position position1 : position) {
//                        for (model.Event event : events) {
//                            if (!(event.getType().equals("deviceMoving") || event.getType().equals("deviceStopped")
//                                    || event.getType().equals("deviceOffline") || event.getType().equals("deviceOnline"))) {
//                                if (event.getPositionId().equals(position1.getId())) {
//                                    event.setServerTime(position1.getDeviceTime());
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                    eventChart(events, position.length);
//                });
//            });
            break;
        }

    }

    private void eventChart(Event[] events, int length) {


        model.Event[] finalEvents = new model.Event[length];
        int k = 0;

        for (model.Event event : events) {
            if (!(event.getType().equals("deviceMoving") || event.getType().equals("deviceStopped") || event.getType().equals("")
                    || event.getType().equals("deviceOffline") || event.getType().equals("deviceOnline") || event.getType().contains("Idle"))) {
                finalEvents[k] = event;
                if (event.getType().equals("alarm")) {
                    finalEvents[k].setType(event.getAttributes().getAlarm());
                }
                if (finalEvents[k].getServerTime().split("\\.").length == 2) {
                    String tmp1 = finalEvents[k].getServerTime().split("\\.")[0];
                    String[] tmp2 = tmp1.split("T");
                    finalEvents[k].setServerTime(tmp2[0] + " " + tmp2[1]);
                }
                k = k + 1;
            }
        }
    }

//    void drawGraph(){
//        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
//        dataSet.setValue(791, "Population", "1750 AD");
//        dataSet.setValue(978, "Population", "1800 AD");
//        dataSet.setValue(1262, "Population", "1850 AD");
//        dataSet.setValue(1650, "Population", "1900 AD");
//        dataSet.setValue(2519, "Population", "1950 AD");
//        dataSet.setValue(6070, "Population", "2000 AD");
//
//        JFreeChart chart = ChartFactory.createBarChart(
//                "World Population growth", "Year", "Population in millions",
//                dataSet, PlotOrientation.VERTICAL, false, true, false);
//
//    }
//    public static void writeChartToPDF(JFreeChart chart, int width, int height, String fileName) {
//        PdfWriter writer = null;
//
//        Document document = new Document();
//
//        try {
//            writer = PdfWriter.getInstance(document, new FileOutputStream(
//                    fileName));
//            document.open();
//            PdfContentByte contentByte = writer.getDirectContent();
//            PdfTemplate template = contentByte.createTemplate(width, height);
//            Graphics2D graphics2d = template.createGraphics(width, height,
//                    new DefaultFontMapper());
//            Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width,
//                    height);
//
//            chart.draw(graphics2d, rectangle2d);
//
//            graphics2d.dispose();
//            contentByte.addTemplate(template, 0, 0);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        document.close();
//    }
}

