package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetailModel {
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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

    public class Data {

        @SerializedName("agent")
        @Expose
        private String agent;
        @SerializedName("os")
        @Expose
        private String os;
        @SerializedName("device_id")
        @Expose
        private String deviceId;
        @SerializedName("location")
        @Expose
        private String location;
        @SerializedName("timezone")
        @Expose
        private String timezone;
        @SerializedName("fcm_id")
        @Expose
        private String fcmId;
        @SerializedName("is_cancel")
        @Expose
        private Boolean isCancel;
        @SerializedName("version_no")
        @Expose
        private String versionNo;
        @SerializedName("build_no")
        @Expose
        private String buildNo;
        @SerializedName("os_type")
        @Expose
        private String osType;
        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("is_new_device")
        @Expose
        private Boolean isNewDevice;

        public String getAgent() {
            return agent;
        }

        public void setAgent(String agent) {
            this.agent = agent;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getFcmId() {
            return fcmId;
        }

        public void setFcmId(String fcmId) {
            this.fcmId = fcmId;
        }

        public Boolean getIsCancel() {
            return isCancel;
        }

        public void setIsCancel(Boolean isCancel) {
            this.isCancel = isCancel;
        }

        public String getVersionNo() {
            return versionNo;
        }

        public void setVersionNo(String versionNo) {
            this.versionNo = versionNo;
        }

        public String getBuildNo() {
            return buildNo;
        }

        public void setBuildNo(String buildNo) {
            this.buildNo = buildNo;
        }

        public String getOsType() {
            return osType;
        }

        public void setOsType(String osType) {
            this.osType = osType;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Boolean getIsNewDevice() {
            return isNewDevice;
        }

        public void setIsNewDevice(Boolean isNewDevice) {
            this.isNewDevice = isNewDevice;
        }

    }
}


