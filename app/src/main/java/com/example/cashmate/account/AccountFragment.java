package com.example.cashmate.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashmate.R;
import com.example.cashmate.database.User.User;
import com.example.cashmate.database.User.UserHandle;
import com.example.cashmate.group.GroupFragment;


public class AccountFragment extends Fragment {

    private TextView name_user, mail_user;
    private UserHandle userHandle;
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.account, container, false);
        name_user = view.findViewById(R.id.name_user);
        mail_user = view.findViewById(R.id.mail_user);
        userHandle = new UserHandle(requireContext());
        loadUserBasicInfo();

        // ===== Thông tin cá nhân =====
        view.findViewById(R.id.profile).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // ===== NHÓM =====
        view.findViewById(R.id.Wallet_group).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new GroupFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // ===== CÀI ĐẶT =====
        view.findViewById(R.id.setting).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new SettingFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // ===== GIỚI THIỆU =====
        view.findViewById(R.id.introduce).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new IntroduceFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // ===== CÂU HỎI THƯỜNG GẶP (FAQ) =====
        view.findViewById(R.id.support).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new FAQFragment())
                        .addToBackStack(null)
                        .commit()
        );

        return view;
    }

    private void loadUserBasicInfo() {
        String email = requireContext()
                .getSharedPreferences("USER_SESSION", 0)
                .getString("email", null);

        if (email == null) return;

        User user = userHandle.getUserByEmail(email);
        if (user == null) return;

        name_user.setText(
                user.getFullName() == null || user.getFullName().isEmpty()
                        ? "Chưa có tên"
                        : user.getFullName()
        );

        mail_user.setText(user.getEmail());
    }



}
