package com.xxx.myapplication.model.http;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.xxx.myapplication.ConfigClass;

import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    private static ApiService service;

    private static final Object SYC = new Object();   //被锁的对象

    public static ApiService getInstance() {
        if (service == null) {
            synchronized (SYC) {
                if (service == null) {
                    Retrofit.Builder builder = new Retrofit.Builder()
                            .baseUrl(ConfigClass.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(getOkHttpClient());
                    service = builder.build().create(ApiService.class);
                }
            }
        }
        return service;
    }

    /**
     * 添加OkHttp3
     */
    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new ApiIntercept()).build();
    }

    /**
     * 获取上传文件的请求体
     */
    public static MultipartBody.Part getFileRequestBody(String fileName) {
        File file = new File(fileName);
        RequestBody requestBody = RequestBody.create(MediaType.parse("file/*"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestBody);
    }
}