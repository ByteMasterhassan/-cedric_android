package com.cedricapp.utils;

import android.os.Build;

public class DeviceUtil {
    public static String getUserDeviceModel(){
       return Build.MANUFACTURER+" "+Build.BRAND+" "+Build.MODEL+" "+Build.PRODUCT;
    }

    public static String getOS(){
        return Build.VERSION.RELEASE;
    }
}
