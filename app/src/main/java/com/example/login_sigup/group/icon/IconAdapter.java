package com.example.login_sigup.group.icon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_sigup.R;

import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {

    public interface OnIconClickListener {
        void onIconClick(int icon);
    }

    private final List<Integer> icons;
    private final OnIconClickListener listener;

    public IconAdapter(List<Integer> icons, OnIconClickListener listener) {
        this.icons = icons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_icon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int icon = icons.get(position);
        holder.imgIcon.setImageResource(icon);
        holder.imgIcon.setOnClickListener(v -> listener.onIconClick(icon));
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
        }
    }
}
