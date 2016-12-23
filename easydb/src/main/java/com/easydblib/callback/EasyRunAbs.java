package com.easydblib.callback;

/**
 * 任务回调接口基类
 * @author : zhousf
 */

public abstract class EasyRunAbs<T> {

    public abstract T run() throws Exception;

    public abstract void onMainThread(T data) throws Exception;

}
