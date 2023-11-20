package com.cedricapp.utils;

public class StripeStatusUtil {
    public static boolean isUserAllowToUseApp(String stripeSubscriptionStatus){
        switch (stripeSubscriptionStatus){
            case "incomplete":
            case "incomplete_expired":
            case "past_due":
            case "canceled":
            case "unpaid":
            default:
                return false;
            case "trialing":
            case "active":
                return true;
        }
    }
}
