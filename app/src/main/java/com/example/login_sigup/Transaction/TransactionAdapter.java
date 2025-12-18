package com.example.login_sigup.Transaction;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_sigup.R;
import com.example.login_sigup.database.transaction.Transaction;

import java.util.List;
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    List<Transaction> list;

    public TransactionAdapter(List<Transaction> list) {
        this.list = list;
    }

    public void setData(List<Transaction> newList) {
        list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_income, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        Transaction t = list.get(i);

        h.tvAmount.setText(String.format("%,.0f đ", t.getAmount()));
        h.tvNote.setText(t.getNote());

        if ("EXPENSE".equals(t.getTypeTransaction())) {
            h.tvAmount.setTextColor(Color.RED);
        } else {
            h.tvAmount.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvAmount, tvNote;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvAmount = v.findViewById(R.id.tvAmount);
            tvNote = v.findViewById(R.id.tvNote);
        }
    }
}
