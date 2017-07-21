package com.lt.volley.http;

import android.os.Handler;

import com.lt.volley.http.error.DecodeError;
import com.lt.volley.http.error.VolleyError;

import org.json.JSONException;

import java.util.concurrent.Executor;

/**
 * Created by Administrator on 2015/12/4.
 */
public class DefaultResponseDelivery implements ResponseDelivery {

    private final Executor mExecutor;

    public DefaultResponseDelivery(final Handler handler) {
        mExecutor = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    public DefaultResponseDelivery(Executor executor) {
        mExecutor = executor;
    }

    @Override
    public void postResponse(Request request, VolleyResponse volleyResponse, Runnable runnable) {
        request.setDelivered();
        mExecutor.execute(new ResponseDeliveryRunnable(request, volleyResponse, runnable));
    }

    @Override
    public void postError(Request request, VolleyError error) {
        request.setDelivered();
        VolleyResponse volleyResponse = new VolleyResponse(error);
        mExecutor.execute(new ResponseDeliveryRunnable(request, volleyResponse, null));
    }

    private class ResponseDeliveryRunnable implements Runnable {
        private final Request mRequest;
        private final VolleyResponse mVolleyResponse;
        private final Runnable mRunnable;

        public ResponseDeliveryRunnable(Request request, VolleyResponse volleyResponse, Runnable runnable) {
            mRequest = request;
            mVolleyResponse = volleyResponse;
            mRunnable = runnable;
        }

        @Override
        public void run() {
            if (mRequest.isCanceled()) {
                mRequest.finish();
                return;
            }

            if (mVolleyResponse.isSuccess()) {
//                HttpBaseEntity entity = mRequest.getHttpBaseEntity();
//                try {
//                    entity.decode(mVolleyResponse.mContent);
//                    Log.d("Volley", mResponse.mContent);
                    mRequest.requestSuccess(mVolleyResponse.mContent);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    mRequest.requestError(new DecodeError(e));
//                }
            } else {
                mRequest.requestError(mVolleyResponse.mVolleyError);
            }

            if (mRunnable != null) {
                mRunnable.run();
            }
        }
    }
}
