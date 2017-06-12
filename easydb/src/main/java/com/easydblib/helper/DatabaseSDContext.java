package com.easydblib.helper;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

/**
 * SD卡数据库上下文
 * @author : zhousf
 */
public class DatabaseSDContext extends ContextWrapper {

    private String dbDir;

    public DatabaseSDContext(Context base, String dbDir) {
        super(base);
        this.dbDir = dbDir;
    }

    @Override
    public File getDatabasePath(String name) {
        File dirFile = new File(dbDir);
        if (!dirFile.exists())
            dirFile.mkdirs();
        // 标记数据库文件是否创建成功
        boolean isFileCreateSuccess = false;
        // 数据库路径
        String dbPath = dbDir +"/" + name;
        File dbFile = new File(dbPath);
        // 如果数据库文件不存在则创建该文件
         if (!dbFile.exists()) {
             try {
                 // 创建文件
                isFileCreateSuccess = dbFile.createNewFile();
             } catch (IOException e) {
                e.printStackTrace();
             }
         } else
            isFileCreateSuccess = true;
         //返回数据库文件对象
         if (isFileCreateSuccess)
            return dbFile;
         else
            return null;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }


    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);


    }


}
