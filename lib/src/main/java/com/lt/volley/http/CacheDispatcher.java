package com.lt.volley.http;

import android.os.Process;
import android.util.Log;

import com.lt.volley.cache.BaseCache;
import com.lt.volley.cache.BaseEntity;
import com.lt.volley.utils.BaseUtil;

import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Administrator on 2015/12/3.
 */
public class CacheDispatcher extends Thread {

    private static final String TAG = "CacheDispatcher";

    private BlockingQueue<Request> mDiskCacheQueue;

    private BlockingQueue<Request> mNetworkQueue;

    private ResponseDelivery mResponseDelivery;

    private BaseCache mDiskCache;

    private BaseCache mMemCache;

    private boolean mFinish = false;

    public CacheDispatcher(BlockingQueue<Request> cacheQueue, BlockingQueue<Request> networkQueue,
                           ResponseDelivery responseDelivery, BaseCache memCache, BaseCache diskCache) {
        mDiskCacheQueue = cacheQueue;
        mNetworkQueue = networkQueue;
        mResponseDelivery = responseDelivery;
        mDiskCache = diskCache;
        mMemCache = memCache;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while(true) {
            Request request;
            try {
                request = mDiskCacheQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.w(TAG, "mDiskCacheQueue interrupted");
                if (mFinish) return;
                continue;
            }

            if (request.isCanceled()) {
                request.finish();
                continue;
            }

            try {
                //处理某些情况下强制刷新缓存
                if (request.isForceUpdate()) {
                    mNetworkQueue.put(request);
                    continue;
                }

                BaseEntity entity;
                entity = mMemCache.get(request.getCacheKey());
                if (entity == null) {
                    entity = mDiskCache.get(request.getCacheKey());
                }
                if (entity == null) {
                    mNetworkQueue.put(request);
                    continue;
                }

//                VolleyResponse volleyResponse = new VolleyResponse(BaseUtil.parseToString(entity.getBytes()));
                VolleyResponse volleyResponse = new VolleyResponse();
                volleyResponse.parseContent(entity.getBytes());
                JSONObject jsonObject = new JSONObject(volleyResponse.mContent);
                long softTtl = 0, cacheTime = 0;
                if (jsonObject.has("softTtl")) {
                    softTtl = jsonObject.getLong("softTtl");
                }
                if (jsonObject.has("cacheTime")) {
                    cacheTime = jsonObject.getLong("cacheTime");
                }

                if (softTtl != 0 && cacheTime != 0 &&
                    softTtl + cacheTime < request.getRequestStartTime()) {
                    Log.d(TAG, "cache out of date");
                    mNetworkQueue.put(request);
                    continue;
                }
                //Cache data is not expired
                if (jsonObject.has("content")) {
                    volleyResponse.mContent = jsonObject.getString("content");
                }

//                if (TextUtils.isEmpty(response.mContent)) {
//                    mNetworkQueue.put(request);
//                    continue;
//                }

                Log.d(TAG, "delivery form cache");
                mResponseDelivery.postResponse(request, volleyResponse, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void finish() {
        mFinish = true;
    }
}
