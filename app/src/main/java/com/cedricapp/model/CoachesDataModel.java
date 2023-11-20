package com.cedricapp.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CoachesDataModel {
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

        @SerializedName("workouts")
        @Expose
        private List<Workout> workouts = null;

        public List<Workout> getWorkouts() {
            return workouts;
        }

        public void setWorkouts(List<Workout> workouts) {
            this.workouts = workouts;
        }
    }


    public class Workout implements Parcelable {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("exerciseTypes")
        @Expose
        private List<String> exerciseTypes = null;
        @SerializedName("category")
        @Expose
        private Category category;

        @SerializedName("thumbnail")
        @Expose
        private String thumbnail;
        @SerializedName("reps")
        @Expose
        private String reps;
        @SerializedName("sets")
        @Expose
        private String sets;
        @SerializedName("duration")
        @Expose
        private  String duration;

        @SerializedName("videos")
        @Expose
        private List<Video> videos = null;


        @SerializedName("isWatched")
        @Expose
        private Boolean isWatched;


        protected Workout(Parcel in) {
            if (in.readByte() == 0) {
                id = null;
            } else {
                id = in.readInt();
            }
            name = in.readString();
            description = in.readString();
            exerciseTypes = in.createStringArrayList();
            thumbnail = in.readString();
            reps = in.readString();
            sets = in.readString();
            duration = in.readString();
            byte tmpIsWatched = in.readByte();
            isWatched = tmpIsWatched == 0 ? null : tmpIsWatched == 1;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (id == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(id);
            }
            dest.writeString(name);
            dest.writeString(description);
            dest.writeStringList(exerciseTypes);
            dest.writeString(thumbnail);
            dest.writeString(reps);
            dest.writeString(sets);
            dest.writeString(duration);
            dest.writeByte((byte) (isWatched == null ? 0 : isWatched ? 1 : 2));
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public final Creator<Workout> CREATOR = new Creator<Workout>() {
            @Override
            public Workout createFromParcel(Parcel in) {
                return new Workout(in);
            }

            @Override
            public Workout[] newArray(int size) {
                return new Workout[size];
            }
        };

        public List<Video> getVideos() {
            return videos;
        }

        public void setVideos(List<Video> videos) {
            this.videos = videos;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public Boolean getWatched() {
            return isWatched;
        }

        public void setWatched(Boolean watched) {
            isWatched = watched;
        }

        public Creator<Workout> getCREATOR() {
            return CREATOR;
        }


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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getExerciseTypes() {
            return exerciseTypes;
        }

        public void setExerciseTypes(List<String> exerciseTypes) {
            this.exerciseTypes = exerciseTypes;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getReps() {
            return reps;
        }

        public void setReps(String reps) {
            this.reps = reps;
        }

        public String getSets() {
            return sets;
        }

        public void setSets(String sets) {
            this.sets = sets;
        }

        public Boolean getIsWatched() {
            return isWatched;
        }

        public void setIsWatched(Boolean isWatched) {
            this.isWatched = isWatched;
        }

    }

    public class Category {

        @SerializedName("category_name")
        @Expose
        private String categoryName;
        @SerializedName("icon")
        @Expose
        private String icon;

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

    }

    public class Video {

        @SerializedName("workout_id")
        @Expose
        private String workoutId;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("resolution")
        @Expose
        private String resolution;

        public String getWorkoutId() {
            return workoutId;
        }

        public void setWorkoutId(String workoutId) {
            this.workoutId = workoutId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getResolution() {
            return resolution;
        }

        public void setResolution(String resolution) {
            this.resolution = resolution;
        }

    }
}