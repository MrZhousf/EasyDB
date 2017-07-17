package com.easydblib.handler;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.util.Log;

import com.easydblib.EasyDBConfig;
import com.easydblib.dao.RealBaseDao;
import com.easydblib.helper.BaseDBHelper;
import com.easydblib.info.Result;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;

/**
 * @author : zhousf
 * @description : 数据库操作代理类：主要进行预处理、日志打印
 * @date : 2017/5/23.
 */
@SuppressWarnings(value = "all")
public class EasyDBProxyHandler<T> implements InvocationHandler {

    private Object obj;
    private BaseDBHelper helper;
    private Dao<T, Long> dao;
    private Class<T> mClass;
    private String databaseName;

    public EasyDBProxyHandler(BaseDBHelper helper,Dao<T, Long> dao, Class<T> mClass, String databaseName) {
        this.helper = helper;
        this.dao = dao;
        this.mClass = mClass;
        this.databaseName = databaseName;
    }

    public RealBaseDao<T> getProxy(Object targetObject) {
        this.obj = targetObject;
        Object proxy = Proxy.newProxyInstance(targetObject.getClass().getClassLoader(),
                targetObject.getClass().getInterfaces(), this);
        return (RealBaseDao<T>)proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long startTime = getTime();
        doBefore();
        Object result = method.invoke(obj, args);
        doAfter(method,result,startTime);
        return result;
    }

    /**
     * 执行前操作
     */
    private void doBefore(){
        prepareDeal();
    }

    /**
     * 执行后操作
     */
    private void doAfter(Method method, Object result, long startTime){
        if(result != null){
            if(result instanceof Result){
                Result<T> real = (Result<T>) result;
                String methodName = method.getName();
                if(Result.LINE == real.getType()){
                    showLog(methodName+"["+(getTime()-startTime)+"ms] 影响行数："+real.getLine());
                }else if(Result.COUNT == real.getType()){
                    showLog(methodName+"["+(getTime()-startTime)+"ms] 影响行数："+real.getCount());
                }else if(Result.LIST == real.getType()){
                    showLog(methodName+"["+(getTime()-startTime)+"ms] 影响行数："+real.getList().size());
                }else if(Result.IS_EXIST == real.getType()){
                    showLog(methodName+"["+(getTime()-startTime)+"ms] ："+real.isExist());
                }else{
                    showLog(methodName+"["+(getTime()-startTime)+"ms] ");
                }
                //异常处理
                Exception exception = real.getException();
                if(exception != null){
                    if(exception instanceof ClassCastException){
                        showErr("列值类型不正确："+ exception.getMessage());
                    }
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     * 预处理
     */
    private void prepareDeal(){
        checkTable();
    }

    /**
     * 检查数据表
     */
    private void checkTable(){
        try {
            if(!dao.isTableExists()){
                TableUtils.createTableIfNotExists(dao.getConnectionSource(),mClass);
            }
        } catch (Exception e) {
            if(e instanceof SQLiteDiskIOException){
                //当用户误删除.db数据库文件时进行数据库恢复(若.db-journal日志文件删除则无法恢复)
//                helper.onOpen(helper.getWritableDatabase());
                SQLiteDatabase db = helper.getWritableDatabase();
                helper.getWritableDatabase().openOrCreateDatabase(db.getPath(),null);
                try {
                    dao = helper.getDao(mClass);
                    if(!dao.isTableExists()){
                        TableUtils.createTableIfNotExists(dao.getConnectionSource(),mClass);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                showErr("恢复数据库："+databaseName);
            }else{
                e.printStackTrace();
            }
        }
    }

    private long getTime(){
        return System.currentTimeMillis();
    }

    /**
     * 打印日志
     */
    private void showLog(String msg){
        if(EasyDBConfig.showDBLog)
            Log.d(EasyDBConfig.logTAG,msg+" | "+mClass.getSimpleName()+" | "+databaseName);
    }

    private void showErr(String msg){
        if(EasyDBConfig.showDBLog)
            Log.e(EasyDBConfig.logTAG,msg+" | "+mClass.getSimpleName()+" | "+databaseName);
    }





}



