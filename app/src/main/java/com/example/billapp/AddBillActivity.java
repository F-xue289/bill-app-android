package com.example.billapp;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class AddBillActivity extends AppCompatActivity {
    private EditText mAmountEditText;
    private RadioGroup mTypeRadioGroup;
    private EditText mCategoryEditText;
    private EditText mNoteEditText;
    private TextView mDateTextView;
    private Button mSaveButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);

        mAmountEditText = findViewById(R.id.amountEditText);
        mTypeRadioGroup = findViewById(R.id.typeRadioGroup);
        mCategoryEditText = findViewById(R.id.categoryEditText);
        mNoteEditText = findViewById(R.id.noteEditText);
        mDateTextView = findViewById(R.id.dateTextView);
        mSaveButton = findViewById(R.id.saveButton);
        mCancelButton = findViewById(R.id.cancelButton);

        // 设置当前日期
        mDateTextView.setText(new Date().toString());

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBill();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveBill() {
        double amount = Double.parseDouble(mAmountEditText.getText().toString());
        
        // 获取选中的类型
        int selectedId = mTypeRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        String type = selectedRadioButton.getText().toString();
        
        String category = mCategoryEditText.getText().toString();
        String note = mNoteEditText.getText().toString();
        long billDate = new Date().getTime();

        ContentValues values = new ContentValues();
        values.put(BillContract.BillEntry.COLUMN_AMOUNT, amount);
        values.put(BillContract.BillEntry.COLUMN_TYPE, type);
        values.put(BillContract.BillEntry.COLUMN_CATEGORY, category);
        values.put(BillContract.BillEntry.COLUMN_NOTE, note);
        values.put(BillContract.BillEntry.COLUMN_BILL_DATE, billDate);

        Uri uri = getContentResolver().insert(BillContract.BillEntry.CONTENT_URI, values);
        if (uri != null) {
            setResult(RESULT_OK);
            finish();
        }
    }
}