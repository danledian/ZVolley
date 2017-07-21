package com.lt.volley.http;

import com.lt.volley.http.error.VolleyError;
import com.lt.volley.utils.BaseUtil;

/**
 * Created by Administrator on 2015/12/2.
 */
public class VolleyResponse {

    public interface Listener {
        /**
         * Called when response is returned success
         */
        void onSuccess(String message);

        /**
         * Called when error occurred
         */
        void onError(VolleyError response);
    }

    public String mContent;

    public VolleyError mVolleyError;

    public VolleyResponse() {
    }

    public VolleyResponse(VolleyError error) {
        mVolleyError = error;
    }

    public boolean isSuccess() {
        return mVolleyError == null;
    }

    public void parseContent(NetworkResponse networkResponse) {
        mContent = networkResponse.getResponseBody().getString();
    }

    public void parseContent(byte[] bytes) {
        mContent = BaseUtil.parseToString(bytes);
    }
}
