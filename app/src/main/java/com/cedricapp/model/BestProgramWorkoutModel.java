package com.cedricapp.model;

public class BestProgramWorkoutModel {
    public String imgThumbnail,title,time,url ;




    public BestProgramWorkoutModel() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }




    public BestProgramWorkoutModel(String title,String imgThumbnail,String time , String url) {
        this.title = title;
        this.url = url;
        this.time=time;
        this.imgThumbnail=imgThumbnail;


    }

    public String getImgThumbnail() {
        return imgThumbnail;
    }

    public void setImgThumbnail(String imgThumbnail) {
        this.imgThumbnail = imgThumbnail;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
