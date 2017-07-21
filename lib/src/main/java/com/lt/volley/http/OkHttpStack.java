package com.lt.volley.http;

import android.util.Log;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ldd on 2016/3/5.
 */
public class OkHttpStack {

    private OkHttpClient mOkHttpClient;

    public Response performRequest(Request request, Map<String, String> additionalHeaders)
            throws IOException {
        if (request == null) throw new NullPointerException("request cannot be null");

        initOkHttp(request);

        okhttp3.Request.Builder builder = new okhttp3.Request.Builder()
                .url(request.getUrl())
                .tag(request.getTag());

        for (String key : request.getHeaders().keySet()) {
            builder.header(key, request.getHeaders().get(key));
        }
        for (String key : additionalHeaders.keySet()) {
            builder.header(key, request.getHeaders().get(key));
        }
        okhttp3.Request okHttpRequest = setConnectionParametersForRequest(builder, request);

        return mOkHttpClient.newCall(okHttpRequest).execute();
    }

    private class SSLTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[] {};
        }
    }

    private void initOkHttp(Request request) {

        int timeout = request.getTimeoutMs();
        Log.d("okhttp", "timeout:" + timeout);

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] {new SSLTrustManager()}, new java.security.SecureRandom());
            mOkHttpClient = new OkHttpClient();
            mOkHttpClient = mOkHttpClient.newBuilder()
                    .sslSocketFactory(sc.getSocketFactory())
//                    //do not verify
//                    .hostnameVerifier(new HostnameVerifier() {
//                        @Override
//                        public boolean verify(String hostname, SSLSession session) {
//                            return true;
//                        }
//                    })
                    .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                    .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static okhttp3.Request setConnectionParametersForRequest(okhttp3.Request.Builder
                                                                  builder, Request request)
            throws IOException {
        switch (request.getMethod()) {
            case Request.Method.GET:
                builder.get();
                break;
            case Request.Method.DELETE:
                builder.delete();
                break;
            case Request.Method.POST:
                builder.post(createRequestBody(request));
                break;
            case Request.Method.PUT:
                builder.put(createRequestBody(request));
                break;
            case Request.Method.HEAD:
                builder.head();
                break;
            case Request.Method.OPTIONS:
                builder.method("OPTIONS", null);
                break;
            case Request.Method.TRACE:
                builder.method("TRACE", null);
                break;
            case Request.Method.PATCH:
                builder.patch(createRequestBody(request));
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
        return builder.build();
    }

    private static RequestBody createRequestBody(Request request) {
        final byte[] body = request.getBody();
        if (body == null) return null;

        return RequestBody.create(MediaType.parse(request.getBodyContentType()), body);
    }
}
