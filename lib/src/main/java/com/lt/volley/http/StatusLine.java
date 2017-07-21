package com.lt.volley.http;

/**
 * Created by ldd on 2016/10/21.
 */

public class StatusLine {

    private final int mCode;
    private final String mMessage;
    private final String mProtocol;

    public StatusLine(int code, String message) {
        mCode = code;
        mProtocol = "HTTP/1.1";
        mMessage = message;
    }

    public StatusLine(int code, String message, String protocol) {
        mCode = code;
        mMessage = message;
        mProtocol = protocol;
    }

    public int getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getProtocol() {
        return mProtocol;
    }

    @Override
    public String toString() {
        return mProtocol + " " + mCode + " " + mMessage;
    }
}
