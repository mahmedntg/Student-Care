package com.example.zainab.studentcare.utils;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NotificationApp implements Callback<MyResponse> {

    static final String BASE_URL = "https://fcm.googleapis.com/";

    public void sendRequest(MyRequest request) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        NotificationCall notificationAPI = retrofit.create(NotificationCall.class);

        Call<MyResponse> call = notificationAPI.sendNotification(request);
        call.enqueue(this);

    }


    @Override
    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
        if (response.isSuccessful()) {
            response.body();
        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<MyResponse> call, Throwable t) {
        t.printStackTrace();
    }

    OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
        @Override
        public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder().header("Authorization", "key=AIzaSyAHpaBY6ZedKU8ICYy6aZpNHuaVgsToN6M");
            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }
    }).build();


    public void sendNotification(String token, String title, String body) {

        MyRequest notificationRequest = new MyRequest();
        notificationRequest.setTo(token);
        notificationRequest.setPriority("normal");
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setBody(body);
        notificationRequest.setNotification(notification);
        this.sendRequest(notificationRequest);
    }

    public void sendAllNotification(String topic, String title, String body) {

        MyRequest notificationRequest = new MyRequest();
        notificationRequest.setTo("/topics/all");
        notificationRequest.setPriority("normal");
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setBody(body);
        notificationRequest.setNotification(notification);
        this.sendRequest(notificationRequest);
    }
}
