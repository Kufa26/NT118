package com.example.cashmate.Transaction;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmate.R;
import com.example.cashmate.database.transaction.TransactionHandle;
import com.example.cashmate.database.User.UserHandle;

import java.util.Calendar;

public class TransactionFragment extends Fragment {

    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private TransactionHandle transactionHandle;
    private UserHandle userHandle;

    private TextView tvBalance;
    private TextView tvCurrentMonth, tvCurrentYear;
    private TextView tvStartBalance, tvEndBalance, tvTotal;
    private TextView btnPreviousMonth, btnNextMonth;

    private int currentMonth;
    private int currentYear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction, container, false);

        tvBalance = view.findViewById(R.id.tvBalance);
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
        tvCurrentYear  = view.findViewById(R.id.tvCurrentYear);
        tvStartBalance = view.findViewById(R.id.tvStartBalance);
        tvEndBalance   = view.findViewById(R.id.tvEndBalance);
        tvTotal        = view.findViewById(R.id.tvTotal);
        btnPreviousMonth = view.findViewById(R.id.btnPreviousMonth);
        btnNextMonth     = view.findViewById(R.id.btnNextMonth);

        rvTransactions = view.findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        transactionHandle = new TransactionHandle(getContext());
        transactionHandle.normalizeCreatedAtFromDate();
        userHandle = new UserHandle(getContext());

        adapter = new TransactionAdapter(null, this::loadDataByMonth);
        rvTransactions.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        currentYear  = calendar.get(Calendar.YEAR);

        updateMonthUI();
        loadDataByMonth();

        btnPreviousMonth.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth == 0) {
                currentMonth = 12;
                currentYear--;
            }
            updateMonthUI();
            loadDataByMonth();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth == 13) {
                currentMonth = 1;
                currentYear++;
            }
            updateMonthUI();
            loadDataByMonth();
        });

        Button btnViewReport = view.findViewById(R.id.btnViewReport);
        btnViewReport.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new TransactionDetailsFragment())
                        .addToBackStack(null)
                        .commit()
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataByMonth();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.swapCursor(null);
        }
    }

    private void updateMonthUI() {
        tvCurrentMonth.setText("Tháng " + currentMonth);
        tvCurrentYear.setText(String.valueOf(currentYear));
    }

    private void loadDataByMonth() {
        String userId = null;
        if (userHandle != null && userHandle.getCurrentUser() != null) {
            userId = userHandle.getCurrentUser().getIdUser();
        }

        Cursor cursor = transactionHandle.getByMonth(currentMonth, currentYear, userId);
        adapter.swapCursor(cursor);

        double income  = transactionHandle.getTotalByMonth(currentMonth, currentYear, "INCOME", userId);
        double expense = transactionHandle.getTotalByMonth(currentMonth, currentYear, "EXPENSE", userId);
        double startBalance = transactionHandle.getStartBalance(currentMonth, currentYear, userId);
        double endBalance   = startBalance + income - expense;

        tvStartBalance.setText(String.format("%,.0f đ", startBalance));
        tvEndBalance.setText(String.format("%,.0f đ", endBalance));
        tvTotal.setText(String.format("%+.0f đ", income - expense));
        tvBalance.setText(String.format("%,.0f đ", endBalance));
    }
}
