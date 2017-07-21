package com.lt.volley.cache;

import java.io.UnsupportedEncodingException;

/**
 * Created by ldd on 2015/12/8.
 */
public class StringEntity extends BaseEntity {

    public StringEntity(byte[] bytes) {
        super(bytes);
    }

    public StringEntity(String string) {
        super(string.getBytes());
    }

    public String getContent() {
        try {
            return new String(mBytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
