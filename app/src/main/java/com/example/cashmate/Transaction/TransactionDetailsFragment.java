package com.example.cashmate.Transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashmate.R;
import com.example.cashmate.database.transaction.TransactionHandle;
import com.example.cashmate.database.User.UserHandle;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

public class TransactionDetailsFragment extends Fragment {

    private TransactionHandle transactionHandle;
    private UserHandle userHandle;

    private TextView tvBalanceTop;
    private TextView tvStartBalance, tvEndBalance;
    private TextView tvIncomeGroup, tvExpenseGroup;
    private TextView tvNetIncome;

    private TabLayout tabLayout;
    private TextView tvIncomeGroupAmount;
    private TextView tvExpenseGroupAmount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_detail, container, false);

        transactionHandle = new TransactionHandle(requireContext());
        userHandle = new UserHandle(requireContext());

        tabLayout       = view.findViewById(R.id.tabLayout);
        tvBalanceTop    = view.findViewById(R.id.tvBalance);
        tvStartBalance  = view.findViewById(R.id.tvStartBalance);
        tvEndBalance    = view.findViewById(R.id.tvEndBalance);
        tvNetIncome     = view.findViewById(R.id.tvNetIncome);
        tvIncomeGroup   = view.findViewById(R.id.tvIncomeAmount);
        tvExpenseGroup  = view.findViewById(R.id.tvExpenseAmount);
        tvIncomeGroupAmount  = view.findViewById(R.id.tvIncomeGroupAmount);
        tvExpenseGroupAmount = view.findViewById(R.id.tvExpenseGroupAmount);

        view.findViewById(R.id.tvClose).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        view.findViewById(R.id.cardIncomeGroup).setOnClickListener(v -> openListByType("INCOME"));
        view.findViewById(R.id.cardExpenseGroup).setOnClickListener(v -> openListByType("EXPENSE"));

        setupMonthTabs();
        setupTabListener();

        return view;
    }

    private void setupMonthTabs() {
        tabLayout.removeAllTabs();

        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear  = cal.get(Calendar.YEAR);

        for (int i = 10; i >= 1; i--) {
            Calendar tmp = (Calendar) cal.clone();
            tmp.add(Calendar.MONTH, -i);
            int m = tmp.get(Calendar.MONTH) + 1;
            int y = tmp.get(Calendar.YEAR);
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(String.format("%02d/%d", m, y));
            tab.setTag(new int[]{m, y});
            tabLayout.addTab(tab);
        }

        Calendar lastMonth = (Calendar) cal.clone();
        lastMonth.add(Calendar.MONTH, -1);
        TabLayout.Tab prevTab = tabLayout.newTab();
        prevTab.setText("THÁNG TRƯỚC");
        prevTab.setTag(new int[]{lastMonth.get(Calendar.MONTH) + 1, lastMonth.get(Calendar.YEAR)});
        tabLayout.addTab(prevTab);

        TabLayout.Tab currentTab = tabLayout.newTab();
        currentTab.setText("THÁNG NÀY");
        currentTab.setTag(new int[]{currentMonth, currentYear});
        tabLayout.addTab(currentTab, true);

        loadReport(currentMonth, currentYear);
    }

    private void setupTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int[] time = (int[]) tab.getTag();
                loadReport(time[0], time[1]);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadReport(int month, int year) {
        String userId = null;
        if (userHandle != null && userHandle.getCurrentUser() != null) {
            userId = userHandle.getCurrentUser().getIdUser();
        }

        double income  = transactionHandle.getTotalByMonth(month, year, "INCOME", userId);
        double expense = transactionHandle.getTotalByMonth(month, year, "EXPENSE", userId);
        double startBalance = transactionHandle.getStartBalance(month, year, userId);
        double endBalance   = startBalance + income - expense;

        tvBalanceTop.setText(String.format("%,.0f đ", endBalance));
        tvStartBalance.setText(String.format("%,.0f đ", startBalance));
        tvEndBalance.setText(String.format("%,.0f đ", endBalance));

        tvIncomeGroup.setText(String.format("%,.0f đ", income));
        tvExpenseGroup.setText(String.format("%,.0f đ", expense));
        tvNetIncome.setText(String.format("%+.0f đ", income - expense));

        tvIncomeGroupAmount.setText(String.format("%,.0f đ", income));
        tvExpenseGroupAmount.setText(String.format("%,.0f đ", expense));
    }

    private void openListByType(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("typeTransaction", type);

        TransactionListFragment fragment = new TransactionListFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

