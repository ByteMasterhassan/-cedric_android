package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApplicationDetailsModel {

    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("app_version")
        @Expose
        private String appVersion;
        @SerializedName("build_code")
        @Expose
        private Integer buildCode;
        @SerializedName("status")
        @Expose
        private String status;

        /*public Integer getVersionName() {
            return versionName;
        }

        public void setVersionName(Integer versionName) {
            this.versionName = versionName;
        }*/

        public Integer getBuildCode() {
            return buildCode;
        }

        public void setBuildCode(Integer buildCode) {
            this.buildCode = buildCode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }
    }
}
