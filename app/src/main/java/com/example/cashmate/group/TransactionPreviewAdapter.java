package com.example.cashmate.group;

import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmate.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransactionPreviewAdapter
        extends RecyclerView.Adapter<TransactionPreviewAdapter.ViewHolder> {

    private final Cursor cursor;
    private final DecimalFormat moneyFormat = new DecimalFormat("#,###");

    public TransactionPreviewAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        if (!cursor.moveToPosition(position)) return;

        // DATA
        double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("date")); // dd/MM/yyyy
        String name = cursor.getString(cursor.getColumnIndexOrThrow("nameCategory"));
        String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
        String type = cursor.getString(cursor.getColumnIndexOrThrow("typeTransaction"));
        String icon = cursor.getString(cursor.getColumnIndexOrThrow("iconCategory"));

        //CATEGORY + NOTE
        h.tvCategoryName.setText(name);

        if (note != null && !note.isEmpty()) {
            h.tvNote.setText(note);
            h.tvNote.setVisibility(View.VISIBLE);
        } else {
            h.tvNote.setVisibility(View.GONE);
        }

        // DATE HEADER
        // dd/MM/yyyy
        String[] parts = date.split("/");
        h.tvDate.setText(parts[0]); // ngày
        h.tvFullDate.setText("tháng " + parts[1] + " " + parts[2]);
        h.tvDateLabel.setText(getWeekdayLabel(date));

        // ICON
        int iconRes = h.itemView.getContext().getResources()
                .getIdentifier(icon, "drawable", h.itemView.getContext().getPackageName());

        h.ivCategoryIcon.setImageResource(
                iconRes != 0 ? iconRes : R.drawable.ic_food
        );

        //AMOUNT + KHUNG THEO LOẠI
        String money = moneyFormat.format(amount);

        if ("INCOME".equalsIgnoreCase(type)) {
            // THU THÌ XANH DƯƠNG
            h.tvAmount.setText("+" + money);
            h.tvAmount.setTextColor(Color.parseColor("#1E88E5"));
            h.tvDateTotal.setText("+" + money);
            h.tvDateTotal.setTextColor(Color.parseColor("#1E88E5"));
            h.transactionRoot.setBackgroundResource(
                    R.drawable.border_income_blue
            );
        } else {
            // CHI THÌ ĐỎ
            h.tvAmount.setText("-" + money);
            h.tvAmount.setTextColor(Color.parseColor("#E53935"));
            h.tvDateTotal.setText("-" + money);
            h.tvDateTotal.setTextColor(Color.parseColor("#E53935"));
            h.transactionRoot.setBackgroundResource(
                    R.drawable.border_expense_red
            );
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    // TÍNH THỨ TỪ NGÀY
    private String getWeekdayLabel(String dateStr) {
        try {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(dateStr);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY: return "Thứ Hai";
                case Calendar.TUESDAY: return "Thứ Ba";
                case Calendar.WEDNESDAY: return "Thứ Tư";
                case Calendar.THURSDAY: return "Thứ Năm";
                case Calendar.FRIDAY: return "Thứ Sáu";
                case Calendar.SATURDAY: return "Thứ Bảy";
                case Calendar.SUNDAY: return "Chủ nhật";
                default: return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    //  VIEW HOLDER
    static class ViewHolder extends RecyclerView.ViewHolder {

        // Date header
        TextView tvDate, tvDateLabel, tvFullDate, tvDateTotal;

        // Transaction
        ImageView ivCategoryIcon;
        TextView tvCategoryName, tvNote, tvAmount;
        View transactionRoot;

        ViewHolder(View v) {
            super(v);

            // Date header
            tvDate = v.findViewById(R.id.tvDate);
            tvDateLabel = v.findViewById(R.id.tvDateLabel);
            tvFullDate = v.findViewById(R.id.tvFullDate);
            tvDateTotal = v.findViewById(R.id.tvDateTotal);

            // Transaction
            ivCategoryIcon = v.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = v.findViewById(R.id.tvCategoryName);
            tvNote = v.findViewById(R.id.tvNote);
            tvAmount = v.findViewById(R.id.tvAmount);
            transactionRoot = v.findViewById(R.id.transactionRoot);
        }
    }
}
