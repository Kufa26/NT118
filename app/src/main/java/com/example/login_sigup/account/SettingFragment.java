package com.example.login_sigup.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.login_sigup.R;

public class SettingFragment extends Fragment {

    private Switch mode_switch;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting, container, false);

        mode_switch = view.findViewById(R.id.mode_switch);
        ImageButton btnBack = view.findViewById(R.id.btnBack);

        // Sử dụng chung tên file để MainActivity cũng có thể đọc được
        sharedPreferences = getActivity().getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);

        // 1. Kiểm tra trạng thái lưu trữ để gạt nút Switch cho đúng
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        mode_switch.setChecked(isDarkMode);

        btnBack.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

        // 2. Xử lý đổi màu
        mode_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Lưu trạng thái ngay lập tức
            sharedPreferences.edit().putBoolean("isDarkMode", isChecked).apply();

            // Đổi Mode hệ thống
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // 3. QUAN TRỌNG: Không dùng configChanges trong Manifest để Android tự load lại file colors (night)
            if (getActivity() != null) {
                getActivity().recreate();
            }
        });

        return view;
    }
}