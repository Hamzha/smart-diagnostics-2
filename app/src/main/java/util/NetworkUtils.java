package util;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import callbackinterface.CommonInterface;

public class NetworkUtils {
    private Context context;
    private String TAG = ">>>" + NetworkUtils.class.getSimpleName();

    public NetworkUtils(Context context) {
        this.context = context;
    }

    public void post(String URL, int timer, final CommonInterface interact) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            interact.commonCallBack(response, CommonConst.SUCCESS_RESPONSE_CODE);
        }, volleyError -> {
            if (volleyError.networkResponse == null) {
                interact.commonCallBack(volleyError.getMessage(), CommonConst.LATE_RESPONSE_CODE);
            } else {
                interact.commonCallBack(volleyError.getMessage(), CommonConst.WRONG_CREDENTIALS_RESPONSE_CODE);
            }
        }) {
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(timer, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    public void get(String URL, int timer, final CommonInterface getInterface) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, response -> getInterface.commonCallBack(response, CommonConst.SUCCESS_RESPONSE_CODE), volleyError -> {
            if (volleyError.networkResponse == null) {
                getInterface.commonCallBack(volleyError.getMessage(), CommonConst.LATE_RESPONSE_CODE);
            } else {
                getInterface.commonCallBack(volleyError.getMessage(), CommonConst.REQUEST_ERROR_CODE);
            }
        }) {
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");

                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(timer, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void getJSON(String URL, int timer, final CommonInterface getInterface) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, response -> getInterface.commonCallBack(response, CommonConst.SUCCESS_RESPONSE_CODE), volleyError -> {
            if (volleyError.networkResponse == null) {
                getInterface.commonCallBack(volleyError.getMessage(), CommonConst.LATE_RESPONSE_CODE);
            } else {
                getInterface.commonCallBack(volleyError.getMessage(), CommonConst.REQUEST_ERROR_CODE);
            }
        }) {
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");

                return headers;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(timer, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    void delete(String URL, int timer, final CommonInterface callBackResponse) {

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, URL, response ->
                callBackResponse.commonCallBack(response, CommonConst.SUCCESS_RESPONSE_CODE),
                volleyError -> {
                    if (volleyError.networkResponse == null) {
                        callBackResponse.commonCallBack("Error", CommonConst.LATE_RESPONSE_CODE);
                    } else
                        callBackResponse.commonCallBack("Error", CommonConst.REQUEST_ERROR_CODE);
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(timer, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    void postWithStringBody(String URL, int timer, final String Body, final CommonInterface callBackResponse) {
        Common.logd(TAG,Body);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response ->
                callBackResponse.commonCallBack(response, CommonConst.SUCCESS_RESPONSE_CODE), volleyError -> {
            if (volleyError.networkResponse == null) {
                callBackResponse.commonCallBack("Error", CommonConst.LATE_RESPONSE_CODE);

            } else {
                callBackResponse.commonCallBack("Error", CommonConst.REQUEST_ERROR_CODE);

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return Body == null ? null : Body.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);

            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(timer, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    void putToken(String URL, int timer, final CommonInterface commonInterface) {
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, response -> commonInterface.commonCallBack(response, CommonConst.SUCCESS_RESPONSE_CODE), volleyError -> {
            commonInterface.commonCallBack(volleyError.getMessage(), volleyError.networkResponse.statusCode);
            //            if (volleyError.networkResponse == null) {
//                commonInterface.commonCallBack(null, CommonConst.LATE_RESPONSE_CODE);
//            } else {
//                commonInterface.commonCallBack(null, CommonConst.REQUEST_ERROR_CODE);
//            }
        }) {


            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(timer, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    public void putWithStringBody(String URL, int timer, final String Body, final CommonInterface callBackResponse) {
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, response -> callBackResponse.commonCallBack(response, CommonConst.SUCCESS_RESPONSE_CODE), volleyError -> {
            if (volleyError.networkResponse == null) {
                callBackResponse.commonCallBack(null, CommonConst.LATE_RESPONSE_CODE);
            } else {
                Common.logd(TAG, String.valueOf(volleyError.networkResponse.statusCode));
                callBackResponse.commonCallBack(null, CommonConst.REQUEST_ERROR_CODE);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() {
                return Body == null ? null : Body.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);

            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(timer, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }
}
