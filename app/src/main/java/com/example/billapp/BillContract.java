package com.example.billapp;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BillContract {
    private BillContract() {}

    public static final String AUTHORITY = "com.example.billapp.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_BILLS = "bills";

    public static final class BillEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BILLS);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_BILLS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_BILLS;

        public static final String TABLE_NAME = "bills";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_BILL_DATE = "billDate";
    }
}