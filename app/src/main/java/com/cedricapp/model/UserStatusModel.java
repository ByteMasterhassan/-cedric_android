package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserStatusModel {
    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserStatusModel{" +
                "data=" + data +
                '}';
    }


    public class Data {

        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("subscription_id")
        @Expose
        private String subscriptionId;
        @SerializedName("subscription_status")
        @Expose
        private String subscriptionStatus;

        @SerializedName("starts_at")
        @Expose
        private String subscriptionStartsAt;

        @SerializedName("ends_at")
        @Expose
        private String subscriptionEndsAt;
        @SerializedName("is_dev")
        @Expose
        private Boolean isDev;

        public String getSubscriptionId() {
            return subscriptionId;
        }

        public void setSubscriptionId(String subscriptionId) {
            this.subscriptionId = subscriptionId;
        }

        public Boolean getDev() {
            return isDev;
        }

        public void setDev(Boolean dev) {
            isDev = dev;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSubscriptionStatus() {
            return subscriptionStatus;
        }

        public void setSubscriptionStatus(String subscriptionStatus) {
            this.subscriptionStatus = subscriptionStatus;
        }

        public Boolean getIsDev() {
            return isDev;
        }

        public void setIsDev(Boolean isDev) {
            this.isDev = isDev;
        }

        public String getSubscriptionStartsAt() {
            return subscriptionStartsAt;
        }

        public void setSubscriptionStartsAt(String subscriptionStartsAt) {
            this.subscriptionStartsAt = subscriptionStartsAt;
        }

        public String getSubscriptionEndsAt() {
            return subscriptionEndsAt;
        }

        public void setSubscriptionEndsAt(String subscriptionEndsAt) {
            this.subscriptionEndsAt = subscriptionEndsAt;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "status='" + status + '\'' +
                    ", subscriptionId='" + subscriptionId + '\'' +
                    ", subscriptionStatus='" + subscriptionStatus + '\'' +
                    ", subscriptionStartsAt='" + subscriptionStartsAt + '\'' +
                    ", subscriptionEndsAt='" + subscriptionEndsAt + '\'' +
                    ", isDev=" + isDev +
                    '}';
        }
    }
}
