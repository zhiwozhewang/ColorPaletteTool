package com.whatscolors.demo.api;


import com.google.gson.JsonObject;
import com.whatscolors.demo.bean.CheckBean;
import com.whatscolors.demo.bean.WechatBean;
import com.whatscolors.demo.utils.http.RespDTO;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {
    //登录
    @FormUrlEncoded
    @POST("api/v1/login")
    Observable<JsonObject> getLoginBean(@Header("authorization") String authorization, @FieldMap Map<String, Object> MAP);

    //一次性密码登录
    @POST("api/v1/login/otp")
    Observable<JsonObject> getLoginBeanOtp(@Header("authorization") String authorization, @Body JsonObject jsonObject);

    //注册
    @FormUrlEncoded
    @POST("api/v1/register")
    Observable<JsonObject> getRegistBean(@Header("authorization") String authorization, @FieldMap Map<String, Object> MAP);

    //找回密码
    @FormUrlEncoded
    @POST("api/v1/forgot-password/send-email")
    Observable<JsonObject> getPsBean(@Header("authorization") String authorization, @Field("email") String email);

    //获取用户的信息
    @GET("api/v1/profile")
    Observable<JsonObject> getProfileBean(@Header("authorization") String authorization);

    //上传支付数据
    @POST("api/v1/payment/google/receipt")
    Observable<RespDTO<JsonObject>> sendPayData(@Header("authorization") String authorization, @Body JsonObject purchase);

    //验证支付 [GET] /api/v1/profile 或者 [GET] /api/v1/profile/service
    @GET("api/v1/profile")
    Observable<CheckBean> checkPay(@Header("authorization") String authorization);//, @Query("user_key") String user_key);

    //    上传次数
    @POST("api/v1/profile/times")
    Observable<RespDTO<JsonObject>> postTimes(@Header("authorization") String authorization, @Body JsonObject times);


    //   微信 统一下单
    @POST("api/v1/payment/weChat/unifiedOrder")
    Observable<WechatBean> unifiedOrder(@Header("authorization") String authorization, @Body JsonObject product_id);


    //签约
    @POST("/xxx/contracts")
    Observable<Response<Contract>> getContract();

    /**
     * 查看自动续费签约状态
     *
     * @param contractId
     * @return
     */
    @GET("/xxx/contracts/{contract_id}")
    Observable<Response<OrderStatus>> getOrderStatus(@Path("contract_id") String contractId);

    //获取更新数据
    @GET("api/v1/app-versions/newest")
    Observable<RespDTO<VersionBean>> getVersionUpdate(@Header("authorization") String authorization);

    @GET("/api/v1/app-loading-screen")
    Observable<RespDTO<GifBean>> getUpdateGif(@Header("authorization") String authorization);

    //下载gif
    @Streaming
    @GET
    Observable<ResponseBody> downloadGif(@Url String url);


    //获取账户状态
    @GET("/api/v1/profile/check-status")
    Observable<RespDTO> checkState(@Header("authorization") String authorization);


}