package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Group implements Parcelable {

    public final static Parcelable.Creator<Group> CREATOR = new Creator<Group>() {


        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        public Group[] newArray(int size) {
            return (new Group[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("attributes")
    @Expose
    private GroupAttributes attributes;
    @SerializedName("groupId")
    @Expose
    private Integer groupId;
    @SerializedName("name")
    @Expose
    private String name;

    private Group(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.attributes = ((GroupAttributes) in.readValue((GroupAttributes.class.getClassLoader())));
        this.groupId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Group() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GroupAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(GroupAttributes attributes) {
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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(attributes);
        dest.writeValue(groupId);
        dest.writeValue(name);
    }

    public int describeContents() {
        return 0;
    }

}