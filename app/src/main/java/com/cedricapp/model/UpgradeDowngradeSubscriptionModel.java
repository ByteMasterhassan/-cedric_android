package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpgradeDowngradeSubscriptionModel {

    @SerializedName("data")
    @Expose
    private Data data;

    @SerializedName("message")
    @Expose
    private String message;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UpgradeDowngradeSubscriptionModel{" +
                "data=" + data +
                ", message='" + message + '\'' +
                '}';
    }

    public class Data {

        @SerializedName("subscription_id")
        @Expose
        private String subscriptionId;
        @SerializedName("plan_id")
        @Expose
        private String planId;
        @SerializedName("period_starts")
        @Expose
        private String periodStarts;
        @SerializedName("period_ends")
        @Expose
        private String periodEnds;
        @SerializedName("trial_ends_at")
        @Expose
        private String trialEndsAt;

        public String getSubscriptionId() {
            return subscriptionId;
        }

        public void setSubscriptionId(String subscriptionId) {
            this.subscriptionId = subscriptionId;
        }

        public String getPlanId() {
            return planId;
        }

        public void setPlanId(String planId) {
            this.planId = planId;
        }

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

        public String getTrialEndsAt() {
            return trialEndsAt;
        }

        public void setTrialEndsAt(String trialEndsAt) {
            this.trialEndsAt = trialEndsAt;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "subscriptionId='" + subscriptionId + '\'' +
                    ", planId='" + planId + '\'' +
                    ", periodStarts='" + periodStarts + '\'' +
                    ", periodEnds='" + periodEnds + '\'' +
                    ", trialEndsAt='" + trialEndsAt + '\'' +
                    '}';
        }
    }
}
