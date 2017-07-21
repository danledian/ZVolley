package com.lt.volley.http.error;

import com.lt.volley.http.NetworkResponse;

/**
 * Created by ldd on 2015/12/4.
 */
public abstract class VolleyError extends Exception {

    public static final int SERVER_ERROR = 0;
    public static final int TIMEOUT_ERROR = 1;
    public static final int NO_CONNECTION_ERROR = 2;
    public static final int DECODE_ERROR = 3;

    public NetworkResponse mNetworkResponse;

    public VolleyError(Throwable cause) {
        super(cause);
    }

    public VolleyError(NetworkResponse response) {
        mNetworkResponse = response;
    }

    abstract public int getType();
}
