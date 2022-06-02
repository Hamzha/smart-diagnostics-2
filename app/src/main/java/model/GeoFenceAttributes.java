package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeoFenceAttributes implements Parcelable {

    public final static Creator<GeoFenceAttributes> CREATOR = new Creator<GeoFenceAttributes>() {

        public GeoFenceAttributes createFromParcel(Parcel in) {
            return new GeoFenceAttributes(in);
        }

        public GeoFenceAttributes[] newArray(int size) {
            return (new GeoFenceAttributes[size]);
        }

    };
    @SerializedName("color")
    @Expose
    private String color;

    private GeoFenceAttributes(Parcel in) {
        this.color = ((String) in.readValue((String.class.getClassLoader())));
    }

    public GeoFenceAttributes() {
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(color);
    }

    public int describeContents() {
        return 0;
    }

}