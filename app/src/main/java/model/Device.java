package model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Device implements Parcelable {

    public final static Creator<Device> CREATOR = new Creator<Device>() {
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        public Device[] newArray(int size) {
            return (new Device[size]);
        }
    };
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("attributes")
    @Expose
    private DeviceAttributes attributes;
    @SerializedName("groupId")
    @Expose
    private Integer groupId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("uniqueId")
    @Expose
    private String uniqueId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("lastUpdate")
    @Expose
    private String lastUpdate;
    @SerializedName("positionId")
    @Expose
    private Integer positionId;
    @SerializedName("geofenceIds")
    @Expose
    private List<Integer> geofenceIds = null;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("disabled")
    @Expose
    private Boolean disabled;
    private int imageId;

    private Device(Parcel in) {
        this.id = ((Long) in.readValue((Long.class.getClassLoader())));
        this.attributes = ((DeviceAttributes) in.readValue((DeviceAttributes.class.getClassLoader())));
        this.groupId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.uniqueId = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.lastUpdate = ((String) in.readValue((String.class.getClassLoader())));
        this.positionId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.geofenceIds = new ArrayList<Integer>();
        in.readList(this.geofenceIds, null);
        this.phone = ((String) in.readValue((String.class.getClassLoader())));
        this.model = ((String) in.readValue((String.class.getClassLoader())));
        this.contact = ((String) in.readValue((String.class.getClassLoader())));
        this.category = ((String) in.readValue((String.class.getClassLoader())));
        this.disabled = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
    }

    public Device() {
    }

    public Integer getImageId() {
        return this.imageId;
    }

    public void setImageId(Integer num) {
        this.imageId = num;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DeviceAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(DeviceAttributes attributes) {
        this.attributes = attributes;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public List<Integer> getGeofenceIds() {
        return geofenceIds;
    }

    public void setGeofenceIds(List<Integer> geofenceIds) {
        this.geofenceIds = geofenceIds;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(attributes);
        dest.writeValue(groupId);
        dest.writeValue(name);
        dest.writeValue(uniqueId);
        dest.writeValue(status);
        dest.writeValue(lastUpdate);
        dest.writeValue(positionId);
        dest.writeList(geofenceIds);
        dest.writeValue(phone);
        dest.writeValue(model);
        dest.writeValue(contact);
        dest.writeValue(category);
        dest.writeValue(disabled);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", attributes=" + attributes.toString() +
                ", groupId=" + groupId +
                ", name='" + name + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                ", status='" + status + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", positionId=" + positionId +
                ", geofenceIds=" + geofenceIds +
                ", phone='" + phone + '\'' +
                ", model='" + model + '\'' +
                ", contact='" + contact + '\'' +
                ", category='" + category + '\'' +
                ", disabled=" + disabled +
                ", imageId=" + imageId +
                '}';
    }
}