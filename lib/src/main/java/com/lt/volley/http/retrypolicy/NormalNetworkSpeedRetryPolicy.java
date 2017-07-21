package com.lt.volley.http.retrypolicy;

/**
 * Created by ldd on 2015/12/2.
 */
public class NormalNetworkSpeedRetryPolicy extends RetryPolicy{

    private static final int TIMEOUT = 10 * 1000;
    private static final int RETRY_COUNT = 1;

    public NormalNetworkSpeedRetryPolicy() {
        mTimeout = TIMEOUT;
        mMaxRetryCount = RETRY_COUNT;
    }

}
