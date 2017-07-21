package com.lt.volley.http;

import android.net.Network;
import android.util.Log;

import com.lt.volley.http.error.VolleyError;
import com.lt.volley.http.retrypolicy.NormalNetworkSpeedRetryPolicy;
import com.lt.volley.http.retrypolicy.RetryPolicy;
import com.lt.volley.utils.BaseUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class Request{

    public static final String DEFAULT_ENCODING = "utf-8";

    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/x-www-form-urlencoded; charset=%s", DEFAULT_ENCODING);

    /**
     * whether request is canceled
     */
    protected boolean isCanceled = false;

    protected boolean isNeedCache = false;

    protected boolean forceUpdate = false;

    protected boolean isDelivered = false;

    protected long mRequestStartTime = System.currentTimeMillis();

    protected long mSoftTtl = 1000 * 60 * 60 * 24;

    protected String mProtocolContentType;

    protected int mMethod;

    protected String mUrl;

    protected Object mTag;

    protected String mCacheKey;

    protected Map<String, String> mParams;

    protected Map<String, String> mHeaders = new HashMap<>();

    protected VolleyResponse.Listener mListener;

    protected RequestQueue mRequestQueue;

    protected HttpBaseEntity mHttpBaseEntity;

    protected RetryPolicy mRetryPolicy = new NormalNetworkSpeedRetryPolicy();

    protected long mLifeTime;

    protected android.net.Network mNetwork;

    /**
     * Supported request methods.
     */
    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    public Request() {
        mProtocolContentType = PROTOCOL_CONTENT_TYPE;
    }

    public long getRequestStartTime() {
        return mRequestStartTime;
    }

    public void setRequestStartTime(long requestStartTime) {
        mRequestStartTime = requestStartTime;
    }

    public long getSoftTtl() {
        return mSoftTtl;
    }

    public void setSoftTtl(long softTtl) {
        mSoftTtl = softTtl;
    }

    public void setMethod(int method) {
        mMethod = method;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setParams(Map<String, String> params) {
        mParams = params;
    }

    public void setHeaders(Map<String, String> headers) {
        mHeaders.clear();
        mHeaders.putAll(headers);
    }

    public void addHeader(String key, String value) {
        mHeaders.put(key, value);
    }

    public void setResponse(VolleyResponse.Listener listener) {
        mListener = listener;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        if (retryPolicy == null) throw new NullPointerException("retryPolicy cannot be null");
        mRetryPolicy = retryPolicy;
    }

    public Network getNetwork() {
        return mNetwork;
    }

    public void setNetwork(Network mNetwork) {
        this.mNetwork = mNetwork;
    }

    public void requestSuccess(String entity) {
        if (mListener == null) return;
        mListener.onSuccess(entity);
    }

    public void requestError(VolleyError error) {
        if (mListener == null) return;
        mListener.onError(error);
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public int getMethod() {
    	return mMethod;
    }
    
    public String getUrl() {
    	return mUrl;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public int getTimeoutMs() {
        return mRetryPolicy.getCurrentTimeout();
    }

    public RetryPolicy getRetryPolicy() {
        return mRetryPolicy;
    }

    public byte[] getBody() {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    protected static byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        try {
            return stitchParameters(params, paramsEncoding).getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    protected static String stitchParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value, key;
                if (paramsEncoding == null) {
                    value = entry.getValue();
                    key = entry.getKey();
                } else {
                    value = URLEncoder.encode(entry.getValue(), paramsEncoding);
                    key = URLEncoder.encode(entry.getKey(), paramsEncoding);
                }
                encodedParams.append(key);
                encodedParams.append('=');
                encodedParams.append(value);
                encodedParams.append('&');
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
        return encodedParams.toString();
    }

    protected String getParamsEncoding() {
        return DEFAULT_ENCODING;
    }

    public String getBodyContentType() {
        return mProtocolContentType;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
    }

    public void finish() {
        if (mRequestQueue != null) {
            mRequestQueue.finish(this);
        }
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public boolean isNeedCache() {
        return isNeedCache;
    }

    public void setNeedCache(String method) {
        String string = mUrl + stitchParameters(mParams, DEFAULT_ENCODING) + method;
        mCacheKey = BaseUtil.hashKeyForDisk(string);
        isNeedCache = true;
    }

    public void setDelivered() {
        isDelivered = true;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public String getCacheKey() {
        return mCacheKey;
    }

    public HttpBaseEntity getHttpBaseEntity() {
        return mHttpBaseEntity;
    }

    public void setHttpBaseEntity(HttpBaseEntity httpBaseEntity) {
        mHttpBaseEntity = httpBaseEntity;
    }

    private Class<?> mType;

    public void setResponseType(Class<?> cls) {
        mType = cls;
    }

    public Class<?> getResponseType() {
        return mType;
    }

    public long getLifeTime() {
        return mLifeTime;
    }

    public void setLifeTime(long lifeTime) {
        mLifeTime = lifeTime;
    }

    public static class Builder {
        private int mMethod;
        private String mUrl;
        private VolleyResponse.Listener mListener;
        private Map<String, String> mParams = new HashMap<>();
        private Map<String, String> mHeaders = new HashMap<>();

        public Builder setMethod(int method) {
            mMethod = method;
            return this;
        }

        public Builder setUrl(String url) {
            mUrl = url;
            return this;
        }

        public Builder setParams(Map<String, String> params) {
            if (params == null) throw new InvalidParameterException("params cannot be null");
            mParams.clear();
            mParams.putAll(params);
            return this;
        }

        public Builder addParams(Map<String, String> params) {
            if (params == null) throw new InvalidParameterException("params cannot be null");
            mParams.putAll(params);
            return this;
        }

        public Builder addParams(String key, String value) {
            if (key == null) throw new InvalidParameterException("key cannot be null");
            if (value == null) throw new InvalidParameterException("value cannot be null");

            mParams.put(key, value);
            return this;
        }

        public Builder setHeader(Map<String, String> header) {
            if (header == null) throw new InvalidParameterException("header cannot be null");
            mHeaders.clear();
            mHeaders.putAll(header);
            return this;
        }

        public Builder addHeader(Map<String, String> header) {
            if (header == null) throw new InvalidParameterException("header cannot be null");
            mHeaders.putAll(header);
            return this;
        }

        public Builder addHeader(String key, String value) {
            if (key == null) throw new InvalidParameterException("key cannot be null");
            if (value == null) throw new InvalidParameterException("value cannot be null");

            mHeaders.put(key, value);
            return this;
        }

        public Builder onResponse(VolleyResponse.Listener listener) {
            mListener = listener;
            return this;
        }

        public Request build() {
            if (mMethod < Method.DEPRECATED_GET_OR_POST || mMethod > Method.PATCH)
                throw new InvalidParameterException("method not support!");
            if (mUrl == null) throw new InvalidParameterException("url cannot be null");

            Request request = new Request();
            if (mMethod == Method.GET && !mParams.isEmpty()) {
                mUrl = mUrl + "?" + stitchParameters(mParams, null);
            }
            Log.d("Volley", mUrl);
            request.setUrl(mUrl);
            request.setMethod(mMethod);
            request.setParams(mParams);
            request.setHeaders(mHeaders);
            request.setResponse(mListener);
            return request;
        }
    }


    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", mMethod);
            jsonObject.put("content", mParams);
            jsonObject.put("headers", mHeaders);
            jsonObject.put("url", mUrl);
            jsonObject.put("protocol", mProtocolContentType);
            jsonObject.put("tag", mTag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
