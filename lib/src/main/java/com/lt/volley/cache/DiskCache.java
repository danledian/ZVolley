package com.lt.volley.cache;

import android.content.Context;

import com.lt.volley.utils.BaseUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ldd on 2015/12/8.
 */
public class DiskCache implements BaseCache {

    private static final int DEFAULT_MAX_SIZE = 10 * 1024 * 1024;

    private byte[] buff = new byte[100];

    private DiskLruCache mDiskLruCache;

    public DiskCache(Context context) {
        this(context, DEFAULT_MAX_SIZE);
    }

    public DiskCache(Context context, int max) {
        File file = BaseUtil.getCacheDir(context, "diskCache");
        initCache(context, file, max);
    }

    public DiskCache(Context context, String path, int max) {
        File file;
        try {
            file = new File(path);
        } catch (NullPointerException e) {
            file = BaseUtil.getCacheDir(context, "diskCache");
        }
        initCache(context, file, max);
    }

    private void initCache(Context context, File file, int max) {
        try {
            mDiskLruCache = DiskLruCache.open(file, BaseUtil.getAppVersion(context), 1, max);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(String key, BaseEntity entity) {
        if (mDiskLruCache.isClosed()) return;
        OutputStream os = null;
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null) {
                os = editor.newOutputStream(0);
                os.write(entity.getBytes());
                editor.commit();
            }
            mDiskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public BaseEntity get(String key) {
        if (mDiskLruCache.isClosed()) return null;
        byte[] bytes = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            DiskLruCache.Snapshot snapshot;
            snapshot = mDiskLruCache.get(key);
            if (snapshot == null) return null;
            is = snapshot.getInputStream(0);
            baos = new ByteArrayOutputStream();
            int recv;
            while ((recv = is.read(buff, 0, 100)) > 0) {
                baos.write(buff, 0, recv);
            }
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bytes != null) {
            return new BaseEntity(bytes);
        }
        return null;
    }

    @Override
    public BaseEntity remove(String key) {
        try {
            BaseEntity entity = get(key);
            mDiskLruCache.remove(key);
            return entity;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用之后会关闭DiskLruCache，再次使用需要重新打开DiskLruCache
     */
    @Override
    public void clear() {
        try {
            mDiskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
