package com.cedricapp.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.cedricapp.common.Common;

public class ToastUtil {
    static final int GRAVITY = Gravity.CENTER;

    public static void showToastForFragment(Context context, boolean isActivity, Boolean isAdded, String message, int toastDuration) {

        if (context != null) {
            if (!isActivity) {
                if (isAdded) {
                    Toast toast = Toast.makeText(context, message, toastDuration);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                        toast.setGravity(GRAVITY, 0, 0);
                    toast.show();
                } else {
                    if (Common.isLoggingEnabled)
                        Log.d(Common.LOG, "Fragment is not attached with activity");
                }
            } else {
                Toast toast = Toast.makeText(context, message, toastDuration);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                    toast.setGravity(GRAVITY, 0, 0);
                toast.show();
            }
        } else {
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "Context is null");
        }
    }
}
