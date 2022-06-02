package model;

public class Message {
    private long deviceId;
    private String type;

    public long getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(long j) {
        this.deviceId = j;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }
}
