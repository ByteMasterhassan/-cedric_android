package com.cedricapp.interfaces;

import com.cedricapp.model.LevelModel;

public interface LevelListener {
    void levelOnSuccess(LevelModel levelModel);
    void levelOnUnSuccess();
    void levelOnFailure(Throwable throwable);
}
