package com.cedricapp.model;

public class WarmupDataModel {
    public String imgThumbnail,title,time,vId,url ;

    public WarmupDataModel() {
    }

    public WarmupDataModel(String imgThumbnail, String title, String time, String vId, String url) {
        this.imgThumbnail = imgThumbnail;
        this.title = title;
        this.time = time;
        this.vId = vId;
        this.url = url;
    }

    public String getImgThumbnail() {
        return imgThumbnail;
    }

    public void setImgThumbnail(String imgThumbnail) {
        this.imgThumbnail = imgThumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getvId() {
        return vId;
    }

    public void setvId(String vId) {
        this.vId = vId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
