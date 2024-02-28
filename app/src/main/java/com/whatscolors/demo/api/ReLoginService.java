package com.whatscolors.demo.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface ReLoginService {

    //登录
//    @FormUrlEncoded
    @POST("api/v1/auth/app/login")
    Call<JsonObject> getLoginBean(@Header("Auth") String auth);

}
