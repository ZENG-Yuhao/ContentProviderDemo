package com.enzo.contentproviderserver;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.enzo.contentproviderserver.DB.DbContract.OrderEntry;

public class MainActivity extends AppCompatActivity {
    private ListView list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_view = (ListView) findViewById(R.id.list_view);
        final int layoutId = R.layout.activity_main_list_order_item;
        final String[] cols = new String[]{
                OrderEntry._ID,
                OrderEntry.PRODUCT_NAME,
                OrderEntry.ORDER_PRICE,
                OrderEntry.COUNTRY
        };
        final int[] views = new int[]{
                R.id.txt_id,
                R.id.txt_name,
                R.id.txt_price,
                R.id.txt_country
        };

        Button btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // for the sake of clarity, queryAll() does not run in a off-ui-thread.
                CursorAdapter adapter = new SimpleCursorAdapter(MainActivity.this, layoutId, queryAll(), cols, views);
                list_view.setAdapter(adapter);
            }
        });

        btn_refresh.performClick();
    }

    private Cursor queryAll() {
        String[] projection = new String[]{
                OrderEntry._ID,
                OrderEntry.PRODUCT_NAME,
                OrderEntry.ORDER_PRICE,
                OrderEntry.COUNTRY};
        String sortOrder = OrderEntry._ID + " ASC";
        Cursor cursor = getContentResolver().query(OrderEntry.CONTENT_URI, projection, null, null, sortOrder);
        return cursor;
    }
}
