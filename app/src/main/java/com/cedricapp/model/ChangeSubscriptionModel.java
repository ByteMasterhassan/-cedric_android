package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChangeSubscriptionModel {

    @SerializedName("current_plan")
    @Expose
    private CurrentPlan currentPlan;
    @SerializedName("new_plan")
    @Expose
    private NewPlan newPlan;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("status")
    @Expose
    private boolean status;

    public CurrentPlan getCurrentPlan() {
        return currentPlan;
    }

    public void setCurrentPlan(CurrentPlan currentPlan) {
        this.currentPlan = currentPlan;
    }

    public NewPlan getNewPlan() {
        return newPlan;
    }

    public void setNewPlan(NewPlan newPlan) {
        this.newPlan = newPlan;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ChangeSubscriptionModel{" +
                "currentPlan=" + currentPlan +
                ", newPlan=" + newPlan +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }

    public class CurrentPlan {

        @SerializedName("plan_id")
        @Expose
        private String planId;
        @SerializedName("icon")
        @Expose
        private String icon;
        @SerializedName("original_price")
        @Expose
        private Integer originalPrice;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("discount")
        @Expose
        private Integer discount;
        @SerializedName("card_no")
        @Expose
        private String cardNo;

        @SerializedName("interval_name")
        @Expose
        private String intervalName;

        @SerializedName("interval")
        @Expose
        private String interval;

        @SerializedName("interval_count")
        @Expose
        private Integer intervalCount;

        public String getPlanId() {
            return planId;
        }

        public void setPlanId(String planId) {
            this.planId = planId;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public Integer getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(Integer originalPrice) {
            this.originalPrice = originalPrice;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public Integer getDiscount() {
            return discount;
        }

        public void setDiscount(Integer discount) {
            this.discount = discount;
        }

        public String getCardNo() {
            return cardNo;
        }

        public void setCardNo(String cardNo) {
            this.cardNo = cardNo;
        }

        public String getIntervalName() {
            return intervalName;
        }

        public void setIntervalName(String intervalName) {
            this.intervalName = intervalName;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        public Integer getIntervalCount() {
            return intervalCount;
        }

        public void setIntervalCount(Integer intervalCount) {
            this.intervalCount = intervalCount;
        }

        @Override
        public String toString() {
            return "CurrentPlan{" +
                    "planId='" + planId + '\'' +
                    ", icon='" + icon + '\'' +
                    ", originalPrice=" + originalPrice +
                    ", amount='" + amount + '\'' +
                    ", currency='" + currency + '\'' +
                    ", discount=" + discount +
                    ", cardNo='" + cardNo + '\'' +
                    ", intervalName='" + intervalName + '\'' +
                    ", interval='" + interval + '\'' +
                    ", intervalCount=" + intervalCount +
                    '}';
        }
    }

    public class NewPlan {

        @SerializedName("plan_id")
        @Expose
        private String planId;

        @SerializedName("interval_name")
        @Expose
        private String intervalName;

        @SerializedName("interval")
        @Expose
        private String interval;

        @SerializedName("interval_count")
        @Expose
        private Integer intervalCount;
        @SerializedName("icon")
        @Expose
        private String icon;
        @SerializedName("original_price")
        @Expose
        private Integer originalPrice;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("discount")
        @Expose
        private Integer discount;
        @SerializedName("description")
        @Expose
        private List<String> description;
        @SerializedName("excl_tax")
        @Expose
        private Integer exclTax;
        @SerializedName("incl_tax")
        @Expose
        private Integer inclTax;

        @SerializedName("reimbursement")
        @Expose
        private Integer reimbursement;

        public String getPlanId() {
            return planId;
        }

        public void setPlanId(String planId) {
            this.planId = planId;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public Integer getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(Integer originalPrice) {
            this.originalPrice = originalPrice;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public Integer getDiscount() {
            return discount;
        }

        public void setDiscount(Integer discount) {
            this.discount = discount;
        }

        public List<String> getDescription() {
            return description;
        }

        public void setDescription(List<String> description) {
            this.description = description;
        }

        public Integer getExclTax() {
            return exclTax;
        }

        public void setExclTax(Integer exclTax) {
            this.exclTax = exclTax;
        }

        public Integer getInclTax() {
            return inclTax;
        }

        public void setInclTax(Integer inclTax) {
            this.inclTax = inclTax;
        }

        public Integer getReimbursement() {
            return reimbursement;
        }

        public void setReimbursement(Integer reimbursement) {
            this.reimbursement = reimbursement;
        }

        public String getIntervalName() {
            return intervalName;
        }

        public void setIntervalName(String intervalName) {
            this.intervalName = intervalName;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        public Integer getIntervalCount() {
            return intervalCount;
        }

        public void setIntervalCount(Integer intervalCount) {
            this.intervalCount = intervalCount;
        }

        @Override
        public String toString() {
            return "NewPlan{" +
                    "planId='" + planId + '\'' +
                    ", intervalName='" + intervalName + '\'' +
                    ", interval='" + interval + '\'' +
                    ", intervalCount=" + intervalCount +
                    ", icon='" + icon + '\'' +
                    ", originalPrice=" + originalPrice +
                    ", amount='" + amount + '\'' +
                    ", currency='" + currency + '\'' +
                    ", discount=" + discount +
                    ", description=" + description +
                    ", exclTax=" + exclTax +
                    ", inclTax=" + inclTax +
                    ", reimbursement=" + reimbursement +
                    '}';
        }
    }

}
