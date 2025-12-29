package com.example.cashmate.Transaction;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmate.R;
import com.example.cashmate.database.transaction.TransactionHandle;

public class TransactionListFragment extends Fragment {

    private TransactionHandle transactionHandle;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;

    private String typeTransaction; // INCOME / EXPENSE

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_transaction_list, container, false);

        // GET TYPE
        if (getArguments() != null) {
            typeTransaction = getArguments().getString("typeTransaction");
        }
        // BACK BUTTON
        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        // INIT
        transactionHandle = new TransactionHandle(requireContext());
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadData();

        return view;
    }

    private void loadData() {
        Cursor cursor =
                transactionHandle.getAllCursorByType(typeTransaction);

        adapter = new TransactionAdapter(cursor);
        recyclerView.setAdapter(adapter);
    }
}
