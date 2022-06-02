package adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import activity.Eco;
import activity.Idle;
import activity.Route;
import activity.RoutePower;
import activity.Speed;
import model.Device;
import util.Common;
import util.CommonConst;

public class TripAdaptor extends RecyclerView.Adapter<TripAdaptor.holder> {

    private final ArrayList<model.Trip> tripList;
    private final Map<Long, Device> devices;
    private Context context;

    public TripAdaptor(ArrayList<model.Trip> tripList, Map<Long, Device> devices, Context trip) {
        this.tripList = tripList;
        this.devices = devices;
        this.context = trip;
    }

    @NonNull
    @Override
    public TripAdaptor.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("ResourceType") View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item_card_view, parent, false);
        return new holder(v);
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        final model.Trip trip = tripList.get(position);

        Device deviceTmp = devices.get((long) trip.getDeviceId());

        holder.device_name.setText(trip.getDeviceName());
        if ((deviceTmp != null ? deviceTmp.getCategory() : null) == null) {
            holder.record_date.setText(Objects.requireNonNull(""));
        } else
            holder.record_date.setText(Objects.requireNonNull(deviceTmp.getCategory().toUpperCase()));
        String dateFormat = "MMM, dd, yyyy";
        holder.trip_start_time.setText(new SimpleDateFormat(dateFormat).format(trip.getStartTime()));
        String timeFormat = "HH:mm";
        holder.trip_start_date.setText(new SimpleDateFormat(timeFormat).format(trip.getStartTime()));
        holder.trip_end_time.setText(new SimpleDateFormat(dateFormat).format(trip.getEndTime()));
        holder.trip_end_date.setText(new SimpleDateFormat(timeFormat).format(trip.getEndTime()));
        holder.ignitions_start_address.setText((CharSequence) trip.getStartAddress());
        holder.ignition_stop_address.setText((CharSequence) trip.getEndAddress());
        holder.list_count.setText(String.valueOf(position + 1));

        long minutes = TimeUnit.MILLISECONDS.toMinutes(trip.getDuration());
        long hours = TimeUnit.MILLISECONDS.toHours(trip.getDuration());
        minutes = minutes - (hours * 60);

        holder.duration.setText(hours + " " + context.getString(R.string.hours) + "," + minutes + " " + context.getString(R.string.minutes));
        if (trip.getDistance() < 0) {
            trip.setDistance(-(trip.getDistance()));
        }
        holder.est_distance.setText(String.format("%.2f " + context.getString(R.string.kilometer), (trip.getDistance() / 1000)));   //Converting Meters to KM
        holder.avg_speed.setText(String.format("%.2f " + context.getString(R.string.KM_per_hour), (trip.getAverageSpeed() * 1.852)));
        holder.top_speed.setText(String.format("%.2f " + context.getString(R.string.KM_per_hour), (trip.getMaxSpeed() * 1.852)));

        holder.view_on_map_btn.setOnClickListener(view -> {
            if (Common.isInternetConnected(context)) {
                Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
            } else {
                context.startActivity(new Intent(context, Route.class)

                        .putExtra("TripStartTime", trip.getStartTime())
                        .putExtra("TripEndTime", trip.getEndTime())
                        .putExtra("Device", deviceTmp)
                        .putExtra("Trip", trip)
                        .putExtra("avgSpeed", holder.avg_speed.getText())
                        .putExtra("topSpeed", holder.top_speed.getText())
                        .putExtra("distance", holder.est_distance.getText())
                        .putExtra("duration", holder.duration.getText())
                );
            }
        });
        holder.speed_graph_btn.setOnClickListener(view -> {
            if (Common.isInternetConnected(context)) {
                Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
            } else {
                context.startActivity(new Intent(context, Speed.class)
                        .putExtra("TripStartTime", trip.getStartTime())
                        .putExtra("TripEndTime", trip.getEndTime())
                        .putExtra("Device", deviceTmp)
                );
            }
        });
        holder.view_trip_detail_btn.setOnClickListener(view -> {
            if (Common.isInternetConnected(context)) {
                Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
            } else if (deviceTmp.getAttributes().getAccelerometer() != null && deviceTmp.getAttributes().getAccelerometer().toLowerCase().contains("true"))
                context.startActivity(new Intent(context, Eco.class)
                        .putExtra("TripStartTime", trip.getStartTime())
                        .putExtra("TripEndTime", trip.getEndTime())
                        .putExtra("Device", deviceTmp)
                        .putExtra("Trip", trip)
                );
            else {
                Common.failPopup("Please Upgrade your device", "00", context);
            }
        });
        holder.idle_graph_btn.setOnClickListener(view -> {

            if (Common.isInternetConnected(context)) {
                Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
            } else if (deviceTmp.getAttributes().getAccelerometer() != null && deviceTmp.getAttributes().getAccelerometer().toLowerCase().contains("true"))
                context.startActivity(new Intent(context, Idle.class)
                        .putExtra("TripStartTime", trip.getStartTime())
                        .putExtra("TripEndTime", trip.getEndTime())
                        .putExtra("Device", deviceTmp));
            else
                Common.failPopup(context.getString(R.string.update_your_device_trip), "00", context);
        });

        holder.View_on_map_power_btn.setOnClickListener(view ->
        {

            if (Common.isInternetConnected(context)) {
                Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
            } else if (deviceTmp.getAttributes().getAccelerometer() != null && deviceTmp.getAttributes().getAccelerometer().toLowerCase().contains("true"))
                context.startActivity(new Intent(context, RoutePower.class)
                        .putExtra("TripStartTime", trip.getStartTime())
                        .putExtra("TripEndTime", trip.getEndTime())
                        .putExtra("Device", deviceTmp)
                        .putExtra("Trip", trip)
                        .putExtra("avgSpeed", holder.avg_speed.getText())
                        .putExtra("topSpeed", holder.top_speed.getText())
                        .putExtra("distance", holder.est_distance.getText())
                        .putExtra("duration", holder.duration.getText()));
            else
                Common.failPopup("Please upgrade your device", "00", context);
        });

    }

    @Override
    public int getItemCount() {
        return this.tripList.size();
    }

    static class holder extends RecyclerView.ViewHolder {
        TextView device_name;
        TextView record_date;
        TextView trip_start_time;
        TextView trip_start_date;
        TextView trip_end_time;
        TextView trip_end_date;
        TextView ignitions_start_address;
        TextView ignition_stop_address;
        TextView duration;
        TextView est_distance;
        TextView list_count;
        ImageButton view_on_map_btn;
        ImageButton speed_graph_btn;
        ImageButton View_on_map_power_btn;
        ImageButton idle_graph_btn;
        TextView avg_speed;
        TextView top_speed;
        ImageButton view_trip_detail_btn;

        @SuppressLint("CutPasteId")
        holder(@NonNull View itemView) {
            super(itemView);
            device_name = itemView.findViewById(R.id.device_name);
            record_date = itemView.findViewById(R.id.record_date);
            trip_start_time = itemView.findViewById(R.id.trip_start_time);
            trip_start_date = itemView.findViewById(R.id.trip_start_date);
            trip_end_time = itemView.findViewById(R.id.trip_end_time);
            trip_end_date = itemView.findViewById(R.id.trip_end_date);
            ignitions_start_address = itemView.findViewById(R.id.ignition_start_address);
            ignition_stop_address = itemView.findViewById(R.id.ignition_stop_address);
            duration = itemView.findViewById(R.id.duration_time);
            est_distance = itemView.findViewById(R.id.estimate_distance);
            list_count = itemView.findViewById(R.id.list_count);
            view_on_map_btn = itemView.findViewById(R.id.view_on_map_btn);
            speed_graph_btn = itemView.findViewById(R.id.speed_graph_btn);
            avg_speed = itemView.findViewById(R.id.avg_speed);
            top_speed = itemView.findViewById(R.id.top_speed);
            idle_graph_btn = itemView.findViewById(R.id.view_idle_graph_btn);
            view_trip_detail_btn = itemView.findViewById(R.id.view_trip_detail_btn);
            View_on_map_power_btn = itemView.findViewById(R.id.view_on_map_power_btn);

        }
    }
}
