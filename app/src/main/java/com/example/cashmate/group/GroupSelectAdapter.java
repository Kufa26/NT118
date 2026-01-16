package com.example.cashmate.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmate.R;
import com.example.cashmate.database.Category.Category;

import java.util.List;

public class GroupSelectAdapter
        extends RecyclerView.Adapter<GroupSelectAdapter.ViewHolder> {

    public interface OnGroupClick {
        void onClick(Category category);
    }

    private final Context context;
    private final List<Category> list;
    private final OnGroupClick listener;

    public GroupSelectAdapter(Context context,
                              List<Category> list,
                              OnGroupClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = list.get(position);

        holder.tvName.setText(category.getNameCategory());

        int iconRes = context.getResources().getIdentifier(
                category.getIconCategory(),
                "drawable",
                context.getPackageName()
        );

        holder.imgIcon.setImageResource(
                iconRes != 0 ? iconRes : R.drawable.ic_food
        );

        holder.itemView.setOnClickListener(v ->
                listener.onClick(category)
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvName = itemView.findViewById(R.id.tvGroupName);
        }
    }
}
