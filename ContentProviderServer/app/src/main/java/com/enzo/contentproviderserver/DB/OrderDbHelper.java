package com.enzo.contentproviderserver.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.enzo.contentproviderserver.DAO.Order;
import com.enzo.contentproviderserver.DB.DbContract.OrderEntry;

import java.util.ArrayList;

/**
 * <p>
 * Created by ZENG Yuhao. <br>
 * Contact: enzo.zyh@gmail.com
 * </p>
 */

public class OrderDbHelper {
    private DbHelper mDbHelper;

    public OrderDbHelper(DbHelper dbHelper) {
        this.mDbHelper = dbHelper;
    }

    public boolean save(Order order) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // since we have added "ON CONFLICT REPLACE" for primary key (id), row will be automatically replaced if
        // specified id exists already when inserting a row.
        ContentValues values = new ContentValues();
        values.put(OrderEntry._ID, order.getId());
        values.put(OrderEntry.PRODUCT_NAME, order.getProductName());
        values.put(OrderEntry.ORDER_PRICE, order.getOrderPrice());
        values.put(OrderEntry.COUNTRY, order.getCountry());

        long newRowId = db.insert(OrderEntry.TABLE_NAME, null, values);
        return newRowId != -1;
    }

    public Order get(int id) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] cols = {
                OrderEntry._ID,
                OrderEntry.PRODUCT_NAME,
                OrderEntry.ORDER_PRICE,
                OrderEntry.COUNTRY
        };
        String where = OrderEntry._ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        String sortOrder = OrderEntry._ID + " ASC";
        Cursor cursor = db.query(OrderEntry.TABLE_NAME, cols, where, whereArgs, null, null, sortOrder);

        Order orderFound = null;
        if (cursor != null && cursor.getCount() > 0) {
            // we're querying data by primary key, only one single value or null will be got.
            cursor.moveToFirst();
            int orderId = cursor.getInt(cursor.getColumnIndex(OrderEntry._ID));
            String productName = cursor.getString(cursor.getColumnIndex(OrderEntry.PRODUCT_NAME));
            int price = cursor.getInt(cursor.getColumnIndex(OrderEntry.ORDER_PRICE));
            String country = cursor.getString(cursor.getColumnIndex(OrderEntry.COUNTRY));
            orderFound = new Order(orderId, productName, price, country);
        }
        cursor.close();
        return orderFound;
    }

    public ArrayList<Order> getAll() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] cols = {
                OrderEntry._ID,
                OrderEntry.PRODUCT_NAME,
                OrderEntry.ORDER_PRICE,
                OrderEntry.COUNTRY
        };
        String sortOrder = OrderEntry._ID + " ASC";
        Cursor cursor = db.query(OrderEntry.TABLE_NAME, cols, null, null, null, null, sortOrder);
        ArrayList<Order> list = null;
        if (cursor != null && cursor.getCount() > 0) {
            list = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                int orderId = cursor.getInt(cursor.getColumnIndex(OrderEntry._ID));
                String productName = cursor.getString(cursor.getColumnIndex(OrderEntry.PRODUCT_NAME));
                int price = cursor.getInt(cursor.getColumnIndex(OrderEntry.ORDER_PRICE));
                String country = cursor.getString(cursor.getColumnIndex(OrderEntry.COUNTRY));
                list.add(new Order(orderId, productName, price, country));
            }
        }
        cursor.close();
        return list;
    }

    public boolean update(Order order) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(OrderEntry.PRODUCT_NAME, order.getProductName());
        values.put(OrderEntry.ORDER_PRICE, order.getOrderPrice());
        values.put(OrderEntry.COUNTRY, order.getCountry());

        String where = OrderEntry._ID + " = ?";
        String[] whereArgs = {String.valueOf(order.getId())};

        int count = db.update(OrderEntry.TABLE_NAME, values, where, whereArgs);
        return count > 0;
    }

    public boolean delete(int id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String where = OrderEntry._ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        int count = db.delete(OrderEntry.TABLE_NAME, where, whereArgs);
        return count > 0;
    }
}
