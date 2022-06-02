package activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import java.util.ArrayList;
import java.util.Map;

import adaptor.VehicleListCompactAdaptor;
import model.Device;
import model.Position;
import util.BaseClass;

public class VehicleListCompact extends BaseClass implements SwipeRefreshLayout.OnRefreshListener {

    VehicleListCompactAdaptor adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Map<Long, Device> devicesMap;
    private ArrayList<Position> positions;
    Context context;
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

    private void buildRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.vehicle_list_compact_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new VehicleListCompactAdaptor(this,devicesMap,positions);
        recyclerView.setAdapter(adapter);

    }

    private void prepList(){
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
