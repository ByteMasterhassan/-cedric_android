package com.cedricapp.interfaces;

import com.cedricapp.model.SignupResponse;

import retrofit2.Response;

public interface ProfileAPI_Callback {
    public void profileResponse(Response<SignupResponse> response);
    public void profileResponseFailure(Throwable t);
}
