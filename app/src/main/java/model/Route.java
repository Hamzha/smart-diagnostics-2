package model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.jar.Attributes;

public class Route implements Parcelable {

    public final static Parcelable.Creator<Route> CREATOR = new Creator<Route>() {


        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return (new Route[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("attributes")
    @Expose
    private RouteAttributes attributes;
    @SerializedName("deviceId")
    @Expose
    private Integer deviceId;
    @SerializedName("type")
    @Expose
    private Object type;
    @SerializedName("protocol")
    @Expose
    private String protocol;
    @SerializedName("serverTime")
    @Expose
    private String serverTime;
    @SerializedName("deviceTime")
    @Expose
    private String deviceTime;
    @SerializedName("fixTime")
    @Expose
    private String fixTime;
    @SerializedName("outdated")
    @Expose
    private Boolean outdated;
    @SerializedName("valid")
    @Expose
    private Boolean valid;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("altitude")
    @Expose
    private Integer altitude;
    @SerializedName("speed")
    @Expose
    private Integer speed;
    @SerializedName("course")
    @Expose
    private Integer course;
    @SerializedName("address")
    @Expose
    private Object address;
    @SerializedName("accuracy")
    @Expose
    private Integer accuracy;
    @SerializedName("network")
    @Expose
    private Object network;

    private Route(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.attributes = ((RouteAttributes) in.readValue((Attributes.class.getClassLoader())));
        this.deviceId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.type = ((Object) in.readValue((Object.class.getClassLoader())));
        this.protocol = ((String) in.readValue((String.class.getClassLoader())));
        this.serverTime = ((String) in.readValue((String.class.getClassLoader())));
        this.deviceTime = ((String) in.readValue((String.class.getClassLoader())));
        this.fixTime = ((String) in.readValue((String.class.getClassLoader())));
        this.outdated = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.valid = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.latitude = ((Double) in.readValue((Double.class.getClassLoader())));
        this.longitude = ((Double) in.readValue((Double.class.getClassLoader())));
        this.altitude = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.speed = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.course = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.address = ((Object) in.readValue((Object.class.getClassLoader())));
        this.accuracy = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.network = ((Object) in.readValue((Object.class.getClassLoader())));
    }

    public Route() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RouteAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(RouteAttributes attributes) {
        this.attributes = attributes;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getDeviceTime() {
        return deviceTime;
    }

    public void setDeviceTime(String deviceTime) {
        this.deviceTime = deviceTime;
    }

    public String getFixTime() {
        return fixTime;
    }

    public void setFixTime(String fixTime) {
        this.fixTime = fixTime;
    }

    public Boolean getOutdated() {
        return outdated;
    }

    public void setOutdated(Boolean outdated) {
        this.outdated = outdated;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getCourse() {
        return course;
    }

    public void setCourse(Integer course) {
        this.course = course;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public Object getNetwork() {
        return network;
    }

    public void setNetwork(Object network) {
        this.network = network;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(attributes);
        dest.writeValue(deviceId);
        dest.writeValue(type);
        dest.writeValue(protocol);
        dest.writeValue(serverTime);
        dest.writeValue(deviceTime);
        dest.writeValue(fixTime);
        dest.writeValue(outdated);
        dest.writeValue(valid);
        dest.writeValue(latitude);
        dest.writeValue(longitude);
        dest.writeValue(altitude);
        dest.writeValue(speed);
        dest.writeValue(course);
        dest.writeValue(address);
        dest.writeValue(accuracy);
        dest.writeValue(network);
    }

    public int describeContents() {
        return 0;
    }

}