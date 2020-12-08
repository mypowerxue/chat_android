package com.xxx.myapplication.model.http;

import android.app.Activity;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.xxx.myapplication.R;
import com.xxx.myapplication.base.BaseActivity;
import com.xxx.myapplication.model.http.bean.BaseBean;
import com.xxx.myapplication.model.http.util.ApiCode;
import com.xxx.myapplication.model.util.SystemUtil;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public abstract class ApiCallback<T> implements Observer<BaseBean<T>> {

    //弱引用对象
    private WeakReference<BaseActivity> activity;  //绑定上下文
    private CompositeDisposable compositeDisposable = new CompositeDisposable();    //防止内存泄漏

    //必须使用封装的上下文Activity 否则发起网络请求会报错
    public ApiCallback(Activity activity) {
        if (activity instanceof BaseActivity) {
            BaseActivity activity1 = (BaseActivity) activity;
            this.activity = new WeakReference<>(activity1);
        } else {
            onComplete();
        }
    }

    //必须重写 请求成功的方法
    public abstract void onSuccess(T bean);

    //等待重写 请求失败的方法
    public void onError(int errorCode, String errorMessage) {}

    //等待重写 请求开始的方发
    public void onStart(Disposable d) {
        if (activity != null) {
            BaseActivity activity = this.activity.get();
            if (activity != null && activity.isShowData()) {
                if (SystemUtil.isNetworkAvailable(activity)) { //如果没有网络 就停止请求 并且弹出异常
                    onError(ApiCode.SERVICE_ERROR_CODE, "无网络");
                    onComplete();
                }
            }
        }

        //开始请求
        compositeDisposable.add(d);
    }

    //等待重写 请求结束的方发
    public void onEnd() {
        //请求结束
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }


    @Override
    public void onSubscribe(Disposable d) {
        if (activity != null) {
            BaseActivity activity = this.activity.get();
            if (activity != null && activity.isShowData()) {
                onStart(d);
            }
        }
    }

    @Override
    public void onNext(BaseBean<T> bean) {
        if (activity != null) {
            BaseActivity activity = this.activity.get();
            if (activity != null && activity.isShowData()) {
                if (bean != null) {
                    int code = bean.getCode();
                    //使用code值做判断
                    if (code == ApiCode.RESPONSE_CODE) {
                        onSuccess(bean.getData());
                    } else {
                        //根据后台返回的code值做特殊异常处理
//                        ApiError.ServiceCodeErrorFun(code);
                        //回调错误信息
                        String errorMessage = bean.getMessage() == null ? "未知错误" : bean.getMessage();
                        onError(code, errorMessage);
                    }
                } else {
                    //解析失败
                    String errorMessage = "解析异常";
                    onError(ApiCode.SERVICE_ERROR_CODE, errorMessage);
                }
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        if (activity != null) {
            BaseActivity activity = this.activity.get();
            if (activity != null && activity.isShowData()) {
                String errorMessage;
                int code = ApiCode.SERVICE_ERROR_CODE;
                if (e instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
                    ResponseBody errorBody = ((HttpException) e).response().errorBody();
                    if (errorBody != null) {
                        try {
                            String string = errorBody.string();
                            BaseBean bean = new Gson().fromJson(string, BaseBean.class);
                            code = bean.getCode();
//                            ApiError.ServiceCodeErrorFun(code);
                            errorMessage = bean.getMessage();
                        } catch (Exception e1) {
                            errorMessage = "解析异常";
                        }
                    } else {
                        errorMessage = "解析异常";
                    }
                } else if (e instanceof UnknownHostException) {    //首先判断地址是否正确
                    errorMessage = "地址不正确";
                } else if (e instanceof SocketTimeoutException || e instanceof ConnectException) {  //再判断是否是链接超时
                    errorMessage = "链接超时";
                } else {
                    errorMessage = e == null ? "未知错误" : e.getMessage();
                }
                onError(code, errorMessage);
                onComplete();
            }
        }
    }

    @Override
    public void onComplete() {
        onEnd();
    }
}