package com.enzo.contentproviderclient;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.enzo.contentproviderclient.DbContract.OrderEntry;

public class MainActivity extends Activity implements OnClickListener {
    private final Context mContext = MainActivity.this;

    private EditText edtxt_id, edtxt_name, edtxt_price, edtxt_country;
    private ListView  list_order;
    private QueryTask mQueryTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtxt_id = (EditText) findViewById(R.id.edtxt_id);
        edtxt_name = (EditText) findViewById(R.id.edtxt_name);
        edtxt_price = (EditText) findViewById(R.id.edtxt_price);
        edtxt_country = (EditText) findViewById(R.id.edtxt_country);

        Button btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        Button btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);
        Button btn_update = (Button) findViewById(R.id.btn_update);
        btn_update.setOnClickListener(this);

        list_order = (ListView) findViewById(R.id.list_order);

        // register observer
        getContentResolver().registerContentObserver(OrderEntry.CONTENT_URI, true, new MyContentObserver(null));
        // query data the first time when app is launched.
        queryAll();
    }

    private void queryAll() {
        if (mQueryTask != null && mQueryTask.getStatus() == Status.RUNNING)
            mQueryTask.cancel(true);

        mQueryTask = new QueryTask();
        mQueryTask.execute();
    }

    private int getInputId() {
        if (edtxt_id.getText() != null && edtxt_id.getText().length() > 0)
            return Integer.valueOf(edtxt_id.getText().toString());
        else
            return -1;
    }

    private String getInputProductName() {
        return edtxt_name.getText().toString();
    }

    private int getInputOrderPrice() {
        if (edtxt_price.getText() != null && edtxt_price.getText().length() > 0)
            return Integer.valueOf(edtxt_price.getText().toString());
        else
            return -1;
    }

    private String getInputCountry() {
        return edtxt_country.getText().toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                ContentValues insertVals = new ContentValues();
                insertVals.put(OrderEntry._ID, getInputId());
                insertVals.put(OrderEntry.PRODUCT_NAME, getInputProductName());
                insertVals.put(OrderEntry.ORDER_PRICE, getInputOrderPrice());
                insertVals.put(OrderEntry.COUNTRY, getInputCountry());
                getContentResolver().insert(OrderEntry.CONTENT_URI, insertVals);
                break;
            case R.id.btn_delete:
                Uri uri = ContentUris.withAppendedId(OrderEntry.CONTENT_URI, getInputId());
                getContentResolver().delete(uri, null, null);
                break;
            case R.id.btn_update:
                ContentValues updateVals = new ContentValues();
                updateVals.put(OrderEntry.PRODUCT_NAME, getInputProductName());
                updateVals.put(OrderEntry.ORDER_PRICE, getInputOrderPrice());
                updateVals.put(OrderEntry.COUNTRY, getInputCountry());
                // specify id in where clause rather than in uri.
                String selection = OrderEntry._ID + " = ?";
                String[] selectionArgs = {String.valueOf(getInputId())};
                getContentResolver().update(OrderEntry.CONTENT_URI, updateVals, selection, selectionArgs);
                break;
        }
    }


    /**
     * QueryTask
     */
    private class QueryTask extends AsyncTask<Void, Void, Cursor> {
        private final int      ITEM_LAYOUT = R.layout.activity_main_list_order_item;
        private final String[] COLUMNS     = new String[]{
                OrderEntry._ID,
                OrderEntry.PRODUCT_NAME,
                OrderEntry.ORDER_PRICE,
                OrderEntry.COUNTRY
        };
        private final int[]    VIEWS       = new int[]{
                R.id.txt_id,
                R.id.txt_name,
                R.id.txt_price,
                R.id.txt_country
        };

        @Override
        protected Cursor doInBackground(Void... voids) {
            String[] projection = new String[]{
                    OrderEntry._ID,
                    OrderEntry.PRODUCT_NAME,
                    OrderEntry.ORDER_PRICE,
                    OrderEntry.COUNTRY};
            String sortOrder = OrderEntry._ID + " ASC";
            Cursor cursor = getContentResolver().query(OrderEntry.CONTENT_URI, projection, null, null, sortOrder);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            CursorAdapter adapter = new SimpleCursorAdapter(mContext, ITEM_LAYOUT, cursor, COLUMNS, VIEWS);
            list_order.setAdapter(adapter);
        }
    }


    /**
     * MyContentObserver
     */
    private class MyContentObserver extends ContentObserver {
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d("URI", uri.toString());
            // data on provider has been changed, redo the query of all data.
            queryAll();
        }
    }
}
