package xyz.illuminate.server;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.BuildVars;

public class LoginRequest extends Request<String> {

    private final Response.Listener<String> mListener;
    private final Map<String, String> mParams = new HashMap<>();

    public LoginRequest(String email, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(1, BuildVars.DB + "login.php", errorListener);
        mListener = listener;
        mParams.put("userID", email);
        mParams.put("keyword", password);
        mParams.put("device_id", AndroidUtilities.getUniqueId());
        mParams.put("sha256", AndroidUtilities.getCertificateSHA256Fingerprints());

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

    public Map<String, String> getParams() {
        return mParams;
    }
}
