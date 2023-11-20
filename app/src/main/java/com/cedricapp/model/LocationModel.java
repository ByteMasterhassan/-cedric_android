package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LocationModel {
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("accuracy")
    @Expose
    private Double accuracy;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return "LocationModel{" +
                "location=" + location +
                ", accuracy=" + accuracy +
                '}';
    }

    public static class Location {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        @SerializedName("error")
        @Expose
        private Error error;

        public Error getError() {
            return error;
        }

        public void setError(Error error) {
            this.error = error;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

        @Override
        public String toString() {
            return "Location{" +
                    "lat=" + lat +
                    ", lng=" + lng +
                    ", error=" + error +
                    '}';
        }
    }

    public static class Error {

        @SerializedName("errors")
        @Expose
        private ArrayList<Error__1> errors = null;
        @SerializedName("code")
        @Expose
        private Integer code;
        @SerializedName("message")
        @Expose
        private String message;

        public ArrayList<Error__1> getErrors() {
            return errors;
        }

        public void setErrors(ArrayList<Error__1> errors) {
            this.errors = errors;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "errors=" + errors +
                    ", code=" + code +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    public static class Error__1 {

        @SerializedName("domain")
        @Expose
        private String domain;
        @SerializedName("reason")
        @Expose
        private String reason;
        @SerializedName("message")
        @Expose
        private String message;

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "Error__1{" +
                    "domain='" + domain + '\'' +
                    ", reason='" + reason + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
