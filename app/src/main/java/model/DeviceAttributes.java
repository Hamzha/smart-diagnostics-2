package model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceAttributes implements Parcelable
{

    @SerializedName("power_high")
    @Expose
    private String powerHigh;
    @SerializedName("power_lower")
    @Expose
    private String powerLower;
    @SerializedName("power_middle")
    @Expose
    private String powerMiddle;
    @SerializedName("device_name")
    @Expose
    private String deviceName;
    @SerializedName("fuel_low")
    @Expose
    private String fuelLow;
    @SerializedName("fuel_high")
    @Expose
    private String fuelHigh;
    @SerializedName("fuel_middle")
    @Expose
    private String fuelMiddle;
    @SerializedName("analog_fuel")
    @Expose
    private String analogFuel;
    @SerializedName("obd2")
    @Expose
    private String obd2;
    @SerializedName("can")
    @Expose
    private String can;
    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("X")
    @Expose
    private String x;
    @SerializedName("Y")
    @Expose
    private String y;
    @SerializedName("Z")
    @Expose
    private String z;
    @SerializedName("accelerometer")
    @Expose
    private String accelerometer;
    @SerializedName("reverse")
    @Expose
    private String reverse;
    @SerializedName("fuel")
    @Expose
    private String fuel;



    public final static Parcelable.Creator<DeviceAttributes> CREATOR = new Creator<DeviceAttributes>() {


        @SuppressWarnings({
                "unchecked"
        })
        public DeviceAttributes createFromParcel(Parcel in) {
            return new DeviceAttributes(in);
        }

        public DeviceAttributes[] newArray(int size) {
            return (new DeviceAttributes[size]);
        }

    }
            ;

    protected DeviceAttributes(Parcel in) {
        this.powerHigh = ((String) in.readValue((String.class.getClassLoader())));
        this.powerLower = ((String) in.readValue((String.class.getClassLoader())));
        this.powerMiddle = ((String) in.readValue((String.class.getClassLoader())));
        this.deviceName = ((String) in.readValue((String.class.getClassLoader())));
        this.fuelLow = ((String) in.readValue((String.class.getClassLoader())));
        this.fuelHigh = ((String) in.readValue((String.class.getClassLoader())));
        this.fuelMiddle = ((String) in.readValue((String.class.getClassLoader())));
        this.analogFuel = ((String) in.readValue((String.class.getClassLoader())));
        this.fuel = ((String) in.readValue((String.class.getClassLoader())));
        this.obd2 = ((String) in.readValue((String.class.getClassLoader())));
        this.can = ((String) in.readValue((String.class.getClassLoader())));
        this.deviceType = ((String) in.readValue((String.class.getClassLoader())));
        this.x = ((String) in.readValue((String.class.getClassLoader())));
        this.y = ((String) in.readValue((String.class.getClassLoader())));
        this.z = ((String) in.readValue((String.class.getClassLoader())));
        this.accelerometer = ((String) in.readValue((String.class.getClassLoader())));
        this.reverse = ((String) in.readValue((String.class.getClassLoader())));

    }

    public DeviceAttributes() {
    }

    public String getPowerHigh() {
        return powerHigh;
    }

    public void setPowerHigh(String powerHigh) {
        this.powerHigh = powerHigh;
    }

    public String getPowerLower() {
        return powerLower;
    }

    public void setPowerLower(String powerLower) {
        this.powerLower = powerLower;
    }

    public String getPowerMiddle() {
        return powerMiddle;
    }

    public void setPowerMiddle(String powerMiddle) {
        this.powerMiddle = powerMiddle;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getFuelLow() {
        return fuelLow;
    }

    public void setFuelLow(String fuelLow) {
        this.fuelLow = fuelLow;
    }

    public String getFuelHigh() {
        return fuelHigh;
    }

    public void setFuelHigh(String fuelHigh) {
        this.fuelHigh = fuelHigh;
    }

    public String getFuelMiddle() {
        return fuelMiddle;
    }

    public void setFuelMiddle(String fuelMiddle) {
        this.fuelMiddle = fuelMiddle;
    }

    public String getAnalogFuel() {
        return analogFuel;
    }

    public void setAnalogFuel(String analogFuel) {
        this.analogFuel = analogFuel;
    }

    public String getObd2() {
        return obd2;
    }

    public void setObd2(String obd2) {
        this.obd2 = obd2;
    }

    public String getCan() {
        return can;
    }

    public void setCan(String can) {
        this.can = can;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getZ() {
        return z;
    }

    public void setZ(String z) {
        this.z = z;
    }

    public String getAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(String accelerometer) {
        this.accelerometer = accelerometer;
    }

    public String getReserve() {
        return reverse;
    }

    public void setReserve(String reserve) {
        this.reverse = reserve;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(powerHigh);
        dest.writeValue(powerLower);
        dest.writeValue(powerMiddle);
        dest.writeValue(deviceName);
        dest.writeValue(fuelLow);
        dest.writeValue(fuelHigh);
        dest.writeValue(fuelMiddle);
        dest.writeValue(analogFuel);
        dest.writeValue(obd2);
        dest.writeValue(can);
        dest.writeValue(deviceType);
        dest.writeValue(x);
        dest.writeValue(y);
        dest.writeValue(z);
        dest.writeValue(accelerometer);
        dest.writeValue(reverse);
        dest.writeValue(fuel);

    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "DeviceAttributes{" +
                "powerHigh='" + powerHigh + '\'' +
                ", powerLower='" + powerLower + '\'' +
                ", powerMiddle='" + powerMiddle + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", fuelLow='" + fuelLow + '\'' +
                ", fuelHigh='" + fuelHigh + '\'' +
                ", fuelMiddle='" + fuelMiddle + '\'' +
                ", analogFuel='" + analogFuel + '\'' +
                ", obd2='" + obd2 + '\'' +
                ", can='" + can + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", z='" + z + '\'' +
                ", accelerometer='" + accelerometer + '\'' +
                ", reverse='" + reverse + '\'' +
                ", fuel='" + fuel + '\'' +

                '}';
    }
}