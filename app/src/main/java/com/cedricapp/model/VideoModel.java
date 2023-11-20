package com.cedricapp.model;

import com.google.gson.annotations.Expose;

public class VideoModel {

    @Expose private String thumbnail; // @SerializedName("thumbnail")
    @Expose private String  name;   // @SerializedName("name")
    @Expose private String duration; //@SerializedName("duration")
    @Expose private String videoUrl;  //@SerializedName("videoUrl")
    @Expose private String  coachNumber; //@SerializedName("coachNumber")
    @Expose private String  description; //@SerializedName("description")
    @Expose private String  exerciseType ; //@SerializedName("exerciseType")


    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCoachNumber() {
        return coachNumber;
    }

    public void setCoachNumber(String coachNumber) {
        this.coachNumber = coachNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public VideoModel(String thumbnail, String name, String duration, String videoUrl, String coachNumber, String description, String exerciseType) {
        this.thumbnail = thumbnail;
        this.name = name;
        this.duration = duration;
        this.videoUrl = videoUrl;
        this.coachNumber = coachNumber;
        this.description = description;
        this.exerciseType = exerciseType;
    }

    public VideoModel() {
    }
}
