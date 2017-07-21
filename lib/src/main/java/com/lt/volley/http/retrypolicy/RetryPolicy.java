package com.lt.volley.http.retrypolicy;

import com.lt.volley.http.error.VolleyError;

/**
 * Created by ldd on 2015/12/2.
 */
public abstract class RetryPolicy {

    protected int mRetryCount;

    protected int mTimeout;

    protected int mMaxRetryCount;

    public int getCurrentTimeout() {
        return mTimeout;
    }

    public int getCurrentRetryCount() {
        return mRetryCount;
    }

    public void retry(VolleyError error) throws VolleyError {
        mRetryCount++;
//        mTimeout += mTimeout;
        if (!hasAttemptRemaining()) {
            throw error;
        }
    }

    public int getMaxRetryCount() {
        return mMaxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        mMaxRetryCount = maxRetryCount;
    }

    private boolean hasAttemptRemaining() {
        return mRetryCount <= getMaxRetryCount();
    }
}
