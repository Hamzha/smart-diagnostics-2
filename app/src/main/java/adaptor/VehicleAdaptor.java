package adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import java.util.ArrayList;
import java.util.Map;

import activity.Event;
import activity.Fuel;
import activity.Immobilizer;
import activity.LiveVehicle;
import activity.Maintenance;
import activity.Summary;
import activity.Trip;
import de.hdodenhof.circleimageview.CircleImageView;
import listener.RouteToButtonListener;
import model.Device;
import model.Position;
import model.VehicleListModel;
import util.Common;
import util.CommonConst;
import util.StaticFunction;

public class VehicleAdaptor extends RecyclerView.Adapter<VehicleAdaptor.VehicleHolder> {

    private Map<Long, Device> devicesMap;
    private ArrayList<Position> positions;
    private Activity context;

    public VehicleAdaptor(Activity context, VehicleListModel vehicleListModel) {

        this.context = context;
        this.devicesMap = vehicleListModel.getDevicesMap();
        this.positions = vehicleListModel.getPositions();
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.options_card_view_vehicle_adaptor, parent, false);
        return new VehicleHolder(v);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull VehicleHolder holder, int position) {


        Position positionTmp = positions.get(position);
        final Device device = devicesMap.get(positionTmp.getDeviceId());

        holder.marker_dialog_top_bar.setVisibility(View.GONE);
        holder.marker_dialog_top_image.setImageResource(context.getResources().getIdentifier(StaticFunction.markerFile(device, positionTmp), "drawable", context.getPackageName()));
        holder.marker_dialog_top_title.setText(device.getName());


        if (positionTmp.getAddress(context) == null) {
            holder.marker_dialog_top_content.setText(context.getString(R.string.location_not_available));
        } else {
            holder.marker_dialog_top_content.setText(context.getString(R.string.location) + positionTmp.getAddress(context));
        }
        holder.follow_btn.setClickable(false);
        holder.follow_btn.setText(device.getStatus().toUpperCase());
        int status_color = device.getStatus().toLowerCase().equals("online") ? R.color.green_600 : R.color.red_600;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.follow_btn.setBackgroundTintList(context.getResources().getColorStateList(status_color));
        } else {
            holder.follow_btn.setBackgroundColor(status_color);
        }

        if (device.getAttributes().getFuel() == null || !device.getAttributes().getFuel().equals("true")) {
            holder.fuel_btn.setVisibility(View.GONE);
        } else
            holder.fuel_btn.setOnClickListener(view -> {
                if (Common.isInternetConnected(context)) {
                    Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
                } else {
                    StaticFunction.SingleChoiceDialogAndButtonAction(device, context, Fuel.class);

//                    conext.startActivity(new Intent(context, Fuel.class).putExtra("Device", device).putExtra("Name", device.getName()));
                }

            });

        holder.route_to_btn.setOnClickListener(new RouteToButtonListener(context, positionTmp));
        holder.trips_btn.setOnClickListener(view -> {
            if (Common.isInternetConnected(context)) {
                Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
            } else {
                StaticFunction.SingleChoiceDialogAndButtonAction(device, context, Trip.class);
            }

        });

        holder.summary_btn.setOnClickListener(view -> {
            if (Common.isInternetConnected(context)) {
                Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
            } else {
                StaticFunction.SingleChoiceDialogAndButtonAction(device, context, Summary.class);
            }
        });


        holder.mobilizer_btn.setOnClickListener(view -> {
            if (Common.isInternetConnected(context)) {
                Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);

            } else
                context.startActivity(new Intent(context, Immobilizer.class).putExtra("Device", device).putExtra("Name", device.getName()));
        });

        holder.track_it_live_btn.setOnClickListener(view -> {
            if (Common.isInternetConnected(context)) {
                Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
            } else {
                context.startActivity(new Intent(context, LiveVehicle.class).putExtra("Device", device));

            }
        });

        holder.event_btn.setOnClickListener(view -> {
            if (Common.isInternetConnected(context)) {
                Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
            } else {
                StaticFunction.SingleChoiceDialogAndButtonAction(device, context, Event.class);
            }
        });
        holder.mobilizer_btn.setOnClickListener(view -> {
            if (Common.isInternetConnected(context)) {
                Common.failPopup(context.getString(R.string.no_internet_response), String.valueOf(CommonConst.NO_INTERNET_CODE), context);
            } else {
                context.startActivity(new Intent(context, Maintenance.class).putExtra("Device", device).putExtra("Name", device.getName()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return positions.size();
    }

    static class VehicleHolder extends RecyclerView.ViewHolder {

        CardView marker_dialog;
        LinearLayout marker_dialog_top_bar;
        CircleImageView marker_dialog_top_image;
        TextView marker_dialog_top_title;
        TextView marker_dialog_top_content;
        AppCompatButton follow_btn;
        AppCompatButton route_to_btn;
        AppCompatButton trips_btn;
        AppCompatButton summary_btn;
        AppCompatButton mobilizer_btn;
        AppCompatButton track_it_live_btn;
        AppCompatButton event_btn;
        AppCompatButton fuel_btn;

        VehicleHolder(@NonNull View itemView) {
            super(itemView);
            marker_dialog = itemView.findViewById(R.id.marker_dialog_card_view);
            marker_dialog_top_bar = itemView.findViewById(R.id.marker_dialog_top_bar);
            marker_dialog_top_image = itemView.findViewById(R.id.marker_dialog_top_image);
            marker_dialog_top_title = itemView.findViewById(R.id.marker_dialog_top_title);
            marker_dialog_top_content = itemView.findViewById(R.id.marker_dialog_top_content);
            follow_btn = itemView.findViewById(R.id.follow_btn);
            route_to_btn = itemView.findViewById(R.id.route_to_btn);
            trips_btn = itemView.findViewById(R.id.trips_btn);
            mobilizer_btn = itemView.findViewById(R.id.mobilizer_btn);
            track_it_live_btn = itemView.findViewById(R.id.track_it_live_btn);
            event_btn = itemView.findViewById(R.id.event_btn);
            summary_btn = itemView.findViewById(R.id.summary_btn);
            fuel_btn = itemView.findViewById(R.id.fuel_btn);
        }
    }


}
