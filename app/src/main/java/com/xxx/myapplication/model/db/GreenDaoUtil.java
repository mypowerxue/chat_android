package com.xxx.myapplication.model.db;

import android.content.Context;

import com.xxx.myapplication.model.db.greendao.DaoMaster;
import com.xxx.myapplication.model.db.greendao.DaoSession;

import org.greenrobot.greendao.database.Database;

public class GreenDaoUtil {

    private static final String DB_NAME = "xxx";

    private static DaoSession session;

    private GreenDaoUtil(Context context) {
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context, DB_NAME, null);
        Database database = helper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(database);
        session = daoMaster.newSession();
    }

    public static DaoSession getInstance(Context context) {
        if (session == null) {
            synchronized (GreenDaoUtil.class) {
                if (session == null) {
                    new GreenDaoUtil(context);
                }
            }
        }
        return session;
    }

}