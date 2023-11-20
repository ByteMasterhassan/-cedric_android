package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VisualizationModel {
    @SerializedName("data")
    @Expose
    private ArrayList<Data> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("errors")
    @Expose
    private Errors errors;
    @SerializedName("status")
    @Expose
    private Boolean status;

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public static class Data {

        @SerializedName("day")
        @Expose
        private String day;
        @SerializedName("week")
        @Expose
        private String week;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("time")
        @Expose
        private String time;
        @SerializedName("imageURL")
        @Expose
        private String imageURL;
        @SerializedName("audio")
        @Expose
        private String audio;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("playlist_image")
        private String playlistImage;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getImageURL() {
            return imageURL;
        }

        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
        }

        public String getAudio() {
            return audio;
        }

        public void setAudio(String audio) {
            this.audio = audio;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPlaylistImage() {
            return playlistImage;
        }

        public void setPlaylistImage(String playlistImage) {
            this.playlistImage = playlistImage;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "day='" + day + '\'' +
                    ", week='" + week + '\'' +
                    ", name='" + name + '\'' +
                    ", time='" + time + '\'' +
                    ", imageURL='" + imageURL + '\'' +
                    ", audio='" + audio + '\'' +
                    ", description='" + description + '\'' +
                    ", playlistImage='" + playlistImage + '\'' +
                    '}';
        }
    }

    public class Errors {

        @SerializedName("goal_id")
        @Expose
        private ArrayList<String> goalId = null;
        @SerializedName("level_id")
        @Expose
        private ArrayList<String> levelId = null;
        @SerializedName("day")
        @Expose
        private ArrayList<String> day = null;
        @SerializedName("week")
        @Expose
        private ArrayList<String> week = null;

        public ArrayList<String> getGoalId() {
            return goalId;
        }

        public void setGoalId(ArrayList<String> goalId) {
            this.goalId = goalId;
        }

        public ArrayList<String> getLevelId() {
            return levelId;
        }

        public void setLevelId(ArrayList<String> levelId) {
            this.levelId = levelId;
        }

        public ArrayList<String> getDay() {
            return day;
        }

        public void setDay(ArrayList<String> day) {
            this.day = day;
        }

        public ArrayList<String> getWeek() {
            return week;
        }

        public void setWeek(ArrayList<String> week) {
            this.week = week;
        }

    }


}
