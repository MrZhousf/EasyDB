package com.easydb.core;

import android.app.Application;

import com.easydblib.EasyDBConfig;

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
        EasyDBConfig.init()
                .showDBLog(true)//显示数据库操作日志
                .registerHelper(EasyDBHelper.get())//注册数据库Helper-预实例化
                .build();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }



}
