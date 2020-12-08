package com.xxx.myapplication.model.http.bean;

public class MP4Bean {

    private String fileUrl;

    private Long time;

    public void setTime(Long time) {
        this.time = time;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public Long getTime() {
        return time;
    }
}