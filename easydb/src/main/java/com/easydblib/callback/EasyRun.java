package com.easydblib.callback;

/**
 * 异步任务回调接口
 * @author : zhousf
 */

public class EasyRun<T> extends EasyRunAbs<T>{


    /**
     * 异步线程执行
     */
    @Override
    public T run() throws Exception {
        return null;
    }

    /**
     * UI线程执行
     * @param data run方法返回参数
     */
    @Override
    public void onMainThread(T data) throws Exception {

    }
}
