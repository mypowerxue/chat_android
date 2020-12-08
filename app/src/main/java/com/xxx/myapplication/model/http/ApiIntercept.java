package com.xxx.myapplication.model.http;

import android.support.annotation.NonNull;
import android.util.Log;

import com.xxx.myapplication.BuildConfig;
import com.xxx.myapplication.model.sp.SharedConst;
import com.xxx.myapplication.model.sp.SharedPreferencesUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class ApiIntercept implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        //请求头添加
        Request request = headerIntercept(chain);

        //发起请求
        Response response = chain.proceed(request);

        //日志拦截器
        if (BuildConfig.DEBUG) {
            logIntercept(request, response);
        }

        return response;
    }

    /**
     * 请求头拦截器
     */
    private Request headerIntercept(Interceptor.Chain chain) {
        //拦截请求头添加
        SharedPreferencesUtil instance = SharedPreferencesUtil.getInstance();
        String token = instance.getString(SharedConst.VALUE_TOKEN);
        return chain.request()
                .newBuilder()
                .addHeader("x-auth-token", token)
                .build();
    }

    /**
     * 日志拦截器
     */
    private void logIntercept(Request request, Response response) throws IOException {
        //获取到请求类型
        String method = request.method();

        //获取到请求Url
        String url = request.url().toString();

        //获取到请求头
        String header = request.headers().toString().replaceAll("\n", " | ");

        //获取到请求参数 转化为json格式
        String params;
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            if (requestBody instanceof MultipartBody) {  //如果是文件 就打印路径即可
                MultipartBody multipartBody = (MultipartBody) requestBody;
                List<MultipartBody.Part> parts = multipartBody.parts();
                StringBuilder sb = new StringBuilder("[");
                for (MultipartBody.Part part : parts) {
                    sb.append(part.headers()).append(",");
                }
                sb.append("]");
                params = sb.toString().replaceAll("\n", "");
            } else { //如果不是 就打印出所有的数据
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                params = buffer.readUtf8();
            }
        } else {
            params = "[]";
        }

        //获取到响应数据
        String body = response.peekBody(1024 * 1024).string().replaceAll("\n", "");

        Log.e("日志拦截器", method + "\n" +  //打印方法
                "url：" + url + "\n" +   //打印url
                "header：" + header + "\n" +  //打印所有请求头
                "params：" + params + "\n" +  //打印请求参数
                "body：" + body + "\n" +  //打印响应内容
                " \n" + " \n" + " \n");//分割
    }
}