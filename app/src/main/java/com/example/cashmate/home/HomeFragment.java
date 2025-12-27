package com.example.cashmate.home;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmate.R;
import com.example.cashmate.Transaction.TransactionAdapter;
import com.example.cashmate.Transaction.TransactionDetailsFragment;
import com.example.cashmate.Transaction.TransactionFragment;
import com.example.cashmate.database.User.User;
import com.example.cashmate.database.User.UserHandle;
import com.example.cashmate.database.transaction.TransactionHandle;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private View underlineSpent;
    private View underlineIncome;
    private View sectionSpent;
    private View sectionIncome;
    private TextView tabWeek;
    private TextView tabMonth;
    private TextView tvBalance;
    private TextView tvBalance2;
    private TextView tvSpent;
    private TextView tvIncome;
    private ImageView ivEyeToggle;
    private RecyclerView rvRecent;
    private TransactionAdapter recentAdapter;
    private BarChart chartMonth;
    private TextView tvEmptyReport;
    private TransactionHandle transactionHandle;
    private UserHandle userHandle;
    private boolean isBalanceVisible = true;
    private String formattedBalance = "0";
    private String formattedIncome = "0";
    private String formattedExpense = "0";

    private enum Range { WEEK, MONTH }
    private enum Tab { SPENT, INCOME }
    private Tab currentTab = Tab.INCOME;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        underlineSpent = view.findViewById(R.id.underline_spent);
        underlineIncome = view.findViewById(R.id.underline_income);
        sectionSpent = view.findViewById(R.id.layout_spent);
        sectionIncome = view.findViewById(R.id.layout_income);
        selectTab(Tab.INCOME);
        if (sectionSpent != null) sectionSpent.setOnClickListener(v -> selectTab(Tab.SPENT));
        if (sectionIncome != null) sectionIncome.setOnClickListener(v -> selectTab(Tab.INCOME));

        tabWeek = view.findViewById(R.id.tab_week);
        tabMonth = view.findViewById(R.id.tab_month);
        selectRange(Range.WEEK);
        tabWeek.setOnClickListener(v -> selectRange(Range.WEEK));
        tabMonth.setOnClickListener(v -> selectRange(Range.MONTH));

        TextView transdetails = view.findViewById(R.id.report);
        transdetails.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TransactionDetailsFragment())
                .addToBackStack(null)
                .commit());

        TextView recent = view.findViewById(R.id.btn_recent_all);
        recent.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TransactionFragment())
                .addToBackStack(null)
                .commit());

        transactionHandle = new TransactionHandle(requireContext());
        userHandle = new UserHandle(requireContext());

        tvBalance = view.findViewById(R.id.tv_total_balance);
        tvBalance2 = view.findViewById(R.id.tv_total_balance2);
        tvSpent = view.findViewById(R.id.tv_spent);
        tvIncome = view.findViewById(R.id.tv_income);
        rvRecent = view.findViewById(R.id.rv_recent);
        chartMonth = view.findViewById(R.id.chart_month);
        tvEmptyReport = view.findViewById(R.id.tv_empty_report);

        if (rvRecent != null) {
            rvRecent.setLayoutManager(new LinearLayoutManager(getContext()));
            recentAdapter = new TransactionAdapter(transactionHandle.getRecentCursor(getCurrentUserId(), 3));
            rvRecent.setAdapter(recentAdapter);
        }

        loadTotals();
        loadRecent();
        setupChart();
        loadChartData();

        ivEyeToggle = view.findViewById(R.id.iv_eye_toggle);
        if (ivEyeToggle != null) {
            ivEyeToggle.setOnClickListener(v -> {
                isBalanceVisible = !isBalanceVisible;
                applyBalanceVisibility();
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTotals();
        loadRecent();
        loadChartData();
    }

    private void selectTab(Tab tab) {
        if (underlineSpent == null || underlineIncome == null) return;
        currentTab = tab;

        if (tab == Tab.SPENT) {
            underlineSpent.setVisibility(View.VISIBLE);
            underlineIncome.setVisibility(View.GONE);
        } else {
            underlineIncome.setVisibility(View.VISIBLE);
            underlineSpent.setVisibility(View.GONE);
        }
        loadChartData();
    }

    private void selectRange(Range r) {
        if (getContext() == null) return;
        int colorSelected = ContextCompat.getColor(getContext(), android.R.color.black);
        int colorUnselected = 0xFF9AA0AE;
        int bgSelected = R.drawable.seg_tab_bg_selected;
        int bgUnselected = R.drawable.seg_tab_bg_unselected;
        if (r == Range.WEEK) {
            tabWeek.setBackgroundResource(bgSelected);
            tabWeek.setTextColor(colorSelected);
            tabWeek.setTypeface(null, Typeface.BOLD);

            tabMonth.setBackgroundResource(bgUnselected);
            tabMonth.setTextColor(colorUnselected);
            tabMonth.setTypeface(null, Typeface.NORMAL);
        } else {
            tabMonth.setBackgroundResource(bgSelected);
            tabMonth.setTextColor(colorSelected);
            tabMonth.setTypeface(null, Typeface.BOLD);

            tabWeek.setBackgroundResource(bgUnselected);
            tabWeek.setTextColor(colorUnselected);
            tabWeek.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void loadTotals() {
        TransactionHandle.Totals totals = transactionHandle.getTotalsForUser(getCurrentUserId());
        double balance = totals.getBalance();

        formattedBalance = formatMoney(balance);
        formattedIncome = formatMoney(totals.income);
        formattedExpense = formatMoney(totals.expense);

        applyBalanceVisibility();
        if (tvIncome != null) tvIncome.setText(formattedIncome);
        if (tvSpent != null) tvSpent.setText(formattedExpense);
    }

    private void loadRecent() {
        if (recentAdapter == null) return;
        recentAdapter.swapCursor(transactionHandle.getRecentCursor(getCurrentUserId(), 3));
    }

    private void setupChart() {
        if (chartMonth == null) return;
        chartMonth.getDescription().setEnabled(false);
        chartMonth.getAxisRight().setEnabled(false);
        chartMonth.setTouchEnabled(false);
        chartMonth.setScaleEnabled(false);

        XAxis xAxis = chartMonth.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        chartMonth.getAxisLeft().setAxisMinimum(0f);

        Legend legend = chartMonth.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(12f);
    }

    private void loadChartData() {
        if (chartMonth == null) return;

        String userId = getCurrentUserId();
        String type = currentTab == Tab.SPENT ? "EXPENSE" : "INCOME";

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        Cursor c = transactionHandle.getMonthlyTotals(userId, type, 6);
        if (c != null && c.moveToFirst()) {
            do {
                String month = c.getString(c.getColumnIndexOrThrow("month"));
                String year = c.getString(c.getColumnIndexOrThrow("year"));
                double total = c.getDouble(c.getColumnIndexOrThrow("total"));
                labels.add(month + "/" + year);
                entries.add(new BarEntry(labels.size() - 1, (float) total));
            } while (c.moveToNext());
            c.close();
        }

        Collections.reverse(entries);
        Collections.reverse(labels);
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setX(i);
        }

        boolean hasData = !entries.isEmpty();
        if (tvEmptyReport != null) {
            tvEmptyReport.setVisibility(hasData ? View.GONE : View.VISIBLE);
        }
        if (!hasData) {
            chartMonth.clear();
            chartMonth.invalidate();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, currentTab == Tab.SPENT ? "Tổng chi" : "Tổng thu");
        dataSet.setColor(currentTab == Tab.SPENT ? 0xFFFF4D4F : 0xFF2EA7FF);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return NumberFormat.getInstance(Locale.getDefault()).format(barEntry.getY());
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        chartMonth.setData(barData);

        chartMonth.getXAxis().setLabelCount(labels.size());
        chartMonth.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int idx = (int) value;
                return (idx >= 0 && idx < labels.size()) ? labels.get(idx) : "";
            }
        });

        chartMonth.invalidate();
    }

    private void applyBalanceVisibility() {
        String hidden = "\u2022\u2022\u2022";
        if (tvBalance != null) tvBalance.setText(isBalanceVisible ? formattedBalance : hidden);
        if (tvBalance2 != null) tvBalance2.setText(isBalanceVisible ? formattedBalance : hidden);
        if (ivEyeToggle != null) {
            ivEyeToggle.setImageResource(isBalanceVisible ? R.drawable.ic_eye_open : R.drawable.ic_eye_close);
        }
    }

    private String getCurrentUserId() {
        User user = userHandle.getCurrentUser();
        return user != null ? user.getIdUser() : null;
    }

    private String formatMoney(double value) {
        return NumberFormat.getInstance(Locale.getDefault()).format(value) + " \u0111";
    }
}

