package com.cedricapp.interfaces;

public interface LogoutInterface {
    public void isLogout(boolean isLoggedOut);
    public void logoutResponse(String message);
    public void logoutReponseCode(int responseCode);

    public void logoutError(Throwable t);
}
