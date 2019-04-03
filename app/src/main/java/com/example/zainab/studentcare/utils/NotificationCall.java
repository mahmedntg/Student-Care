package com.example.zainab.studentcare.utils;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NotificationCall {

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body MyRequest request);
}
