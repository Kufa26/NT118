package com.example.login_sigup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupHolder> {

    private ArrayList<GroupItem> list;
    private OnGroupClickListener listener;

    public interface OnGroupClickListener {
        void onClick(GroupItem item);
    }

    public GroupAdapter(ArrayList<GroupItem> list, OnGroupClickListener listener) {
        this.list = list;
        this.listener = listener;
    }
    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new GroupHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int i) {
        GroupItem item = list.get(i);
        holder.title.setText(item.getName());
        holder.icon.setImageResource(item.getIcon());
        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class GroupHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;
        public GroupHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvGroupName);
            icon = itemView.findViewById(R.id.imgIcon);
        }
    }
}
