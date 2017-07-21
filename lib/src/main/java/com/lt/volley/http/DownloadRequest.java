package com.lt.volley.http;

import java.security.InvalidParameterException;

/**
 * Json请求类
 * Created by ldd on 2015/12/3.
 */
public class DownloadRequest extends Request {

    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", DEFAULT_ENCODING);
    private final String mLocal;

    private DownloadRequest(Builder builder) {
        mProtocolContentType = PROTOCOL_CONTENT_TYPE;
        mMethod = Method.GET;
        mUrl = builder.mUrl;
        mLocal = builder.mLocal;
        mListener = builder.mListener;
    }

    public String getLocal() {
        return mLocal;
    }

    @Override
    public String getBodyContentType() {
        return mProtocolContentType;
    }

    @Override
    public byte[] getBody() {
        return null;
    }

    public static class Builder {
        private String mUrl;
        private String mLocal;
        private VolleyFileResponse.Listener mListener;

        public Builder setUrl(String url) {
            mUrl = url;
            return this;
        }

        public Builder setLocal(String local) {
            mLocal = local;
            return this;
        }

        public Builder onResponse(VolleyFileResponse.Listener listener) {
            mListener = listener;
            return this;
        }

        public DownloadRequest build() {
            if (mUrl == null) throw new InvalidParameterException("url cannot be null");
            if (mLocal == null) throw new InvalidParameterException("local file cannot be null");

            return new DownloadRequest(this);
        }
    }

}
