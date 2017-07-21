package com.lt.volley.http;

import com.lt.volley.utils.Preconditions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ldd on 2016/10/20.
 */

public class Response {

    private StatusLine mStatusLine;
    private Map<String, String> mHeaders = new HashMap<>();
    private ResponseBody mResponseBody;

    public int getCode() {
        Preconditions.checkNotNull(mStatusLine);
        return mStatusLine.getCode();
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

    public void setStatusLine(StatusLine statusLine) {
        mStatusLine = statusLine;
    }

    public void setHeaders(Map<String, String> headers) {
        Preconditions.checkNotNull(headers);
        mHeaders.clear();
        mHeaders.putAll(headers);
    }

    public void addHeaders(String key, String value) {
        mHeaders.put(key, value);
    }

    public void setResponseBody(ResponseBody responseBody) {
        mResponseBody = responseBody;
    }
}
