package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StripeIntent {
    @SerializedName("data")
    @Expose
    private Data data;

    @SerializedName("error")
    @Expose
    private String error;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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
        return "StripePaymentIntent{" +
                "data=" + data +
                ", error='" + error + '\'' +
                '}';
    }

    public static class Data{
        @SerializedName("payment_intent")
        @Expose
        private String paymentIntent;

        @SerializedName("setup_intent")
        @Expose
        private String setupIntent;

        @SerializedName("customer")
        @Expose
        private String customer;

        @SerializedName("ephemeral_key")
        @Expose
        String ephemeralKey;

        @SerializedName("stripe_key")
        @Expose
        private String stripe_key;

        public String getPaymentIntent() {
            return paymentIntent;
        }

        public void setPaymentIntent(String paymentIntent) {
            this.paymentIntent = paymentIntent;
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }

        public String getEphemeralKey() {
            return ephemeralKey;
        }

        public void setEphemeralKey(String ephemeralKey) {
            this.ephemeralKey = ephemeralKey;
        }


        public String getStripe_key() {
            return stripe_key;
        }

        public void setStripe_key(String stripe_key) {
            this.stripe_key = stripe_key;
        }

        public String getSetupIntent() {
            return setupIntent;
        }

        public void setSetupIntent(String setupIntent) {
            this.setupIntent = setupIntent;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "paymentIntent='" + paymentIntent + '\'' +
                    ", setupIntent='" + setupIntent + '\'' +
                    ", customer='" + customer + '\'' +
                    ", ephemeralKey='" + ephemeralKey + '\'' +
                    ", stripe_key='" + stripe_key + '\'' +
                    '}';
        }
    }
}
