package com.cedricapp.interfaces;

public interface UserDetailsListener {
    public void response(boolean isSuccessful, String message);
    public void responseCode(int responseCode);
    public void responseError(Throwable t);
}
