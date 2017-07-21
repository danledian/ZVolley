package com.lt.volley.http;

import android.os.Handler;
import android.os.Looper;

import com.lt.volley.cache.BaseCache;
import com.lt.volley.cache.MemCache;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by ldd on 2015/12/4.
 */
public class RequestQueue {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private BlockingQueue<Request> mDiskCacheQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<Request> mNetworkQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<Request> mRetryQueue = new LinkedBlockingDeque<>();

    /**
     * The set of current requests
     */
    private final Set<Request> mCurrentRequests = new HashSet<>();

    /**
     *  The network dispatchers.
     */
    private NetworkDispatcher[] mNetworkDispatchers;

    private Network mNetwork;

    private BaseCache mDiskCache;

    private BaseCache mMemCache;

    /**
     *  The cache dispatcher.
     */
    private CacheDispatcher mDiskCacheDispatcher;

    private ResponseDelivery mResponseDelivery;

    public RequestQueue(Network network, BaseCache diskCache) {
        this(network, new MemCache(), diskCache);
    }

    public RequestQueue(Network network, BaseCache memCache, BaseCache diskCache) {
        mNetwork = network;
        mNetworkDispatchers = new NetworkDispatcher[CPU_COUNT];
        mResponseDelivery = new DefaultResponseDelivery(new Handler(Looper.getMainLooper()));
        mDiskCache = diskCache;
        mMemCache = memCache;
    }

    public void start() {
        stop();
        mDiskCacheDispatcher = new CacheDispatcher(mDiskCacheQueue, mNetworkQueue,
                mResponseDelivery, mMemCache, mDiskCache);
        mDiskCacheDispatcher.start();

        for (NetworkDispatcher networkDispatcher: mNetworkDispatchers) {
            networkDispatcher = new NetworkDispatcher(mNetwork, mNetworkQueue,
                    mResponseDelivery, mMemCache, mDiskCache);
            networkDispatcher.start();
        }
    }

    public void stop() {
        if (mDiskCacheDispatcher != null) {
            mDiskCacheDispatcher.finish();
        }

        for (NetworkDispatcher networkDispatcher: mNetworkDispatchers){
            if (networkDispatcher != null) {
                networkDispatcher.finish();
            }
        }
    }


    public void add(Request request) {
        request.setRequestQueue(this);
        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
        }

        if (!request.isNeedCache()) {
            mNetworkQueue.add(request);
            return;
        }

        mDiskCacheQueue.add(request);
    }

    public void finish(Request request) {
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }
    }

    /**
     * A simple predicate or filter interface for Requests, for use by
     * {@link RequestQueue#cancelAll(RequestFilter)}.
     */
    public interface RequestFilter {
        boolean apply(Request request);
    }

    /**
     * Cancels all requests in this queue for which the given filter applies.
     * @param filter The filtering function to use
     */
    public void cancelAll(RequestFilter filter) {
        synchronized (mCurrentRequests) {
            for (Request request : mCurrentRequests) {
                if (filter.apply(request)) {
                    request.setCanceled(true);
                }
            }
        }
    }

    /**
     * Cancels all requests in this queue with the given tag. Tag must be non-null
     * and equality is by identity.
     */
    public void cancelAll(final Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Cannot cancelAll with a null tag");
        }
        cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request request) {
                return request.getTag() == tag;
            }
        });
    }

}
