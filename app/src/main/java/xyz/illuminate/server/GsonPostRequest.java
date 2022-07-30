package xyz.illuminate.server;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class GsonPostRequest<T> extends Request<T> {

    private final Class<T> mClazz;
    private final Object mDataIn;
    private final Gson gson;
    private final Response.Listener<T> mListener;
    private final Map<String, String> mParams;

    public GsonPostRequest(String url, Object dataIn, Class<T> clazz, Map<String, String> prams, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        gson = new Gson();
        mDataIn = dataIn;
        mClazz = clazz;
        mParams = prams;
        mListener = listener;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(gson.fromJson(new String(response.data, HttpHeaderParser.parseCharset(response.headers)), mClazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e2) {
            return Response.error(new ParseError(e2));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return gson.toJson(mDataIn).getBytes();
    }

    public Map<String, String> getParams() {
        return mParams;
    }
}
