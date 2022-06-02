package adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.agriculture.solutions.vechicle.vehicletracker.R;
import com.smart.agriculture.solutions.vechicle.vehicletracker.SmartTracker;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import activity.VehicleListSummary;
import de.hdodenhof.circleimageview.CircleImageView;
import model.Device;
import model.Position;
import util.StaticFunction;

public class VehicleSummaryAdaptor extends RecyclerView.Adapter<VehicleSummaryAdaptor.VehicleHolder> {
    private final Map<Long, Double> maxSpeed;
    private final Map<Long, Double> avgSpeed;
    private final Map<Long, Double> dusration;
    private final Map<Long, Integer> totalTrips;
    private final Map<Long, Double> distance;
    private Context context;
    private ArrayList<Position> positions;
    private int count = 0;
    private Map<Long, Device> devicesMap;
    private String TAG = ">>>" + VehicleListSummary.class.getCanonicalName();

    public VehicleSummaryAdaptor(VehicleListSummary vehicleListCompact) {
        this.context = vehicleListCompact;
        Map<Long, Position> positionsTmp = ((SmartTracker) context.getApplicationContext()).getPositions();
        this.positions = new ArrayList<>();
        for (Map.Entry<Long, Position> entry : positionsTmp.entrySet()) {
            this.positions.add(entry.getValue());
            count++;
        }
        this.devicesMap = ((SmartTracker) context.getApplicationContext()).getDevices();

        maxSpeed = ((SmartTracker) context.getApplicationContext()).getMaxSpeed();
        avgSpeed = ((SmartTracker) context.getApplicationContext()).getTotalAvgSpeed();
        dusration = ((SmartTracker) context.getApplicationContext()).getDuration();
        distance = ((SmartTracker) context.getApplicationContext()).getTotalDistance();
        totalTrips = ((SmartTracker) context.getApplicationContext()).getTotalTrips();

    }

    @NonNull
    @Override
    public VehicleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vechicle_summary_adaptor, parent, false);
        return new VehicleHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VehicleHolder holder, int position) {
        final Position positionTmp = positions.get(position);
        final Device device = devicesMap.get(positionTmp.getDeviceId());
        assert device != null;
        holder.markerDialogTopImage.setImageResource(context.getResources().getIdentifier(StaticFunction.markerFile(device, positionTmp), "drawable", context.getPackageName()));
        holder.vehicleName.setText(device.getName());
//
        if (positionTmp.getAddress(context) == null)
            holder.markerDialogTopContent.setText(context.getString(R.string.location_not_available));
        else
            holder.markerDialogTopContent.setText(context.getString(R.string.location) + positionTmp.getAddress(context));

        holder.follow_btn.setClickable(false);
        holder.follow_btn.setText(device.getStatus().toUpperCase());

        if (dusration.get(positionTmp.getDeviceId()) != null) {
            long minutes = Objects.requireNonNull(dusration.get(positionTmp.getDeviceId())).longValue();
            long hours = minutes / 60;
            minutes = minutes - (hours * 60);
            holder.duration.setText(hours + " " + context.getString(R.string.hours) + "," + minutes + context.getString(R.string.minutes));
        }

        holder.totalTrips.setText(totalTrips.get(positionTmp.getDeviceId()) + "");
        holder.maxSpeed.setText(maxSpeed.get(positionTmp.getDeviceId()) + " KPH");
        holder.avgSpeed.setText(avgSpeed.get(positionTmp.getDeviceId()) + " KPH");
        holder.distance.setText(distance.get(positionTmp.getDeviceId()) + " KM");

    }

    @Override
    public int getItemCount() {
        return count;
    }

    class VehicleHolder extends RecyclerView.ViewHolder {

        CardView linearLayout;
        TextView vehicleName;
        CircleImageView markerDialogTopImage;
        TextView markerDialogTopContent;
        AppCompatButton follow_btn;
        TextView avgSpeed;
        TextView maxSpeed;
        TextView duration;
        TextView totalTrips;
        TextView distance;

        VehicleHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.vehicle_summary_adaptor_view);
            vehicleName = itemView.findViewById(R.id.marker_dialog_top_title_summary);
            markerDialogTopImage = itemView.findViewById(R.id.marker_dialog_top_image_summary);
            markerDialogTopContent = itemView.findViewById(R.id.marker_dialog_top_content_summary);
            follow_btn = itemView.findViewById(R.id.follow_btn_summary);
            avgSpeed = itemView.findViewById(R.id.total_avg_speed_summary);
            maxSpeed = itemView.findViewById(R.id.max_speed_summary);
            duration = itemView.findViewById(R.id.total_duration_summary);
            totalTrips = itemView.findViewById(R.id.total_trip_summary);
            distance = itemView.findViewById(R.id.total_distance_summary);
        }
    }
}
