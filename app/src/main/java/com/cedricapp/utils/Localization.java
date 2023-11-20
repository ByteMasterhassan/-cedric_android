package com.cedricapp.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.ConfigurationCompat;
import androidx.core.os.LocaleListCompat;

import com.cedricapp.common.Common;

import java.util.Objects;

public class Localization {
    static String TAG = "LANGUAGE_TAG";

    public static Resources setLanguage(Context ctx, Resources resourcesCtx) {
        Resources resources;
        String savedLanguage = SessionUtil.getlangCode(ctx);
        String systemLanguage = "en";
        systemLanguage = getSystemLanguage(ctx);
        // String language = Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "In setLanguage(), System Language: " + systemLanguage);
            Log.d(TAG, "In setLanguage(), Shared Preference Language: " + savedLanguage);
        }
        if (!savedLanguage.matches("")) {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "In setLanguage(), saved language in shared preference is not empty and System Language: " + systemLanguage);
                Log.d(TAG, "In setLanguage(), saved language in shared preference is not empty and Shared Preference Language: " + savedLanguage);
            }
            resources = Localization.setLocale(ctx, savedLanguage).getResources();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "In setLanguage(), saved language in shared preference is empty and system language: " + systemLanguage);
                Log.d(TAG, "In setLanguage(), saved language in shared preference is empty and shared preference language: " + savedLanguage);
            }
            if (systemLanguage.matches("sv")) {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "In setLanguage(), system lang matched with sv and saved language in shared preference is empty and system language: " + systemLanguage);
                    Log.d(TAG, "In setLanguage(), system lang matched with sv and saved language in shared preference is empty and saved shared preference language: " + savedLanguage);
                }
                SessionUtil.setlangCode(ctx, "sv");
                resources = Localization.setLocale(ctx, "sv").getResources();
            } else {
                if (Common.isLoggingEnabled) {
                    Log.d(TAG, "In setLanguage(), system lang matched with not sv and saved language in shared preference is empty and system language: " + systemLanguage);
                    Log.d(TAG, "In setLanguage(), system lang matched with not sv and saved language in shared preference is empty and saved shared preference language: " + savedLanguage);
                }
                SessionUtil.setlangCode(ctx, "en");
                resources = Localization.setLocale(ctx, "en").getResources();
            }

        }
        return resources;
    }

    public static Resources setLanguageOnLogin(Context ctx, Resources resourcesCtx) {
        Resources resources;
        String systemLanguage = "en";
        systemLanguage = getSystemLanguage(ctx);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "In setLanguageOnLogin(), System Language: " + systemLanguage);
        }

        if (systemLanguage.matches("sv")) {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "In setLanguageOnLogin(), system language matches with sv and System Language variable value: " + systemLanguage);
            }
            //SessionUtil.setlangCode(ctx, "sv");
            resources = Localization.setLocale(ctx, "sv").getResources();
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "In setLanguageOnLogin(), system language matches not with sv and System Language variable value: " + systemLanguage);
            }
            //SessionUtil.setlangCode(ctx, "en");
            resources = Localization.setLocale(ctx, "en").getResources();
        }


        return resources;
    }

    public static String getLang(Context context) {
        String savedLanguage = SessionUtil.getlangCode(context);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "In getLang(), saved Language in shared preference is " + savedLanguage);
        }
        String currentLanguageStr = getSystemLanguage(context);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "In getLang(), system language is  " + currentLanguageStr);
        }
        //String currentLanguageStr = Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
        if (savedLanguage.matches("")) {
            return currentLanguageStr;
        } else {
            return savedLanguage;
        }
    }

    public static String getSystemLanguage(Context context) {
        String systemLanguage = "en";
        /*Configuration configuration = context.getResources().getConfiguration();
        Locale locale = configuration.locale;
        String systemLanguage = locale.getLanguage();*/
        LocaleListCompat llc = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration());
        if (Common.isLoggingEnabled)
            Log.d(TAG, "in getSystemLanguage(), All languages of system are " + llc.toString());
        if (llc.size() > 0) {
            systemLanguage = Objects.requireNonNull(llc.get(0)).getLanguage();
        }
        if (Common.isLoggingEnabled)
            Log.d(TAG, "In getSystemLanguage(), System language: " + systemLanguage);
        return systemLanguage;
    }


    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.LANGUAGE_TAG";

    // the method is used to set the language at runtime
    public static Context setLocale(Context context, String language) {
        //persist(context, language);
        if (Common.isLoggingEnabled) {
            Log.d(TAG, "In setLocale(), Language in variable: " + language);
        }
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(language);
        AppCompatDelegate.setApplicationLocales(appLocale);

        return context;

    }

}
