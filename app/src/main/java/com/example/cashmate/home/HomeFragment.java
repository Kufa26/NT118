package com.example.cashmate.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cashmate.R;
import com.example.cashmate.Transaction.TransactionDetailsFragment;

public class HomeFragment extends Fragment {

    private View underlineSpent;   // <-- biến thành viên
    private View underlineIncome;  // <-- biến thành viên
    private View sectionSpent;     // tùy chọn: vùng bấm cột trái (nếu có id trong XML)
    private View sectionIncome;    // tùy chọn: vùng bấm cột phải
    private TextView tabWeek, tabMonth;
    private enum Range { WEEK, MONTH }
    private enum Tab { SPENT, INCOME }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);
        underlineSpent  = view.findViewById(R.id.underline_spent);
        underlineIncome = view.findViewById(R.id.underline_income);
        sectionSpent    = view.findViewById(R.id.layout_spent);   // nếu bạn có container
        sectionIncome   = view.findViewById(R.id.layout_income);  // nếu bạn có container
        selectTab(Tab.INCOME);
        if (sectionSpent  != null) sectionSpent.setOnClickListener(v -> selectTab(Tab.SPENT));
        if (sectionIncome != null) sectionIncome.setOnClickListener(v -> selectTab(Tab.INCOME));

        tabWeek  = view.findViewById(R.id.tab_week);
        tabMonth = view.findViewById(R.id.tab_month);
        selectRange(Range.WEEK);
        tabWeek.setOnClickListener(v -> selectRange(Range.WEEK));
        tabMonth.setOnClickListener(v -> selectRange(Range.MONTH));

        TextView transdetails = view.findViewById(R.id.report);
        transdetails.setOnClickListener(v ->{
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TransactionDetailsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        TextView recent = view.findViewById(R.id.btn_recent_all);
        recent.setOnClickListener(v ->{
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TransactionDetailsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
    private void selectTab(Tab tab) {
        if (underlineSpent == null || underlineIncome == null) return;

        if (tab == Tab.SPENT) {
            underlineSpent.setVisibility(View.VISIBLE);
            underlineIncome.setVisibility(View.GONE);
        } else {
            underlineIncome.setVisibility(View.VISIBLE);
            underlineSpent.setVisibility(View.GONE);
        }
    }
    private void selectRange(Range r) {
        if (getContext() == null) return;
        int colorSelected   = ContextCompat.getColor(getContext(), android.R.color.black);
        int colorUnselected = 0xFF9AA0AE;
        int bgSelected   = R.drawable.seg_tab_bg_selected;
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
}}
