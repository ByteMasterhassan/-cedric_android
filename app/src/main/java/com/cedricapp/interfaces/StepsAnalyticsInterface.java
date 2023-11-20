package com.cedricapp.interfaces;

import com.cedricapp.model.AnalyticsModel;

public interface StepsAnalyticsInterface {
    public void dailyAnalytics(AnalyticsModel activitiesModel);
    public void stepAnalyticsFailed(int responseCode, String errorMessage);
}
