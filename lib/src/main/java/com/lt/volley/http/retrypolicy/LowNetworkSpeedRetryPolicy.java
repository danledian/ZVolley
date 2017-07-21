package com.lt.volley.http.retrypolicy;

/**
 * Created by ldd on 2015/12/2.
 */
public class LowNetworkSpeedRetryPolicy extends RetryPolicy{

    private static final int TIMEOUT = 15 * 1000;
    private static final int RETRY_COUNT = 1;

    public LowNetworkSpeedRetryPolicy() {
        mTimeout = TIMEOUT;
        mMaxRetryCount = RETRY_COUNT;
    }

}
