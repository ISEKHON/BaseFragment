package xyz.illuminate.server;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

public class InputStreamRequest extends Request<byte[]> {

    private final Response.Listener<byte[]> mListener;
    private final Map<String, String> mParams;
    public Map<String, String> responseHeaders;

    public InputStreamRequest(int method, String url, Response.Listener<byte[]> listener, Response.ErrorListener errorListener, HashMap<String, String> params) {
        super(method, url, errorListener);
        setShouldCache(false);
        mListener = listener;
        mParams = params;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        responseHeaders = response.headers;
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    protected Map<String, String> getParams() {
        return mParams;
    }

    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }
}
