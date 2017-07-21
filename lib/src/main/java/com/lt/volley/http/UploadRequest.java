package com.lt.volley.http;

/**
 * 文件上传请求类
 * Created by ldd on 2015/12/3.
 */
public class UploadRequest extends Request {

    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("ser-Agent/Fiddler; multipart/form-data; charset=%s", DEFAULT_ENCODING);

    public UploadRequest() {
        mProtocolContentType = PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public String getBodyContentType() {
        return mProtocolContentType;
    }

}
