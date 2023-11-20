package com.cedricapp.model;

import java.util.Date;

public class CalendarModel {
    private int day;
    private int month;
    private int year;
    private Date date;

    private boolean isSelected;

    private boolean isToday;

    private boolean isActivated;




    public CalendarModel(int day, int month, int year, Date date, boolean isSelected, boolean isToday, boolean isActivated) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.date = date;
        this.isSelected = isSelected;
        this.isToday = isToday;
        this.isActivated = isActivated;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }
}
