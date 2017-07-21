package com.lt.volley.cache;

/**
 * Created by ldd on 2015/12/8.
 */
public interface BaseCache {

    /**
     * Put entity by key
     */
    void put(String key, BaseEntity entity);

    /**
     * Return entity by key
     */
    BaseEntity get(String key);

    /**
     * Remove entity by key
     */
    BaseEntity remove(String key);

    /**
     * Clear all cache
     */
    void clear();

}
