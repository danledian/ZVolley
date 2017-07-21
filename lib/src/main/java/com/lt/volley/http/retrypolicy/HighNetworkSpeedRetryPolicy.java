package com.lt.volley.http.retrypolicy;

/**
 * Created by ldd on 2015/12/2.
 */
public class HighNetworkSpeedRetryPolicy extends RetryPolicy{

    private static final int TIMEOUT = 5 * 1000;
    private static final int RETRY_COUNT = 2;

    public HighNetworkSpeedRetryPolicy() {
        mTimeout = TIMEOUT;
        mMaxRetryCount = RETRY_COUNT;
    }

}
