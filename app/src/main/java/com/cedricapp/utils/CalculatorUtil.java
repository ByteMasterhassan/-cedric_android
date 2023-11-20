package com.cedricapp.utils;

import android.content.Context;
import android.util.Log;

import com.cedricapp.common.Common;

public class CalculatorUtil {
    public static float getDistanceNow(Context context,long steps, double height) {
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "getDistanceNow()");
        }
        double stepLength = 0.0;
        if (SessionUtil.getUserUnitType(context).matches("Imperial")) {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "getDistanceNow(): Imperial");
            }
            double heightInCM = height * 30.48;
            stepLength = heightInCM * 0.415;
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "getDistanceNow(): Metric");
            }
            //Height already in CM
            stepLength = height * 0.415;
        }
        //double stepLenght = (hightCM * 0.3619);
        float Distance = (float) (stepLength * steps);
        return Distance / 100;
    }

    public static double caloriesFromSteps(Context context, double height, double weight, double stepsCount) {
        double caloriesBurnedPerMile;
        final double walkingFactor = 0.57;
        double strip;
        double stepCountMile; // step/mile
        double conversationFactor;
        if (Common.isLoggingEnabled) {
            Log.d(Common.LOG, "caloriesCalulatorFromSteps()");
        }

        if (SessionUtil.getUserUnitType(context).matches("Imperial")) {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "caloriesCalulatorFromSteps(): Imperial");
            }
            caloriesBurnedPerMile = walkingFactor * weight;
            //strip = height * 0.415;
            double heightinCM = height * 30.48;
            strip = heightinCM * 0.415;
        } else {
            if (Common.isLoggingEnabled) {
                Log.d(Common.LOG, "caloriesCalulatorFromSteps(): Metric");
            }
            caloriesBurnedPerMile = walkingFactor * (weight * 2.20462);
            strip = height * 0.415;
        }

        stepCountMile = 160934.4 / strip;

        conversationFactor = caloriesBurnedPerMile / stepCountMile;

        caloriesBurnedPerMile = stepsCount * conversationFactor;

        return caloriesBurnedPerMile;
    }
}
