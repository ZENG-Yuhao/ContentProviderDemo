package com.enzo.contentproviderserver.Provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.enzo.contentproviderserver.DB.DbContract.OrderEntry;
import com.enzo.contentproviderserver.DB.DbHelper;

import static com.enzo.contentproviderserver.DB.DbContract.AUTHORITY;

/**
 * <p>
 * Created by ZENG Yuhao. <br>
 * Contact: enzo.zyh@gmail.com
 * </p>
 */

public class DataProvider extends ContentProvider {
    private static final int MATCH_ORDER_TABLE = 1;
    private static final int MATCH_ORDER_ROW   = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private DbHelper       mDbHelper;
    private SQLiteDatabase db;

    static {
        // since there is only one table in SQLite data base, all Uris defined bellow target the only table : Order.
        sUriMatcher.addURI(AUTHORITY, OrderEntry.TABLE_NAME, MATCH_ORDER_TABLE);
        sUriMatcher.addURI(AUTHORITY, OrderEntry.TABLE_NAME + "/#", MATCH_ORDER_ROW);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    /**
     * Deciding selection and selection-arguments according to requested uri.
     *
     * @param uri           requested uri.
     * @param selection     original selection.
     * @param selectionArgs original selection-arguments.
     * @param caller        the method that called this method.
     * @return array of 2 elements, array[0] = new selection, array[1] = new selection arguments.
     */
    private Object[] matchUri(Uri uri, String selection, String[] selectionArgs, String caller) {
        Object[] results = new Object[2];
        switch (sUriMatcher.match(uri)) {
            case MATCH_ORDER_TABLE:
                results[0] = selection;
                results[1] = selectionArgs;
                break;
            case MATCH_ORDER_ROW:
                // A row (id) is specified by uri, original selection and selectionArgs will be ignored.
                Long id = ContentUris.parseId(uri);
                results[0] = OrderEntry._ID + " = ?";
                results[1] = new String[]{id.toString()};
                break;
            default:
                throw new IllegalArgumentException("Unknown uri for " + caller + " : " + uri.toString());
        }
        return results;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable
            String[] selectionArgs, @Nullable String sortOrder) {
        db = mDbHelper.getReadableDatabase();
        Object[] results = matchUri(uri, selection, selectionArgs, "query()");
        selection = (String) results[0];
        selectionArgs = (String[]) results[1];
        return db.query(OrderEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MATCH_ORDER_TABLE:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + OrderEntry.TABLE_NAME;
            case MATCH_ORDER_ROW:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + "." + OrderEntry.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown uri for getType(): " + uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        db = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case MATCH_ORDER_TABLE:
                long newId = db.insert(OrderEntry.TABLE_NAME, null, values);
                if (newId != -1) {
                    notifyDataChanged();
                    return ContentUris.withAppendedId(uri, newId);
                } else
                    return null;
            default:
                throw new IllegalArgumentException("Unknown uri for insert(): " + uri.toString());
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        db = mDbHelper.getWritableDatabase();
        Object[] results = matchUri(uri, selection, selectionArgs, "delete()");
        selection = (String) results[0];
        selectionArgs = (String[]) results[1];
        int numRowsAffected = db.delete(OrderEntry.TABLE_NAME, selection, selectionArgs);
        if (numRowsAffected > 0)
            notifyDataChanged();
        return numRowsAffected;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable
            String[] selectionArgs) {
        db = mDbHelper.getWritableDatabase();
        Object[] results = matchUri(uri, selection, selectionArgs, "update()");
        selection = (String) results[0];
        selectionArgs = (String[]) results[1];
        int numRowsAffected = db.update(OrderEntry.TABLE_NAME, values, selection, selectionArgs);
        if (numRowsAffected > 0)
            notifyDataChanged();
        return numRowsAffected;
    }

    private void notifyDataChanged() {
        getContext().getContentResolver().notifyChange(OrderEntry.CONTENT_URI, null);
    }
}
