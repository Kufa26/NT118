package com.example.cashmate.budget;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cashmate.R;
import java.text.DecimalFormat;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {

    private List<BudgetItem> items;
    private DecimalFormat df = new DecimalFormat("#,###");
    private OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(BudgetItem item);
    }

    public BudgetAdapter(List<BudgetItem> items, OnItemLongClickListener longClickListener) {
        this.items = items;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BudgetItem item = items.get(position);

        // 1. Tên và Icon
        holder.tvName.setText(item.getName());
        holder.imgIcon.setImageResource(item.getIconRes());

        // 2. Tổng ngân sách (Hiển thị góc trên phải)
        holder.tvAmount.setText(df.format(item.getTotalAmount()));

        // 3. --- QUAN TRỌNG: HIỂN THỊ SỐ TIỀN CÒN LẠI ---
        long remaining = item.getTotalAmount() - item.getSpentAmount();
        if (remaining < 0) remaining = 0;

        holder.tvRemaining.setText("Còn lại " + df.format(remaining));
        // -----------------------------------------------

        // 4. Thanh tiến độ (Màu xanh thể hiện phần ĐÃ CHI)
        int progress = 0;
        if (item.getTotalAmount() > 0) {
            progress = (int) (((double) item.getSpentAmount() / item.getTotalAmount()) * 100);
        }
        if (progress > 100) progress = 100;

        holder.progressBar.setProgress(progress);

        // Đổi màu thanh progress nếu gần hết tiền (Optional)
        if (progress >= 90) {
            holder.progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.RED));
        } else {
            holder.progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#27AE60")));
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) longClickListener.onItemLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount, tvRemaining;
        ImageView imgIcon;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvAmount = itemView.findViewById(R.id.tvItemAmount);
            tvRemaining = itemView.findViewById(R.id.tvRemaining);
            imgIcon = itemView.findViewById(R.id.imgItemIcon);
            progressBar = itemView.findViewById(R.id.itemProgressBar);
        }
    }
}