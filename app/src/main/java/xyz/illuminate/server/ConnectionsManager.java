package xyz.illuminate.server;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

import xyz.illuminate.app.FileLog;

public class ConnectionsManager {

    @SuppressLint("StaticFieldLeak")
    private static ConnectionsManager Instance = null;
    private final Context context;
    private boolean ignoreRequests;
    private RequestQueue requestQueue;

    public ConnectionsManager(Context ctx) {
        context = ctx;
        ignoreRequests = false;
        requestQueue = getRequestQueue();
    }

    public static native long getCurrentTimeMillis();

    public static native int getTimeDifference();

    public static native int getCurrentTime();

    public static ConnectionsManager getInstance(Context ctx) {
        if (ConnectionsManager.Instance == null) {
            ConnectionsManager.Instance = new ConnectionsManager(ctx);
        }
        return ConnectionsManager.Instance;
    }

    public static ConnectionsManager getInstance() {
        if (Instance == null) {
            return null;
        }
        return Instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public void cancelAll(String s) {
        if (requestQueue != null) {
            requestQueue.cancelAll(s);
        }
    }

    public void cancelAll() {
        requestQueue.cancelAll(request -> {
            FileLog.d("request running. " + request.getTag());
            return true;
        });
    }

    public void setIgnoreRequests(boolean ignore) {
        ignoreRequests = ignore;
    }

    public void login(String email, String pass, RequestListener requestListener) {
        requestListener.getClass();
        LoginRequest tag = new LoginRequest(email, pass, requestListener::onResponse, error -> requestListener.onErrorResponse(error.getMessage()));
        tag.setTag(tag);
        addToRequestQueue(tag);
    }

    public void get(String url, RequestListener requestListener, String tag) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, requestListener::onResponse, error -> requestListener.onErrorResponse(error.getMessage()));
        stringRequest.setTag(tag);
        addToRequestQueue(stringRequest);
    }

    public void post(String url, HashMap<String, String> hashMap, RequestListener requestListener, String tag) {
        if (ignoreRequests) {
            return;
        }
        GenericRequest genericRequest = new GenericRequest(url, hashMap, requestListener::onResponse, error -> requestListener.onErrorResponse(error.getMessage()));
        genericRequest.setTag(tag);
        addToRequestQueue(genericRequest);
    }


    public interface RequestListener {
        void onErrorResponse(String error);

        void onResponse(Object response);

        void onResponse(byte[] bytes);
    }
}
