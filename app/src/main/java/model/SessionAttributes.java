package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SessionAttributes implements Parcelable {

    public final static Creator<SessionAttributes> CREATOR = new Creator<SessionAttributes>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SessionAttributes createFromParcel(Parcel in) {
            return new SessionAttributes(in);
        }

        public SessionAttributes[] newArray(int size) {
            return (new SessionAttributes[size]);
        }

    };
    @SerializedName("ui.hidePositionAttributes")
    @Expose
    private String uiHidePositionAttributes;
    @SerializedName("speedUnit")
    @Expose
    private String speedUnit;
    @SerializedName("notificationTokens")
    @Expose
    private String notificationTokens;

    protected SessionAttributes(Parcel in) {
        this.uiHidePositionAttributes = ((String) in.readValue((String.class.getClassLoader())));
        this.speedUnit = ((String) in.readValue((String.class.getClassLoader())));
        this.notificationTokens = ((String) in.readValue((String.class.getClassLoader())));
    }

    public SessionAttributes() {
    }

    public String getUiHidePositionAttributes() {
        return uiHidePositionAttributes;
    }

    public void setUiHidePositionAttributes(String uiHidePositionAttributes) {
        this.uiHidePositionAttributes = uiHidePositionAttributes;
    }

    public String getSpeedUnit() {
        return speedUnit;
    }

    public void setSpeedUnit(String speedUnit) {
        this.speedUnit = speedUnit;
    }

    public String getNotificationTokens() {
        return notificationTokens;
    }

    public void setNotificationTokens(String notificationTokens) {
        this.notificationTokens = notificationTokens;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(uiHidePositionAttributes);
        dest.writeValue(speedUnit);
        dest.writeValue(notificationTokens);
    }

    public int describeContents() {
        return 0;
    }

}
