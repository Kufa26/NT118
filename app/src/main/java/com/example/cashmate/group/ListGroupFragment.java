package com.example.cashmate.group;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class ListGroupFragment extends Fragment {

    private RecyclerView rcvGroups;
    private TextView tabIncome, tabExpense;
    private ImageButton btnBack;
    private boolean expenseOnly = false;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.list_group, container, false);

        // ===== BIND VIEW =====
        btnBack = view.findViewById(R.id.btnBack);
        tabIncome = view.findViewById(R.id.tabIncome);
        tabExpense = view.findViewById(R.id.tabExpense);
        rcvGroups = view.findViewById(R.id.rcvGroups);
        Bundle args = getArguments();
        expenseOnly = args != null && args.getBoolean("expenseOnly", false);

        // ===== BACK BUTTON (🔥 FIX CHÍNH Ở ĐÂY) =====
        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        // ===== RECYCLER VIEW =====
        rcvGroups.setLayoutManager(new LinearLayoutManager(getContext()));

        // ===== DEFAULT TAB =====
        if (expenseOnly) {
            setActiveTab(true); // EXPENSE
            loadGroups("EXPENSE");
            tabIncome.setEnabled(false);
            tabIncome.setAlpha(0.4f);
            tabIncome.setOnClickListener(null);
        } else {
            setActiveTab(false);          // false = INCOME
            loadGroups("INCOME");

            // ===== TAB CLICK =====
            tabIncome.setOnClickListener(v -> {
                setActiveTab(false);
                loadGroups("INCOME");
            });

            tabExpense.setOnClickListener(v -> {
                setActiveTab(true);
                loadGroups("EXPENSE");
            });
        }

        return view;
    }

    // ================= LOAD GROUP =================
    private void loadGroups(String type) {
        CategoryHandle handle = new CategoryHandle(requireContext());
        List<Category> list = handle.getCategoriesByType(type);

        GroupSelectAdapter adapter =
                new GroupSelectAdapter(getContext(), list, category -> {

                    Bundle result = new Bundle();
                    result.putLong("idCategory", category.getIdCategory());
                    result.putString("name", category.getNameCategory());
                    result.putString("icon", category.getIconCategory());
                    result.putString("type", category.getTypeCategory());

                    // gửi dữ liệu về PlusFragment
                    getParentFragmentManager()
                            .setFragmentResult("select_group", result);

                    // quay lại PlusFragment
                    getParentFragmentManager()
                            .popBackStack();
                });

        rcvGroups.setAdapter(adapter);
    }

    // ================= TAB UI =================
    private void setActiveTab(boolean isExpense) {
        if (isExpense) {
            tabExpense.setBackgroundResource(R.drawable.tab_active);
            tabExpense.setTextColor(getResources().getColor(R.color.greenbutton));

            tabIncome.setBackgroundResource(R.drawable.tab_inactive);
            tabIncome.setTextColor(Color.BLACK);
        } else {
            tabIncome.setBackgroundResource(R.drawable.tab_active);
            tabIncome.setTextColor(getResources().getColor(R.color.greenbutton));

            tabExpense.setBackgroundResource(R.drawable.tab_inactive);
            tabExpense.setTextColor(Color.BLACK);
        }
    }
}
