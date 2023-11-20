package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DaysUnlockModel {

    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        @SerializedName("program")
        @Expose
        private String program;
        @SerializedName("total_weeks")
        @Expose
        private Integer totalWeeks;
        @SerializedName("total_videos")
        @Expose
        private Integer totalVideos;
        @SerializedName("watched_videos")
        @Expose
        private Integer watchedVideos;
        @SerializedName("unlock_week")
        @Expose
        private Integer unlockWeek;
        @SerializedName("unlock_day")
        @Expose
        private Integer unlockDay;

        public String getProgram() {
            return program;
        }

        public void setProgram(String program) {
            this.program = program;
        }

        public Integer getTotalWeeks() {
            return totalWeeks;
        }

        public void setTotalWeeks(Integer totalWeeks) {
            this.totalWeeks = totalWeeks;
        }

        public Integer getTotalVideos() {
            return totalVideos;
        }

        public void setTotalVideos(Integer totalVideos) {
            this.totalVideos = totalVideos;
        }

        public Integer getWatchedVideos() {
            return watchedVideos;
        }

        public void setWatchedVideos(Integer watchedVideos) {
            this.watchedVideos = watchedVideos;
        }

        public Integer getUnlockWeek() {
            return unlockWeek;
        }

        public void setUnlockWeek(Integer unlockWeek) {
            this.unlockWeek = unlockWeek;
        }

        public Integer getUnlockDay() {
            return unlockDay;
        }

        public void setUnlockDay(Integer unlockDay) {
            this.unlockDay = unlockDay;
        }

    }
}
