/*
 * Copyright 2013 Thomas Hoffmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cedricapp.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cedricapp.common.Common;
import com.cedricapp.service.StepCounterDataSync;

import java.util.Objects;


public class ShutdownReceiver extends BroadcastReceiver {
    String TAG = "SHUTDOWN_TAG";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_SHUTDOWN)) {
            if(Common.isLoggingEnabled)
                Log.d(TAG,"ShutdownReceiver::onReceive()");
        }

    }

     /*@Override
    public void onReceive(final Context context, final Intent intent) {

       Intent startIntent = new Intent(context, StepCountingService.class);
        context.startService(startIntent);*/

    // if the user used a root script for shutdown, the DEVICE_SHUTDOWN
    // broadcast might not be send. Therefore, the app will check this
    // setting on the next boot and displays an error message if it's not
    // set to true
        /*context.getSharedPreferences("pedometer", Context.MODE_PRIVATE).edit()
                .putBoolean("correctShutdown", true).apply();*/

        /*Competition CompitionDate = Repository.getCompitionDate();
        activityAdapterListModel todayActivity=null;
        String DisCountDB=null;
        int DisValueDB=0;
        String CalCountDB=null;
        int CalValueDB=0;
        if (CompitionDate != null) {
            todayActivity  = Repository.GetTodayActivityV1(context, UtilityTz.convertUTCToLocalTime(CompitionDate.getStartDate()));
        }
        int steps = todayActivity.getmSteps();
        Realm_User userGlobal = provideUserLocal(context);
        if(userGlobal!=null) {
            DisCountDB = String.format("%.0f", Math.abs(UtilityTz.getDistanceNow(steps, userGlobal.getHeight_in_cm())));
            DisValueDB = Integer.parseInt(DisCountDB);
            CalCountDB = String.format("%.0f", Math.abs(UtilityTz.CaloriesCalulatorFromSteps(userGlobal.getHeight_in_cm(), userGlobal.getWeight(), steps)));
            CalValueDB = Integer.parseInt(CalCountDB);
            SaveSharedPreference.setDistance(context, DisCountDB + "");

            ActivityDaily activityDaily = new ActivityDaily();
            activityDaily.setmSteps(steps);
            *//* activityDaily.setmStars(Integer.parseInt(userGlobal.getStars_count()));*//*
            activityDaily.setmCalories(CalValueDB);
            activityDaily.setmDistance(DisValueDB);
            activityDaily.setmDate(UtilityTz.convertTimeToUTC());
            activityDaily.setSyncedWithServer(false);
            Repository.AddStepCounterLocalV3(activityDaily);
        }*/
}

    /*public Realm_User provideUserLocal(Context context)
    {

        try{
            String [] params= SaveSharedPreference.getLoggedParams(context);

            Realm_User user=Repository.provideUserLocal(params[0],params[1]);
            return user;
        }catch (Exception e){

        }


        return  null;
    }*/

