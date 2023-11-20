package com.cedricapp.interfaces;

import com.cedricapp.model.GoalModel;

public interface PlanListener {
    void planOnSuccess(GoalModel planModel);

    void planOnUnSuccess();
    void planOnFailure(Throwable t);
}
