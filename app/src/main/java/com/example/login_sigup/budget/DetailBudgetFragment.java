package com.example.login_sigup.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login_sigup.R;

public class DetailBudgetFragment extends Fragment {

    private TextView tvAmount;
    private Button btnCreate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp layout detail_budget.xml
        View view = inflater.inflate(R.layout.detail_budget, container, false);

        // 1. Ánh xạ các view cơ bản
        tvAmount = view.findViewById(R.id.tvAmount);
        btnCreate = view.findViewById(R.id.btnCreate);

        // Nếu bạn có nút quay lại (ví dụ trong toolbar)
        // ImageButton btnBack = view.findViewById(R.id.btnBack);
        // btnBack.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 2. Chạy hiệu ứng thanh tiến trình và số nhảy
        // Gọi class BudgetAnimator mà bạn đã tạo ở bước trước
        // Giả sử target là 4,000,000 và tổng là 4,000,000
        BudgetAnimator.run(view, 4000000, 4000000);

        // 3. Xử lý sự kiện nút bấm
        btnCreate.setOnClickListener(v -> {
            // Xử lý tạo ngân sách ở đây
        });
    }
}
