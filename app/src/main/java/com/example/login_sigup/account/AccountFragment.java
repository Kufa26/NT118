package com.example.login_sigup.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login_sigup.R;
import com.example.login_sigup.group.GroupFragment;

public class AccountFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.account, container, false);

        // ===== Thông tin cá nhân =====
        view.findViewById(R.id.profile).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // ===== NHÓM (Wallet_group) =====
        view.findViewById(R.id.Wallet_group).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new GroupFragment())
                        .addToBackStack(null) // ⭐ để back quay lại
                        .commit()
        );

        // ===== Cài đặt =====
        view.findViewById(R.id.setting).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new SettingFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // ===== Giới thiệu =====
        view.findViewById(R.id.introduce).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new IntroduceFragment())
                        .addToBackStack(null)
                        .commit()
        );

        return view;
    }
}
