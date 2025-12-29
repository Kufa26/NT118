package com.example.cashmate.budget;

import android.app.AlertDialog;
import android.graphics.Color;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BudgetFragment extends Fragment {
    private String currentTab = "MONTH";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_budget, container, false);
        if (BudgetStorage.getInstance().getList().isEmpty()) {
            view = inflater.inflate(R.layout.budget, container, false);
            setupEmptyView(view);
        } else {
            setupDetailView(view);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean hasData = !BudgetStorage.getInstance().getList().isEmpty();
        View view = getView();

        if (view != null) {
            if (view.findViewById(R.id.recyclerView) != null) {
                if (hasData) {
                    loadDataForTab(view);
                } else {
                    getParentFragmentManager().beginTransaction().detach(this).attach(this).commit();
                }
            }
            else if (hasData) {
                getParentFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }
        }
    }

    private void setupEmptyView(View view) {
        Button btnCreate = view.findViewById(R.id.btnCreateBudget);
        if (btnCreate != null) {
            btnCreate.setOnClickListener(v -> openAddBudgetScreen());
        }
    }

    private void setupDetailView(View view) {
        View tabMonth = view.findViewById(R.id.tabMonth);
        TextView tvTabMonth = view.findViewById(R.id.tvTabMonth);
        View lineMonth = view.findViewById(R.id.lineMonth);
        Button btnCreateMore = view.findViewById(R.id.btnCreate);

        if (tabMonth != null) {
            tabMonth.setOnClickListener(v -> {
                currentTab = "MONTH";
                updateTabUI(tvTabMonth, lineMonth, null, null);
                loadDataForTab(view);
            });
        }
        if (btnCreateMore != null) {
            btnCreateMore.setOnClickListener(v -> openAddBudgetScreen());
        }

        updateTabUI(tvTabMonth, lineMonth, null, null);
        loadDataForTab(view);
    }

    // --- HÀM LOAD DỮ LIỆU VÀ TÍNH TOÁN ---
    private void loadDataForTab(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        TextView tvRemainingAmount = view.findViewById(R.id.tvAmount);
        TextView tvTotalBudget = view.findViewById(R.id.tvTotalBudget);
        TextView tvSpent = view.findViewById(R.id.tvSpent);
        TextView tvDaysLeft = view.findViewById(R.id.tvDaysLeft);

        if (recyclerView == null) return;

        List<BudgetItem> allItems = BudgetStorage.getInstance().getList();
        List<BudgetItem> filteredList = new ArrayList<>();

        long totalBudget = 0;
        long totalSpent = 0;

        for (BudgetItem item : allItems) {
            String itemType = item.getTimeType();
            if (itemType == null) itemType = "MONTH";

            if (itemType.equals(currentTab)) {
                filteredList.add(item);
                totalBudget += item.getTotalAmount();
                totalSpent += item.getSpentAmount();
            }
        }

        long remaining = totalBudget - totalSpent;
        if (remaining < 0) remaining = 0;

        // Cập nhật UI
        if (tvRemainingAmount != null) {
            DecimalFormat df = new DecimalFormat("#,###");
            tvRemainingAmount.setText(df.format(remaining));

            // --- SỬA MÀU SẮC ---
            if (totalBudget == 0) {
                tvRemainingAmount.setTextColor(Color.parseColor("#757575"));
            } else if (remaining == 0) {
                tvRemainingAmount.setTextColor(Color.RED);
            } else {
                tvRemainingAmount.setTextColor(Color.parseColor("#2E7D32"));
            }
        }

        if (tvTotalBudget != null) tvTotalBudget.setText(formatShortAmount(totalBudget));
        if (tvSpent != null) tvSpent.setText(formatShortAmount(totalSpent));

        if (tvDaysLeft != null) {
            if (currentTab.equals("MONTH")) {
                Calendar cal = Calendar.getInstance();
                int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                int currentDay = cal.get(Calendar.DAY_OF_MONTH);
                int daysLeft = lastDay - currentDay;
                tvDaysLeft.setText(daysLeft + " ngày");
            } else {
                tvDaysLeft.setText("-");
            }
        }
        BudgetAnimator.run(view, (int)remaining, (int)totalBudget);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        BudgetAdapter adapter = new BudgetAdapter(filteredList, item -> showDeleteDialog(item));
        recyclerView.setAdapter(adapter);
    }

    private void updateTabUI(TextView activeTv, View activeLine, TextView inactiveTv, View inactiveLine) {
        if (activeTv != null) activeTv.setTextColor(Color.parseColor("#27AE60"));
        if (activeLine != null) activeLine.setVisibility(View.VISIBLE);
        if (inactiveTv != null) inactiveTv.setTextColor(Color.parseColor("#888888"));
        if (inactiveLine != null) inactiveLine.setVisibility(View.INVISIBLE);
    }

    private void showDeleteDialog(BudgetItem item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa ngân sách")
                .setMessage("Xóa ngân sách '" + item.getName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    BudgetStorage.getInstance().deleteItem(item);
                    onResume(); // Refresh
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void openAddBudgetScreen() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new Add_Budget())
                .addToBackStack(null).commit();
    }

    private String formatShortAmount(long amount) {
        if (amount >= 1000000) return (amount / 1000000) + " M";
        else if (amount >= 1000) return (amount / 1000) + " K";
        return String.valueOf(amount);
    }
}
