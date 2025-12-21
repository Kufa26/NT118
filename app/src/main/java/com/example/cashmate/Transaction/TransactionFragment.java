package com.example.cashmate.Transaction;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmate.R;
import com.example.cashmate.database.transaction.TransactionHandle;

public class TransactionFragment extends Fragment {

    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private TransactionHandle transactionHandle;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.transaction, container, false);

        // ===== RecyclerView =====
        rvTransactions = view.findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        transactionHandle = new TransactionHandle(getContext());

        // ✅ DÙNG CURSOR
        Cursor cursor = transactionHandle.getAllCursor();
        adapter = new TransactionAdapter(cursor);
        rvTransactions.setAdapter(adapter);

        // ===== Button xem báo cáo =====
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

        // ✅ LOAD LẠI CURSOR SAU KHI THÊM / SỬA
        Cursor newCursor = transactionHandle.getAllCursor();
        adapter.swapCursor(newCursor);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // tránh leak
        if (adapter != null) {
            adapter.swapCursor(null);
        }
    }
}
