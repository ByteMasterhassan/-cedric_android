package com.cedricapp.model;

public class NotificationModel {
    private int id;
    private String title;
    private String description;
    private String time;
    private boolean isRead;

    public NotificationModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public NotificationModel() {
    }

    public NotificationModel(int id, String title, String description, String time, boolean isRead) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.time = time;
        this.isRead = isRead;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "NotificationModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", time='" + time + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
