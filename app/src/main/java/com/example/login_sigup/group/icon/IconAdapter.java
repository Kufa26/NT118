package com.example.login_sigup.group.icon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_sigup.R;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {

    private final int[] icons;
    private final OnIconClick listener;

    public interface OnIconClick {
        void onClick(int iconRes);
    }

    public IconAdapter(int[] icons, OnIconClick listener) {
        this.icons = icons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_icon, parent, false);
        return new IconViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        int iconRes = icons[position];
        holder.imgIcon.setImageResource(iconRes);
        holder.itemView.setOnClickListener(v -> listener.onClick(iconRes));
    }

    @Override
    public int getItemCount() {
        return icons.length;
    }

    static class IconViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        IconViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
        }
    }
}
