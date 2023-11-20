package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LevelModel {
    @SerializedName("data")
    @Expose
    private List<Datum> data;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LevelModel{" +
                "data=" + data +
                '}';
    }

    public class Datum {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name_en")
        @Expose
        private String name;
        @SerializedName("name_sv")
        @Expose
        private String nameSv;

        @SerializedName("description_en")
        @Expose
        private String descriptionEN;
        @SerializedName("description_sv")
        @Expose
        private String descriptionSV;

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

        public String getDescriptionEN() {
            return descriptionEN;
        }

        public void setDescriptionEN(String descriptionEN) {
            this.descriptionEN = descriptionEN;
        }

        public String getDescriptionSV() {
            return descriptionSV;
        }

        public void setDescriptionSV(String descriptionSV) {
            this.descriptionSV = descriptionSV;
        }

        @Override
        public String toString() {
            return "Datum{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", nameSv='" + nameSv + '\'' +
                    ", descriptionEN='" + descriptionEN + '\'' +
                    ", descriptionSV='" + descriptionSV + '\'' +
                    '}';
        }
    }
}
