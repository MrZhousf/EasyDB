package com.easydblib.helper;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.easydblib.EasyDBConfig;
import com.easydblib.annotation.TableModel;
import com.easydblib.callback.IUpgrade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

/**
 * 数据库构建器
 * @author : zhousf
 */
public class DBBuilder {

    //版本号
    private int dbVersion;

    //数据库存放路径
    private String dbPath;

    //数据库名称
    private String dbName;

    //表
    private List<Class<?>> tables;

    private List<IUpgrade> upgrades;

    //是否显示数据库操作日志
    private boolean showDBLog;

    //日志显示标识
    private String logTAG;

    public DBBuilder() {
        //默认为系统数据库
        dbPath = null;
        dbName = "easy_database.db";
        tables = new ArrayList<>();
    }

    /**
     * 数据库升级回调
     * @param upgrade 升级接口回调
     */
    public DBBuilder onUpgrade(IUpgrade upgrade){
        if(upgrades == null){
            upgrades = new ArrayList<>();
        }
        upgrades.add(upgrade);
        return this;
    }

    /**
     * 设置数据库版本号
     * @param dbVersion 版本号
     */
    public DBBuilder setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
        return this;
    }

    /**
     * 设置数据库SD卡路径（不设置则为系统数据库）
     * @param dbPath 不包含文件名
     */
    public DBBuilder setDbPath(String dbPath) {
        this.dbPath = dbPath;
        return this;
    }

    /**
     * 设置数据库名称
     * @param dbName 数据库名称
     */
    public DBBuilder setDbName(String dbName) {
        if(!TextUtils.isEmpty(dbName)){
            if(!dbName.endsWith(".db")){
                dbName += ".db";
            }
            this.dbName = dbName;
        }
        return this;
    }

    /**
     * 是否显示数据库操作日志
     * @param showDBLog true显示 false不显示
     */
    public DBBuilder showDBLog(boolean showDBLog){
        this.showDBLog = showDBLog;
        return this;
    }

    /**
     * 日志显示标识
     * @param logTAG 日志标识
     */
    public DBBuilder setLogTAG(String logTAG){
        this.logTAG = logTAG;
        return this;
    }

    public DBHelper build(Context context){
        EasyDBConfig.init()
                .showDBLog(showDBLog)
                .setLogTAG(logTAG)
                .build();
        initTable(context,tables);
        return DBHelper.init(context,this);
    }

    private void initTable(Context context,List<Class<?>> tables) {
        try {
            String packageCodePath = context.getPackageCodePath();
            DexFile df = new DexFile(packageCodePath);
            Log.w("BaseApplication","-------------------------");
            for (Enumeration enumeration = df.entries(); enumeration.hasMoreElements(); ) {
                String className = String.valueOf(enumeration.nextElement());
                Class clazz;
                try {
                    clazz = Class.forName(className);
                    if(clazz != null){
                        if(clazz.isAnnotationPresent(TableModel.class)){
                            Log.w("BaseApplication---",clazz.getName());
                            if(!tables.contains(clazz)){
                                tables.add(clazz);
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {

                }
            }
            Log.w("BaseApplication","-------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDbVersion() {
        return dbVersion;
    }

    public String getDbPath() {
        return dbPath;
    }

    public String getDbName() {
        return dbName;
    }

    public List<Class<?>> getTables() {
        return tables;
    }

    public List<IUpgrade> getUpgrades() {
        return upgrades;
    }
}
