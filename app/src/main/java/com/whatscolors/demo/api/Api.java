package com.whatscolors.demo.api;

import android.text.TextUtils;

import com.whatscolors.demo.base.BaseApplication;
import com.whatscolors.demo.utils.NetWorkUtils;
import com.whatscolors.demo.utils.SPUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * des:retorfit api
 * Created by xsf
 * on 2016.06.15:47
 */
public class Api {
    //读超时长，单位：毫秒
    public static final int READ_TIME_OUT = 10000;
    //连接时长，单位：毫秒
    public static final int CONNECT_TIME_OUT = 10000;
    public Retrofit retrofit;
    public ApiService movieService;
    public OkHttpClient okHttpClient;
    // private static SparseArray<Api> sRetrofitManager = new SparseArray<>();

    /*************************缓存设置*********************/
/*
   1. noCache 不使用缓存，全部走网络

    2. noStore 不使用缓存，也不存储缓存

    3. onlyIfCached 只使用缓存

    4. maxAge 设置最大失效时间，失效则不使用 需要服务器配合

    5. maxStale 设置最大失效时间，失效则不使用 需要服务器配合 感觉这两个类似 还没怎么弄清楚，清楚的同学欢迎留言

    6. minFresh 设置有效时间，依旧如上

    7. FORCE_NETWORK 只走网络

    8. FORCE_CACHE 只走缓存*/

    /**
     * 设缓存有效期为两天
     */
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    /**
     * 查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
     * max-stale 指示客户机可以接收超出超时期间的响应消息。如果指定max-stale消息的值，那么客户机可接收超出超时期指定值之内的响应消息。
     */
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    /**
     * 查询网络的Cache-Control设置，头部Cache-Control设为max-age=0
     * (假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)时则不会使用缓存而请求服务器
     */
    private static final String CACHE_CONTROL_AGE = "max-age=0";
    public static ReLoginService reLoginService;
    private static Api retrofitManager;

    //构造方法私有
    private Api() {
        //开启Log
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        LoggingInterceptor   logInterceptor =  new LoggingInterceptor();

        //缓存
        File cacheFile = new File(BaseApplication.getAppContext().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb
        //增加头部信息
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request build = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("app-agent", SPUtils.getSharedStringData(BaseApplication.getAppContext(), "useragent"))
//                        .addHeader("authorization", SPUtils.getSharedStringData(BaseApplication.getAppContext(), "usertoken"))

                        .build();
                return chain.proceed(build);
            }
        };

        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                .addInterceptor(mRewriteCacheControlInterceptor)
                .addInterceptor(headerInterceptor)
//                .addInterceptor(reLogininterceptor) //登录超时拦截
                .addNetworkInterceptor(logInterceptor)
                .cache(cache)
                .build();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls().create();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(ApiConstants.NETEAST_HOST)
                .build();
        movieService = retrofit.create(ApiService.class);
    }


    /**
     * @param "hostType" NETEASE_NEWS_VIDEO：1 （新闻，视频），GANK_GIRL_PHOTO：2（图片新闻）;
     *                   EWS_DETAIL_HTML_PHOTO:3新闻详情html图片)
     */
    public static ApiService getDefault() {
      /*  Api retrofitManager = sRetrofitManager.get(hostType);
        if (retrofitManager == null) {
            retrofitManager = new Api();
            sRetrofitManager.put(hostType, retrofitManager);
        }*/
        if (retrofitManager == null) {
            synchronized (Api.class) {
                if (retrofitManager == null) {
                    retrofitManager = new Api();
                }
            }
        }
        return retrofitManager.movieService;
    }

    /**
     * OkHttpClient
     *
     * @return
     */
    public static OkHttpClient getOkHttpClient() {
        /*Api retrofitManager = sRetrofitManager.get(HostType.NETEASE_NEWS_VIDEO);
        if (retrofitManager == null) {
            retrofitManager = new Api();
            sRetrofitManager.put(HostType.NETEASE_NEWS_VIDEO, retrofitManager);
        }*/
        Api retrofitManager = new Api();
        return retrofitManager.okHttpClient;
    }


    /**
     * 云端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String cacheControl = request.cacheControl().toString();
            if (!NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {
                request = request.newBuilder()
                        .cacheControl(TextUtils.isEmpty(cacheControl) ? CacheControl.FORCE_NETWORK : CacheControl.FORCE_CACHE)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置

                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };
//    private final Interceptor reLogininterceptor = new Interceptor() {
//        @Override
//        public Response intercept(final Chain chain) throws IOException {
//            // 原始请求
//            Request request = chain.request();
//            Response response = chain.proceed(request);
//            ResponseBody responseBody = response.body();
//            BufferedSource source = responseBody.source();
//            source.request(Long.MAX_VALUE);
//            String respString = source.buffer().clone().readString(Charset.defaultCharset());
//            // TODO 这里判断是否是登录超时的情况
//            JSONObject j = null;
//            try {
//                j = new JSONObject(respString);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            // 这里与后台约定的状态码123表示登录超时
//            if (j != null && j.optInt("code") == ApiConstants.STATE_OLD_LOGIN) {
////                LogUtils.logd("--->登录失效，自动重新登录");
//                // TODO 本地获取到之前的userkey信息
//                String userKey = SPUtils.getSharedStringData(BaseApplication.getAppContext(), "userkey");
//                if (TextUtils.isEmpty(userKey)) {
////                    LogUtils.logd("--->用户为空需要用户主动去登录");
//                    // 扔出需要手动重新登录的异常（BaseSubscriber里处理）
////                    return Observable.error(new ServerException(ApiConstants.STATE_OLD_LOGIN,""));
//                    throw new ServerException(ApiConstants.STATE_RELOGIN, j.optString("message"));
//                }
//                Call<JsonObject> call = null;
//                try {
//                    String auth = AESUtil.encode(AESUtil.toGson("userKey", userKey));
////                    call = movieService.getLoginBean(auth);
//                    call = getReloginService().getLoginBean(auth);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                JsonObject json = call.execute().body();
//                // 判断是否登录成功了
//                if (json.get("code").getAsInt() == ApiConstants.STATE_SUCCESS) {
//                    // TODO 登录成功后，根据需要保存用户信息、会话信息等
//                    String token = json.getAsJsonObject("data").get("token").getAsString();
//                    SPUtils.setSharedStringData(BaseApplication.getAppContext(), "usertoken", token);
//                    // 最重要的是将当前请求重新执行一遍!!!
//                    Request build = chain.request().newBuilder()
//                            .header("token", token)
//                            .build();
//                    response = chain.proceed(build);
////                    LogUtils.logd("--->完成二次请求");
//                } else {
////                    LogUtils.logd("--->自动登录失败");
//                    throw new ServerException(ApiConstants.STATE_RELOGIN, json.get("message").getAsString());
//                }
//            }
//            return response;
//        }
//    };

    public static ReLoginService getReloginService() {
        if (reLoginService == null) {
            // TODO 最后关闭日志
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //增加头部信息
            Interceptor headerInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request build = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("app-agent", SPUtils.getSharedStringData(BaseApplication.getAppContext(), "useragent"))
                            .build();
                    return chain.proceed(build);
                }
            };
            // OkHttp3.0的使用方式
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(headerInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.NETEAST_HOST)
                    .addConverterFactory(GsonConverterFactory.create())
//                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
            reLoginService = retrofit.create(ReLoginService.class);
        }
        return reLoginService;

    }

}