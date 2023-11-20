package com.cedricapp.model;

public class DateModel {
    private String date;
    private int day;
    private int month;
    private String dayName;
    private String fullDate;

    public DateModel(String date, int day, int month, String dayName, String fullDate) {
        this.date = date;
        this.day = day;
        this.month = month;
        this.dayName = dayName;
        this.fullDate = fullDate;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getFullDate() {
        return fullDate;
    }

    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }
}
