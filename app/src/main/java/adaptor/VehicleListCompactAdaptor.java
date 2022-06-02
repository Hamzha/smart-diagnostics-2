package adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import activity.Event;
import activity.Fuel;
import activity.Immobilizer;
import activity.LiveVehicle;
import activity.Maintenance;
import activity.Summary;
import activity.Trip;
import activity.VehicleListCompact;
import de.hdodenhof.circleimageview.CircleImageView;
import listener.RouteToButtonListener;
import model.Device;
import model.DialogBox;
import model.Position;
import util.Common;
import util.CommonConst;
import util.StaticFunction;

public class VehicleListCompactAdaptor extends RecyclerView.Adapter<VehicleListCompactAdaptor.VehicleHolder> {

    @SuppressLint("SimpleDateFormat")
    private
    DateFormat formatter5 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private Map<Long, Device> devicesMap;


    private ArrayList<Position> positions;
    private String TAG = ">>>" + VehicleListCompact.class.getSimpleName();
    private VehicleListCompact context;
    private AlertDialog dialog;

    public VehicleListCompactAdaptor(VehicleListCompact vehicleListCompact, Map<Long, Device> devicesMap, ArrayList<Position> positions) {
        this.context = vehicleListCompact;
        this.positions = positions;
        this.devicesMap = devicesMap;
    }

    public void setDevicesMap(Map<Long, Device> devicesMap) {
        this.devicesMap = devicesMap;
    }

    public void setPositions(ArrayList<Position> positions) {
        this.positions = positions;
    }


    @NonNull
    @Override
    public VehicleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("ResourceType") View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_list_compact_adaptor, parent, false);
        return new VehicleHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VehicleHolder holder, int position) {

        final Position positionTmp = positions.get(position);
        final Device device = devicesMap.get(positionTmp.getDeviceId());
        assert device != null;
        String marker = StaticFunction.markerFile(device, positionTmp);

        try {
            holder.signalPoser.setImageResource(R.drawable.signal);
            if (positionTmp.getPositionAttributes().getRssi() == 0) {
                holder.signalPoser.setColorFilter(ContextCompat.getColor(context, R.color.red_900), android.graphics.PorterDuff.Mode.MULTIPLY);
            } else if (positionTmp.getPositionAttributes().getRssi() >= 1 && positionTmp.getPositionAttributes().getRssi() <= 3) {
                holder.signalPoser.setColorFilter(ContextCompat.getColor(context, R.color.orange_700), android.graphics.PorterDuff.Mode.MULTIPLY);
            } else {
                holder.signalPoser.setColorFilter(ContextCompat.getColor(context, R.color.green_A700), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        } catch (Exception e) {
            Common.logd(TAG, e.toString());
        }

        try {
            holder.satellitePoawer.setImageResource(R.drawable.satillite);
            if (positionTmp.getPositionAttributes().getSat() >= 0 && positionTmp.getPositionAttributes().getSat() <= 4) {
                holder.satellitePoawer.setColorFilter(ContextCompat.getColor(context, R.color.red_900), android.graphics.PorterDuff.Mode.MULTIPLY);
            } else if (positionTmp.getPositionAttributes().getSat() >= 5 && positionTmp.getPositionAttributes().getSat() <= 10) {
                holder.satellitePoawer.setColorFilter(ContextCompat.getColor(context, R.color.orange_700), android.graphics.PorterDuff.Mode.MULTIPLY);
            } else if (positionTmp.getPositionAttributes().getSat() >= 11) {
                holder.satellitePoawer.setColorFilter(ContextCompat.getColor(context, R.color.green_A700), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        } catch (Exception e) {
            Common.logd(TAG, e.toString());
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        long dateTime = 0;
        try {
            dateTime = format.parse(positionTmp.getDeviceTime()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Common.logd(TAG, e.getMessage());
        }
        Date dateTest = new Date(dateTime);

        long date = dateTest.getTime();
        long currentTime = Calendar.getInstance().getTime().getTime();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime - date);

        if (minutes >= 30) {
            holder.markerDialogInfo.setBackground(context.getResources().getDrawable(R.drawable.round_red));
        } else {
            holder.markerDialogInfo.setBackground(context.getResources().getDrawable(R.drawable.round));
        }
        holder.lastUpdate.setText(context.getString(R.string.last_update) + " " + formatter5.format(date));
        try {
            holder.batteryLevel.setText(StaticFunction.round(positionTmp.getPositionAttributes().getPower(), 1) + " V");
        } catch (Exception ex) {
            Common.logd(TAG, ex.getMessage());
        }
        holder.vehicleName.setText(device.getName());
        holder.markerDialogTopImage.setImageResource(context.getResources().getIdentifier(marker, "drawable", context.getPackageName()));

        holder.linearLayout.setOnClickListener(view -> {

            if (dialog == null) {
                dialog = showCustomDialogMain(device, context);
            } else if (!dialog.isShowing()) {
                dialog = showCustomDialogMain(device, context);
            }
        });

        if (device.getAttributes().getFuel() != null && device.getAttributes().getFuel().equals("true"))
            if (device.getAttributes().getFuelLow() != null && device.getAttributes().getFuelMiddle() != null && device.getAttributes().getFuelHigh() != null) {//x^2 * fuel_high + x * fuel_middle + fuel_low
                double fuel = (positionTmp.getPositionAttributes().getFuel() * positionTmp.getPositionAttributes().getFuel() * Double.parseDouble(device.getAttributes().getFuelHigh()))
                        + (positionTmp.getPositionAttributes().getFuel() * Double.parseDouble(device.getAttributes().getFuelMiddle()))
                        + Double.parseDouble(device.getAttributes().getFuelLow());
                Common.logd(TAG,(Math.round(fuel * 100.0) / 100.0) + "L");
                holder.fuel.setText((Math.round(fuel * 100.0) / 100.0) + "L");
            } else
                holder.fuel.setText((Math.round(positionTmp.getPositionAttributes().getFuel() * 100.0) / 100.0) + "L");
    }

    @Override
    public int getItemCount() {
        return positions.size();
    }

    class VehicleHolder extends RecyclerView.ViewHolder {
        LinearLayout markerDialog;
        TextView vehicleName;
        CircleImageView markerDialogTopImage;
        LinearLayout linearLayout;
        ImageView signalPoser;
        ImageView satellitePoawer;
        TextView lastUpdate;
        CardView markerDialogInfo;
        TextView batteryLevel;
        TextView fuel;

        VehicleHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.main_click);
            markerDialog = itemView.findViewById(R.id.main_marker_dialog_info);
            vehicleName = itemView.findViewById(R.id.main_dialog_name);
            markerDialogTopImage = itemView.findViewById(R.id.main_marker_dialog_top_image_info);
            signalPoser = itemView.findViewById(R.id.signal_poser);
            satellitePoawer = itemView.findViewById(R.id.satellite);
            lastUpdate = itemView.findViewById(R.id.last_update);
            markerDialogInfo = itemView.findViewById(R.id.marker_dialog_info);
            batteryLevel = itemView.findViewById(R.id.battery_lvl);
            fuel = itemView.findViewById(R.id.fuel_compact);
        }
    }


    @SuppressLint("ResourceAsColor")
    private AlertDialog showCustomDialogMain(final Device device, Activity activity) {

        Position position = ((SmartTracker) activity.getApplicationContext()).getPositions().get(device.getId());

        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        dialogBuilder = new AlertDialog.Builder(activity);
        @SuppressLint({"InflateParams", "ResourceType"}) View layoutView = activity.getLayoutInflater().inflate(R.layout.options_card_view_compact_list, null);
        dialogBuilder.setView(layoutView);
        dialog = dialogBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        CircleImageView circleImageViewIcon = dialog.findViewById(R.id.marker_dialog_top_image);
        TextView address = dialog.findViewById(R.id.marker_dialog_top_content);

        ((TextView) dialog.findViewById(R.id.marker_dialog_top_title)).setText(device.getName());

        AVLoadingIndicatorView progressBar = dialog.findViewById(R.id.progress_login_dialog);

        AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner(activity, circleImageViewIcon, address, progressBar, position, device);
        asyncTaskRunner.execute();

        dialog.findViewById(R.id.dialog_bt_close).setOnClickListener(v -> dialog.dismiss());

        ((AppCompatButton) dialog.findViewById(R.id.follow_btn)).setText(device.getStatus().toUpperCase());

        int status_color = device.getStatus().toLowerCase().equals("online") ? R.color.green_600 : R.color.red_600;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.findViewById(R.id.follow_btn).setBackgroundTintList(activity.getResources().getColorStateList(status_color));
        } else {
            dialog.findViewById(R.id.follow_btn).setBackgroundColor(status_color);
        }
        dialog.findViewById(R.id.route_to_btn).setOnClickListener(new RouteToButtonListener(activity, position));

        if (device.getAttributes().getFuel() == null || !device.getAttributes().getFuel().contains("true")) {
            dialog.findViewById(R.id.fuel_btn).setVisibility(View.GONE);
        } else
            dialog.findViewById(R.id.fuel_btn).setOnClickListener(view -> {
                if (Common.isInternetConnected(context)) {
                    Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
                } else {
                    StaticFunction.SingleChoiceDialogAndButtonAction(device, activity, Fuel.class);
//                    context.startActivity(new Intent(context, Fuel.class).putExtra("Device", device).putExtra("Name", device.getName()));
                }
            });

        (dialog.findViewById(R.id.trips_btn)).setOnClickListener(view -> {
            if (Common.isInternetConnected(activity)) {
                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
            } else {
                StaticFunction.SingleChoiceDialogAndButtonAction(device, activity, Trip.class);
            }
        });

        dialog.findViewById(R.id.mobilizer_btn).setOnClickListener(view -> {
            if (Common.isInternetConnected(activity)) {
                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
            } else
                activity.startActivity(new Intent(activity, Immobilizer.class).putExtra("Device", device).putExtra("Name", device.getName()));
        });

        dialog.findViewById(R.id.track_it_live_btn).setOnClickListener(view -> {
            if (Common.isInternetConnected(activity)) {
                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
            } else {
                Common.logd(">>>" + TAG, device.toString());
                activity.startActivity(new Intent(activity, LiveVehicle.class)
                        .putExtra("Device", (Parcelable) device)
                        .putExtra("Name", device.getName())
                        .putExtra("Fuel", device.getAttributes().getFuel())
                        .putExtra("obd2", device.getAttributes().getObd2())
                        .putExtra("can", device.getAttributes().getCan()));
            }
        });

        dialog.findViewById(R.id.event_btn).setOnClickListener(view -> {
            if (Common.isInternetConnected(activity)) {
                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
            } else {
                StaticFunction.SingleChoiceDialogAndButtonAction(device, activity, Event.class);
            }
        });

        dialog.findViewById(R.id.summary_btn).setOnClickListener(view -> {
            if (Common.isInternetConnected(activity)) {
                Common.failPopup(activity.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), activity);
            } else {
                StaticFunction.SingleChoiceDialogAndButtonAction(device, activity, Summary.class);
            }
        });

        dialog.findViewById(R.id.maintenance_btn).setOnClickListener(view -> {
            if (Common.isInternetConnected(context)) {
                Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
            } else {
                context.startActivity(new Intent(context, Maintenance.class).putExtra("Device", device).putExtra("Name", device.getName()));
            }
        });

        dialog.show();
        return dialog;
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunner extends AsyncTask<String, String, DialogBox> {
        private Position position;
        Context activity;
        CircleImageView circleImageViewIcon;
        TextView textViewAddress;
        AVLoadingIndicatorView progressBar;
        Device device;

        AsyncTaskRunner(Activity context, CircleImageView circleImageViewIcon, TextView address, AVLoadingIndicatorView progressBar, Position positions, Device device) {
            WeakReference<Activity> activityReference = new WeakReference<>(context);
            activity = activityReference.get();
            this.circleImageViewIcon = circleImageViewIcon;
            this.textViewAddress = address;
            this.progressBar = progressBar;
            this.position = positions;
            this.device = device;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            circleImageViewIcon.setVisibility(View.INVISIBLE);
            textViewAddress.setVisibility(View.INVISIBLE);

        }

        @Override
        protected DialogBox doInBackground(String... strings) {
            position = Common.setAddress(position, activity);
            return new DialogBox(position, StaticFunction.markerFile(device, position));
        }

        @Override
        protected void onPostExecute(DialogBox s) {
            super.onPostExecute(s);
            if (s.getPosition().getAddress(activity) == null)
                textViewAddress.setText(activity.getString(R.string.location_not_available));
            else
                textViewAddress.setText((CharSequence) s.getPosition().getAddress(activity));
            circleImageViewIcon.setImageResource(activity.getResources().getIdentifier(s.getIconName(), "drawable", activity.getPackageName()));

            progressBar.setVisibility(View.GONE);
            circleImageViewIcon.setVisibility(View.VISIBLE);
            textViewAddress.setVisibility(View.VISIBLE);

        }
    }


}
