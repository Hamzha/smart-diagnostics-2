package activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import model.Device;
import model.Position;
import util.BaseClass;
import util.Common;
import util.CommonConst;
import util.NetworkUtils;
import util.StaticFunction;
import util.StaticRequest;
import util.URLS;

public class Immobilizer extends BaseClass {


    Device device;
    List<Pair<String, JSONObject>> customCommands;
    String TAG = ">>>" + Immobilizer.class.getSimpleName();
    int timer = 15000;
    CircleImageView top_image;
    TextView name_info;
    TextView current_status_info;
    TextView vehicle_model_info;
    TextView vehicle_type_info;
    TextView park_lock_info;
    boolean check = true;
    Context context;
//    private boolean mobilizerAvailable;

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
        setContentView(R.layout.activity_immobilizer);
        context = this;
        langSelector(context);

        runInit();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        CookieHandler.setDefault(((SmartTracker) getApplication()).getCookieManager());
        device = getIntent().getParcelableExtra("Device");
        customCommands = new ArrayList<>();
        Toolbar(context, getString(R.string.park_lock)+"/"+device.getName());

        Map<Long, Position> positionList = ((SmartTracker) getApplicationContext()).getPositions();

        park_lock_info = findViewById(R.id.park_lock_info);
        name_info = findViewById(R.id.name_info);
        vehicle_model_info = findViewById(R.id.vehicle_model_info);
        vehicle_type_info = findViewById(R.id.vehicle_type_info);
        top_image = findViewById(R.id.top_image_info);
        current_status_info = findViewById(R.id.current_status_info);
        top_image.setImageResource(this.getResources().getIdentifier(StaticFunction.markerFile(device, Objects.requireNonNull(positionList.get(device.getId()))), "drawable", this.getPackageName()));

        name_info.setText(device.getName());

        if (device.getStatus().equals(""))
            current_status_info.setText(getString(R.string.not_available));
        else {
            current_status_info.setText(device.getStatus().toUpperCase());
            if (device.getStatus().toLowerCase().equals("online")) {
                current_status_info.setText(getString(R.string.online));
            } else {
                current_status_info.setText(R.string.offline);
            }
        }
        if (device.getCategory() == null) {
            vehicle_type_info.setText(getString(R.string.not_available));
        } else if (device.getCategory().equals(""))
            vehicle_type_info.setText(getString(R.string.not_available));
        else {
            vehicle_type_info.setText(device.getCategory());
            if (device.getCategory().toLowerCase().equals("car")) {
                vehicle_type_info.setText(getString(R.string.car));
            } else if (device.getCategory().toLowerCase().equals("tractor")) {
                vehicle_type_info.setText(getString(R.string.tractor));
            } else if (device.getCategory().toLowerCase().equals("crane")) {
                vehicle_type_info.setText(getString(R.string.crane));
            } else if (device.getCategory().toLowerCase().equals("pickup")) {
                vehicle_type_info.setText(getString(R.string.pickup));
            } else if (device.getCategory().toLowerCase().equals("bus")) {
                vehicle_type_info.setText(getString(R.string.bus));
            } else if (device.getCategory().toLowerCase().equals("offroad")) {
                vehicle_type_info.setText(getString(R.string.offroad));
            } else if (device.getCategory().toLowerCase().equals("motorcycle")) {
                vehicle_type_info.setText(getString(R.string.motorcycle));
            } else if (device.getCategory().toLowerCase().equals("boat")) {
                vehicle_type_info.setText(getString(R.string.tubewell));
            } else if (device.getCategory().toLowerCase().equals("truck")) {
                vehicle_type_info.setText(getString(R.string.truck));
            }
        }

        if (device.getModel().equals(""))
            vehicle_model_info.setText(getString(R.string.not_available));
        else
            vehicle_model_info.setText(device.getModel());
        if (!Objects.requireNonNull(positionList.get(device.getId())).getPositionAttributes().getOut1()) {
            park_lock_info.setText(getString(R.string.park_lock_unlock));
            park_lock_info.setBackground(getResources().getDrawable(R.color.green_700));
        } else {
            park_lock_info.setText(getString(R.string.park_lock_locked));
            park_lock_info.setBackground(getResources().getDrawable(R.color.red_700));
        }
        getCommands(device.getId(),context);
    }

    private void getCommands(long deviceId, Context context) {
        NetworkUtils networkUtils = new NetworkUtils(context);
        final String URL = URLS.Command(deviceId);
        networkUtils.get(URL, timer, (response, statusCode) -> {
            if (Common.responseCheck(statusCode, context)) {
                Common.retryDialog(context, statusCode, bool -> {
                    if (bool) {
                        timer = timer + 5000;
                        init();
                    } else {
                        finish();
                    }
                });
            } else {

                if (response.length() < 5) {
                    final SweetAlertDialog eDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                    eDialog.setTitle(getString(R.string.not_available));
                    eDialog.show();
                    check = false;
                } else {
                    check = true;
                    JsonArray jsonArray = new JsonParser().parse(response).getAsJsonArray();
                    for (JsonElement jsonObject : jsonArray) {     //Splitting response to JSONArray and treverse each object
                        String jsnObj = jsonObject.toString();
                        try {
                            if (jsnObj.toLowerCase().contains("engine on")) {        //Json object contains ENGINE ON
                                JSONObject jsn = new JSONObject(jsnObj);
                                Common.JSONReplace(jsn, "deviceId", jsn.getString("deviceId"), Long.toString(deviceId));
                                customCommands.add(new Pair<>("on", jsn));
                            } else if (jsnObj.toLowerCase().contains("engine off")) {  //Json object contains ENGINE ON
                                JSONObject jsn = new JSONObject(jsnObj);
                                Common.JSONReplace(jsn, "deviceId", jsn.getString("deviceId"), Long.toString(deviceId));
                                customCommands.add(new Pair<>("off", jsn));
                            }
                        } catch (Exception e) {
                            Common.failPopup(getString(R.string.late_response), String.valueOf(CommonConst.LATE_RESPONSE_CODE), context);
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


    }

    public void lockButtonClickAction(View view) {
        if (check)
            StaticRequest.immobilizerButtonAction(Immobilizer.this, customCommands, device.getId(), "on", timer, this, park_lock_info);
        else {
            final SweetAlertDialog eDialog = new SweetAlertDialog(Immobilizer.this, SweetAlertDialog.ERROR_TYPE);
            eDialog.setTitle(getString(R.string.no_immobilizer_response));
            eDialog.show();
        }
    }

    public void unlockButtonClickAction(View view) {
        if (check)
            StaticRequest.immobilizerButtonAction(Immobilizer.this, customCommands, device.getId(), "off", timer, this, park_lock_info);
        else {
            final SweetAlertDialog eDialog = new SweetAlertDialog(Immobilizer.this, SweetAlertDialog.ERROR_TYPE);
            eDialog.setTitle(getString(R.string.no_immobilizer_response));
            eDialog.show();
        }
    }

}
