package com.example.login_sigup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.login_sigup.R;

public class TransactionFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction, container, false);

        Button transdetails = view.findViewById(R.id.btnViewReport);
        transdetails.setOnClickListener(v ->{
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TransactionDetailsFragment())
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }
}
