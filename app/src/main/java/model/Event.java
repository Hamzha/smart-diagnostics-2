package model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event implements Parcelable {

    public final static Parcelable.Creator<Event> CREATOR = new Creator<Event>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return (new Event[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("attributes")
    @Expose
    private EventAttributes attributes;
    @SerializedName("deviceId")
    @Expose
    private Integer deviceId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("serverTime")
    @Expose
    private String serverTime;
    @SerializedName("positionId")
    @Expose
    private Integer positionId;
    @SerializedName("geofenceId")
    @Expose
    private Integer geofenceId;
    @SerializedName("maintenanceId")
    @Expose
    private Integer maintenanceId;

    protected Event(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.attributes = ((EventAttributes) in.readValue((EventAttributes.class.getClassLoader())));
        this.deviceId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.serverTime = ((String) in.readValue((String.class.getClassLoader())));
        this.positionId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.geofenceId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.maintenanceId = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public Event() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EventAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(EventAttributes attributes) {
        this.attributes = attributes;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public Integer getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(Integer geofenceId) {
        this.geofenceId = geofenceId;
    }

    public Integer getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(Integer maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(attributes);
        dest.writeValue(deviceId);
        dest.writeValue(type);
        dest.writeValue(serverTime);
        dest.writeValue(positionId);
        dest.writeValue(geofenceId);
        dest.writeValue(maintenanceId);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", attributes=" + attributes +
                ", deviceId=" + deviceId +
                ", type='" + type + '\'' +
                ", serverTime='" + serverTime + '\'' +
                ", positionId=" + positionId +
                ", geofenceId=" + geofenceId +
                ", maintenanceId=" + maintenanceId +
                '}';
    }
}