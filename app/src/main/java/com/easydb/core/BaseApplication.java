package com.easydb.core;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.easydb.demo.model.SimpleData;
import com.easydblib.callback.IUpgrade;
import com.easydblib.helper.DBHelper;

import java.sql.SQLException;

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
        DBHelper.builder()
                .setDbPath(Environment.getExternalStorageDirectory() + "/easy_db")//数据库保存路径
                .setDbName("easy")//数据库名称
                .setDbVersion(2)//数据库版本号
                .showDBLog(true)//显示数据库操作日志
                .setLogTAG("EASY_DB")//日志显示标识
                .build(this);
        DBHelper.builder().onUpgrade(new IUpgrade() {
            @Override
            public void upgrade(DBHelper dbHelper, int oldVersion, int newVersion) throws SQLException {
                Log.d("upgrade","oldVersion="+oldVersion+",newVersion="+newVersion);
                if(oldVersion < 2){
                    //增加字段ext
                    dbHelper.addColumn(SimpleData.class,"ext",String.class,"100");
                }
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }



}
