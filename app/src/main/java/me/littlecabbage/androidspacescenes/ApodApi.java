package me.littlecabbage.androidspacescenes;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Wrapper for the NASA Astronomy Photo of the Day API
 */
public class ApodApi {
    private static final String ROOT_URL = "https://api.nasa.gov/planetary/apod?";
    private static final String API_KEY_PARAM = "api_key";
    private static final String DATE_PARAM = "date";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Uri ROOT_URI = Uri.parse(ROOT_URL);

    private static HashMap<String, ApodApi> instances;

    private String key;

    private class CallbackListener implements Response.Listener<JSONObject> {
        VoidCallBack<JSONObject> callback;

        CallbackListener(VoidCallBack<JSONObject> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(JSONObject response) {
            callback.call(response);
        }
    }

    private class CallbackErrorListener implements Response.ErrorListener {
        VoidCallBack<VolleyError> callback;

        CallbackErrorListener(VoidCallBack<VolleyError> callback) {
            this.callback = callback;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            callback.call(error);
        }
    }

    private ApodApi(String key) {
        this.key = key;
    }

    private String getUrl() {
        return getUrl(new Date());
    }

    private String getUrl(Date date) {
        return ROOT_URI.buildUpon()
                .appendQueryParameter(API_KEY_PARAM, key)
                .appendQueryParameter(DATE_PARAM, DATE_FORMAT.format(date))
                .build()
                .toString();
    }

    public static ApodApi getInstance(String key) {
        if (instances == null) {
            instances = new HashMap<>();
        }
        if (!instances.containsKey(key)) {
            instances.put(key, new ApodApi(key));
        }
        return instances.get(key);
    }

    public void getPhoto(Context context, VoidCallBack<JSONObject> callback, VoidCallBack<VolleyError> errorHandler) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
            Request.Method.GET,
            getUrl(),
            null,
            new CallbackListener(callback),
            new CallbackErrorListener(errorHandler)
        );
        Volley.newRequestQueue(context).add(jsObjRequest);
    }

    public void getPhoto(Context context, Date date, VoidCallBack<JSONObject> callback, VoidCallBack<VolleyError> errorHandler) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                getUrl(date),
                null,
                new CallbackListener(callback),
                new CallbackErrorListener(errorHandler)
        );
        Volley.newRequestQueue(context).add(jsObjRequest);
    }
}
