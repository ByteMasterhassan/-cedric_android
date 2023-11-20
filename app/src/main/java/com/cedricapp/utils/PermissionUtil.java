package com.cedricapp.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import com.cedricapp.common.Common;
import com.cedricapp.interfaces.PermissionDialogInterface;
import com.cedricapp.interfaces.PermissionRequestInterface;
import com.cedricapp.service.StepsService;

public class PermissionUtil {
    private static String TAG = "SELF_PERMISSION_TAG";

    public static void checkLocationPermission(Context context, PermissionDialogInterface permissionDialogInterface, PermissionRequestInterface permissionRequestInterface) {
        try {
            if (context != null) {
                if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "Fine location permission not granted");
                    }
                    //initDialog();
                    SessionUtil.setLocationPermissionBackground(context, false);

                    if (SessionUtil.showPermissionDialogAgain(context)) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Show location dialog");
                        }
                        permissionDialogInterface.showDialog("location");
                    }

                } else {
                    SessionUtil.setLocationPermissionBackground(context, true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Fine location permission granted and then check notification permission");
                        }
                        checkNotificationPermission(context, permissionDialogInterface, permissionRequestInterface);
                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Fine location permission granted and then check step counter permission");
                        }
                        checkStepCounterPermission(context, permissionDialogInterface, permissionRequestInterface);
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Context is null in checkLocationPermission()");
                }
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    public static void checkCameraPermission(Context context, PermissionRequestInterface permissionRequestInterface) {
        try {
            if (context != null) {
                if (context.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    permissionRequestInterface.permissionRequest(2);

                } else {
                    SessionUtil.setCameraPermission(context, true);
                }

            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Context is null in checkCameraPermission()");
                }
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }

    public static void checkNotificationPermission(Context context, PermissionDialogInterface permissionDialogInterface, PermissionRequestInterface permissionRequestInterface) {
        try {
            if (context != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        if (Common.isLoggingEnabled) {
                            Log.e(TAG, "Notification permission not granted");
                        }
                        SessionUtil.setNotificationPermission(context, false);
                        permissionRequestInterface.permissionRequest(4);

                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "Notification permission granted and check step counter permission");
                        }
                        //SessionUtil.setCameraPermission(context, true);
                        SessionUtil.setNotificationPermission(context, true);
                        checkStepCounterPermission(context, permissionDialogInterface, permissionRequestInterface);
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Context is null in checkNotificationPermission()");
                }
            }
        } catch (Exception ex) {
            if (Common.isLoggingEnabled) {
                ex.printStackTrace();
            }
        }
    }


    public static void checkStepCounterPermission(Context context, PermissionDialogInterface permissionDialogInterface, PermissionRequestInterface permissionRequestInterface) {
        try {
            if (context != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "checkStepCounterPermission(): greater or equal to android Q");
                    }

                    if (context.checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {

                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "checkStepCounterPermission(): ACTIVITY_RECOGNITION granted");
                        }
                        if (!StepCountServiceUtil.isMyServiceRunning(StepsService.class, context)) {
                            if (Common.isLoggingEnabled) {
                                Log.d(TAG, "step count service not already running. Start service");
                            }
                            StepCountServiceUtil.startStepCountService(context);
                        }else{
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "step count service already running.");
                            }
                        }

                        SessionUtil.setStepCounterPermission(context, true);
                        if (!isBatterySavorPermissionActive(context)) {
                            if (Common.isLoggingEnabled) {
                                Log.e(TAG, "Battery savor permission not active. Ask permission from user");
                            }
                            checkBatteryUsageRestrictionPermission(context, permissionDialogInterface);
                        }

                    } else {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "step count permission not allowed. ask permission");
                        }
                        SessionUtil.setStepCounterPermission(context, false);
                        permissionRequestInterface.permissionRequest(5);
                        //ask for permission for step counter

                    }
                } else {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "checkStepCounterPermission(): lower then android Q");
                    }
                    if (!StepCountServiceUtil.isMyServiceRunning(StepsService.class, context)) {
                        if (Common.isLoggingEnabled) {
                            Log.d(TAG, "checkStepCounterPermission(): greater or equal to android Q");
                        }
                        StepCountServiceUtil.startStepCountService(context);
                    }
                }
            } else {
                if (Common.isLoggingEnabled) {
                    Log.e(TAG, "Context is null in checkStepCounterPermission()");
                }
            }
        } catch (Exception exception) {
            if (Common.isLoggingEnabled) {
                exception.printStackTrace();
            }
        }
    }

    public static void checkBatteryUsageRestrictionPermission(Context context, PermissionDialogInterface permissionDialogInterface) {
        SessionUtil.setBatteryConsumptionPermissionBackground(context, false);

        if (!isBatterySavorPermissionActive(context)) {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "checkBatteryUsageRestrictionPermission(): battery saver permission not active");
            }
            if (context != null) {
                if (SessionUtil.showPermissionDialogAgain(context)) {
                    if (Common.isLoggingEnabled) {
                        Log.d(TAG, "checkBatteryUsageRestrictionPermission(): show dialog for battery saver permission");
                    }
                    permissionDialogInterface.showDialog("battery");
                }
            }
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(TAG, "checkBatteryUsageRestrictionPermission(): battery saver permission allowed");
            }
                SessionUtil.setBatteryConsumptionPermissionBackground(context, true);
        }

    }

    public static boolean isBatterySavorPermissionActive(Context context) {
        if (context != null) {
            PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return manager.isIgnoringBatteryOptimizations(context.getPackageName());
        } else {
            return false;
        }
    }
}
