package com.easydblib.helper;

import android.content.Context;

import com.easydblib.callback.IUpgrade;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 数据库操作单例
 * @author : zhousf
 */
public class DBHelper extends BaseDBHelper{


    private static DBHelper singleton;

    private static DBBuilder dbBuilder;

    public static DBHelper init(Context context,DBBuilder dbBuilder) {
        if (null == singleton) {
            synchronized (DBHelper.class) {
                if (null == singleton) {
                    singleton = new DBHelper(context,dbBuilder);
                }
            }
        }
        return singleton;
    }

    public static DBHelper get(){
        return singleton;
    }

    private DBHelper(Context context,DBBuilder dbBuilder) {
        super(context, dbBuilder.getDbPath(),dbBuilder.getDbName(),dbBuilder.getDbVersion(),dbBuilder.getTables());
    }

    public static DBBuilder builder(){
        if(dbBuilder == null){
            dbBuilder = new DBBuilder();
        }
        return dbBuilder;
    }


    @Override
    protected boolean upgrade(int oldVersion, int newVersion) throws SQLException {
        if(dbBuilder != null){
            List<IUpgrade> upgrades = dbBuilder.getUpgrades();
            if(upgrades != null && !upgrades.isEmpty()){
                for(IUpgrade upgrade : upgrades){
                    upgrade.upgrade(this,oldVersion,newVersion);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 增加字段
     * @param table 表
     * @param columnName 增加列名
     * @param columnType 列类型
     * @param defaultValue 默认值
     */
    public void addColumn(Class table,String columnName,Object columnType,Object defaultValue){
        try {
            String column;
            String value = String.valueOf(defaultValue);;
            if(columnType == String.class || columnType == Date.class){
                column = "TEXT";
                value = "'"+String.valueOf(defaultValue)+"'";
            } else if(columnType == Long.class || columnType == Integer.class){
                column = "INTEGER";
            } else if(columnType == Boolean.class){
                column = "NUMERIC";
            } else if(columnType == Double.class || columnType == Float.class){
                column = "REAL";
            }else{
                column = "TEXT";
                value = "'"+String.valueOf(defaultValue)+"'";
            }
            final String sql = "ALTER TABLE '"+table.getSimpleName().toLowerCase()
                    +"' ADD COLUMN "+columnName+" "+column+" DEFAULT "+value+";";
            getDao(table).executeRaw(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
