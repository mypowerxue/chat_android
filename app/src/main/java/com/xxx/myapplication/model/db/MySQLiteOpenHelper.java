package com.xxx.myapplication.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xxx.myapplication.model.db.greendao.DaoMaster;

import org.greenrobot.greendao.database.Database;

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {

    /**
     * @param context 上下文
     * @param name    原来定义的数据库的名字   新旧数据库一致
     * @param factory 可以null
     */
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion 更新数据库的时候自己调用
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Log.e("MySQLiteOpenHelper", "更新了数据库");
        /**
         *  将所有的数据dao全部传入
         */
    }
}