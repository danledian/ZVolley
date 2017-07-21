package com.lt.volley.http.error;

import android.util.Log;

import com.lt.volley.http.NetworkResponse;
import com.lt.volley.http.StatusLine;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ldd on 2015/12/6.
 */
public class ServerError extends VolleyError {

    private static final String TAG = "ServerError";

    @Override
    public int getType() {
        return SERVER_ERROR;
    }

    public ServerError(NetworkResponse response) {
        super(response);
        getErrorMsg(response.getData());
    }

    private void getErrorMsg(byte[] bytes) {
        if(bytes == null){
            return;
        }
        boolean isJson = true;
        String message = null;
        try {
            message = new String(bytes, "utf-8");
            Log.d(TAG, message);
            JSONObject job = new JSONObject(message);
            int code = job.optInt("code", 500);
            String error = job.optString("error", "");
            mNetworkResponse.setStatusLine(new StatusLine(code, error));
        } catch (JSONException e){
            isJson = false;
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(!isJson){
                mNetworkResponse.setStatusLine(new StatusLine(mNetworkResponse.getStatusCode(), message));
            }
        }
    }
}
