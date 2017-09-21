package com.enzo.contentproviderclient;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Copied completely from Server App of provider for having coherent information to access the provider.
 */

public final class DbContract {
    private DbContract() {
    }

    public static final String AUTHORITY = "com.enzo.contentproviderserver.provider";

    public static class OrderEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OrderEntry.TABLE_NAME);

        public static final String TABLE_NAME   = "table_order";
        public static final String PRODUCT_NAME = "product_name";
        public static final String ORDER_PRICE  = "order_price";
        public static final String COUNTRY      = "country";
    }
}
