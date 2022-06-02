package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Trip implements Parcelable {

    public final static Parcelable.Creator<Trip> CREATOR = new Creator<Trip>() {

        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        public Trip[] newArray(int size) {
            return (new Trip[size]);
        }

    };
    @SerializedName("deviceId")
    @Expose
    private Integer deviceId;
    @SerializedName("deviceName")
    @Expose
    private String deviceName;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("averageSpeed")
    @Expose
    private Double averageSpeed;
    @SerializedName("maxSpeed")
    @Expose
    private Double maxSpeed;
    @SerializedName("spentFuel")
    @Expose
    private Double spentFuel;
    @SerializedName("startOdometer")
    @Expose
    private Double startOdometer;
    @SerializedName("endOdometer")
    @Expose
    private Double endOdometer;
    @SerializedName("startPositionId")
    @Expose
    private Integer startPositionId;
    @SerializedName("endPositionId")
    @Expose
    private Integer endPositionId;
    @SerializedName("startLat")
    @Expose
    private Double startLat;
    @SerializedName("startLon")
    @Expose
    private Double startLon;
    @SerializedName("endLat")
    @Expose
    private Double endLat;
    @SerializedName("endLon")
    @Expose
    private Double endLon;
    @SerializedName("startTime")
    @Expose
    private Date startTime;
    @SerializedName("startAddress")
    @Expose
    private Object startAddress;
    @SerializedName("endTime")
    @Expose
    private Date endTime;
    @SerializedName("endAddress")
    @Expose
    private Object endAddress;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("driverUniqueId")
    @Expose
    private Object driverUniqueId;
    @SerializedName("driverName")
    @Expose
    private Object driverName;

    protected Trip(Parcel in) {
        this.deviceId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.deviceName = ((String) in.readValue((String.class.getClassLoader())));
        this.distance = ((Double) in.readValue((Double.class.getClassLoader())));
        this.averageSpeed = ((Double) in.readValue((Double.class.getClassLoader())));
        this.maxSpeed = ((Double) in.readValue((Double.class.getClassLoader())));
        this.spentFuel = ((Double) in.readValue((Integer.class.getClassLoader())));
        this.startOdometer = ((Double) in.readValue((Double.class.getClassLoader())));
        this.endOdometer = ((Double) in.readValue((Double.class.getClassLoader())));
        this.startPositionId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.endPositionId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.startLat = ((Double) in.readValue((Double.class.getClassLoader())));
        this.startLon = ((Double) in.readValue((Double.class.getClassLoader())));
        this.endLat = ((Double) in.readValue((Double.class.getClassLoader())));
        this.endLon = ((Double) in.readValue((Double.class.getClassLoader())));
        this.startTime = ((Date) in.readValue((String.class.getClassLoader())));
        this.startAddress = ((Object) in.readValue((Object.class.getClassLoader())));
        this.endTime = ((Date) in.readValue((String.class.getClassLoader())));
        this.endAddress = ((Object) in.readValue((Object.class.getClassLoader())));
        this.duration = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.driverUniqueId = ((Object) in.readValue((Object.class.getClassLoader())));
        this.driverName = ((Object) in.readValue((Object.class.getClassLoader())));
    }

    public Trip() {
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(Double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Double getSpentFuel() {
        return spentFuel;
    }

    public void setSpentFuel(Double spentFuel) {
        this.spentFuel = spentFuel;
    }

    public Double getStartOdometer() {
        return startOdometer;
    }

    public void setStartOdometer(Double startOdometer) {
        this.startOdometer = startOdometer;
    }

    public Double getEndOdometer() {
        return endOdometer;
    }

    public void setEndOdometer(Double endOdometer) {
        this.endOdometer = endOdometer;
    }

    public Integer getStartPositionId() {
        return startPositionId;
    }

    public void setStartPositionId(Integer startPositionId) {
        this.startPositionId = startPositionId;
    }

    public Integer getEndPositionId() {
        return endPositionId;
    }

    public void setEndPositionId(Integer endPositionId) {
        this.endPositionId = endPositionId;
    }

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLon() {
        return startLon;
    }

    public void setStartLon(Double startLon) {
        this.startLon = startLon;
    }

    public Double getEndLat() {
        return endLat;
    }

    public void setEndLat(Double endLat) {
        this.endLat = endLat;
    }

    public Double getEndLon() {
        return endLon;
    }

    public void setEndLon(Double endLon) {
        this.endLon = endLon;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Object getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(Object startAddress) {
        this.startAddress = startAddress;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Object getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(Object endAddress) {
        this.endAddress = endAddress;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Object getDriverUniqueId() {
        return driverUniqueId;
    }

    public void setDriverUniqueId(Object driverUniqueId) {
        this.driverUniqueId = driverUniqueId;
    }

    public Object getDriverName() {
        return driverName;
    }

    public void setDriverName(Object driverName) {
        this.driverName = driverName;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(deviceId);
        dest.writeValue(deviceName);
        dest.writeValue(distance);
        dest.writeValue(averageSpeed);
        dest.writeValue(maxSpeed);
        dest.writeValue(spentFuel);
        dest.writeValue(startOdometer);
        dest.writeValue(endOdometer);
        dest.writeValue(startPositionId);
        dest.writeValue(endPositionId);
        dest.writeValue(startLat);
        dest.writeValue(startLon);
        dest.writeValue(endLat);
        dest.writeValue(endLon);
        dest.writeValue(startTime);
        dest.writeValue(startAddress);
        dest.writeValue(endTime);
        dest.writeValue(endAddress);
        dest.writeValue(duration);
        dest.writeValue(driverUniqueId);
        dest.writeValue(driverName);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "deviceId=" + deviceId +
                ", deviceName='" + deviceName + '\'' +
                ", distance=" + distance +
                ", averageSpeed=" + averageSpeed +
                ", maxSpeed=" + maxSpeed +
                ", spentFuel=" + spentFuel +
                ", startOdometer=" + startOdometer +
                ", endOdometer=" + endOdometer +
                ", startPositionId=" + startPositionId +
                ", endPositionId=" + endPositionId +
                ", startLat=" + startLat +
                ", startLon=" + startLon +
                ", endLat=" + endLat +
                ", endLon=" + endLon +
                ", startTime=" + startTime +
                ", startAddress=" + startAddress +
                ", endTime=" + endTime +
                ", endAddress=" + endAddress +
                ", duration=" + duration +
                ", driverUniqueId=" + driverUniqueId +
                ", driverName=" + driverName +
                '}';
    }
}