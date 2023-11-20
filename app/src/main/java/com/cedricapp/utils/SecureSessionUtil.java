package com.cedricapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.cedricapp.model.CredentialsModel;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecureSessionUtil {
    private final String ENC_SHARED_PREFERENCE = "ENC_SHARED_PREFERENCE";
    private final String ENC_EMAIL = "ENC_EMAIL";
    private final String ENC_PWD = "ENC_PWD";

    private final String ENC_STRIPE_KEY = "ENC_STRIPE_KEY";

    private final String ENC_PAYMENT_INTENT = "ENC_PAYMENT_INTENT";

    private final String ENC_CUSTOMER = "ENC_CUSTOMER";

    private final String ENC_EPHEMERAL_KEY = "ENC_EPHEMERAL_KEY";
    SharedPreferences sharedPreferences;
    MasterKey mainKey;

    public SecureSessionUtil(Context context) {
        try {
            mainKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            sharedPreferences = EncryptedSharedPreferences
                    .create(
                            context,
                            ENC_SHARED_PREFERENCE,
                            mainKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

    }

    public void saveCredentials(CredentialsModel credentialsModel){
        sharedPreferences.edit().putString(ENC_EMAIL, credentialsModel.getEmail()).apply();
        sharedPreferences.edit().putString(ENC_PWD, credentialsModel.getPassword()).apply();

    }

    public CredentialsModel getCredentials(){
        return new CredentialsModel(sharedPreferences.getString(ENC_EMAIL,""),sharedPreferences.getString(ENC_PWD,""));
    }

    public void saveStripeCustomer(String stripeCustomer){
        sharedPreferences.edit().putString(ENC_CUSTOMER, stripeCustomer).apply();
    }

    public String getStripeCustomer(){
        return sharedPreferences.getString(ENC_CUSTOMER,"");
    }

    public void saveStripeKey(String stripeKey){
        sharedPreferences.edit().putString(ENC_STRIPE_KEY, stripeKey).apply();
    }

    public String getStripeKey(){
        return sharedPreferences.getString(ENC_STRIPE_KEY,"");
    }

    public void savePaymentIntent(String paymentIntent){
        sharedPreferences.edit().putString(ENC_PAYMENT_INTENT, paymentIntent).apply();
    }

    public String getPaymentIntent(){
        return sharedPreferences.getString(ENC_PAYMENT_INTENT,"");
    }

    public void saveStripeEphemeral(String ephemeralKey){
        sharedPreferences.edit().putString(ENC_EPHEMERAL_KEY, ephemeralKey).apply();
    }

    public String getStripeEphemeral(){
        return sharedPreferences.getString(ENC_EPHEMERAL_KEY,"");
    }

}
