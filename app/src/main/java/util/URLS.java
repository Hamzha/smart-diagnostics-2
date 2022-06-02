package util;

public class URLS {

    public static String Login(String email, String password) {
        return URLConst.BASE_URL + URLConst.SESSION + URLConst.EMAIL + "=" + email + "&" + URLConst.PASSWORD + "=" + password;
    }

    static String Position() {
        return URLConst.BASE_URL + URLConst.POSITION + URLConst.USER_ID + "=" + URLConst.FROM;
    }

    static String Devices() {
        return URLConst.BASE_URL + URLConst.DEVICES + URLConst.USER_ID + "=" + URLConst.FROM;
    }

    static String GeoFence() {
        return URLConst.BASE_URL + URLConst.GEOFENCES;
    }

    public static String Trips(String fromDate, String toDate, long deviceId) {
        return URLConst.BASE_URL + URLConst.TRIPS + URLConst.DEVICE_ID + deviceId + URLConst.FROM + fromDate + URLConst.TO + toDate;
    }

    public static String Command(long deviceId) {
        return URLConst.BASE_URL + URLConst.COMMANDS_SEND + URLConst.DEVICE_ID + deviceId;
    }

    public static String Events(long deviceId, String fromDate, String toDate) {
        return URLConst.BASE_URL + URLConst.EVENTS + URLConst.DEVICE_ID + deviceId + URLConst.FROM + fromDate + URLConst.TO + toDate;
    }

    public static String PositionIds(StringBuilder ids) {
        return URLConst.BASE_URL + URLConst.POSITION + ids;
    }

    public static String DeviceById(String ids) {
        return URLConst.BASE_URL + URLConst.DEVICES + ids;
    }

    public static String UserById(String ids) {
        return URLConst.BASE_URL + URLConst.USER    ;
    }

    public static String Route(long deviceId, String fromDate, String toDate) {
        return URLConst.BASE_URL + URLConst.ROUTES + URLConst.DEVICE_ID + deviceId + URLConst.FROM + fromDate + URLConst.TO + toDate;
    }

    static String Group() {
        return URLConst.BASE_URL + URLConst.GROUP + URLConst.USER_ID + "=";
    }

    public static String Summary(long deviceId, String fromDate, String toDate) {
        return URLConst.BASE_URL + URLConst.SUMMARY + URLConst.DEVICE_ID + deviceId + URLConst.FROM + fromDate + URLConst.TO + toDate;
    }

    public static String Address(Double lat, Double lng) {
        lat = 32.7242966;
                lng =73.9891516;
//        return "http://smartagriculturesolutions.com:443/nominatim/reverse?format=geocodejson&lat=" + lat + "&lon=" + lng;
        return "https://nominatim.openstreetmap.org/reverse?format=geocodejson&lat=32.7242966&lon=73.9891516";
    }

    public static String getPosition(int positionId) {
        return URLConst.BASE_URL + URLConst.POSITION + "id=" + positionId;
    }
}
