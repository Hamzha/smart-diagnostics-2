package util;

public class CommonConst {

    public static final String LONGITUDE = "longitute";
    public static final String LATITUDE = "latitude";
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;


    /************************Title Bar*************/
    public static final String VEHICLE_LIST = "Vehicle List";
    public static final String VEHICLE_LIST_COMPACT = "Vehicle List";

    /******************Code For Occasions*********************/
    public static final int SUCCESS_RESPONSE_CODE = 200;
    public static final int WRONG_CREDENTIALS_RESPONSE_CODE = 422;
    public static final int LATE_RESPONSE_CODE = 411;
    public static final int     NO_INTERNET_CODE = 500;
    public static final int ROUTE_ACTIVITY_ERROR = 312;
    static final int CALENDER_DAYS_ERROR = 340;

    /*****************Msg For Occasions*************************/
    public static final String NO_INTERNET_RESPONSE = "Check Internet Connection.";
    public static final String INVALID_CREDENTIALS_RESPONSE = "Wrong Username/Password!";
    public static final String INVALID_INPUTS_RESPONSE = "Invalid Input";
    public static final String LATE_RESPONSE = "Request Timed Out.";

    /**************Trip days****************************************/
    static final Integer[] TRIP_DAYS = new Integer[]{1, 7, 60};
    public static final String DATE_ENCODE_RESPONSE = "Communication Error.";
    /****************Trip Activity*******************************/
    public static final int DATE_ENCODE_CODE = 600;
    static final String REQUEST_ERROR_RESPONSE = "Communication Error.";
    /***************Request Error**********************************/
    static final int REQUEST_ERROR_CODE = 800;
    public static final String NO_DATA_RESPONSE = "Communication Error.";
    /**************No Immobilizer*********************************/
    static final int NO_IMMOBILIZER_CODE = 600;
    static final String NO_IMMOBILIZER_RESPONSE = "Park Lock not Available for This Device.";
    /************No Data****************************************/
    public static final int NO_DATA_CODE = 800;
    public static final String IMMOBILIZER_RESPONSE = "Communication Error.";
    /*************Immobilizer Issue***********************/
    public static final int IMMOBILIZER_CODE = 102;
    /************Activity Restart **************************/
    public static final int ACTIVITY_RETRY_CODE = 9;
    public static final int ACTIVITY_MOVE_BACK = -9;
    public static final int ACTIVITY_VOID = 0;
    /*************IMMOBILIZER***************************/
    static final String IMMOBILIZER_ENABLED = "immobilizer_enabled";
    static final String IMMOBILIZER_DISABLED = "immobilizer_disabled";
    /****************Stupid Errors*********************************/
    static final int ACTIVITY_ERROR_CODE = -100;
    static final String ACTIVITY_RESPONSE = "Please Restart the Application````````.";
    /************Vehicle list****************************/
    public static String TRACTOR = "tractor";


    /************************DAte Format*********************/
    public static final String dataFormatWithMonth = "MMM dd, yyyy\n(EEEE)";


}
