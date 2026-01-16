package com.example.cashmate.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmate.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TopSpendAdapter extends RecyclerView.Adapter<TopSpendAdapter.ViewHolder> {

    public static class TopSpendItem {
        public final String name;
        public final String icon;
        public final double amount;
        public final double percent;

        public TopSpendItem(String name, String icon, double amount, double percent) {
            this.name = name;
            this.icon = icon;
            this.amount = amount;
            this.percent = percent;
        }
    }

    private final List<TopSpendItem> items = new ArrayList<>();
    private final NumberFormat moneyFormat =
            NumberFormat.getInstance(Locale.getDefault());

    public void setItems(List<TopSpendItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_spend, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        TopSpendItem item = items.get(position);

        h.tvName.setText(item.name != null ? item.name : "");
        h.tvAmount.setText(String.format("%s \u0111", moneyFormat.format(item.amount)));

        int percentValue = (int) Math.round(item.percent);
        h.tvPercent.setText(String.format(Locale.getDefault(), "%d%%", percentValue));

        if (item.icon != null) {
            Context ctx = h.itemView.getContext();
            int res = ctx.getResources().getIdentifier(
                    item.icon,
                    "drawable",
                    ctx.getPackageName()
            );
            if (res != 0) {
                h.ivIcon.setImageResource(res);
            } else {
                h.ivIcon.setImageResource(R.drawable.ic_expense);
            }
        } else {
            h.ivIcon.setImageResource(R.drawable.ic_expense);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivIcon;
        final TextView tvName;
        final TextView tvAmount;
        final TextView tvPercent;

        ViewHolder(@NonNull View v) {
            super(v);
            ivIcon = v.findViewById(R.id.iv_icon);
            tvName = v.findViewById(R.id.tv_name);
            tvAmount = v.findViewById(R.id.tv_amount);
            tvPercent = v.findViewById(R.id.tv_percent);
        }
    }
}
