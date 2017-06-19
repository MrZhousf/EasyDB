package com.easydblib.info;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作结果
 * @author : zhousf
 */
public class Result<T> {

    public static final int LIST = 1;
    public static final int MODEL = 2;
    public static final int LINE = 3;
    public static final int COUNT = 4;
    public static final int IS_EXIST = 5;

    private int type;
    private Exception exception;
    private List<T> list = new ArrayList<>();
    private T model;
    private int line;
    private long count;
    private boolean isExist;

    public Result(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

}
