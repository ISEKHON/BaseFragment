package xyz.illuminate.server;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {

    private final Class<T> tClass;
    private final Gson gson;
    private final Map<String, String> mHeaders;
    private final Response.Listener<T> mListener;

    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(1, url, errorListener);
        gson = new Gson();
        tClass = clazz;
        mHeaders = headers;
        mListener = listener;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(gson.fromJson(new String(response.data, HttpHeaderParser.parseCharset(response.headers)), tClass), HttpHeaderParser.parseCacheHeaders(response));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (UnsupportedEncodingException e2) {
            return Response.error(new ParseError(e2));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> map = mHeaders;
        if (map == null) {
            map = super.getHeaders();
        }
        return map;
    }

}
