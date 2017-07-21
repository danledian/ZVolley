package com.lt.volley.http;

import com.lt.volley.http.error.VolleyError;

/**
 * Created by ldd on 2015/12/3.
 */
public interface Network {
    /**
     * Performs the specified request.
     * @param request Request to process
     * @return A {@link NetworkResponse} with data and caching metadata; will never be null
     */
    NetworkResponse performRequest(Request request) throws VolleyError;
}
