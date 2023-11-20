package com.cedricapp.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LoginResponse {
    public boolean status;
    public String message;
    public Errors errors;
    public Data data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        return "LoginResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", errors=" + errors +
                ", data=" + data +
                '}';
    }

    public static class Errors {
        public ArrayList<String> email;

        public ArrayList<String> getEmail() {
            return email;
        }

        public void setEmail(ArrayList<String> email) {
            this.email = email;
        }

        @NonNull
        @Override
        public String toString() {
            return "Errors{" +
                    "email=" + email +
                    '}';
        }
    }

    public static class Data {
        private String access_token;
        private String refresh_token;
        private String token_type;
        private String token_expires_at;
        @SerializedName("is_logged_in")
        @Expose
        private Boolean isLoggedIn;

        @SerializedName("platform")
        @Expose
        private String platform;

        public User user;

        public Boolean getLoggedIn() {
            return isLoggedIn;
        }

        public void setLoggedIn(Boolean loggedIn) {
            isLoggedIn = loggedIn;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getRefresh_token() {
            return refresh_token;
        }

        public void setRefresh_token(String refresh_token) {
            this.refresh_token = refresh_token;
        }

        public String getToken_type() {
            return token_type;
        }

        public void setToken_type(String token_type) {
            this.token_type = token_type;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String getToken_expires_at() {
            return token_expires_at;
        }

        public void setToken_expires_at(String token_expires_at) {
            this.token_expires_at = token_expires_at;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "access_token='" + access_token + '\'' +
                    ", refresh_token='" + refresh_token + '\'' +
                    ", token_type='" + token_type + '\'' +
                    ", token_expires_at='" + token_expires_at + '\'' +
                    ", user=" + user +
                    '}';
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }


        public static class User {
            public String id;
            public String name;
            public String email;
            public String is_profile_completed;
            public Profile profile;
            public Subscription subscription;
            private Boolean is_email_verified;
            public ArrayList<Allergies> allergies;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
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

            public String getIs_profile_completed() {
                return is_profile_completed;
            }

            public void setIs_profile_completed(String is_profile_completed) {
                this.is_profile_completed = is_profile_completed;
            }

            public Profile getProfile() {
                return profile;
            }

            public void setProfile(Profile profile) {
                this.profile = profile;
            }

            public Subscription getSubscription() {
                return subscription;
            }

            public void setSubscription(Subscription subscription) {
                this.subscription = subscription;
            }

            public ArrayList<Allergies> getAllergies() {
                return allergies;
            }

            public void setAllergies(ArrayList<Allergies> allergies) {
                this.allergies = allergies;
            }

            public Boolean getIs_email_verified() {
                return is_email_verified;
            }

            public void setIs_email_verified(Boolean is_email_verified) {
                this.is_email_verified = is_email_verified;
            }

            @Override
            public String toString() {
                return "User{" +
                        "id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", email='" + email + '\'' +
                        ", is_profile_completed='" + is_profile_completed + '\'' +
                        ", profile=" + profile +
                        ", subscription=" + subscription +
                        ", is_email_verified='" + is_email_verified + '\'' +
                        ", allergies=" + allergies +
                        '}';
            }

            public static class Profile {
                public String weight;
                public String height;
                public String age;
                public String gender;
                public String goal_id;
                public String goal;
                public String level_id;
                public String level;
                public String unit;
                public String user_image;
                public String food_preference;
                public Integer food_preference_id;
                public String allergy_ids;
                public String product_id;
                public String lang;


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

                public String getGoal_id() {
                    return goal_id;
                }

                public void setGoal_id(String goal_id) {
                    this.goal_id = goal_id;
                }

                public String getGoal() {
                    return goal;
                }

                public void setGoal(String goal) {
                    this.goal = goal;
                }

                public String getLevel_id() {
                    return level_id;
                }

                public void setLevel_id(String level_id) {
                    this.level_id = level_id;
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

                public String getUser_image() {
                    return user_image;
                }

                public void setUser_image(String user_image) {
                    this.user_image = user_image;
                }


                public String getFood_preference() {
                    return food_preference;
                }

                public void setFood_preference(String food_preferences) {
                    this.food_preference = food_preferences;
                }

                public String getAllergy_ids() {
                    return allergy_ids;
                }

                public void setAllergy_ids(String allergy_ids) {
                    this.allergy_ids = allergy_ids;
                }


                public String getProduct_id() {
                    return product_id;
                }

                public void setProduct_id(String product_id) {
                    this.product_id = product_id;
                }

                public Integer getFood_preference_id() {
                    return food_preference_id;
                }

                public void setFood_preference_id(Integer food_preference_id) {
                    this.food_preference_id = food_preference_id;
                }

                public String getLang() {
                    return lang;
                }

                public void setLang(String lang) {
                    this.lang = lang;
                }

                @Override
                public String toString() {
                    return "Profile{" +
                            "weight='" + weight + '\'' +
                            ", height='" + height + '\'' +
                            ", age='" + age + '\'' +
                            ", gender='" + gender + '\'' +
                            ", goal_id='" + goal_id + '\'' +
                            ", goal='" + goal + '\'' +
                            ", level_id='" + level_id + '\'' +
                            ", level='" + level + '\'' +
                            ", unit='" + unit + '\'' +
                            ", user_image='" + user_image + '\'' +
                            ", food_preference='" + food_preference + '\'' +
                            ", food_preference_id=" + food_preference_id +
                            ", allergy_ids='" + allergy_ids + '\'' +
                            ", product_id='" + product_id + '\'' +
                            ", lang='" + lang + '\'' +
                            '}';
                }
            }

            public static class Subscription {
                public String subscription_id;
                public String stripe_status;

                String payment_status;
                public String stripe_price;

                public String quantity;
                public String starts_at;
                public String trial_ends_at;
                public String ends_at;

                private String customer;

                private String payment_intent;

                private String ephemeral_key;

                private String stripe_key;


                public String getStripe_id() {
                    return subscription_id;
                }

                public void setStripe_id(String stripe_id) {
                    this.subscription_id = stripe_id;
                }

                public String getStripe_status() {
                    return stripe_status;
                }

                public void setStripe_status(String stripe_status) {
                    this.stripe_status = stripe_status;
                }

                public String getStripe_price() {
                    return stripe_price;
                }

                public void setStripe_price(String stripe_price) {
                    this.stripe_price = stripe_price;
                }

                public String getQuantity() {
                    return quantity;
                }

                public void setQuantity(String quantity) {
                    this.quantity = quantity;
                }

                public String getTrial_ends_at() {
                    return trial_ends_at;
                }

                public void setTrial_ends_at(String trial_ends_at) {
                    this.trial_ends_at = trial_ends_at;
                }

                public String getEnds_at() {
                    return ends_at;
                }

                public void setEnds_at(String ends_at) {
                    this.ends_at = ends_at;
                }

                public String getStarts_at() {
                    return starts_at;
                }

                public void setStarts_at(String starts_at) {
                    this.starts_at = starts_at;
                }

                public String getPayment_status() {
                    return payment_status;
                }

                public void setPayment_status(String payment_status) {
                    this.payment_status = payment_status;
                }

                public String getCustomer() {
                    return customer;
                }

                public void setCustomer(String customer) {
                    this.customer = customer;
                }

                public String getPayment_intent() {
                    return payment_intent;
                }

                public void setPayment_intent(String payment_intent) {
                    this.payment_intent = payment_intent;
                }

                public String getEphemeral_key() {
                    return ephemeral_key;
                }

                public void setEphemeral_key(String ephemeral_key) {
                    this.ephemeral_key = ephemeral_key;
                }

                public String getStripe_key() {
                    return stripe_key;
                }

                public void setStripe_key(String stripe_key) {
                    this.stripe_key = stripe_key;
                }

                @Override
                public String toString() {
                    return "Subscription{" +
                            "subscription_id='" + subscription_id + '\'' +
                            ", stripe_status='" + stripe_status + '\'' +
                            ", payment_status='" + payment_status + '\'' +
                            ", stripe_price='" + stripe_price + '\'' +
                            ", quantity='" + quantity + '\'' +
                            ", starts_at='" + starts_at + '\'' +
                            ", trial_ends_at='" + trial_ends_at + '\'' +
                            ", ends_at='" + ends_at + '\'' +
                            ", customer='" + customer + '\'' +
                            ", payment_intent='" + payment_intent + '\'' +
                            ", ephemeral_key='" + ephemeral_key + '\'' +
                            ", stripe_key='" + stripe_key + '\'' +
                            '}';
                }
            }

            public static class  Allergies{
                String id;
                String name;



                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                @Override
                public String toString() {
                    return "Allergies{" +
                            "id='" + id + '\'' +
                            ", name='" + name + '\'' +
                            '}';
                }
            }
        }
    }
}
