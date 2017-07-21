package com.lt.volley.http.error;

import com.lt.volley.http.NetworkResponse;
import com.lt.volley.http.ResponseBody;
import com.lt.volley.http.StatusLine;

/**
 * Created by ldd on 2015/12/6.
 */
public class NoConnectionError extends VolleyError {
    public NoConnectionError(Throwable cause) {
        super(cause);
        StatusLine statusLine = new StatusLine(404, "");
        ResponseBody responseBody = new ResponseBody();
        responseBody.setBytes("NoConnectionError".getBytes());
        mNetworkResponse = new NetworkResponse(statusLine, responseBody, null);
//        mNetworkResponse = new NetworkResponse(404, "NoConnectionError".getBytes(), null, false, 0);
    }

    @Override
    public int getType() {
        return NO_CONNECTION_ERROR;
    }
}
