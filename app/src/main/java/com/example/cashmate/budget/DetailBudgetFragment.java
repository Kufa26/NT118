package com.example.cashmate.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmate.R;

import java.util.ArrayList;
import java.util.List;

public class DetailBudgetFragment extends Fragment {

    private TextView tvAmount, tvTotalBudget, tvSpent, tvDaysLeft;
    private Button btnCreate;
    private RecyclerView recyclerView;
    private BudgetAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detail_budget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvAmount = view.findViewById(R.id.tvAmount);
        tvTotalBudget = view.findViewById(R.id.tvTotalBudget);
        tvSpent = view.findViewById(R.id.tvSpent);
        tvDaysLeft = view.findViewById(R.id.tvDaysLeft);
        btnCreate = view.findViewById(R.id.btnCreate);
        recyclerView = view.findViewById(R.id.recyclerView);

        long targetAmount = 0;
        String groupName = "Chưa chọn nhóm";

        if (getArguments() != null) {
            targetAmount = getArguments().getLong("amount", 0);
            groupName = getArguments().getString("group_name", "Chưa chọn nhóm");
        }

        BudgetAnimator.run(view, (int) targetAmount, (int) targetAmount);

        if (tvTotalBudget != null) {
            tvTotalBudget.setText(formatShortAmount(targetAmount));
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<BudgetItem> items = new ArrayList<>();
        items.add(new BudgetItem(groupName, targetAmount, 0, R.drawable.ic_food, "MONTH"));

        adapter = new BudgetAdapter(items, item -> {
            Toast.makeText(getContext(), "Bạn đang xem chi tiết: " + item.getName(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);

        if (btnCreate != null) {
            btnCreate.setOnClickListener(v -> {
            });
        }
    }

    private String formatShortAmount(long amount) {
        if (amount >= 1000000) {
            return (amount / 1000000) + " M";
        } else if (amount >= 1000) {
            return (amount / 1000) + " K";
        }
        return String.valueOf(amount);
    }
}