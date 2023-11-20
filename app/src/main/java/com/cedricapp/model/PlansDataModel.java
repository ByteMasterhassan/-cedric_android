package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PlansDataModel {
    @SerializedName("data")
    @Expose
    private ArrayList<Data> data = null;
    @SerializedName("status")
    @Expose
    private Boolean status;

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
    public class Data {

        @SerializedName("name_en")
        @Expose
        private String nameEn;

        @SerializedName("name_sv")
        @Expose
        private String nameSv;
        @SerializedName("plan_id")
        @Expose
        private String planId;
        @SerializedName("interval")
        @Expose
        private String interval;
        @SerializedName("interval_count")
        @Expose
        private String intervalCount;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("icon")
        @Expose
        private Object icon;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("original_price")
        @Expose
        private String originalPrice;
        @SerializedName("discount")
        @Expose
        private String discount;

        @SerializedName("description_en")
        @Expose
        private String descriptionEn;

        @SerializedName("description_sv")
        @Expose
        private String descriptionSv;


        public String getPlanId() {
            return planId;
        }

        public void setPlanId(String planId) {
            this.planId = planId;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        public String getIntervalCount() {
            return intervalCount;
        }

        public void setIntervalCount(String intervalCount) {
            this.intervalCount = intervalCount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public Object getIcon() {
            return icon;
        }

        public void setIcon(Object icon) {
            this.icon = icon;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(String originalPrice) {
            this.originalPrice = originalPrice;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }

        public String getNameSv() {
            return nameSv;
        }

        public void setNameSv(String nameSv) {
            this.nameSv = nameSv;
        }

        public String getDescriptionEn() {
            return descriptionEn;
        }

        public void setDescriptionEn(String descriptionEn) {
            this.descriptionEn = descriptionEn;
        }

        public String getDescriptionSv() {
            return descriptionSv;
        }

        public void setDescriptionSv(String descriptionSv) {
            this.descriptionSv = descriptionSv;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "nameEn='" + nameEn + '\'' +
                    ", nameSv='" + nameSv + '\'' +
                    ", planId='" + planId + '\'' +
                    ", interval='" + interval + '\'' +
                    ", intervalCount='" + intervalCount + '\'' +
                    ", currency='" + currency + '\'' +
                    ", amount='" + amount + '\'' +
                    ", icon=" + icon +
                    ", status='" + status + '\'' +
                    ", originalPrice='" + originalPrice + '\'' +
                    ", discount='" + discount + '\'' +
                    ", descriptionEn='" + descriptionEn + '\'' +
                    ", descriptionSv='" + descriptionSv + '\'' +
                    '}';
        }
    }

}
