package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GoalModel {
    @SerializedName("data")
    @Expose
    private ArrayList<Datum> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("error")
    @Expose
    private String error;


    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "GoalModel{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", error='" + error + '\'' +
                '}';
    }

    public static class Datum{
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name_en")
        @Expose
        private String name;
        @SerializedName("name_sv")
        @Expose
        private String nameSv;
        @SerializedName("stripe_product")
        @Expose
        private StripeProduct stripeProduct;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNameSv() {
            return nameSv;
        }

        public void setNameSv(String nameSv) {
            this.nameSv = nameSv;
        }

        public StripeProduct getStripeProduct() {
            return stripeProduct;
        }

        public void setStripeProduct(StripeProduct stripeProduct) {
            this.stripeProduct = stripeProduct;
        }


        @Override
        public String toString() {
            return "Datum{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", nameSv='" + nameSv + '\'' +
                    ", stripeProduct=" + stripeProduct +
                    '}';
        }

        public class StripeProduct {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("goal_id")
            @Expose
            private String goalId;
            @SerializedName("name_en")
            @Expose
            private String name;
            @SerializedName("name_sv")
            @Expose
            private String nameSV;
            @SerializedName("description_en")
            @Expose
            private String description;
            @SerializedName("description_sv")
            @Expose
            private String description_SV;
            @SerializedName("product_id")
            @Expose
            private String productId;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getGoalId() {
                return goalId;
            }

            public void setGoalId(String goalId) {
                this.goalId = goalId;
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

            public String getProductId() {
                return productId;
            }

            public void setProductId(String productId) {
                this.productId = productId;
            }

            public String getNameSV() {
                return nameSV;
            }

            public void setNameSV(String nameSV) {
                this.nameSV = nameSV;
            }

            public String getDescription_SV() {
                return description_SV;
            }

            public void setDescription_SV(String description_SV) {
                this.description_SV = description_SV;
            }

            @Override
            public String toString() {
                return "StripeProduct{" +
                        "id=" + id +
                        ", goalId='" + goalId + '\'' +
                        ", name='" + name + '\'' +
                        ", nameSV='" + nameSV + '\'' +
                        ", description='" + description + '\'' +
                        ", description_SV='" + description_SV + '\'' +
                        ", productId='" + productId + '\'' +
                        '}';
            }
        }
    }
}
