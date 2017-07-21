package com.lt.volley.http;

import org.json.JSONException;

/**
 * Created by ldd on 2015/12/22.
 */
public abstract class HttpBaseEntity {

    protected static final String TAG = "HttpBaseEntity";

    public void decode(String msg) throws JSONException{
        logMessage(msg);
        fromWebString(msg);
    }

    public abstract void logMessage(String message);

    public abstract void fromWebString(String msg) throws JSONException;
}
