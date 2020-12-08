package com.xxx.myapplication.model.http;

import com.xxx.myapplication.model.http.bean.BaseBean;
import com.xxx.myapplication.model.http.bean.FileInfoBean;
import com.xxx.myapplication.model.http.bean.FriendBean;
import com.xxx.myapplication.model.http.bean.LoginBean;
import com.xxx.myapplication.model.http.bean.ResultBean;
import com.xxx.myapplication.model.http.bean.UserInfoBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    //登录
    @FormUrlEncoded
    @POST("user/login")
    Observable<BaseBean<LoginBean>> login(
            @Field("account") String account,
            @Field("password") String password
    );

    //注册
    @FormUrlEncoded
    @POST("user/register")
    Observable<BaseBean<ResultBean>> register(
            @Field("account") String smsCode,
            @Field("password") String password,
            @Field("nickName") String nickName
    );

    //忘记密码
    @FormUrlEncoded
    @POST("user/forget")
    Observable<BaseBean<ResultBean>> forgetPsw(
            @Field("account") String account,
            @Field("newPassword") String password
    );

    //获取用户信息
    @GET("user/getUserInfo")
    Observable<BaseBean<UserInfoBean>> getUserInfo(
            @Query("userId") int userId
    );


    //发送添加好友请求
    @FormUrlEncoded
    @POST("friend/sendAddFriendRequest")
    Observable<BaseBean<ResultBean>> sendAddFriend(
            @Field("userId") int userId,
            @Field("friendId") int friendId
    );

    //确认添加好友请求
    @FormUrlEncoded
    @POST("friend/confirmAddFriend")
    Observable<BaseBean<ResultBean>> confirmAddFriend(
            @Field("type") int type,
            @Field("userId") int userId,
            @Field("friendId") int friendId
    );

    //删除好友
    @FormUrlEncoded
    @POST("friend/deleteFriends")
    Observable<BaseBean<ResultBean>> deleteFriends(
            @Field("userId") int userId,
            @Field("friendId") int friendId
    );

    //查询用户列表
    @GET("friend/getUserList")
    Observable<BaseBean<List<FriendBean>>> searchFriendList(
            @Query("userId") int userId,
            @Query("account") String account
    );

    //获取好友列表
    @GET("friend/getFriendList")
    Observable<BaseBean<List<FriendBean>>> getFriendList(
            @Query("userId") int userId,
            @Query("type") int type
    );


    //聊天传递图片
    @Multipart
    @POST("chat/sendImage")
    Observable<BaseBean<FileInfoBean>> sendImageChat(
            @Part("userId") int userId,
            @Part MultipartBody.Part file
    );


    //聊天传递语音
    @Multipart
    @POST("chat/sendMp4")
    Observable<BaseBean<FileInfoBean>> sendMp4Chat(
            @Part("userId") int userId,
            @Part MultipartBody.Part file
    );

}