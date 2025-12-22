package com.example.cashmate.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.cashmate.R;

public class BudgetFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.budget, container, false);


        Button crBudget = view.findViewById(R.id.bnt_create);
        crBudget.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new Add_Budget())
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }
}
