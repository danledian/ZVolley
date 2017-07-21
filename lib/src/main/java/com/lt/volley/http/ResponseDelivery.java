package com.lt.volley.http;

import com.lt.volley.http.error.VolleyError;

/**
 * Created by Administrator on 2015/12/4.
 */
public interface ResponseDelivery {

    void postResponse(Request request, VolleyResponse volleyResponse, Runnable runnable);

    void postError(Request request, VolleyError error);

}
