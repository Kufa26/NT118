package com.example.cashmate.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmate.R;
import com.example.cashmate.database.Category.Category;
import com.example.cashmate.database.Category.CategoryHandle;

import java.util.List;

public class GroupFragment extends Fragment {

    private RecyclerView rcvGroups;
    private TextView tabExpense, tabIncome;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.select_group, container, false);

        // ===== BACK =====
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // ===== ADD GROUP =====
        LinearLayout btnAddGroup = view.findViewById(R.id.btnAddGroup);
        btnAddGroup.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new AddGroupFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // ===== TAB =====
        tabExpense = view.findViewById(R.id.tabExpense);
        tabIncome = view.findViewById(R.id.tabIncome);

        // ===== LIST =====
        rcvGroups = view.findViewById(R.id.rcvGroups);
        rcvGroups.setLayoutManager(new LinearLayoutManager(getContext()));

        // 🔥 MẶC ĐỊNH: KHOẢN THU
        setActiveTab(false);
        loadGroups("INCOME");

        // ===== CLICK TAB =====
        tabExpense.setOnClickListener(v -> {
            setActiveTab(true);
            loadGroups("EXPENSE");
        });

        tabIncome.setOnClickListener(v -> {
            setActiveTab(false);
            loadGroups("INCOME");
        });

        return view;
    }

    // ===== LOAD DATA =====
    private void loadGroups(String type) {
        CategoryHandle handle = new CategoryHandle(getContext());
        List<Category> list = handle.getCategoriesByType(type);
        rcvGroups.setAdapter(new GroupAdapter(getContext(), list));
    }

    // ===== UI TAB STATE =====
    private void setActiveTab(boolean isExpense) {
        if (isExpense) {
            tabExpense.setBackgroundResource(R.drawable.tab_active);
            tabExpense.setTextColor(getResources().getColor(R.color.greenbutton));

            tabIncome.setBackgroundResource(R.drawable.tab_inactive);
            tabIncome.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            tabIncome.setBackgroundResource(R.drawable.tab_active);
            tabIncome.setTextColor(getResources().getColor(R.color.greenbutton));

            tabExpense.setBackgroundResource(R.drawable.tab_inactive);
            tabExpense.setTextColor(getResources().getColor(android.R.color.black));
        }
    }
}
