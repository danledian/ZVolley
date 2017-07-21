package com.lt.volley.http;

import android.os.Process;
import android.util.Log;

import com.lt.volley.cache.BaseCache;
import com.lt.volley.cache.BaseEntity;
import com.lt.volley.http.error.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Administrator on 2015/12/3.
 */
public class NetworkDispatcher extends Thread {

    private static final String TAG = "NetworkDispatcher";

    private BlockingQueue<Request> mNetworkQueue;

    private Network mNetwork;

    private ResponseDelivery mResponseDelivery;

    private BaseCache mDiskCache;

    private BaseCache mMemCache;

    private boolean mFinish = false;

    public NetworkDispatcher(Network network, BlockingQueue<Request> networkQueue,
                             ResponseDelivery delivery, BaseCache memCache, BaseCache diskCache) {
        mNetworkQueue = networkQueue;
        mNetwork = network;
        mResponseDelivery = delivery;
        mDiskCache = diskCache;
        mMemCache = memCache;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        while(true) {
            Request request;
            try {
                request = mNetworkQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.w(TAG, "mNetworkQueue interrupted");
                if (mFinish) return;
                continue;
            }

            if (request.isCanceled()) {
                request.finish();
                continue;
            }

            try {
                NetworkResponse networkResponse = mNetwork.performRequest(request);
                request.setLifeTime(networkResponse.getNetworkTimeMs());
                if (request.isDelivered()) {
                    request.finish();
                    continue;
                }

                VolleyResponse volleyResponse;
                if (request instanceof DownloadRequest) {
                    volleyResponse = new VolleyFileResponse();
                    DownloadRequest downloadRequest = (DownloadRequest)request;
                    String path = downloadRequest.getLocal();
                    ((VolleyFileResponse)volleyResponse).saveFile(networkResponse, path, (VolleyFileResponse.Listener) downloadRequest.mListener);
                } else {
                    volleyResponse = new VolleyResponse();
                    volleyResponse.parseContent(networkResponse);
                }

                if (request.isNeedCache()) {
                    String cacheEntity;
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("softTtl", request.getSoftTtl());
                        jsonObject.put("cacheTime", request.getRequestStartTime());
                        jsonObject.put("content", volleyResponse.mContent);
                        cacheEntity = jsonObject.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        cacheEntity = "";
                    }
                    Log.d("Volley", cacheEntity);

                    BaseEntity entity = new BaseEntity(cacheEntity);
                    mMemCache.put(request.getCacheKey(), entity);
                    mDiskCache.put(request.getCacheKey(), entity);
                }
                Log.d("Volley", "delivery form http");

                request.setDelivered();

                mResponseDelivery.postResponse(request, volleyResponse, null);
            } catch (VolleyError e) {
                e.printStackTrace();
                mResponseDelivery.postError(request, e);
            }
        }
    }

    public void finish() {
        mFinish = true;
    }
}
