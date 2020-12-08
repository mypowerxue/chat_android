package com.xxx.myapplication.model.db.entry;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class WebSocketMessageBean {

    @Id
    private Long id;
    private Integer sendId;
    private Integer receiveId;
    private Integer sendType;
    private Integer messageType;
    private String message;
    private Integer status;
    private String createTime;
    @Generated(hash = 1595322523)
    public WebSocketMessageBean(Long id, Integer sendId, Integer receiveId,
            Integer sendType, Integer messageType, String message, Integer status,
            String createTime) {
        this.id = id;
        this.sendId = sendId;
        this.receiveId = receiveId;
        this.sendType = sendType;
        this.messageType = messageType;
        this.message = message;
        this.status = status;
        this.createTime = createTime;
    }
    @Generated(hash = 151913959)
    public WebSocketMessageBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getSendId() {
        return this.sendId;
    }
    public void setSendId(Integer sendId) {
        this.sendId = sendId;
    }
    public Integer getReceiveId() {
        return this.receiveId;
    }
    public void setReceiveId(Integer receiveId) {
        this.receiveId = receiveId;
    }
    public Integer getSendType() {
        return this.sendType;
    }
    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }
    public Integer getMessageType() {
        return this.messageType;
    }
    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }
    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Integer getStatus() {
        return this.status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }


}
