package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllergyModelForRegistration {
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("message")
    @Expose
    private String message;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
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
        return "AllergyModelForRegistration{" +
                "data=" + data +
                ", message='" + message + '\'' +
                '}';
    }

    class Datum{
        @SerializedName("id")
        @Expose
        private String allergyID;

        @SerializedName("name")
        @Expose
        private String allergyName;

        public String getAllergyID() {
            return allergyID;
        }

        public void setAllergyID(String allergyID) {
            this.allergyID = allergyID;
        }

        public String getAllergyName() {
            return allergyName;
        }

        public void setAllergyName(String allergyName) {
            this.allergyName = allergyName;
        }

        @Override
        public String toString() {
            return "Datum{" +
                    "allergyID='" + allergyID + '\'' +
                    ", allergyName='" + allergyName + '\'' +
                    '}';
        }
    }
}


