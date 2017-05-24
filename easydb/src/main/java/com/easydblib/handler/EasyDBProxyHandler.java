package com.easydblib.handler;

import android.util.Log;

import com.easydblib.EasyDBConfig;
import com.easydblib.dao.RealBaseDao;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author : zhousf
 * @description : 数据库操作代理类：主要进行预处理、日志打印
 * @date : 2017/5/23.
 */

public class EasyDBProxyHandler<T> implements InvocationHandler {

    private Object obj;
    private Dao<T, Integer> dao;
    private Class<T> mClass;
    private String databaseName;

    public EasyDBProxyHandler(Dao<T, Integer> dao, Class<T> mClass, String databaseName) {
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
            String methodName = method.getName();
            if(result instanceof Integer || result instanceof Long){
                String line = String.valueOf(result);
                doLog(methodName+"["+(getTime()-startTime)+"ms] 影响行数："+line);
            }else if(result instanceof List){
                int line = ((List) result).size();
                doLog(methodName+"["+(getTime()-startTime)+"ms] 影响行数："+line);
            }else if(result instanceof Boolean){
                String res = String.valueOf(result);
                doLog(methodName+"["+(getTime()-startTime)+"ms] ："+res);
            }else {
                doLog(methodName+"["+(getTime()-startTime)+"ms] ");
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
            TableUtils.createTableIfNotExists(dao.getConnectionSource(),mClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getTime(){
        return System.currentTimeMillis();
    }

    /**
     * 打印日志
     */
    private void doLog(String msg){
        if(EasyDBConfig.showDBLog)
            Log.d(EasyDBConfig.logTAG,msg+" | "+mClass.getSimpleName()+" | "+databaseName);
    }





}



