package com.lt.volley.cache;

/**
 * Created by ldd on 2015/12/8.
 */
public class BaseEntity {

    protected byte[] mBytes;

    public int size() {
        return mBytes.length;
    }

    public byte[] getBytes() {
        return mBytes;
    }

    public String getString() {
        return new String(mBytes);
    }

    public BaseEntity (byte[] bytes) {
        mBytes = bytes;
    }

    public BaseEntity(String string) {
        this(string.getBytes());
    }

}
