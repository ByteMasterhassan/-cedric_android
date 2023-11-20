package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnsubscribeLaterModel {
    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("period_starts")
        @Expose
        private String periodStarts;
        @SerializedName("period_ends")
        @Expose
        private String periodEnds;
        @SerializedName("status")
        @Expose
        private String status;

        public String getPeriodStarts() {
            return periodStarts;
        }

        public void setPeriodStarts(String periodStarts) {
            this.periodStarts = periodStarts;
        }

        public String getPeriodEnds() {
            return periodEnds;
        }

        public void setPeriodEnds(String periodEnds) {
            this.periodEnds = periodEnds;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }

}
