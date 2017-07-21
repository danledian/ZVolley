package com.lt.volley.http;

import android.util.Log;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

/**
 * Json请求类
 * Created by ldd on 2015/12/3.
 */
public class JsonRequest extends Request {

    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", DEFAULT_ENCODING);

    public JsonRequest() {
        mProtocolContentType = PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public String getBodyContentType() {
        return mProtocolContentType;
    }

    @Override
    public byte[] getBody() {
        Map<String, String> params = getParams();
        return params.get("json").getBytes();
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

            JsonRequest request = new JsonRequest();
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

}
