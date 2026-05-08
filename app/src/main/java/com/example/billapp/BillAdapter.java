package com.example.billapp;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private Context mContext;
    private List<BillEntity> mBills;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private OnBillDeleteListener mDeleteListener;

    public interface OnBillDeleteListener {
        void onBillDeleted();
    }

    public BillAdapter(Context context) {
        mContext = context;
    }

    public void setOnBillDeleteListener(OnBillDeleteListener listener) {
        mDeleteListener = listener;
    }

    @Override
    public BillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BillViewHolder holder, int position) {
        BillEntity bill = mBills.get(position);
        holder.amountTextView.setText(String.format("¥%.2f", bill.getAmount()));
        holder.typeTextView.setText(bill.getType());
        holder.categoryTextView.setText(bill.getCategory());
        holder.noteTextView.setText(bill.getNote());
        holder.dateTextView.setText(mDateFormat.format(bill.getBillDate()));
        
        // 根据类型设置金额颜色
        if ("收入".equals(bill.getType())) {
            holder.amountTextView.setTextColor(0xFF4CAF50);
        } else {
            holder.amountTextView.setTextColor(0xFFF44336);
        }
    }

    @Override
    public int getItemCount() {
        return mBills != null ? mBills.size() : 0;
    }

    public void setBills(List<BillEntity> bills) {
        mBills = bills;
        notifyDataSetChanged();
    }

    public void setupItemTouchHelper(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                BillEntity bill = mBills.get(position);
                Uri uri = Uri.withAppendedPath(BillContract.BillEntry.CONTENT_URI, String.valueOf(bill.getId()));
                mContext.getContentResolver().delete(uri, null, null);
                mBills.remove(position);
                notifyItemRemoved(position);
                
                // 通知监听器账单已删除
                if (mDeleteListener != null) {
                    mDeleteListener.onBillDeleted();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    class BillViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView;
        TextView typeTextView;
        TextView categoryTextView;
        TextView noteTextView;
        TextView dateTextView;

        public BillViewHolder(View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            noteTextView = itemView.findViewById(R.id.noteTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}