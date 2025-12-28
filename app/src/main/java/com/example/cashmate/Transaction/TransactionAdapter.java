package com.example.cashmate.Transaction;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmate.R;
import com.example.cashmate.database.transaction.TransactionHandle;
import com.example.cashmate.plus.PlusFragment;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    public interface OnDataChanged {
        void onDataChanged();
    }

    private Cursor cursor;
    private final OnDataChanged onDataChanged;

    public TransactionAdapter(Cursor cursor) {
        this(cursor, null);
    }

    public TransactionAdapter(Cursor cursor, OnDataChanged onDataChanged) {
        this.cursor = cursor;
        this.onDataChanged = onDataChanged;
    }

    // ================= CURSOR =================
    public void swapCursor(Cursor newCursor) {
        if (cursor != null) cursor.close();
        cursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        if (cursor == null || !cursor.moveToPosition(position)) return;

        long idTransaction = cursor.getLong(cursor.getColumnIndexOrThrow("idTransaction"));
        double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
        String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        String weekday = cursor.getString(cursor.getColumnIndexOrThrow("weekday"));
        String type = cursor.getString(cursor.getColumnIndexOrThrow("typeTransaction"));
        String nameCategory = cursor.getString(cursor.getColumnIndexOrThrow("nameCategory"));
        String icon = cursor.getString(cursor.getColumnIndexOrThrow("iconCategory"));

        String[] p = date.split("[-/]");
        h.tvDate.setText(p[0]);
        h.tvFullDate.setText("tháng " + p[1] + " " + p[2]);
        h.tvDateLabel.setText(weekday);

        h.tvCategoryName.setText(nameCategory != null ? nameCategory : "");

        if (note != null && !note.trim().isEmpty()) {
            h.tvNote.setText(note);
            h.tvNote.setVisibility(View.VISIBLE);
        } else {
            h.tvNote.setVisibility(View.GONE);
        }

        if (icon != null) {
            int res = h.itemView.getContext()
                    .getResources()
                    .getIdentifier(icon, "drawable", h.itemView.getContext().getPackageName());
            if (res != 0) h.ivIcon.setImageResource(res);
        }

        View root = h.itemView.findViewById(R.id.transactionRoot);

        if ("INCOME".equalsIgnoreCase(type)) {
            h.tvAmount.setText(String.format("%,.0f", amount));
            h.tvAmount.setTextColor(h.itemView.getContext().getResources().getColor(R.color.lightgreen));
            h.tvDateTotal.setText(String.format("+%,.0f", amount));
            root.setBackgroundResource(R.drawable.green_border);
        } else {
            h.tvAmount.setText(String.format("%,.0f", amount));
            h.tvAmount.setTextColor(Color.RED);
            h.tvDateTotal.setText(String.format("-%,.0f", amount));
            root.setBackgroundResource(R.drawable.red_border);
        }

        h.itemView.setOnLongClickListener(v -> {
            String[] options = {"Sửa", "Xóa"};

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Tùy chọn")
                    .setItems(options, (dialog, which) -> {
                        TransactionHandle handle = new TransactionHandle(v.getContext());

                        if (which == 0) {
                            Bundle b = new Bundle();
                            b.putLong("idTransaction", idTransaction);

                            PlusFragment f = new PlusFragment();
                            f.setArguments(b);

                            ((FragmentActivity) v.getContext())
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, f)
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            new AlertDialog.Builder(v.getContext())
                                    .setTitle("Xác nhận xóa")
                                    .setMessage("Bạn có chắc muốn xóa giao dịch này?")
                                    .setPositiveButton("Xóa", (d, w) -> {
                                        handle.delete(idTransaction);
                                        if (onDataChanged != null) {
                                            onDataChanged.onDataChanged();
                                        } else {
                                            swapCursor(handle.getAllCursor());
                                        }
                                    })
                                    .setNegativeButton("Hủy", null)
                                    .show();
                        }
                    })
                    .show();

            return true;
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDateLabel, tvFullDate, tvDateTotal;
        TextView tvCategoryName, tvAmount, tvNote;
        ImageView ivIcon;

        ViewHolder(@NonNull View v) {
            super(v);
            tvDate = v.findViewById(R.id.tvDate);
            tvDateLabel = v.findViewById(R.id.tvDateLabel);
            tvFullDate = v.findViewById(R.id.tvFullDate);
            tvDateTotal = v.findViewById(R.id.tvDateTotal);
            tvCategoryName = v.findViewById(R.id.tvCategoryName);
            tvAmount = v.findViewById(R.id.tvAmount);
            tvNote = v.findViewById(R.id.tvNote);
            ivIcon = v.findViewById(R.id.ivCategoryIcon);
        }
    }
}

