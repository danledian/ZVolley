package com.lt.volley.http;

import android.content.Context;

import com.lt.volley.cache.DiskCache;
import com.lt.volley.cache.MemCache;

/**
 * Created by Administrator on 2015/12/4.
 */
public class Volley {

//    private static Context sContext;
//
//    public static Context getContext() {
//        return sContext;
//    }
    private static ByteArrayPool sPool;

    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }

    public static RequestQueue newRequestQueue(Context context, HttpStack httpStack) {
//        sContext = context;
        if (httpStack == null) {
            httpStack = new HurlStack();
        }
        sPool = new ByteArrayPool(4096);

        Network network = new BaseNetwork(httpStack);
        RequestQueue requestQueue = new RequestQueue(network, new MemCache(), new DiskCache(context));
        requestQueue.start();

        return requestQueue;
    }

    public static ByteArrayPool getPool() {
        return sPool;
    }

}
