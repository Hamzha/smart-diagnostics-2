package adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.NumberFormat;
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
import activity.Summary;
import activity.Trip;
import activity.VehicleListCompact;
import activity.VehicleListCompactNew;
import de.hdodenhof.circleimageview.CircleImageView;
import listener.RouteToButtonListener;
import model.Device;
import model.DialogBox;
import model.Position;
import util.Common;
import util.CommonConst;
import util.StaticFunction;

public class VehicleListCompactAdaptorNew extends RecyclerView.Adapter<VehicleListCompactAdaptorNew.VehicleHolder> {

    @SuppressLint("SimpleDateFormat")
    private final
    DateFormat formatter5 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private Map<Long, Device> devicesMap;
    NumberFormat numberFormat;
    private ArrayList<Position> positions;
    private final String TAG = ">>>" + VehicleListCompact.class.getSimpleName();
    private final VehicleListCompactNew context;

    public VehicleListCompactAdaptorNew(VehicleListCompactNew vehicleListCompact, Map<Long, Device> devicesMap, ArrayList<Position> positions) {
        this.context = vehicleListCompact;
        this.positions = positions;
        this.devicesMap = devicesMap;
        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);

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
        @SuppressLint("ResourceType") View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_list_compact_adaptor_new, parent, false);
        return new VehicleHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VehicleHolder holder, int position) {

        Position positionTmp = positions.get(position);
        final Device device = devicesMap.get(positionTmp.getDeviceId());
        assert device != null;
        String marker = StaticFunction.markerFile(device, positionTmp);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        long dateTime = 0;
        try {
            dateTime = Objects.requireNonNull(format.parse(positionTmp.getDeviceTime())).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Common.logd(TAG, e.getMessage());
        }
        Date dateTest = new Date(dateTime);
        long date = dateTest.getTime();

//        long currentTime = Calendar.getInstance().getTime().getTime();
//        long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime - date);
//        holder.lastUpdate.setText(formatter5.format(date) + "(" + minutes + " minutes ago)");

        holder.lastUpdate.setText(formatter5.format(date));
        String finalText = device.getName() + " Ignition Off " + positionTmp.getAddress(context);

        if (positionTmp.getPositionAttributes().getIgnition() != null && positionTmp.getPositionAttributes().getIgnition())
            finalText = device.getName() + " Ignition On " + positionTmp.getAddress(context);
        if (positionTmp.getSpeed() != null)
            holder.speedNew.setText(numberFormat.format((float) (positionTmp.getSpeed() * 1.852)) + "km/h");

        Spannable wordSpan = new SpannableString(finalText);
        wordSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, device.getName().length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(Color.RED), device.getName().length(), device.getName().length() + 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.text.setText(wordSpan);

        holder.markerDialogTopImage.setImageResource(context.getResources().getIdentifier(marker, "drawable", context.getPackageName()));

        holder.linearLayout.setOnClickListener(view -> {
//            context.startActivity(new Intent(context, OnScreen.class)
//                    .putExtra("Device", device)
//                    .putExtra("Position", positionTmp)
//            );
////
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_box_event_map);
            dialog.show();

            MapsInitializer.initialize(context);


            LatLng latLng = new LatLng(positionTmp.getLatitude(), positionTmp.getLongitude());
            MapView mMapView = dialog.findViewById(R.id.event_map);
            mMapView.onCreate(dialog.onSaveInstanceState());
            mMapView.onResume();
            mMapView.getMapAsync(googleMap1 -> {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng);
                googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                googleMap1.addMarker(markerOptions);
                googleMap1.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            });
        });
    }

    @Override
    public int getItemCount() {
        return positions.size();
    }

    static class VehicleHolder extends RecyclerView.ViewHolder {
        CircleImageView markerDialogTopImage;
        LinearLayout linearLayout;
        TextView text;
        TextView lastUpdate;
        TextView speedNew;

        VehicleHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.main_click_new);
            markerDialogTopImage = itemView.findViewById(R.id.main_marker_dialog_top_image_info_new);
            text = itemView.findViewById(R.id.text);
            lastUpdate = itemView.findViewById(R.id.last_update_new);
            speedNew = itemView.findViewById(R.id.speed_new);
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

        dialog.show();
        return dialog;
    }

    @SuppressLint("StaticFieldLeak")
    private static class AsyncTaskRunner extends AsyncTask<String, String, DialogBox> {
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
