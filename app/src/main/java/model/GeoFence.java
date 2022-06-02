package model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeoFence implements Parcelable {

    public final static Creator<GeoFence> CREATOR = new Creator<GeoFence>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GeoFence createFromParcel(Parcel in) {
            return new GeoFence(in);
        }

        public GeoFence[] newArray(int size) {
            return (new GeoFence[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("attributes")
    @Expose
    private GeoFenceAttributes geofenceAttributes;
    @SerializedName("calendarId")
    @Expose
    private Integer calendarId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("area")
    @Expose
    private String area;

    private GeoFence(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.geofenceAttributes = ((GeoFenceAttributes) in.readValue((GeoFenceAttributes.class.getClassLoader())));
        this.calendarId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.area = ((String) in.readValue((String.class.getClassLoader())));
    }

    public GeoFence() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GeoFenceAttributes getGeofenceAttributes() {
        return geofenceAttributes;
    }

    public void setGeofenceAttributes(GeoFenceAttributes geofenceAttributes) {
        this.geofenceAttributes = geofenceAttributes;
    }

    public Integer getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Integer calendarId) {
        this.calendarId = calendarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(geofenceAttributes);
        dest.writeValue(calendarId);
        dest.writeValue(name);
        dest.writeValue(description);
        dest.writeValue(area);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "GeoFence{" +
                "id=" + id +
                ", geofenceAttributes=" + geofenceAttributes +
                ", calendarId=" + calendarId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", area='" + area + '\'' +
                '}';
    }
}