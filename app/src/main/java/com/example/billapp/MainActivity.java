package com.example.billapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BillAdapter.OnBillDeleteListener {
    private RecyclerView mRecyclerView;
    private BillAdapter mAdapter;
    private Button mAddBillButton;
    private TextView mTotalAmountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_expense_list);
        mAddBillButton = findViewById(R.id.btn_add_expense);
        mTotalAmountTextView = findViewById(R.id.tv_total_amount);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BillAdapter(this);
        mAdapter.setOnBillDeleteListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setupItemTouchHelper(mRecyclerView);

        mAddBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddBillActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBills();
        calculateTotalAmount();
    }

    private void loadBills() {
        Uri uri = BillContract.BillEntry.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        List<BillEntity> bills = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(BillContract.BillEntry._ID));
                double amount = cursor.getDouble(cursor.getColumnIndex(BillContract.BillEntry.COLUMN_AMOUNT));
                String type = cursor.getString(cursor.getColumnIndex(BillContract.BillEntry.COLUMN_TYPE));
                String category = cursor.getString(cursor.getColumnIndex(BillContract.BillEntry.COLUMN_CATEGORY));
                String note = cursor.getString(cursor.getColumnIndex(BillContract.BillEntry.COLUMN_NOTE));
                long billDateMillis = cursor.getLong(cursor.getColumnIndex(BillContract.BillEntry.COLUMN_BILL_DATE));

                BillEntity bill = new BillEntity();
                bill.setId(id);
                bill.setAmount(amount);
                bill.setType(type);
                bill.setCategory(category);
                bill.setNote(note);
                bill.setBillDate(new Date(billDateMillis));

                bills.add(bill);
            }
            cursor.close();
        }

        mAdapter.setBills(bills);
    }

    private void calculateTotalAmount() {
        Uri uri = BillContract.BillEntry.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        double totalIncome = 0;
        double totalExpense = 0;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                double amount = cursor.getDouble(cursor.getColumnIndex(BillContract.BillEntry.COLUMN_AMOUNT));
                String type = cursor.getString(cursor.getColumnIndex(BillContract.BillEntry.COLUMN_TYPE));

                if ("收入".equals(type)) {
                    totalIncome += amount;
                } else if ("支出".equals(type)) {
                    totalExpense += amount;
                }
            }
            cursor.close();
        }

        double netAmount = totalIncome - totalExpense;
        
        // 根据净金额设置颜色和符号
        if (netAmount >= 0) {
            // 收入大于等于支出，显示绿色，使用+号
            mTotalAmountTextView.setTextColor(0xFF4CAF50);
            mTotalAmountTextView.setText(String.format("¥ +%.2f", netAmount));
        } else {
            // 支出大于收入，显示红色，使用-号
            mTotalAmountTextView.setTextColor(0xFFF44336);
            mTotalAmountTextView.setText(String.format("¥ %.2f", netAmount));
        }
    }

    @Override
    public void onBillDeleted() {
        // 账单删除后重新计算总金额
        calculateTotalAmount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadBills();
            calculateTotalAmount();
        }
    }
}