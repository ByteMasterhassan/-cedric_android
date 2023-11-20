package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpdateLanguage implements Serializable {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("lang")
    @Expose
    private String lang;

    @SerializedName("error")
    @Expose
    private String error;


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public String getLang() {
            return lang;
    }

    public void setLang(String lang) {
            this.lang = lang;
    }

    @Override
    public String toString() {
        return "UpdateLanguage{" +
                "message='" + message + '\'' +
                ", lang='" + lang + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
