package adaptor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.ui.IconGenerator;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import callbackinterface.CommonInterface;
import util.Common;
import util.CommonConst;
import util.StaticFunction;
import util.StaticRequest;
import util.URLS;

public class EventAdaptor extends RecyclerView.Adapter<EventAdaptor.EventHolder> {

    private String[][] events;
    private int size;
    public static Context context;
    String TAG = ">>>" + EventAdaptor.class.getSimpleName();

    public EventAdaptor(String[][] events, int size, Context event) {
        this.events = events;
        this.size = size;
        context = event;
    }

    public void setEvents(String[][] events, int size) {
        this.size = size;
        this.events = events;
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_adaptor, parent, false);
        return new EventAdaptor.EventHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
        @SuppressLint("SimpleDateFormat") DateFormat formatter5 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        holder.eventTime.setText(formatter5.format(Long.valueOf(this.events[position][0])));
        if (events[position][1].contains("Idle Enter")) {
            holder.eventType.setText(context.getString(R.string.idle_enter));
        } else if (events[position][1].contains("Idle Exit")) {
            holder.eventType.setText(context.getString(R.string.idle_exit));
        } else if (events[position][1].toLowerCase().equals("ignitionon")) {
            holder.eventType.setText(context.getString(R.string.ignition_on_txt));
        } else if (events[position][1].toLowerCase().equals("ignitionoff")) {
            holder.eventType.setText(context.getString(R.string.ignition_off_txt));
        } else if (events[position][1].toLowerCase().contains("acc")) {
            holder.eventType.setText(context.getString(R.string.harsh_acceleration_txt).replace(":", ""));
        } else if (events[position][1].toLowerCase().contains("brak")) {
            holder.eventType.setText(context.getString(R.string.harsh_braking_txt).replace(":", ""));
        } else if (events[position][1].toLowerCase().contains("corner")) {
            holder.eventType.setText(context.getString(R.string.harsh_cornering_txt).replace(":", ""));
        } else if (events[position][1].toLowerCase().contains("enter") && events[position][1].toLowerCase().contains("geofence")) {
            holder.eventType.setText(context.getString(R.string.geofence_enter));
        } else if (events[position][1].toLowerCase().contains("exit") && events[position][1].toLowerCase().contains("geofence")) {
            holder.eventType.setText(context.getString(R.string.geofence_exit));
        } else if (events[position][1].toLowerCase().contains("powercut")) {
            holder.eventType.setText(context.getString(R.string.power_cut));
        } else if (events[position][1].toLowerCase().contains("tow")) {
            holder.eventType.setText(context.getString(R.string.tow));
        } else if (events[position][1].toLowerCase().contains("speed")) {
            holder.eventType.setText(context.getString(R.string.over_speeding_txt_route));
        } else {
            holder.eventType.setText(events[position][1]);
        }

        holder.eventPosition.setText(events[position][2]);


    }

    @Override
    public int getItemCount() {
        return size;
    }

    static class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eventType;
        TextView eventTime;
        TextView eventPosition;
        private IconGenerator iconGenerator;


        EventHolder(@NonNull View itemView) {
            super(itemView);

            iconGenerator = new IconGenerator(context);
            eventTime = itemView.findViewById(R.id.event_date);
            eventType = itemView.findViewById(R.id.event_status);
            eventPosition = itemView.findViewById(R.id.position_id);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Common.logd(">>>", eventPosition.getText().toString());

            final LatLng[] latLng = {new LatLng(31.5204, 74.3587)};

            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_box_event_map);
            dialog.show();

            MapsInitializer.initialize(context);


            StaticRequest.fetchRouteFromServer(URLS.getPosition(Integer.parseInt(eventPosition.getText().toString())), 15000, context, (response, statusCode) -> {
                if (statusCode == CommonConst.ACTIVITY_VOID) {
                    Gson gson = new GsonBuilder().create();
                    model.Position[] routes = gson.fromJson(response, model.Position[].class);
                    latLng[0] = new LatLng(routes[0].getLatitude(), routes[0].getLongitude());
                    MapView mMapView = dialog.findViewById(R.id.event_map);
                    mMapView.onCreate(dialog.onSaveInstanceState());
                    mMapView.onResume();// needed to get the map to display immediately
                    mMapView.getMapAsync(googleMap1 -> {
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng[0])
                                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon("Event : " + eventType.getText().toString() + "\nTime : " + eventTime.getText().toString() + "\nSpeed : " + routes[0].getSpeed() * 1.857 + " kph")));
                        googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 18));
                        googleMap1.addMarker(markerOptions);
                        googleMap1.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    });
                }
                else{
                    dialog.cancel();
                }
            });
        }
    }


}
