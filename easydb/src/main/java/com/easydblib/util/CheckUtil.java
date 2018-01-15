package com.easydblib.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.easydblib.EasyDBConfig;

/**
 * 检测工具类
 * @author : zhousf
 */
public class CheckUtil {

    /**
     * 判断SD卡是否存在
     */
    public static boolean checkSD(String databasePath, String databaseName){
        boolean sdExist = Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
        doLog("SD卡"+(sdExist?"存在":"不存在"));
        boolean path = TextUtils.isEmpty(databasePath);
        if(path){
            doLog("系统数据库");
        }else{
            doLog("SD卡数据库："+databasePath+"/"+databaseName);
        }
        return sdExist && !path;
    }


    /**
     * 打印日志
     */
    private static void doLog(String msg){
        if(EasyDBConfig.showDBLog)
            Log.d(EasyDBConfig.logTAG,msg);
    }



}
