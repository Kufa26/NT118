package com.example.login_sigup.Transaction;

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

import com.example.login_sigup.R;
import com.example.login_sigup.database.transaction.TransactionHandle;

public class TransactionFragment extends Fragment {

    RecyclerView rvTransactions;
    TransactionAdapter adapter;
    TransactionHandle transactionHandle;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.transaction, container, false);

        rvTransactions = view.findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        transactionHandle = new TransactionHandle(getContext());
        adapter = new TransactionAdapter(transactionHandle.getAll());
        rvTransactions.setAdapter(adapter);

        Button btnViewReport = view.findViewById(R.id.btnViewReport);
        btnViewReport.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TransactionDetailsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // ❌ KHÔNG CÓ btnAddTransaction Ở ĐÂY

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.setData(transactionHandle.getAll());
    }
}
