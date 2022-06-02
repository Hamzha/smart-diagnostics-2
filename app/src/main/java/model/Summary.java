package model;

import java.util.HashMap;
import java.util.Map;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "deviceId",
        "deviceName",
        "distance",
        "averageSpeed",
        "maxSpeed",
        "spentFuel",
        "startOdometer",
        "endOdometer",
        "engineHours"
})
public class Summary implements Parcelable
{

    @JsonProperty("deviceId")
    private Integer deviceId;
    @JsonProperty("deviceName")
    private String deviceName;
    @JsonProperty("distance")
    private Double distance;
    @JsonProperty("averageSpeed")
    private Double averageSpeed;
    @JsonProperty("maxSpeed")
    private Double maxSpeed;
    @JsonProperty("spentFuel")
    private Double spentFuel;
    @JsonProperty("startOdometer")
    private Double startOdometer;
    @JsonProperty("endOdometer")
    private Double endOdometer;
    @JsonProperty("engineHours")
    private Integer engineHours;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Parcelable.Creator<Summary> CREATOR = new Creator<Summary>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Summary createFromParcel(Parcel in) {
            return new Summary(in);
        }

        public Summary[] newArray(int size) {
            return (new Summary[size]);
        }

    }
            ;

    protected Summary(Parcel in) {
        this.deviceId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.deviceName = ((String) in.readValue((String.class.getClassLoader())));
        this.distance = ((Double) in.readValue((Double.class.getClassLoader())));
        this.averageSpeed = ((Double) in.readValue((Double.class.getClassLoader())));
        this.maxSpeed = ((Double) in.readValue((Double.class.getClassLoader())));
        this.spentFuel = ((Double) in.readValue((Double.class.getClassLoader())));
        this.startOdometer = ((Double) in.readValue((Double.class.getClassLoader())));
        this.endOdometer = ((Double) in.readValue((Double.class.getClassLoader())));
        this.engineHours = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.additionalProperties = ((Map<String, Object> ) in.readValue((Map.class.getClassLoader())));
    }

    public Summary() {
    }

    @JsonProperty("deviceId")
    public Integer getDeviceId() {
        return deviceId;
    }

    @JsonProperty("deviceId")
    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    @JsonProperty("deviceName")
    public String getDeviceName() {
        return deviceName;
    }

    @JsonProperty("deviceName")
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @JsonProperty("distance")
    public Double getDistance() {
        return distance;
    }

    @JsonProperty("distance")
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @JsonProperty("averageSpeed")
    public Double getAverageSpeed() {
        return averageSpeed;
    }

    @JsonProperty("averageSpeed")
    public void setAverageSpeed(Double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    @JsonProperty("maxSpeed")
    public Double getMaxSpeed() {
        return maxSpeed;
    }

    @JsonProperty("maxSpeed")
    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @JsonProperty("spentFuel")
    public Double getSpentFuel() {
        return spentFuel;
    }

    @JsonProperty("spentFuel")
    public void setSpentFuel(Double spentFuel) {
        this.spentFuel = spentFuel;
    }

    @JsonProperty("startOdometer")
    public Double getStartOdometer() {
        return startOdometer;
    }

    @JsonProperty("startOdometer")
    public void setStartOdometer(Double startOdometer) {
        this.startOdometer = startOdometer;
    }

    @JsonProperty("endOdometer")
    public Double getEndOdometer() {
        return endOdometer;
    }

    @JsonProperty("endOdometer")
    public void setEndOdometer(Double endOdometer) {
        this.endOdometer = endOdometer;
    }

    @JsonProperty("engineHours")
    public Integer getEngineHours() {
        return engineHours;
    }

    @JsonProperty("engineHours")
    public void setEngineHours(Integer engineHours) {
        this.engineHours = engineHours;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
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
        dest.writeValue(engineHours);
        dest.writeValue(additionalProperties);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Summary{" +
                "deviceId=" + deviceId +
                ", deviceName='" + deviceName + '\'' +
                ", distance=" + distance +
                ", averageSpeed=" + averageSpeed +
                ", maxSpeed=" + maxSpeed +
                ", spentFuel=" + spentFuel +
                ", startOdometer=" + startOdometer +
                ", endOdometer=" + endOdometer +
                ", engineHours=" + engineHours +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}