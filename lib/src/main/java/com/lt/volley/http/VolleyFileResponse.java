package com.lt.volley.http;

import android.util.Log;

import com.lt.volley.http.error.DownloadError;
import com.lt.volley.http.error.VolleyError;
import com.lt.volley.utils.Preconditions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 2015/12/2.
 */
public class VolleyFileResponse extends VolleyResponse{

    public interface Listener extends VolleyResponse.Listener {

        void onProgress(int progress, int total);
    }

    public String mContent;

    public VolleyError mVolleyError;

    public VolleyFileResponse() {

    }

    public VolleyFileResponse(VolleyError error) {
        mVolleyError = error;
    }


    public void saveFile(NetworkResponse networkResponse, String path, Listener listener) throws DownloadError {
        try {
            saveFile(path, networkResponse.getResponseBody(), listener);
        } catch (IOException e) {
            throw new DownloadError(e);
        }
        mContent = path;
    }

    private static void saveFile(String path, ResponseBody responseBody,
                          VolleyFileResponse.Listener listener) throws IOException{
        Preconditions.checkNotNull(path);
        File file = new File(path);
        if (file.exists()) {
            Log.i("Volley", path + " delete result: " + file.delete());
        }
        File dir = file.getParentFile();
        if (dir == null) {
            throw new IOException("path is " + path);
        }
        if (!dir.exists()) {
            Log.i("Volley", "mkdirs result: " + dir.mkdirs());
        }

        byte[] buf = new byte[4096];
        int readSize;
        int totalSize = (int) file.length();
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(totalSize);
        FileOutputStream fos = new FileOutputStream(file);
        InputStream inputStream = responseBody.getInputStream();
        while ((readSize = inputStream.read(buf)) != -1) {
            raf.write(buf, 0, readSize);
            totalSize += readSize;
            if (listener != null) {
                listener.onProgress(totalSize, responseBody.mContentLength);
            }
        }
        fos.close();
        inputStream.close();
    }
}
