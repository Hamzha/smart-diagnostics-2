package activity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adaptor.VehicleListCompactAdaptorNew;
import callbackinterface.CommonInterface;
import model.Device;
import model.Position;
import util.BaseClass;
import util.Common;
import util.NetworkUtils;
import util.URLS;

public class VehicleListCompactNew extends BaseClass implements SwipeRefreshLayout.OnRefreshListener {

    VehicleListCompactAdaptorNew adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Map<Long, Device> devicesMap;
    private ArrayList<Position> positions;
    Context context;
    String TAG = ">>>" + VehicleListCompactNew.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list_compact);
        context = this;
        langSelector(context);
        init();
    }

    private void init() {
        Toolbar(context, getString(R.string.vehicle_list));
        prepList();
        buildRecyclerView();
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void buildRecyclerView(){


//        NetworkUtils networkUtils = new NetworkUtils(context);
//        Common.logd(TAG, URLS.Address(positions.get(0).getLatitude(), positions.get(0).getLongitude()));
//        networkUtils.get(URLS.Address(positions.get(0).getLatitude(), positions.get(0).getLongitude()), 15000, new CommonInterface() {
//            @Override
//            public void commonCallBack(String response, int statusCode) {
//                Common.logd(TAG, "Test Test " + response + " Status:" + statusCode);
//            }
//        });
        Geocoder geocoder = new Geocoder(context);
        for(int i=0;i<positions.size();i++){
            try {

                Address address = geocoder.getFromLocation(positions.get(i).getLatitude(),positions.get(i).getLongitude(),1).get(0);
                String stringBuilder = address.getAddressLine(0);
                positions.get(i).setAddress(stringBuilder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Common.logd(TAG,positions.get(i).toString());

        }

        RecyclerView recyclerView = findViewById(R.id.vehicle_list_compact_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new VehicleListCompactAdaptorNew(this, devicesMap, positions);
        recyclerView.setAdapter(adapter);
    }

    private void prepList() {
        Map<Long, Position> positionsTmp = ((SmartTracker) this.getApplicationContext()).getPositions();
        this.devicesMap = ((SmartTracker) this.getApplicationContext()).getDevices();
        this.positions = new ArrayList<>();
        for (Map.Entry<Long, Position> entry : positionsTmp.entrySet()) {
            this.positions.add(entry.getValue());
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            prepList();
            adapter.setPositions(positions);
            adapter.setDevicesMap(devicesMap);
            swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }, 2000);
    }
}
