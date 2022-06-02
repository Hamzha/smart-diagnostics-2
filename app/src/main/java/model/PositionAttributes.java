package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PositionAttributes implements Parcelable {

    public final static Parcelable.Creator<PositionAttributes> CREATOR = new Creator<PositionAttributes>() {


        public PositionAttributes createFromParcel(Parcel in) {
            return new PositionAttributes(in);
        }

        public PositionAttributes[] newArray(int size) {
            return (new PositionAttributes[size]);
        }

    };
    @SerializedName("priority")
    @Expose
    private Integer priority;
    @SerializedName("sat")
    @Expose
    private Integer sat;
    @SerializedName("event")
    @Expose
    private Integer event;
    @SerializedName("ignition")
    @Expose
    private Boolean ignition;
    @SerializedName("motion")
    @Expose
    private Boolean motion;
    @SerializedName("rssi")
    @Expose
    private Integer rssi;
    @SerializedName("gpsStatus")
    @Expose
    private Integer gpsStatus;
    @SerializedName("pdop")
    @Expose
    private Double pdop;
    @SerializedName("power")
    @Expose
    private Double power;
    @SerializedName("hdop")
    @Expose
    private Double hdop;
    @SerializedName("battery")
    @Expose
    private Double battery;
    @SerializedName("io24")
    @Expose
    private Integer io24;
    @SerializedName("io68")
    @Expose
    private Integer io68;
    @SerializedName("operator")
    @Expose
    private Integer operator;
    @SerializedName("io199")
    @Expose
    private Integer io199;
    @SerializedName("io16")
    @Expose
    private Integer io16;
    @SerializedName("io15")
    @Expose
    private Integer io15;
    @SerializedName("totalDistance")
    @Expose
    private Double totalDistance;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("hours")
    @Expose
    private double hours;
    @SerializedName("fuel")
    @Expose
    private double fuel;
    @SerializedName("io253")
    @Expose
    private int io253;
    @SerializedName("io255")
    @Expose
    private int io255;
    @SerializedName("io254")
    @Expose
    private int io254;
    @SerializedName("io179")
    @Expose
    private int io179;
    @SerializedName("out1")
    @Expose
    private boolean out1;
    @SerializedName("io30")
    @Expose
    private Double io30;
    @SerializedName("io31")
    @Expose
    private Double io31;
    @SerializedName("io32")
    @Expose
    private Double io32;
    @SerializedName("io33")
    @Expose
    private Double io33;
    @SerializedName("io36")
    @Expose
    private Double io36;
    @SerializedName("io37")
    @Expose
    private Double io37;
    @SerializedName("io38")
    @Expose
    private Double io38;
    @SerializedName("io39")
    @Expose
    private Double io39;
    @SerializedName("io40")
    @Expose
    private Double io40;
    @SerializedName("io41")
    @Expose
    private Double io41;
    @SerializedName("io42")
    @Expose
    private Double io42;
    @SerializedName("io43")
    @Expose
    private Double io43;
    @SerializedName("io251")
    @Expose
    private Double io251;
    @SerializedName("deviceTemp")
    @Expose
    private Double deviceTemp;
    @SerializedName("rpm")
    @Expose
    private Double rpm;
    @SerializedName("throttle")
    @Expose
    private Double throttle;
    @SerializedName("obdSpeed")
    @Expose
    private Double obdSpeed;

    private PositionAttributes(Parcel in) {
        this.priority = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.sat = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.event = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.ignition = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.motion = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.rssi = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.gpsStatus = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.pdop = ((Double) in.readValue((Double.class.getClassLoader())));
        this.hdop = ((Double) in.readValue((Double.class.getClassLoader())));
        this.power = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io24 = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.battery = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io68 = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.operator = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.io199 = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.io16 = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.io253 = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.io255 = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.io179 = (Integer) in.readValue(Integer.class.getClassLoader());
        this.io254 = (Integer) in.readValue(Integer.class.getClassLoader());
        this.io15 = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.io30 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io31 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io32 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io33 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io36 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io37 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io38 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io39 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io40 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io41 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io42 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io43 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.io251 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.distance = ((Double) in.readValue((Double.class.getClassLoader())));
        this.totalDistance = ((Double) in.readValue((Double.class.getClassLoader())));
        this.hours = ((Double) in.readValue((Double.class.getClassLoader())));
        this.fuel = ((Double) in.readValue((Double.class.getClassLoader())));
        this.out1 = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.deviceTemp = ((Double) in.readValue((Double.class.getClassLoader())));
        this.rpm = ((Double) in.readValue((Double.class.getClassLoader())));
        this.throttle = ((Double) in.readValue((Double.class.getClassLoader())));
        this.obdSpeed = ((Double) in.readValue((Double.class.getClassLoader())));
    }

    public PositionAttributes() {
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getSat() {
        return sat;
    }

    public void setSat(Integer sat) {
        this.sat = sat;
    }

    public Integer getEvent() {
        return event;
    }

    public void setEvent(Integer event) {
        this.event = event;
    }

    public Boolean getIgnition() {
        return ignition;
    }

    public void setIgnition(Boolean ignition) {
        this.ignition = ignition;
    }

    public Boolean getMotion() {
        return motion;
    }

    public void setMotion(Boolean motion) {
        this.motion = motion;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public Integer getGpsStatus() {
        return gpsStatus;
    }

    public void setGpsStatus(Integer gpsStatus) {
        this.gpsStatus = gpsStatus;
    }

    public Double getPdop() {
        return pdop;
    }

    public void setPdop(Double pdop) {
        this.pdop = pdop;
    }

    public Double getHdop() {
        return hdop;
    }

    public void setHdop(Double hdop) {
        this.hdop = hdop;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public Integer getIo24() {
        return io24;
    }

    public void setIo24(Integer io24) {
        this.io24 = io24;
    }

    public Double getBattery() {
        return battery;
    }

    public void setBattery(Double battery) {
        this.battery = battery;
    }

    public Integer getIo68() {
        return io68;
    }

    public void setIo68(Integer io68) {
        this.io68 = io68;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public Integer getIo199() {
        return io199;
    }

    public void setIo199(Integer io199) {
        this.io199 = io199;
    }

    public Integer getIo253() {
        return io253;
    }

    public void setIo253(Integer io253) {
        this.io253 = io253;
    }

    public Integer getIo16() {
        return io16;
    }

    public void setIo16(Integer io16) {
        this.io16 = io16;
    }

    public Integer getIo255() {
        return io255;
    }

    public void setIo255(Integer io255) {
        this.io255 = io255;
    }

    public Integer getIo254() {
        return io254;
    }

    public void setIo254(Integer io254) {
        this.io254 = io254;
    }

    public Integer getIo15() {
        return io15;
    }

    public void setIo179(Integer io179) {
        this.io179 = io179;
    }

    public Integer getIo179() {
        return io179;
    }

    public void setIo15(Integer io15) {
        this.io15 = io15;
    }

    public Double getIo30() {
        return io30;
    }

    public void setIo30(Double io30) {
        this.io30 = io30;
    }

    public Double getIo31() {
        return io31;
    }

    public void setIo31(Double io31) {
        this.io31 = io31;
    }

    public Double getIo32() {
        return io32;
    }

    public void setIo32(Double io32) {
        this.io32 = io32;
    }

    public Double getIo33() {
        return io33;
    }

    public void setIo33(Double io33) {
        this.io33 = io33;
    }

    public Double getIo36() {
        return io36;
    }

    public void setIo36(Double io36) {
        this.io36 = io36;
    }

    public Double getIo37() {
        return io37;
    }

    public void setIo37(Double io37) {
        this.io37 = io37;
    }

    public Double getIo38() {
        return io38;
    }

    public void setIo38(Double io38) {
        this.io38 = io38;
    }

    public Double getIo39() {
        return io39;
    }

    public void setIo39(Double io39) {
        this.io39 = io39;
    }

    public Double getIo40() {
        return io40;
    }

    public void setIo40(Double io40) {
        this.io40 = io40;
    }

    public Double getIo41() {
        return io41;
    }

    public void setIo41(Double io41) {
        this.io41 = io41;
    }

    public Double getIo42() {
        return io42;
    }

    public void setIo42(Double io42) {
        this.io42 = io42;
    }

    public Double getIo43() {
        return io43;
    }

    public void setIo43(Double io43) {
        this.io43 = io43;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public double getFuel() {
        return fuel;
    }

    public void setFuel(double hours) {
        this.fuel = hours;
    }

    public boolean getOut1() {
        return out1;
    }

    public void setout1(boolean out1) {
        this.out1 = out1;
    }

    public Double getIo251() {
        return io251;
    }

    public void setIo251(Double io251) {
        this.io251 = io251;
    }

    public Double getDeviceTemp() {
        return deviceTemp;
    }

    public void setDeviceTemp(Double deviceTemp) {
        this.deviceTemp = deviceTemp;
    }

    public Double getRpm() {
        return rpm;
    }

    public void setRpm(Double rpm) {
        this.rpm = rpm;
    }

    public Double getThrottle() {
        return throttle;
    }

    public void setThrottle(Double throttle) {
        this.throttle = throttle;
    }

    public Double getObdSpeed() {
        return obdSpeed;
    }

    public void setObdSpeed(Double obdSpeed) {
        this.obdSpeed = obdSpeed;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(priority);
        dest.writeValue(sat);
        dest.writeValue(event);
        dest.writeValue(ignition);
        dest.writeValue(motion);
        dest.writeValue(rssi);
        dest.writeValue(gpsStatus);
        dest.writeValue(pdop);
        dest.writeValue(hdop);
        dest.writeValue(power);
        dest.writeValue(io24);
        dest.writeValue(battery);
        dest.writeValue(io68);
        dest.writeValue(operator);
        dest.writeValue(io199);
        dest.writeValue(io16);
        dest.writeValue(distance);
        dest.writeValue(totalDistance);
        dest.writeValue(hours);
        dest.writeValue(fuel);
        dest.writeValue(io253);
        dest.writeValue(io255);
        dest.writeValue(io254);
        dest.writeValue(io15);
        dest.writeValue(io179);
        dest.writeValue(io30);
        dest.writeValue(io31);
        dest.writeValue(io32);
        dest.writeValue(io33);
        dest.writeValue(io36);
        dest.writeValue(io37);
        dest.writeValue(io38);
        dest.writeValue(io39);
        dest.writeValue(io40);
        dest.writeValue(io41);
        dest.writeValue(io42);
        dest.writeValue(io43);
        dest.writeValue(out1);
        dest.writeValue(io251);

        dest.writeValue(throttle);
        dest.writeValue(obdSpeed);
        dest.writeValue(rpm);
        dest.writeValue(deviceTemp);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "PositionAttributes{" +
                "priority=" + priority +
                ", sat=" + sat +
                ", event=" + event +
                ", ignition=" + ignition +
                ", motion=" + motion +
                ", rssi=" + rssi +
                ", gpsStatus=" + gpsStatus +
                ", pdop=" + pdop +
                ", power=" + power +
                ", hdop=" + hdop +
                ", battery=" + battery +
                ", io24=" + io24 +
                ", io68=" + io68 +
                ", operator=" + operator +
                ", io199=" + io199 +
                ", io16=" + io16 +
                ", io15=" + io15 +
                ", io30=" + io30 +
                ", io31=" + io31 +
                ", io32=" + io32 +
                ", io33=" + io33 +
                ", io36=" + io36 +
                ", io37=" + io37 +
                ", io38=" + io38 +
                ", io39=" + io39 +
                ", io40=" + io40 +
                ", io41=" + io41 +
                ", io42=" + io42 +
                ", io43=" + io43 +
                ", totalDistance=" + totalDistance +
                ", distance=" + distance +
                ", hours=" + hours +
                ", io253=" + io253 +
                ", io255=" + io255 +
                ", io254=" + io254 +
                ", io179=" + io179 +
                ", out1=" + out1 +
                ", io251=" + io251 +
                ", throttle=" + throttle +
                ", deviceTemp=" + deviceTemp +
                ", rpm=" + rpm +
                ", obdSpeed=" + obdSpeed +
                '}';
    }
}