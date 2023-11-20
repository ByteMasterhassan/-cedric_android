package com.cedricapp.utils;

import android.content.Context;

import com.cedricapp.interfaces.ProfileAPI_Callback;
import com.cedricapp.model.ProfileActivation;
import com.cedricapp.model.SignupResponse;
import com.cedricapp.retrofit.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUpdateUtil {
    public static void updateUserProfile(Context context, String authToken , ProfileActivation profile, ProfileAPI_Callback profileAPI_callback){
        Call<SignupResponse> updateProfileCall = ApiClient.getService().updateProfileAtSignUp("Bearer "+authToken, profile.getUserID(), profile.getWeight(), profile.getHeight(), profile.getAge(), profile.getGender(), profile.getGoal_id(),profile.getLevel_id(),profile.getUnit(),profile.getUsername(),profile.getUserImage(),profile.getFood_preference());
        updateProfileCall.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                profileAPI_callback.profileResponse(response);
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                profileAPI_callback.profileResponseFailure(t);
            }
        });

    }
}
