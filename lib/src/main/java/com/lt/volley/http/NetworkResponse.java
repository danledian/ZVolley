package com.lt.volley.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ldd on 2015/11/30.
 */
public class NetworkResponse {

    private byte[] mData;

    private StatusLine mStatusLine;
    private Map<String, String> mHeaders = new HashMap<>();
    private ResponseBody mResponseBody;

    private long mNetworkTimeMs = 0;

    public NetworkResponse(int statusCode, byte[] data, Map<String, String> headers, boolean notModify, long networkTimeMs) {
        mStatusLine = new StatusLine(statusCode, "");
        mData = data;
        mHeaders.clear();
        mHeaders.putAll(headers);
        mNetworkTimeMs = networkTimeMs;
    }

    public NetworkResponse(StatusLine statusLine,
                           ResponseBody responseBody,
                           Map<String, String> headers) {
        mStatusLine = statusLine;
        mResponseBody = responseBody;
        mHeaders.clear();
        if (headers != null) {
            mHeaders.putAll(headers);
        }
    }

    public void setNetworkTimeMs(long networkTimeMs) {
        mNetworkTimeMs = networkTimeMs;
    }

    public int getStatusCode() {
        return mStatusLine.getCode();
    }

    public byte[] getStatusMessage(){
        return mStatusLine.getMessage().getBytes();
    }

    public byte[] getData() {
        if (mResponseBody == null) {
            return new byte[0];
        }
        return mResponseBody.getString().getBytes();
    }

    long getNetworkTimeMs() {
        return mNetworkTimeMs;
    }

    public void setStatusLine(StatusLine statusLine) {
        mStatusLine = statusLine;
    }

    public StatusLine getStatusLine() {
        return mStatusLine;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public ResponseBody getResponseBody() {
        return mResponseBody;
    }

}
