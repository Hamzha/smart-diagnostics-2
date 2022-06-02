package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteAttributes implements Parcelable {

    public final static Parcelable.Creator<RouteAttributes> CREATOR = new Creator<RouteAttributes>() {


        public RouteAttributes createFromParcel(Parcel in) {
            return new RouteAttributes(in);
        }

        public RouteAttributes[] newArray(int size) {
            return (new RouteAttributes[size]);
        }

    };
    @SerializedName("alarm")
    @Expose
    private String alarm;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("totalDistance")
    @Expose
    private Double totalDistance;
    @SerializedName("motion")
    @Expose
    private Boolean motion;

    private RouteAttributes(Parcel in) {
        this.alarm = ((String) in.readValue((String.class.getClassLoader())));
        this.distance = ((Double) in.readValue((Double.class.getClassLoader())));
        this.totalDistance = ((Double) in.readValue((Double.class.getClassLoader())));
        this.motion = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
    }

    public RouteAttributes() {
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Boolean getMotion() {
        return motion;
    }

    public void setMotion(Boolean motion) {
        this.motion = motion;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(alarm);
        dest.writeValue(distance);
        dest.writeValue(totalDistance);
        dest.writeValue(motion);
    }

    public int describeContents() {
        return 0;
    }

}