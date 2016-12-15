package com.myeasydb.core;

import android.app.Application;

/**
 * @author : zhousf
 */

public class BaseApplication extends Application {

    static BaseApplication baseApplication;

    public static BaseApplication getApplication() {
        return baseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }



}
