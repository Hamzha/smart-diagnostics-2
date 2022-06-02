package adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import model.Device;
import model.Position;
import util.StaticFunction;

public class DeviceDetailAdaptor extends RecyclerView.Adapter<DeviceDetailAdaptor.DeviceHolder> {
    private Context context;
    private Map<Long, Device> devicesMap;
    private ArrayList<Position> positions;
    private int count = 0;

    public DeviceDetailAdaptor(Map<Long, Device> devices, Map<Long, Position> positions, Context context) {
        this.context = context;
        this.devicesMap = devices;
        this.positions = new ArrayList<>();
        for (Map.Entry<Long, Position> entry : positions.entrySet()) {
            this.positions.add(entry.getValue());
            count++;
        }
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_adaptor, parent, false);
        return new DeviceHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DeviceHolder holder, int i) {
        final Position position = positions.get(i);
        final Device device = devicesMap.get(position.getDeviceId());

        StringBuilder stringBuilder = new StringBuilder();

        assert device != null;
        if (device.getCategory() == null || !device.getCategory().toLowerCase().equals("car"))
            stringBuilder.append("tractor_icon");
        else
            stringBuilder.append("car_icon");

        holder.marker_dialog.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        holder.marker_dialog_top_bar.setVisibility(View.GONE);

        holder.marker_dialog_top_image.setImageResource(context.getResources().getIdentifier(StaticFunction.markerFile(device,position), "drawable", context.getPackageName()));
        holder.marker_dialog_name_info.setText(device.getName());
        if (device.getStatus().equals(""))
            holder.marker_dialog_current_status_info.setText(context.getString(R.string.not_available));
        else
            holder.marker_dialog_current_status_info.setText(device.getStatus().toUpperCase());

        if (device.getCategory() == null || device.getCategory().equals(""))
            holder.marker_dialog_vehicle_type_info.setText(context.getString(R.string.not_available));
        else
            holder.marker_dialog_vehicle_type_info.setText(device.getCategory());

        if (device.getModel().equals(""))
            holder.marker_dialog_vehicle_model_info.setText(context.getString(R.string.not_available));
        else
            holder.marker_dialog_vehicle_model_info.setText(device.getModel());
        if (device.getPhone().equals(""))
            holder.marker_dialog_contact_info.setText(context.getString(R.string.not_available));
        else
            holder.marker_dialog_contact_info.setText(device.getPhone());
    }

    @Override
    public int getItemCount() {
        return count;
    }

    class DeviceHolder extends RecyclerView.ViewHolder {

        CardView marker_dialog;
        LinearLayout marker_dialog_top_bar;
        CircleImageView marker_dialog_top_image;
        TextView marker_dialog_name_info;
        TextView marker_dialog_current_status_info;
        TextView marker_dialog_vehicle_model_info;
        TextView marker_dialog_vehicle_type_info;
        TextView marker_dialog_contact_info;

        DeviceHolder(@NonNull View itemView) {
            super(itemView);
            marker_dialog = itemView.findViewById(R.id.marker_dialog_info);
            marker_dialog_top_bar = itemView.findViewById(R.id.marker_dialog_top_bar_info);
            marker_dialog_top_image = itemView.findViewById(R.id.marker_dialog_top_image_info);
            marker_dialog_current_status_info = itemView.findViewById(R.id.marker_dialog_current_status_info_);
            marker_dialog_name_info = itemView.findViewById(R.id.marker_dialog_name_info);
            marker_dialog_vehicle_type_info = itemView.findViewById(R.id.marker_dialog_vehicle_type_info);
            marker_dialog_vehicle_model_info = itemView.findViewById(R.id.marker_dialog_vehicle_model_info);
            marker_dialog_contact_info = itemView.findViewById(R.id.marker_dialog_contact_info);

        }
    }

}
