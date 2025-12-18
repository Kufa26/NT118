package com.example.login_sigup.budget;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.login_sigup.R;


public class Add_Budget extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState)
    {
        View view = inflater.inflate(R.layout.create_a_budget, container, false);

        TextView btnClose = view.findViewById(R.id.btnBack);
        btnClose.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }
}
