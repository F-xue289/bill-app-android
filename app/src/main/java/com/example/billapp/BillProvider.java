package com.example.billapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class BillProvider extends ContentProvider {
    private static final int BILLS = 100;
    private static final int BILL_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BillContract.AUTHORITY, BillContract.PATH_BILLS, BILLS);
        sUriMatcher.addURI(BillContract.AUTHORITY, BillContract.PATH_BILLS + "/#", BILL_ID);
    }

    private AppDatabase mDatabase;

    @Override
    public boolean onCreate() {
        mDatabase = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = sUriMatcher.match(uri);
        Cursor cursor;

        switch (match) {
            case BILLS:
                cursor = getBillsCursor();
                break;
            case BILL_ID:
                int id = (int) ContentUris.parseId(uri);
                cursor = getBillByIdCursor(id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor getBillsCursor() {
        List<BillEntity> bills = mDatabase.billDao().getAllBills();
        MatrixCursor cursor = new MatrixCursor(new String[]{
                BillContract.BillEntry._ID,
                BillContract.BillEntry.COLUMN_AMOUNT,
                BillContract.BillEntry.COLUMN_TYPE,
                BillContract.BillEntry.COLUMN_CATEGORY,
                BillContract.BillEntry.COLUMN_NOTE,
                BillContract.BillEntry.COLUMN_BILL_DATE
        });

        for (BillEntity bill : bills) {
            cursor.addRow(new Object[]{
                    bill.getId(),
                    bill.getAmount(),
                    bill.getType(),
                    bill.getCategory(),
                    bill.getNote(),
                    bill.getBillDate().getTime()
            });
        }

        return cursor;
    }

    private Cursor getBillByIdCursor(int id) {
        BillEntity bill = mDatabase.billDao().getBillById(id);
        MatrixCursor cursor = new MatrixCursor(new String[]{
                BillContract.BillEntry._ID,
                BillContract.BillEntry.COLUMN_AMOUNT,
                BillContract.BillEntry.COLUMN_TYPE,
                BillContract.BillEntry.COLUMN_CATEGORY,
                BillContract.BillEntry.COLUMN_NOTE,
                BillContract.BillEntry.COLUMN_BILL_DATE
        });

        if (bill != null) {
            cursor.addRow(new Object[]{
                    bill.getId(),
                    bill.getAmount(),
                    bill.getType(),
                    bill.getCategory(),
                    bill.getNote(),
                    bill.getBillDate().getTime()
            });
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BILLS:
                return BillContract.BillEntry.CONTENT_LIST_TYPE;
            case BILL_ID:
                return BillContract.BillEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BILLS:
                return insertBill(uri, values);
            default:
                throw new IllegalArgumentException("Insert is not supported for " + uri);
        }
    }

    private Uri insertBill(Uri uri, ContentValues values) {
        double amount = values.getAsDouble(BillContract.BillEntry.COLUMN_AMOUNT);
        String type = values.getAsString(BillContract.BillEntry.COLUMN_TYPE);
        String category = values.getAsString(BillContract.BillEntry.COLUMN_CATEGORY);
        String note = values.getAsString(BillContract.BillEntry.COLUMN_NOTE);
        long billDateMillis = values.getAsLong(BillContract.BillEntry.COLUMN_BILL_DATE);

        BillEntity bill = new BillEntity(
                amount,
                type,
                category,
                note,
                new java.util.Date(billDateMillis)
        );

        long id = mDatabase.billDao().insertBill(bill);
        if (id == -1) {
            Log.e("BillProvider", "Failed to insert bill");
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int deletedRows;

        switch (match) {
            case BILL_ID:
                int id = (int) ContentUris.parseId(uri);
                deletedRows = mDatabase.billDao().deleteBillById(id);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int updatedRows;

        switch (match) {
            case BILL_ID:
                int id = (int) ContentUris.parseId(uri);
                updatedRows = updateBill(id, values);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

        if (updatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updatedRows;
    }

    private int updateBill(int id, ContentValues values) {
        BillEntity bill = mDatabase.billDao().getBillById(id);
        if (bill == null) {
            return 0;
        }

        if (values.containsKey(BillContract.BillEntry.COLUMN_AMOUNT)) {
            bill.setAmount(values.getAsDouble(BillContract.BillEntry.COLUMN_AMOUNT));
        }
        if (values.containsKey(BillContract.BillEntry.COLUMN_TYPE)) {
            bill.setType(values.getAsString(BillContract.BillEntry.COLUMN_TYPE));
        }
        if (values.containsKey(BillContract.BillEntry.COLUMN_CATEGORY)) {
            bill.setCategory(values.getAsString(BillContract.BillEntry.COLUMN_CATEGORY));
        }
        if (values.containsKey(BillContract.BillEntry.COLUMN_NOTE)) {
            bill.setNote(values.getAsString(BillContract.BillEntry.COLUMN_NOTE));
        }
        if (values.containsKey(BillContract.BillEntry.COLUMN_BILL_DATE)) {
            bill.setBillDate(new java.util.Date(values.getAsLong(BillContract.BillEntry.COLUMN_BILL_DATE)));
        }

        return mDatabase.billDao().updateBill(bill);
    }
}