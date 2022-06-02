package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class EventAttributes implements Parcelable {

    public final static Parcelable.Creator<EventAttributes> CREATOR = new Creator<EventAttributes>() {


        public EventAttributes createFromParcel(Parcel in) {
            return new EventAttributes(in);
        }

        public EventAttributes[] newArray(int size) {
            return (new EventAttributes[size]);
        }

    };
    @SerializedName("alarm")
    @Expose
    private String alarm;
    @SerializedName("io251")
    @Expose
    private Double io251;

    protected EventAttributes(Parcel in) {
        this.alarm = ((String) in.readValue((String.class.getClassLoader())));

    }

    public EventAttributes() {
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public Double getIo251() {
        return io251;
    }

    public void setIo251(Double io251) {
        this.io251 = io251;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(alarm);
        dest.writeValue(io251);

    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "EventAttributes{" +
                "alarm='" + alarm + '\'' +
                ", io251=" + io251 +
                '}';
    }
}