package model;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupAttributes implements Parcelable {

    public final static Parcelable.Creator<GroupAttributes> CREATOR = new Creator<GroupAttributes>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GroupAttributes createFromParcel(Parcel in) {
            return new GroupAttributes(in);
        }

        public GroupAttributes[] newArray(int size) {
            return (new GroupAttributes[size]);
        }

    };

    protected GroupAttributes(Parcel in) {
    }

    public GroupAttributes() {
    }

    public void writeToParcel(Parcel dest, int flags) {
    }

    public int describeContents() {
        return 0;
    }

}