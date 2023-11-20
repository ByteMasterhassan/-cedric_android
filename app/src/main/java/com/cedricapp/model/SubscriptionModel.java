package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionModel {
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("message")
    @Expose
    private String message;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SubscriptionModel{" +
                "data=" + data +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }

    public class Data {

        @SerializedName("subscription_id")
        @Expose
        private String subscriptionId;
        @SerializedName("plan_id")
        @Expose
        private String planID;

        @SerializedName("icon")
        @Expose
        private Object icon;
        @SerializedName("level")
        @Expose
        private String level;
        @SerializedName("goal")
        @Expose
        private String goal;
        @SerializedName("package")
        @Expose
        private String _package;
        @SerializedName("amount")
        @Expose
        private Integer amount;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("expiry_date")
        @Expose
        private String expiryDate;

        @SerializedName("is_unsubscribed")
        @Expose
        private Boolean isUnsubscribed;

        public String getSubscriptionId() {
            return subscriptionId;
        }

        public void setSubscriptionId(String subscriptionId) {
            this.subscriptionId = subscriptionId;
        }

        public Object getIcon() {
            return icon;
        }

        public void setIcon(Object icon) {
            this.icon = icon;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getGoal() {
            return goal;
        }

        public void setGoal(String goal) {
            this.goal = goal;
        }

        public String getPackage() {
            return _package;
        }

        public void setPackage(String _package) {
            this._package = _package;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getPlanID() {
            return planID;
        }

        public void setPlanID(String planID) {
            this.planID = planID;
        }

        public String get_package() {
            return _package;
        }

        public void set_package(String _package) {
            this._package = _package;
        }

        public Boolean getUnsubscribed() {
            return isUnsubscribed;
        }

        public void setUnsubscribed(Boolean unsubscribed) {
            isUnsubscribed = unsubscribed;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "subscriptionId='" + subscriptionId + '\'' +
                    ", planID='" + planID + '\'' +
                    ", icon=" + icon +
                    ", level='" + level + '\'' +
                    ", goal='" + goal + '\'' +
                    ", _package='" + _package + '\'' +
                    ", amount=" + amount +
                    ", currency='" + currency + '\'' +
                    ", status='" + status + '\'' +
                    ", expiryDate='" + expiryDate + '\'' +
                    ", isUnsubscribed=" + isUnsubscribed +
                    '}';
        }
    }
}
