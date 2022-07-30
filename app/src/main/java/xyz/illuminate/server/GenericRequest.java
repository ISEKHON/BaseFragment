package xyz.illuminate.server;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

public class GenericRequest extends Request<String> {

    private final Response.Listener<String> mListener;
    private final Map<String, String> mParams;

    public GenericRequest(String url, HashMap<String, String> mParams, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.mParams = mParams;
        mListener = listener;

    }

    public Map<String, String> getParams() {
        return mParams;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new String(response.data, HttpHeaderParser.parseCharset(response.headers)), HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
