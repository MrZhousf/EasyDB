package com.easydblib.handler;

import android.os.Message;

import com.easydblib.callback.EasyRun;

/**
 * Handler 信息体
 * @author : zhousf
 */

public class MessageInfo<T> {

    public int what;

    public EasyRun<T> easyRun;

    public T model;

    public MessageInfo() {
    }

    public Message build(){
        Message msg = new Message();
        msg.what = this.what;
        msg.obj = this;
        return msg;
    }
}
