package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProgressDataModel implements Serializable {

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private String message;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public ProgressDataModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ProgressDataModel{" +
                "data=" + data +
                ", message='" + message + '\'' +
                '}';
    }


    public static class Data implements Serializable{

        @SerializedName("program")
        @Expose
        private String program;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("workouts")
        @Expose
        private List<Workouts> workouts = null;

        public String getProgram() {
            return program;
        }

        public void setProgram(String program) {
            this.program = program;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Workouts> getWorkouts() {
            return workouts;
        }

        public void setWorkouts(List<Workouts> workouts) {
            this.workouts = workouts;
        }



        public class Workouts implements Serializable{

            @SerializedName("day")
            @Expose
            private String day;
            @SerializedName("week")
            @Expose
            private String week;
            @SerializedName("workout")
            @Expose
            private List<Workout> workout = null;

            public String getDay() {
                return day;
            }

            public void setDay(String day) {
                this.day = day;
            }

            public String getWeek() {
                return week;
            }

            public void setWeek(String week) {
                this.week = week;
            }

            public List<Workout> getWorkout() {
                return workout;
            }

            public void setWorkout(List<Workout> workout) {
                this.workout = workout;
            }


            public class Workout implements Serializable{

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
                private ExerciseTypes exerciseTypes;
             /*   @SerializedName("category")
                @Expose
                private Category category;*/
                @SerializedName("videoUrl")
                @Expose
                private String videoUrl;
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
                private String duration;
                @SerializedName("is_watched")
                @Expose
                private Boolean isWatched;

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

                public ExerciseTypes getExerciseTypes() {
                    return exerciseTypes;
                }

                public void setExerciseTypes(ExerciseTypes exerciseTypes) {
                    this.exerciseTypes = exerciseTypes;
                }



                public String getVideoUrl() {
                    return videoUrl;
                }

                public void setVideoUrl(String videoUrl) {
                    this.videoUrl = videoUrl;
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

                public String getDuration() {
                    return duration;
                }

                public void setDuration(String duration) {
                    this.duration = duration;
                }

                public Boolean getIsWatched() {
                    return isWatched;
                }

                public void setIsWatched(Boolean isWatched) {
                    this.isWatched = isWatched;
                }


            }


            public class ExerciseTypes implements Serializable{

                @SerializedName("workout_type")
                @Expose
                private String workoutType;

                public String getWorkoutType() {
                    return workoutType;
                }

                public void setWorkoutType(String workoutType) {
                    this.workoutType = workoutType;
                }

            }


        }
    }
}
