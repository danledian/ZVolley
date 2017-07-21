package com.lt.volley.http;

import android.util.Log;

import com.lt.volley.utils.Preconditions;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HurlStack implements HttpStack {

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String TAG = "HurlStack";

    public HurlStack() {}

    @Override
    public NetworkResponse performRequest(Request request, Map<String, String> additionalHeaders)
            throws IOException {
        Preconditions.checkNotNull(request, "request cannot be null");
        Map<String, String> headers = new HashMap<>();
        headers.putAll(request.getHeaders());
        headers.putAll(additionalHeaders);

        URL url = new URL(request.getUrl());
        HttpURLConnection httpURLConnection = initHttpURlConnection(url, request);
        for (String key: headers.keySet()) {
            httpURLConnection.addRequestProperty(key, headers.get(key));
        }
        setConnectionParametersForRequest(httpURLConnection, request);
//        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        int responseCode = httpURLConnection.getResponseCode();
        if (responseCode == -1) {
            // -1 is returned by getResponseCode() if the response code could not be retrieved.
            // Signal to the caller that something was wrong with the connection.
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        StatusLine statusLine = getStatusLine(httpURLConnection);
        ResponseBody responseBody = null;
        if (hasResponseBody(request.getMethod(), statusLine.getCode())) {
            responseBody = getBodyFromConnect(httpURLConnection);
        }
        Map<String, String> responseHeaders = getHeader(httpURLConnection);
        return new NetworkResponse(statusLine, responseBody, responseHeaders);
//        StatusLine responseStatus = new BasicStatusLine(protocolVersion,
//                httpURLConnection.getResponseCode(), httpURLConnection.getResponseMessage());
//        BasicHttpResponse response = new BasicHttpResponse(responseStatus);
//        if (hasResponseBody(request.getMethod(), responseStatus.getStatusCode())) {
//            response.setEntity(entityFromConnection(httpURLConnection));
//        }
//        for (Map.Entry<String, List<String>> header : httpURLConnection.getHeaderFields().entrySet()) {
//            if (header.getKey() != null) {
//                Header h = new BasicHeader(header.getKey(), header.getValue().get(0));
//                response.addHeader(h);
//            }
//        }

//        Log.d(TAG, new String(readInputStream(response.getEntity().getContent()), "utf-8"));

//        return response;
    }

    public static byte[] readInputStream(InputStream inStream){

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while( (len = inStream.read(buffer)) !=-1 ){
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();//网页的二进制数据
            outStream.close();
            inStream.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private HttpURLConnection initHttpURlConnection(URL url, Request request) throws IOException {
//        SSLContext sc = null;
//        try {
//            sc = SSLContext.getInstance("TLS");
//            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (sc != null) {
//            HttpURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//            HttpURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
//        }

        HttpURLConnection httpURLConnection;

        if(request.getNetwork() == null){
            Log.d(TAG, "getNetwork null");
            httpURLConnection = (HttpURLConnection) url.openConnection();
        }else {
            Log.d(TAG, "getNetwork not null");
            httpURLConnection = (HttpURLConnection)request.getNetwork().openConnection(new URL(request.getUrl()));
        }

//        httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
        Log.d("Volley", "timeout:" + request.getTimeoutMs());
        httpURLConnection.setConnectTimeout(request.getTimeoutMs());
        httpURLConnection.setReadTimeout(request.getTimeoutMs());
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true);

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] {new SSLTrustManager()}, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if ("https".equals(url.getProtocol()) && mSslSocketFactory != null) {
//            ((HttpURLConnection)httpURLConnection).setSSLSocketFactory(mSslSocketFactory);
//        }

        return httpURLConnection;
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

    private static boolean hasResponseBody(int requestMethod, int responseCode) {
        return requestMethod != Request.Method.HEAD
                && !(HttpStatus.SC_CONTINUE <= responseCode && responseCode < HttpStatus.SC_OK)
                && responseCode != HttpStatus.SC_NO_CONTENT
                && responseCode != HttpStatus.SC_NOT_MODIFIED;
    }

    private static ResponseBody getBodyFromConnect(HttpURLConnection connection) {
        ResponseBody responseBody = new ResponseBody();
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException ioe) {
            inputStream = connection.getErrorStream();
        }
        responseBody.setInputStream(inputStream);
        responseBody.setContentLength(connection.getContentLength());
        responseBody.setEncode(connection.getContentEncoding());
        responseBody.setContentType(connection.getContentType());
        return responseBody;
    }

    private static com.lt.volley.http.StatusLine getStatusLine(HttpURLConnection connection) throws IOException {
        return new com.lt.volley.http.StatusLine(connection.getResponseCode(), connection.getResponseMessage());
    }

    private static Map<String, String> getHeader(HttpURLConnection connection) throws IOException {
        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                headers.put(header.getKey(), header.getValue().get(0));
            }
        }
        return headers;
    }

//    private static HttpEntity entityFromConnection(HttpURLConnection connection) {
//        BasicHttpEntity entity = new BasicHttpEntity();
//        InputStream inputStream;
//        try {
//            inputStream = connection.getInputStream();
//        } catch (IOException ioe) {
//            inputStream = connection.getErrorStream();
//        }
//        entity.setContent(inputStream);
//        entity.setContentLength(connection.getContentLength());
//        entity.setContentEncoding(connection.getContentEncoding());
//        entity.setContentType(connection.getContentType());
//        return entity;
//    }

    private static void setConnectionParametersForRequest(HttpURLConnection connection,
                                                  Request request) throws IOException {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_OR_POST:
                // This is the deprecated way that needs to be handled for backwards compatibility.
                // If the request's post body is null, then the assumption is that the request is
                // GET.  Otherwise, it is assumed that the request is a POST.
//                byte[] postBody = request.getPostBody();
//                if (postBody != null) {
//                    // Prepare output. There is no need to set Content-Length explicitly,
//                    // since this is handled by HttpURLConnection using the size of the prepared
//                    // output stream.
//                    connection.setDoOutput(true);
//                    connection.setRequestMethod("POST");
//                    connection.addRequestProperty(HEADER_CONTENT_TYPE,
//                            request.getPostBodyContentType());
//                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
//                    out.write(postBody);
//                    out.close();
//                }
                break;
            case Request.Method.GET:
                // Not necessary to set the request method because connection defaults to GET but
                // being explicit here.
                connection.setRequestMethod("GET");
//                addBodyIfExists(connection, request);
                break;
            case Request.Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case Request.Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case Request.Method.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            case Request.Method.HEAD:
                connection.setRequestMethod("HEAD");
                break;
            case Request.Method.OPTIONS:
                connection.setRequestMethod("OPTIONS");
                break;
            case Request.Method.TRACE:
                connection.setRequestMethod("TRACE");
                break;
            case Request.Method.PATCH:
                connection.setRequestMethod("PATCH");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private static void addBodyIfExists(HttpURLConnection connection, Request request)
            throws IOException {
        byte[] body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty(HEADER_CONTENT_TYPE, request.getBodyContentType());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(body, 0, body.length);
            out.close();
        }
    }

}
