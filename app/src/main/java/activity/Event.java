package activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import adaptor.EventAdaptor;
import model.Device;
import model.Position;
import util.BaseClass;
import util.Common;
import util.CommonConst;
import util.NetworkUtils;
import util.URLS;

public class Event extends BaseClass {

    String TAG = "mainData[i][0]" + Event.class.getSimpleName();
    private Device device;
    private Date[] startDate = {null};
    private Date[] endDate = {null};
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private RecyclerView.LayoutManager mLayoutManager;
    private int timer = 15000;
    private ArrayList<String> stringArrayList;
    private String[][] mainData;
    private EventAdaptor mAdapter;
    Context context;
    int size;
    ArrayList<model.Event> eventArrayList;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        context = this;
        langSelector(context);

        runInit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {
                searchItem.collapseActionView();

                String[][] tmp = new String[size][2];
                int k = 0;
                for (int i = 0; i < size; i++) {
                    if (mainData[i][1].toLowerCase().contains(query.toLowerCase())) {
                        tmp[k][0] = mainData[i][0];
                        tmp[k][1] = mainData[i][1];
                        k += 1;
                    }
                }
                mAdapter.setEvents(tmp, k);
                mAdapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }


    private void init() {
        stringArrayList = new ArrayList<>();
        device = getIntent().getParcelableExtra("Device");
        startDate[0] = (Date) getIntent().getSerializableExtra("StartDate");
        endDate[0] = (Date) getIntent().getSerializableExtra("EndDate");
        eventArrayList = new ArrayList<>();
        Toolbar(this, getString(R.string.event) + "/" + device.getName());

        avLoadingIndicatorView = findViewById(R.id.progress_events);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);

        mLayoutManager = new LinearLayoutManager(context);

        initFetch(startDate[0], endDate[0], context);
        setDateEventHeader(startDate[0], endDate[0]);
    }

    protected void initFetch(Date startDate, Date endDate, Context context) {
        fetchTripsFromServer(startDate, endDate, context);
    }

    @SuppressLint("DefaultLocale")
    private void fetchTripsFromServer(final Date startdate, Date enddate, Context context) {
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
                    final model.Event[] events = gson.fromJson(response, model.Event[].class);

                    for (model.Event event : events) {
                        if (!(event.getType().equals("deviceMoving") || event.getType().equals("deviceStopped")
                                || event.getType().equals("deviceOffline") || event.getType().equals("deviceOnline"))) {
                            eventArrayList.add(event);
                            if (event.getPositionId() != 0) {
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
                                    for (int i = 0; i < eventArrayList.size(); i++) {
                                        if (eventArrayList.get(i).getPositionId() == (position1.getId())) {
                                            eventArrayList.get(i).setServerTime(position1.getDeviceTime());
                                            eventArrayList.get(i).getAttributes().setIo251(position1.getPositionAttributes().getIo251());
                                            break;

                                        }
                                        if (eventArrayList.get(i).getServerTime().split("\\.").length == 2) {
                                            String tmp1 = eventArrayList.get(i).getServerTime().split("\\.")[0];
                                            String[] tmp2 = tmp1.split("T");
                                            eventArrayList.get(i).setServerTime(tmp2[0] + " " + tmp2[1]);
                                        }
                                        if (eventArrayList.get(i).getType().equals("alarm")) {
                                            eventArrayList.get(i).setType(eventArrayList.get(i).getAttributes().getAlarm());
                                        }
                                    }
                                }

                                createEventAdaptor(eventArrayList, position.length-1, context);
                            }
                        } catch (Exception ex) {
                            Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
                        }
                    });
                } catch (Exception ex) {
                    Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
                }
            }
        });
    }


    @SuppressLint("SimpleDateFormat")
    protected void setDateEventHeader(Date fromDate, Date toDate) {
        ((TextView) findViewById(R.id.event_start_date)).setText(new SimpleDateFormat(CommonConst.dataFormatWithMonth).format(fromDate));
        ((TextView) findViewById(R.id.event_end_date)).setText(new SimpleDateFormat(CommonConst.dataFormatWithMonth).format(toDate));
    }

    private void createEventAdaptor(ArrayList<model.Event> events, int size, Context context) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getServerTime().split("\\.").length == 2) {
                String tmp1 = events.get(i).getServerTime().split("\\.")[0];
                String[] tmp2 = tmp1.split("T");
                events.get(i).setServerTime(tmp2[0] + " " + tmp2[1]);
            }
            Common.logd(TAG, "createEventAdaptor -> " +eventArrayList.get(i).getServerTime());
        }
        this.mainData = new String[events.size()][3];
        boolean chk = false;
        for (int i = 0; i < events.size(); i++) {

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            long dateTime = 0;
            try {
                dateTime = Objects.requireNonNull(format.parse(eventArrayList.get(i).getServerTime())).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date dateTest = new Date(dateTime);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTest);
            calendar.add(Calendar.HOUR, 5);

            this.mainData[i][1] = eventArrayList.get(i).getType();
            this.mainData[i][0] = String.valueOf(calendar.getTime().getTime());
            Common.logd(TAG,"dateTest  "+ String.valueOf(dateTest.getTime()));
            Common.logd(TAG, "calendar  "+String.valueOf(calendar.getTime().getTime()));
            this.mainData[i][2] = String.valueOf(eventArrayList.get(i).getPositionId());
            if (this.mainData[i][1].equals("idle") && !chk) {
                this.mainData[i][1] = getString(R.string.idle_enter);
                chk = true;
            } else if (this.mainData[i][1].equals("idle") && chk) {
                this.mainData[i][1] = getString(R.string.idle_exit);
                chk = false;
            }
        }

        Arrays.sort(this.mainData, (first, second) -> {
            // here you should usually check that first and second
            // a) are not null and b) have at least two items
            // updated after comments: comparing Double, not Strings
            // makes more sense, thanks Bart Kiers
            return Double.valueOf(second[0]).compareTo(
                    Double.valueOf(first[0])
            );
        });
//
//        for (int i = 0; i < events.size(); i++) {
//            Common.logd(TAG, mainData[i][0] + "-" + mainData[i][1]);
//        }

        if (events.size() == 0) {
            findViewById(R.id.no_events).setVisibility(View.VISIBLE);
        }

        avLoadingIndicatorView.setVisibility(View.INVISIBLE);

        RecyclerView mRecyclerView = findViewById(R.id.events_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        this.mAdapter = new EventAdaptor(this.mainData, events.size(), context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(this.mAdapter);
    }
}
