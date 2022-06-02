package model;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import util.SharedPrefConst;
import util.SharedPreferenceHelper;

public class Position implements Parcelable {

    public final static Creator<Position> CREATOR = new Creator<Position>() {

        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        public Position[] newArray(int size) {
            return (new Position[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("attributes")
    @Expose
    private PositionAttributes positionAttributes;
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
    private Double altitude;
    @SerializedName("speed")
    @Expose
    private Double speed;
    @SerializedName("course")
    @Expose
    private double course;
    @SerializedName("address")
    @Expose
    private Object address;
    @SerializedName("accuracy")
    @Expose
    private Integer accuracy;
    @SerializedName("network")
    @Expose
    private Object network;

    /**********************Adress**************************************/

    protected Position(Parcel in) {
        this.id = ((Long) in.readValue((Integer.class.getClassLoader())));
        this.positionAttributes = ((PositionAttributes) in.readValue((PositionAttributes.class.getClassLoader())));
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
        this.altitude = ((Double) in.readValue((Integer.class.getClassLoader())));
        this.speed = ((Double) in.readValue((Integer.class.getClassLoader())));
        this.course = ((double) in.readValue((Integer.class.getClassLoader())));
        this.address = ((Object) in.readValue((Object.class.getClassLoader())));
        this.accuracy = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.network = ((Object) in.readValue((Object.class.getClassLoader())));
    }

    public Position() {
    }

    public long getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PositionAttributes getPositionAttributes() {
        return positionAttributes;
    }

    public void setPositionAttributes(PositionAttributes positionAttributes) {
        this.positionAttributes = positionAttributes;
    }

    public long getDeviceId() {
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

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public double getCourse() {
        return course;
    }

    public void setCourse(double course) {
        this.course = course;
    }

        public Object getAddress(Context context) {
            return this.address;
    }

    public void setAddress(String address) {
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
        dest.writeValue(positionAttributes);
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

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", positionAttributes=" + positionAttributes.toString() +
                ", deviceId=" + deviceId +
                ", type=" + type +
                ", protocol='" + protocol + '\'' +
                ", serverTime='" + serverTime + '\'' +
                ", deviceTime='" + deviceTime + '\'' +
                ", fixTime='" + fixTime + '\'' +
                ", outdated=" + outdated +
                ", valid=" + valid +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", speed=" + speed +
                ", course=" + course +
                ", address=" + address +
                ", accuracy=" + accuracy +
                ", network=" + network +
                '}';
    }


}