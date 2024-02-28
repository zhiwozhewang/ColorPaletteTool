package com.whatscolors.demo.api;

/**
 * Author:      wangwei
 * Date:        2020/11/3 15:15
 * Version:
 */

import com.whatscolors.demo.utils.LogUtils;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 日志拦截器
 * Created by baishixian on 2017/3/13.
 */

public class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        okhttp3.Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            return response;
        }
        okhttp3.MediaType mediaType = responseBody.contentType();
        String content = responseBody.string();
        LogUtils.loge("\n");
        LogUtils.loge("----------Start----------------");
        LogUtils.loge("| " + request.toString());
        String method = request.method();
        if ("POST".equals(method)) {
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                if (body != null) {
                    for (int i = 0; i < body.size(); i++) {
                        sb.append(body.encodedName(i)).append("=").append(body.encodedValue(i)).append(",");
                    }
                }
                sb.delete(sb.length() - 1, sb.length());
                LogUtils.loge("| RequestParams:{" + sb.toString() + "}");
            }
        }
        LogUtils.loge("| Response:" + content);
        LogUtils.loge("----------End:" + duration + "毫秒----------");
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }
}
