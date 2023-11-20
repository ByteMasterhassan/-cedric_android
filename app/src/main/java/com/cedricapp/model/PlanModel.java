package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlanModel {
    @SerializedName("data")
    @Expose
    private List<Datum> data;

    @SerializedName("error")
    @Expose
    private String error;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "PlanModel{" +
                "data=" + data +
                ", error='" + error + '\'' +
                '}';
    }

    public class Datum {

        @SerializedName("goal_id")
        @Expose
        private Integer goalId;
        @SerializedName("product_id")
        @Expose
        private String productId;
        @SerializedName("name")
        @Expose
        private String name;
        /*@SerializedName("nameSV")
        @Expose
        private String nameSV;*/
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("plans")
        @Expose
        private List<Plan> plans;

        public Integer getGoalId() {
            return goalId;
        }

        public void setGoalId(Integer goalId) {
            this.goalId = goalId;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Plan> getPlans() {
            return plans;
        }

        public void setPlans(List<Plan> plans) {
            this.plans = plans;
        }

        @Override
        public String toString() {
            return "Datum{" +
                    "goalId=" + goalId +
                    ", productId='" + productId + '\'' +
                    ", name='" + name + '\'' +
                    /*", nameSV='" + nameSV + '\'' +*/
                    ", description='" + description + '\'' +
                    ", plans=" + plans +
                    '}';
        }

   /*     public String getNameSV() {
            return nameSV;
        }

        public void setNameSV(String nameSV) {
            this.nameSV = nameSV;
        }*/

        public class Plan {

            @SerializedName("plan_id")
            @Expose
            private String planId;
            @SerializedName("interval")
            @Expose
            private String interval;

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

            @Override
            public String toString() {
                return "Plan{" +
                        "planId='" + planId + '\'' +
                        ", interval='" + interval + '\'' +
                        '}';
            }
        }

    }
}
