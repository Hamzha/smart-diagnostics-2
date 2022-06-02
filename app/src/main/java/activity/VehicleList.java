package activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import adaptor.VehicleAdaptor;
import cn.pedant.SweetAlert.SweetAlertDialog;
import model.Device;
import model.Position;
import model.VehicleListModel;
import util.BaseClass;
import util.Common;
import util.SharedPrefConst;
import util.SharedPreferenceHelper;
import util.StaticFunction;

public class VehicleList extends BaseClass implements SwipeRefreshLayout.OnRefreshListener {
    String TAG = ">>>" + VehicleList.class.getSimpleName();
    private VehicleAdaptor mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);
        CookieHandler.setDefault(((SmartTracker) getApplication()).getCookieManager());
        context = this;
        langSelector(context);

        Toolbar(context, getString(R.string.vehicle_list));
        init();
    }

    private void init() {
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);
        AsyncTaskRunner asyncTask = new AsyncTaskRunner(context);
        asyncTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void buildRecyclerView(VehicleListModel vehicleListModel) {
        RecyclerView mRecyclerView = findViewById(R.id.vehicle_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mAdapter = new VehicleAdaptor(this, vehicleListModel);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {

            Map<Long, Position> positionsTmp = ((SmartTracker) getApplicationContext()).getPositions();
            Map<Long, Device> devicesMap = ((SmartTracker) getApplicationContext()).getDevices();

            ArrayList<Position> positions = setAddress(positionsTmp, context);
            mAdapter.setDevicesMap(devicesMap);
            mAdapter.setPositions(positions);

            swipeRefreshLayout.setRefreshing(false);
            mAdapter.notifyDataSetChanged();

        }, 8000);
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunner extends AsyncTask<String, String, VehicleListModel> {
        SweetAlertDialog eDialog;
        Context activity;

        AsyncTaskRunner(Context context) {
            WeakReference<Context> activityReference = new WeakReference<>(context);
            activity = activityReference.get();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            eDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
            eDialog.setTitle(getString(R.string.dialog_box_msg));
            eDialog.setCancelable(false);
            eDialog.show();

        }

        @Override
        protected VehicleListModel doInBackground(String... contexts) {
            Map<Long, Position> positionsTmp = ((SmartTracker) activity.getApplicationContext()).getPositions();
            Map<Long, Device> devicesMap = ((SmartTracker) activity.getApplicationContext()).getDevices();
            return new VehicleListModel(setAddress(positionsTmp, activity), devicesMap);
        }

        @Override
        protected void onPostExecute(VehicleListModel vehicleListModel) {
            super.onPostExecute(vehicleListModel);
            buildRecyclerView(vehicleListModel);
            StaticFunction.dismissProgressDialog(eDialog);
        }
    }

    public ArrayList<Position> setAddress(Map<Long, Position> positionMap, Context context) {
        boolean langFlag = SharedPreferenceHelper.getSharedPreferenceBoolean(context, SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.LANG_TYPE, false);
        ArrayList<Position> positionsList = new ArrayList<>();
        Geocoder geocoder;
        if (langFlag) {
            geocoder = new Geocoder(context, new Locale("en"));
        } else {
            geocoder = new Geocoder(context, new Locale("ur"));
        }
        Position position;
        for (Map.Entry<Long, Position> entry : positionMap.entrySet()) {
            position = entry.getValue();
            if (position.getLatitude() < 90 && position.getLatitude() > -90 && position.getLongitude() < 180 && position.getLongitude() > -180) {
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(position.getLatitude(), position.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if ((addresses != null ? addresses.size() : 0) > 0) {
                    Common.logd(TAG, String.valueOf(addresses.get(0).getAddressLine(0)));
                    position.setAddress(addresses.get(0).getAddressLine(0));
                    positionsList.add(position);
                } else {
                    positionsList.add(position);
                }
            }
        }
        return positionsList;
    }


}

