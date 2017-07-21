package com.lt.volley.http;

import android.os.SystemClock;
import android.util.Log;

import com.lt.volley.http.error.ServerError;
import com.lt.volley.http.error.TimeoutError;
import com.lt.volley.http.error.VolleyError;
import com.lt.volley.http.retrypolicy.RetryPolicy;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/3.
 */
public class BaseNetwork implements Network {

    private static final String TAG = "BaseNetwork";
    private static int DEFAULT_POOL_SIZE = 4096;
    protected final HttpStack mHttpStack;

    public BaseNetwork(HttpStack httpStack) {
        mHttpStack = httpStack;
    }

    @Override
    public NetworkResponse performRequest(Request request) throws VolleyError {
        long requestStart = SystemClock.elapsedRealtime();

        while (true) {
//            HttpResponse httpResponse = null;
//            Response response = null;
//            byte[] responseContents = null;
//            Map<String, String> responseHeaders = null;
            NetworkResponse networkResponse = null;
//            int statusCode = -1;
            try {

                Map<String, String> headers = new HashMap<>();
//                addCacheHeaders(headers, request.getCacheEntry());
                networkResponse = mHttpStack.performRequest(request, headers);
//                response = new OkHttpStack().performRequest(request, headers);
//                statusCode = response.code();
//                responseHeaders = convertHeaders(response.headers());
//                responseContents = response.body().bytes();
//                ResponseBody responseBody = response.body();
//                StatusLine statusLine = httpResponse.getStatusLine();
//                statusCode = statusLine.getStatusCode();
//                responseHeaders = convertHeaders(httpResponse.getAllHeaders());
//
//                HttpEntity entity = httpResponse.getEntity();
//
//                //TODO support 304
//
//                //handle 204
//                if (entity != null) {
//                    responseContents = entityToBytes(entity);
//                } else {
//                    // Add 0 byte response as a way of honestly representing a
//                    // no-content request.
//                    responseContents = new byte[0];
//                }
                int statusCode = networkResponse.getStatusCode();
                long requestLifetime = SystemClock.elapsedRealtime() - requestStart;
                networkResponse.setNetworkTimeMs(requestLifetime);
                if (statusCode < 200 || statusCode > 299) {
                    throw new IOException();
                }

                return networkResponse;
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                Log.e("Volley", "SocketTimeoutException");
                attemptRetryOnException("socket", request, new TimeoutError(e));
            } catch (ConnectException e) {
                e.printStackTrace();
                Log.e("Volley", "ConnectException");
                attemptRetryOnException("connect", request, new TimeoutError(e));
            } catch (MalformedURLException e) {
                throw new RuntimeException("Bad URL " + request.getUrl(), e);
            } catch (IOException e) {
                Log.w("Volley", android.util.Log.getStackTraceString(e));
//                if (responseContents == null) {
//                    responseContents = new byte[0];
//                }
                if (networkResponse == null) {
                    StatusLine statusLine = new StatusLine(500, "");
                    ResponseBody responseBody = new ResponseBody();
                    responseBody.setBytes(null);
                    networkResponse = new NetworkResponse(statusLine, responseBody, null);
                    networkResponse.setNetworkTimeMs(SystemClock.elapsedRealtime() - requestStart);
                }

                throw new ServerError(networkResponse);
            }
        }
    }

    private static void attemptRetryOnException(Object tag, Request request, VolleyError error) throws VolleyError{
        RetryPolicy retryPolicy = request.getRetryPolicy();
        retryPolicy.retry(error);
    }

    private byte[] entityToBytes(HttpEntity entity) throws IOException {
        byte[] buffer;
        ByteArrayOutputStream baos;
        try {
            InputStream in = entity.getContent();
            if (in == null) {
                Log.e(TAG, "entityToBytes: input stream is null");
                throw new IOException();
            }
            baos = new ByteArrayOutputStream();
            buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
            }
        } finally {
            try {
                // Close the InputStream and release the resources by "consuming the content".
                entity.consumeContent();
            } catch (IOException e) {
                // This can happen if there was an exception above that left the entity in
                // an invalid state.
                Log.v(TAG, "Error occurred when calling consumingContent");
            }

        }
        return baos.toByteArray();
    }

//    private void addCacheHeaders(Map<String, String> headers, Cache.Entry entry) {
//        // If there's no cache entry, we're done.
//        if (entry == null) {
//            return;
//        }
//
//        if (entry.etag != null) {
//            headers.put("If-None-Match", entry.etag);
//        }
//
//        if (entry.lastModified > 0) {
//            Date refTime = new Date(entry.lastModified);
//            headers.put("If-Modified-Since", DateUtils.formatDate(refTime));
//        }
//    }

    /**
     * Converts Headers[] to Map<String, String>.
     */
    protected static Map<String, String> convertHeaders(Header[] headers) {
        Map<String, String> result = new HashMap<>();
        for (Header header : headers) {
            result.put(header.getName(), header.getValue());
        }
        return result;
    }
//    protected static Map<String, String> convertHeaders(Headers headers) {
//        Map<String, String> result = new HashMap<>();
//        for (String name : headers.names()) {
//            result.put(name, headers.get(name));
//        }
//        return result;
//    }
}
