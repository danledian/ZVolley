package com.lt.volley.http;

import com.lt.volley.utils.Preconditions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ldd on 2016/10/20.
 */

public class ResponseBody {

    protected static final String DEFAULT_DECODE = "utf-8";

    protected String mEncode = DEFAULT_DECODE;
    protected String mContentType;
    protected InputStream mInputStream;
    protected int mContentLength;

    public InputStream getInputStream() {
        return mInputStream;
    }

    public String getEncode() {
        return mEncode;
    }

    public String getContentType() {
        return mContentType;
    }

    public void setInputStream(InputStream inputStream) {
        mInputStream = inputStream;
    }

    public void setContentLength(int contentLength) {
        mContentLength = contentLength;
    }

    public void setEncode(String encode) {
        if (Preconditions.isEmpty(encode)) return;
        mEncode = encode;
    }

    public void setBytes(byte[] bytes) {
        if (bytes == null) {
            bytes = new byte[0];
        }
        mInputStream = new ByteArrayInputStream(bytes);
    }

    public void setContentType(String contentType) {
        mContentType = contentType;
    }

    public String getString() {
//        byte[] buf = new byte[4096];

        byte[] buf = Volley.getPool().getBuf(4096);
        PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(Volley.getPool(), mContentLength);
        try {
            int count;
            while ((count = mInputStream.read(buf)) != -1) {
                bytes.write(buf, 0, count);
            }
            return new String(bytes.toByteArray(), mEncode);
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        }finally {
            Volley.getPool().returnBuf(buf);
            try {
                bytes.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
