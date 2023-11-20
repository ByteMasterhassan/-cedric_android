package com.cedricapp.model;

public class InstructorDataModel {
    String instructorName,NoOfVideos;
    Integer instructorImage,cameraIcon;

    public InstructorDataModel(String instructorName, String noOfVideos, Integer instructorImage, Integer cameraIcon) {
        this.instructorName = instructorName;
        NoOfVideos = noOfVideos;
        this.instructorImage = instructorImage;
        this.cameraIcon = cameraIcon;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getNoOfVideos() {
        return NoOfVideos;
    }

    public void setNoOfVideos(String noOfVideos) {
        NoOfVideos = noOfVideos;
    }

    public Integer getInstructorImage() {
        return instructorImage;
    }

    public void setInstructorImage(Integer instructorImage) {
        this.instructorImage = instructorImage;
    }

    public Integer getCameraIcon() {
        return cameraIcon;
    }

    public void setCameraIcon(Integer cameraIcon) {
        this.cameraIcon = cameraIcon;
    }
}
