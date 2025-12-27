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

    private Cursor cursor;

    public TransactionAdapter(Cursor cursor) {
        this.cursor = cursor;
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

        // ================= DATA =================
        long idTransaction =
                cursor.getLong(cursor.getColumnIndexOrThrow("idTransaction"));

        double amount =
                cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));

        String note =
                cursor.getString(cursor.getColumnIndexOrThrow("note"));

        String date =
                cursor.getString(cursor.getColumnIndexOrThrow("date"));

        String weekday =
                cursor.getString(cursor.getColumnIndexOrThrow("weekday"));

        String type =
                cursor.getString(cursor.getColumnIndexOrThrow("typeTransaction"));

        String nameCategory =
                cursor.getString(cursor.getColumnIndexOrThrow("nameCategory"));

        String icon =
                cursor.getString(cursor.getColumnIndexOrThrow("iconCategory"));

        // ================= DATE =================
        String[] p = date.split("[-/]");
        h.tvDate.setText(p[0]);
        h.tvFullDate.setText("tháng " + p[1] + " " + p[2]);
        h.tvDateLabel.setText(weekday);

        // ================= CATEGORY =================
        h.tvCategoryName.setText(nameCategory != null ? nameCategory : "");

        // ================= NOTE =================
        if (note != null && !note.trim().isEmpty()) {
            h.tvNote.setText(note);
            h.tvNote.setVisibility(View.VISIBLE);
        } else {
            h.tvNote.setVisibility(View.GONE);
        }

        // ================= ICON =================
        if (icon != null) {
            int res = h.itemView.getContext()
                    .getResources()
                    .getIdentifier(
                            icon,
                            "drawable",
                            h.itemView.getContext().getPackageName()
                    );
            if (res != 0) h.ivIcon.setImageResource(res);
        }

        // ================= MONEY =================
        if ("INCOME".equalsIgnoreCase(type)) {

            // 🔵 TRONG Ô – ĐỔI MÀU
            h.tvAmount.setText(String.format("%,.0f", amount));
            h.tvAmount.setTextColor(Color.parseColor("#2196F3"));

            // ⚪ NGOÀI Ô – GIỮ MÀU XML
            h.tvDateTotal.setText(String.format("+%,.0f", amount));

        } else { // EXPENSE

            // 🔴 TRONG Ô – ĐỔI MÀU
            h.tvAmount.setText(String.format("%,.0f", amount));
            h.tvAmount.setTextColor(Color.RED);

            // ⚪ NGOÀI Ô – GIỮ MÀU XML
            h.tvDateTotal.setText(String.format("-%,.0f", amount));
        }

        // ================= LONG PRESS =================
        h.itemView.setOnLongClickListener(v -> {

            String[] options = {"Sửa", "Xoá"};

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Tuỳ chọn")
                    .setItems(options, (dialog, which) -> {

                        TransactionHandle handle =
                                new TransactionHandle(v.getContext());

                        if (which == 0) {
                            // ===== SỬA =====
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
                            // ===== XOÁ =====
                            new AlertDialog.Builder(v.getContext())
                                    .setTitle("Xác nhận xoá")
                                    .setMessage("Bạn có chắc muốn xoá giao dịch này?")
                                    .setPositiveButton("Xoá", (d, w) -> {
                                        handle.delete(idTransaction);
                                        swapCursor(handle.getAllCursor());
                                    })
                                    .setNegativeButton("Huỷ", null)
                                    .show();
                        }
                    })
                    .show();

            return true;
        });
    }

    // ================= VIEW HOLDER =================
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
