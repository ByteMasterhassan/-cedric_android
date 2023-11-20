package com.cedricapp.model;

public class SleepVisualizationDataModel {
    String imgUrl ,audioUrl;

    SleepVisualizationDataModel(){

    }

    public SleepVisualizationDataModel(String imgUrl, String audioUrl) {
        this.imgUrl = imgUrl;
        this.audioUrl = audioUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}
