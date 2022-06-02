package activity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import java.util.Map;

import adaptor.DeviceDetailAdaptor;
import model.Device;
import model.Position;
import util.BaseClass;
import util.Common;
import util.CommonConst;

@Deprecated
public class VehicleDetail extends BaseClass {

    String TAG = ">>>" + VehicleDetail.class.getSimpleName();
    DeviceDetailAdaptor adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_detail_activity);
        if (Common.isInternetConnected(this)) {
            Common.retryDialog(this, CommonConst.NO_INTERNET_CODE, bool -> {
                if (bool) {
                    init();
                } else {
                    finish();
                }
            });
        } else {
            init();
        }
    }

    private void init() {
        Toolbar(this, getString(R.string.vehicle_details));

        Map<Long, Device> devices = ((SmartTracker) getApplication()).getDevices();
        Map<Long, Position> positions = ((SmartTracker) getApplication()).getPositions();

        RecyclerView recyclerView = findViewById(R.id.detail_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceDetailAdaptor(devices, positions, this);
        recyclerView.setAdapter(adapter);
    }
}
