package com.lt.volley.cache;


/**
 * Created by ldd on 2015/12/8.
 */
public class MemCache implements BaseCache {

    private LruCache<String, BaseEntity> mMemCache;

    public MemCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 10;
        initCache(cacheSize);
    }

    public MemCache(int size) {
        initCache(size);
    }

    private void initCache(int size) {
        mMemCache = new LruCache<String, BaseEntity>(size) {
            @Override
            protected int sizeOf(String key, BaseEntity value) {
                return value.size();
            }
        };
    }

    @Override
    public void put(String key, BaseEntity entity) {
        mMemCache.put(key, entity);
    }

    @Override
    public BaseEntity get(String key) {
        return mMemCache.get(key);
    }

    @Override
    public BaseEntity remove(String key) {
        return mMemCache.remove(key);
    }

    @Override
    public void clear() {
        mMemCache.evictAll();
    }

}
