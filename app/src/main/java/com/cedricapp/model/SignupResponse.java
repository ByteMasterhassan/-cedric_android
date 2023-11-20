package com.cedricapp.model;

import java.util.ArrayList;
import java.util.Date;

public class SignupResponse {
    String id, email, name, password, weight, age, cvc, goals, level, height, gender, unitType;
    public String message;
    public Errors errors;
    public Data data;
    public String goal_id;
    public String level_id;
    public boolean status;
    public Object email_verified_at;
    public String is_completed;
    public Object otp;
    public Object role;
    public Object remember_token;
    public Date created_at;
    public Date updated_at;
    public String stripe_id;
    public Object pm_type;
    public Object pm_last_four;
    public Object trial_ends_at;

   /* public SignupResponse() {
    }*/

   /* public SignupResponse(String id, String email, String name, String password, String weight, String age, String cvc, String goals, String level, String height, String gender, String unitType, String message, Errors errors, Data data, String goal_id, String level_id, boolean status, Object email_verified_at, String is_completed, Object otp, Object role, Object remember_token, Date created_at, Date updated_at, String stripe_id, Object pm_type, Object pm_last_four, Object trial_ends_at) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.weight = weight;
        this.age = age;
        this.cvc = cvc;
        this.goals = goals;
        this.level = level;
        this.height = height;
        this.gender = gender;
        this.unitType = unitType;
        this.message = message;
        this.errors = errors;
        this.data = data;
        this.goal_id = goal_id;
        this.level_id = level_id;
        this.status = status;
        this.email_verified_at = email_verified_at;
        this.is_completed = is_completed;
        this.otp = otp;
        this.role = role;
        this.remember_token = remember_token;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.stripe_id = stripe_id;
        this.pm_type = pm_type;
        this.pm_last_four = pm_last_four;
        this.trial_ends_at = trial_ends_at;
    }*/

    public String getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(String goal_id) {
        this.goal_id = goal_id;
    }

    public String getLevel_id() {
        return level_id;
    }

    public void setLevel_id(String level_id) {
        this.level_id = level_id;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public Object getEmail_verified_at() {
        return email_verified_at;
    }

    public void setStripe_id(String stripe_id) {
        this.stripe_id = stripe_id;
    }

    public void setEmail_verified_at(Object email_verified_at) {
        this.email_verified_at = email_verified_at;
    }

    public String getIs_completed() {
        return is_completed;
    }

    public void setIs_completed(String is_completed) {
        this.is_completed = is_completed;
    }

    public Object getOtp() {
        return otp;
    }

    public void setOtp(Object otp) {
        this.otp = otp;
    }

    public Object getRole() {
        return role;
    }

    public void setRole(Object role) {
        this.role = role;
    }

    public Object getRemember_token() {
        return remember_token;
    }

    public void setRemember_token(Object remember_token) {
        this.remember_token = remember_token;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getStripe_id() {
        return stripe_id;
    }

    public Object getPm_type() {
        return pm_type;
    }

    public void setPm_type(Object pm_type) {
        this.pm_type = pm_type;
    }

    public Object getPm_last_four() {
        return pm_last_four;
    }

    public void setPm_last_four(Object pm_last_four) {
        this.pm_last_four = pm_last_four;
    }

    public Object getTrial_ends_at() {
        return trial_ends_at;
    }

    public void setTrial_ends_at(Object trial_ends_at) {
        this.trial_ends_at = trial_ends_at;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

/* public SignupResponse(String name, String email, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }*/


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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "SignupResponse{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", weight='" + weight + '\'' +
                ", age='" + age + '\'' +
                ", cvc='" + cvc + '\'' +
                ", goals='" + goals + '\'' +
                ", level='" + level + '\'' +
                ", height='" + height + '\'' +
                ", gender='" + gender + '\'' +
                ", unitType='" + unitType + '\'' +
                ", message='" + message + '\'' +
                ", errors=" + errors +
                ", data=" + data +
                ", goal_id='" + goal_id + '\'' +
                ", level_id='" + level_id + '\'' +
                ", status=" + status +
                ", email_verified_at=" + email_verified_at +
                ", is_completed='" + is_completed + '\'' +
                ", otp=" + otp +
                ", role=" + role +
                ", remember_token=" + remember_token +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", stripe_id='" + stripe_id + '\'' +
                ", pm_type=" + pm_type +
                ", pm_last_four=" + pm_last_four +
                ", trial_ends_at=" + trial_ends_at +
                '}';
    }


    public class Errors {
        public ArrayList<String> email;

        public ArrayList<String> user_id;

        public ArrayList<String> weight;
         public ArrayList<String> height;
         public ArrayList<String> age;
         public  ArrayList<String> gender;
         public ArrayList<String> goal_id;
         public ArrayList<String> level_id;
         public ArrayList<String> unit;

        public ArrayList<String> getEmail() {
            return email;
        }

        public void setEmail(ArrayList<String> email) {
            this.email = email;
        }

        public ArrayList<String> getUser_id() {
            return user_id;
        }

        public void setUser_id(ArrayList<String> user_id) {
            this.user_id = user_id;
        }

        public ArrayList<String> getWeight() {
            return weight;
        }

        public void setWeight(ArrayList<String> weight) {
            this.weight = weight;
        }

        public ArrayList<String> getHeight() {
            return height;
        }

        public void setHeight(ArrayList<String> height) {
            this.height = height;
        }

        public ArrayList<String> getAge() {
            return age;
        }

        public void setAge(ArrayList<String> age) {
            this.age = age;
        }

        public ArrayList<String> getGender() {
            return gender;
        }

        public void setGender(ArrayList<String> gender) {
            this.gender = gender;
        }

        public ArrayList<String> getGoal_id() {
            return goal_id;
        }

        public void setGoal_id(ArrayList<String> goal_id) {
            this.goal_id = goal_id;
        }

        public ArrayList<String> getLevel_id() {
            return level_id;
        }

        public void setLevel_id(ArrayList<String> level_id) {
            this.level_id = level_id;
        }

        public ArrayList<String> getUnit() {
            return unit;
        }

        public void setUnit(ArrayList<String> unit) {
            this.unit = unit;
        }
    }


    public static class Data {
        private String access_token;
        private String refresh_token;
        private String token_type;
        private String expires_at;
        private String id;
        private String email;
        private String current_period_end;
        private String current_period_start;
        private String customer;
        public String user_id;
        public String weight;
        public String height;
        public String age;
        public String gender;
        public String goal;
        public String level;
        public String unit;
        public Date created_at;
        public Date updated_at;
        public String goal_id;
        public String level_id;
        public String name;
        public String is_completed;
        public String subscription_id;
        public String trail_ends;
        public String trial_end;
        public String avatar;
        public String food_preference_id;
        public String food_preference;
        public String allergy_ids;
        ArrayList<String> allergies;


        /*public Data() {
        }*/

       /* public Data(String access_token, String refresh_token, String token_type, String expires_at, String id, String email, String current_period_end, String current_period_start, String customer, String trial_end, String user_id, String weight, String height, String age, String gender, String goal, String level, String unit, Date created_at, Date updated_at, String goal_id, String level_id, String name, String is_completed, String subscription_id) {
            this.access_token = access_token;
            this.refresh_token = refresh_token;
            this.token_type = token_type;
            this.expires_at = expires_at;
            this.id = id;
            this.email = email;
            this.current_period_end = current_period_end;
            this.current_period_start = current_period_start;
            this.customer = customer;
            this.trial_end = trial_end;
            this.user_id = user_id;
            this.weight = weight;
            this.height = height;
            this.age = age;
            this.gender = gender;
            this.goal = goal;
            this.level = level;
            this.unit = unit;
            this.created_at = created_at;
            this.updated_at = updated_at;
            this.goal_id = goal_id;
            this.level_id = level_id;
            this.name = name;
            this.is_completed = is_completed;
            this.subscription_id = subscription_id;
        }*/

       /* public Data(String access_token, String refresh_token, String token_type, String expires_at, String id, String email, String current_period_end, String current_period_start, String customer, String trial_end, String user_id, String weight, String height, String age, String gender, String goal, String level, String unit, Date created_at, Date updated_at, String goal_id, String level_id, String name, String is_completed, String subscription_id, String avatarURL) {
            this.access_token = access_token;
            this.refresh_token = refresh_token;
            this.token_type = token_type;
            this.expires_at = expires_at;
            this.id = id;
            this.email = email;
            this.current_period_end = current_period_end;
            this.current_period_start = current_period_start;
            this.customer = customer;
            this.trial_end = trial_end;
            this.user_id = user_id;
            this.weight = weight;
            this.height = height;
            this.age = age;
            this.gender = gender;
            this.goal = goal;
            this.level = level;
            this.unit = unit;
            this.created_at = created_at;
            this.updated_at = updated_at;
            this.goal_id = goal_id;
            this.level_id = level_id;
            this.name = name;
            this.is_completed = is_completed;
            this.subscription_id = subscription_id;
            this.avatarURL = avatarURL;
        }*/

  /*      public Data(String id, String email, String user_id, String weight, String height, String age, String gender, String unit, Date created_at, Date updated_at, String goal_id, String level_id, String name, String avatarURL) {
            this.id = id;
            this.email = email;
            this.user_id = user_id;
            this.weight = weight;
            this.height = height;
            this.age = age;
            this.gender = gender;
            this.unit = unit;
            this.created_at = created_at;
            this.updated_at = updated_at;
            this.goal_id = goal_id;
            this.level_id = level_id;
            this.name = name;
            this.avatarURL = avatarURL;
        }*/


        public String getIs_completed() {
            return is_completed;
        }

        public void setIs_completed(String is_completed) {
            this.is_completed = is_completed;
        }

        public String getSubscription_id() {
            return subscription_id;
        }

        public void setSubscription_id(String subscription_id) {
            this.subscription_id = subscription_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }


        public String getRefresh_token() {
            return refresh_token;
        }

        public void setRefresh_token(String refresh_token) {
            this.refresh_token = refresh_token;
        }

        public Date getCreated_at() {
            return created_at;
        }

        public String getGoal_id() {
            return goal_id;
        }


        public String getLevel_id() {
            return level_id;
        }

        public void setGoal_id(String goal_id) {
            this.goal_id = goal_id;
        }

        public void setLevel_id(String level_id) {
            this.level_id = level_id;
        }

        public void setCreated_at(Date created_at) {
            this.created_at = created_at;
        }

        public Date getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(Date updated_at) {
            this.updated_at = updated_at;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getGoal() {
            return goal;
        }

        public void setGoal(String goal) {
            this.goal = goal;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCurrent_period_end() {
            return current_period_end;
        }

        public void setCurrent_period_end(String current_period_end) {
            this.current_period_end = current_period_end;
        }

        public String getCurrent_period_start() {
            return current_period_start;
        }

        public void setCurrent_period_start(String current_period_start) {
            this.current_period_start = current_period_start;
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }


        public String getToken_type() {
            return token_type;
        }

        public void setToken_type(String token_type) {
            this.token_type = token_type;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getExpires_at() {
            return expires_at;
        }

        public void setExpires_at(String expires_at) {
            this.expires_at = expires_at;
        }


        public String getTrail_ends() {
            return trail_ends;
        }

        public void setTrail_ends(String trail_ends) {
            this.trail_ends = trail_ends;
        }

        public String getTrial_end() {
            return trial_end;
        }

        public void setTrial_end(String trial_end) {
            this.trial_end = trial_end;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "access_token='" + access_token + '\'' +
                    ", refresh_token='" + refresh_token + '\'' +
                    ", token_type='" + token_type + '\'' +
                    ", expires_at='" + expires_at + '\'' +
                    ", id='" + id + '\'' +
                    ", email='" + email + '\'' +
                    ", current_period_end='" + current_period_end + '\'' +
                    ", current_period_start='" + current_period_start + '\'' +
                    ", customer='" + customer + '\'' +
                    ", user_id='" + user_id + '\'' +
                    ", weight='" + weight + '\'' +
                    ", height='" + height + '\'' +
                    ", age='" + age + '\'' +
                    ", gender='" + gender + '\'' +
                    ", goal='" + goal + '\'' +
                    ", level='" + level + '\'' +
                    ", unit='" + unit + '\'' +
                    ", created_at=" + created_at +
                    ", updated_at=" + updated_at +
                    ", goal_id='" + goal_id + '\'' +
                    ", level_id='" + level_id + '\'' +
                    ", name='" + name + '\'' +
                    ", is_completed='" + is_completed + '\'' +
                    ", subscription_id='" + subscription_id + '\'' +
                    ", trail_ends='" + trail_ends + '\'' +
                    ", trial_end='" + trial_end + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getFood_preference_id() {
            return food_preference_id;
        }

        public void setFood_preference_id(String food_preference_id) {
            this.food_preference_id = food_preference_id;
        }

        public String getFood_preference() {
            return food_preference;
        }

        public void setFood_preference(String food_preference) {
            this.food_preference = food_preference;
        }

        public String getAllergy_ids() {
            return allergy_ids;
        }

        public void setAllergy_ids(String allergy_ids) {
            this.allergy_ids = allergy_ids;
        }

        public ArrayList<String> getAllergies() {
            return allergies;
        }

        public void setAllergies(ArrayList<String> allergies) {
            this.allergies = allergies;
        }
    }

}


































