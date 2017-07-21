package com.lt.volley.utils;

/**
 * Created by ldd on 2017/1/16.
 */

public final class Preconditions {

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, String msg) {
        if (reference == null) {
            throw new NullPointerException(msg);
        }
        return reference;
    }

    public static boolean isEmpty(String string) {
        return string == null || "".equals(string);
    }

}
