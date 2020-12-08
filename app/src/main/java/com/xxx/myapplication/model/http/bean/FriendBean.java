package com.xxx.myapplication.model.http.bean;

import java.io.Serializable;
import java.util.List;

public class FriendBean implements Serializable {

    private int userId; //用户Id
    private int friendId;   //好友Id
    private String account;
    private String nickName;
    private Object picUrl;
    private double status;

    public int getFriendId() {
        return friendId;
    }

    public int getUserId() {
        return userId;
    }

    public int getStatus() {
        return (int) status;
    }

    public String getAccount() {
        return account;
    }

    public String getNickName() {
        return nickName;
    }
}
