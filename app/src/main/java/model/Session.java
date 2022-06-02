
package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Session implements Parcelable {

    public final static Creator<Session> CREATOR = new Creator<Session>() {


        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        public Session[] newArray(int size) {
            return (new Session[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("attributes")
    @Expose
    private SessionAttributes attributes;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("login")
    @Expose
    private String login;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("readonly")
    @Expose
    private Boolean readonly;
    @SerializedName("administrator")
    @Expose
    private Boolean administrator;
    @SerializedName("map")
    @Expose
    private String map;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("zoom")
    @Expose
    private Integer zoom;
    @SerializedName("twelveHourFormat")
    @Expose
    private Boolean twelveHourFormat;
    @SerializedName("coordinateFormat")
    @Expose
    private String coordinateFormat;
    @SerializedName("disabled")
    @Expose
    private Boolean disabled;
    @SerializedName("expirationTime")
    @Expose
    private Object expirationTime;
    @SerializedName("deviceLimit")
    @Expose
    private Integer deviceLimit;
    @SerializedName("userLimit")
    @Expose
    private Integer userLimit;
    @SerializedName("deviceReadonly")
    @Expose
    private Boolean deviceReadonly;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("limitCommands")
    @Expose
    private Boolean limitCommands;
    @SerializedName("poiLayer")
    @Expose
    private String poiLayer;
    @SerializedName("password")
    @Expose
    private Object password;

    Session(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.attributes = ((SessionAttributes) in.readValue((SessionAttributes.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.login = ((String) in.readValue((String.class.getClassLoader())));
        this.email = ((String) in.readValue((String.class.getClassLoader())));
        this.phone = ((String) in.readValue((String.class.getClassLoader())));
        this.readonly = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.administrator = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.map = ((String) in.readValue((String.class.getClassLoader())));
        this.latitude = ((Double) in.readValue((Double.class.getClassLoader())));
        this.longitude = ((Double) in.readValue((Double.class.getClassLoader())));
        this.zoom = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.twelveHourFormat = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.coordinateFormat = ((String) in.readValue((String.class.getClassLoader())));
        this.disabled = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.expirationTime = ((Object) in.readValue((Object.class.getClassLoader())));
        this.deviceLimit = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.userLimit = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.deviceReadonly = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.token = ((String) in.readValue((String.class.getClassLoader())));
        this.limitCommands = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.poiLayer = ((String) in.readValue((String.class.getClassLoader())));
        this.password = ((Object) in.readValue((Object.class.getClassLoader())));
    }

    public Session() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SessionAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(SessionAttributes attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public Boolean getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Boolean administrator) {
        this.administrator = administrator;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
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

    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }

    public Boolean getTwelveHourFormat() {
        return twelveHourFormat;
    }

    public void setTwelveHourFormat(Boolean twelveHourFormat) {
        this.twelveHourFormat = twelveHourFormat;
    }

    public String getCoordinateFormat() {
        return coordinateFormat;
    }

    public void setCoordinateFormat(String coordinateFormat) {
        this.coordinateFormat = coordinateFormat;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Object getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Object expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Integer getDeviceLimit() {
        return deviceLimit;
    }

    public void setDeviceLimit(Integer deviceLimit) {
        this.deviceLimit = deviceLimit;
    }

    public Integer getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(Integer userLimit) {
        this.userLimit = userLimit;
    }

    public Boolean getDeviceReadonly() {
        return deviceReadonly;
    }

    public void setDeviceReadonly(Boolean deviceReadonly) {
        this.deviceReadonly = deviceReadonly;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getLimitCommands() {
        return limitCommands;
    }

    public void setLimitCommands(Boolean limitCommands) {
        this.limitCommands = limitCommands;
    }

    public String getPoiLayer() {
        return poiLayer;
    }

    public void setPoiLayer(String poiLayer) {
        this.poiLayer = poiLayer;
    }

    public Object getPassword() {
        return password;
    }

    public void setPassword(Object password) {
        this.password = password;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(attributes);
        dest.writeValue(name);
        dest.writeValue(login);
        dest.writeValue(email);
        dest.writeValue(phone);
        dest.writeValue(readonly);
        dest.writeValue(administrator);
        dest.writeValue(map);
        dest.writeValue(latitude);
        dest.writeValue(longitude);
        dest.writeValue(zoom);
        dest.writeValue(twelveHourFormat);
        dest.writeValue(coordinateFormat);
        dest.writeValue(disabled);
        dest.writeValue(expirationTime);
        dest.writeValue(deviceLimit);
        dest.writeValue(userLimit);
        dest.writeValue(deviceReadonly);
        dest.writeValue(token);
        dest.writeValue(limitCommands);
        dest.writeValue(poiLayer);
        dest.writeValue(password);
    }

    public int describeContents() {
        return 0;
    }

}