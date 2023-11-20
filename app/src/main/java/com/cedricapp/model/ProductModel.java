package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProductModel {
    @SerializedName("package")
    @Expose
    private String _package;
    @SerializedName("plans")
    @Expose
    private List<Plan> plans;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("coupon")
    @Expose
    private Integer coupon;
    @SerializedName("coupon_type")
    @Expose
    private String couponType;

    public String getPackage() {
        return _package;
    }

    public void setPackage(String _package) {
        this._package = _package;
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCoupon() {
        return coupon;
    }

    public void setCoupon(Integer coupon) {
        this.coupon = coupon;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "_package='" + _package + '\'' +
                ", plans=" + plans +
                ", status='" + status + '\'' +
                ", coupon=" + coupon +
                ", couponType='" + couponType + '\'' +
                '}';
    }

    public class Plan {

        @SerializedName("product_id")
        @Expose
        private String productId;
        @SerializedName("plan_id")
        @Expose
        private String planId;
        @SerializedName("icon")
        @Expose
        private String icon;
        @SerializedName("package")
        @Expose
        private String _package;
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
        private ArrayList<String> description;
        @SerializedName("is_current_plan")
        @Expose
        private Boolean isCurrentPlan;

        @SerializedName("name")
        @Expose
        private String intervalName;

        @SerializedName("interval")
        @Expose
        private String interval;

        @SerializedName("interval_count")
        @Expose
        private String interval_count;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

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

        public String getPackage() {
            return _package;
        }

        public void setPackage(String _package) {
            this._package = _package;
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

        public ArrayList<String> getDescription() {
            return description;
        }

        public void setDescription(ArrayList<String> description) {
            this.description = description;
        }

        public Boolean getIsCurrentPlan() {
            return isCurrentPlan;
        }

        public void setIsCurrentPlan(Boolean isCurrentPlan) {
            this.isCurrentPlan = isCurrentPlan;
        }

        public String get_package() {
            return _package;
        }

        public void set_package(String _package) {
            this._package = _package;
        }

        public Boolean getCurrentPlan() {
            return isCurrentPlan;
        }

        public void setCurrentPlan(Boolean currentPlan) {
            isCurrentPlan = currentPlan;
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

        public String getInterval_count() {
            return interval_count;
        }

        public void setInterval_count(String interval_count) {
            this.interval_count = interval_count;
        }

        @Override
        public String toString() {
            return "Plan{" +
                    "productId='" + productId + '\'' +
                    ", planId='" + planId + '\'' +
                    ", icon='" + icon + '\'' +
                    ", _package='" + _package + '\'' +
                    ", originalPrice=" + originalPrice +
                    ", amount='" + amount + '\'' +
                    ", currency='" + currency + '\'' +
                    ", discount=" + discount +
                    ", description=" + description +
                    ", isCurrentPlan=" + isCurrentPlan +
                    ", intervalName='" + intervalName + '\'' +
                    ", interval='" + interval + '\'' +
                    ", interval_count='" + interval_count + '\'' +
                    '}';
        }
    }
}



