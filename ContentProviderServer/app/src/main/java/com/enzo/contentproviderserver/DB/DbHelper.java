package com.enzo.contentproviderserver.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.enzo.contentproviderserver.DB.DbContract.OrderEntry;


/**
 * <p>
 * Created by ZENG Yuhao. <br>
 * Contact: enzo.zyh@gmail.com
 * </p>
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final int    DATABASE_VERSION = 1;
    public static final String DATABASE_NAME    = "MySQLiteDatabase.db";

    public static final String SQL_CREATE_ORDER_TABLE = "CREATE TABLE " + OrderEntry.TABLE_NAME + " (" +
            OrderEntry._ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE," +
            OrderEntry.PRODUCT_NAME + " TEXT," +
            OrderEntry.ORDER_PRICE + " INTEGER," +
            OrderEntry.COUNTRY + " TEXT);";
    public static final String SQL_DELETE_ORDER_TABLE = "DROP TABLE IF EXISTS " + OrderEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ORDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ORDER_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
