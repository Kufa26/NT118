package com.example.cashmate.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmate.R;

import java.util.ArrayList;
import java.util.List;

public class DetailBudgetFragment extends Fragment {

    private TextView tvAmount, tvTotalBudget, tvSpent, tvDaysLeft;
    private Button btnCreate;
    private RecyclerView recyclerView;
    private BudgetAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detail_budget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ các View
        tvAmount = view.findViewById(R.id.tvAmount);
        tvTotalBudget = view.findViewById(R.id.tvTotalBudget);
        tvSpent = view.findViewById(R.id.tvSpent);
        tvDaysLeft = view.findViewById(R.id.tvDaysLeft);
        btnCreate = view.findViewById(R.id.btnCreate);
        recyclerView = view.findViewById(R.id.recyclerView);

        // 2. Nhận dữ liệu
        long targetAmount = 0;
        String groupName = "Chưa chọn nhóm";

        if (getArguments() != null) {
            targetAmount = getArguments().getLong("amount", 0);
            groupName = getArguments().getString("group_name", "Chưa chọn nhóm");
        }

        // 3. Chạy hiệu ứng
        BudgetAnimator.run(view, (int) targetAmount, (int) targetAmount);

        // 4. Cập nhật TextView tổng ngân sách
        if (tvTotalBudget != null) {
            tvTotalBudget.setText(formatShortAmount(targetAmount));
        }

        // 5. Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 6. Đổ dữ liệu vào danh sách
        List<BudgetItem> items = new ArrayList<>();

        // --- SỬA LỖI 1: Thêm tham số "MONTH" vào Constructor BudgetItem ---
        // Vì constructor mới yêu cầu 5 tham số: tên, tổng tiền, đã chi, icon, loại thời gian
        items.add(new BudgetItem(groupName, targetAmount, 0, R.drawable.ic_food, "MONTH"));

        // --- SỬA LỖI 2: Thêm tham số listener vào Constructor BudgetAdapter ---
        // Adapter mới yêu cầu tham số thứ 2 là sự kiện click. Ở đây ta có thể truyền null hoặc xử lý đơn giản.
        adapter = new BudgetAdapter(items, item -> {
            // Xử lý sự kiện xóa nếu cần (để trống nếu không muốn xóa ở màn hình này)
            Toast.makeText(getContext(), "Bạn đang xem chi tiết: " + item.getName(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);

        // Xử lý sự kiện nút bấm nếu cần
        if (btnCreate != null) {
            btnCreate.setOnClickListener(v -> {
                // Code xử lý khi nhấn nút Tạo Ngân Sách
            });
        }
    }

    // Hàm phụ để định dạng tiền tệ ngắn gọn
    private String formatShortAmount(long amount) {
        if (amount >= 1000000) {
            return (amount / 1000000) + " M";
        } else if (amount >= 1000) {
            return (amount / 1000) + " K";
        }
        return String.valueOf(amount);
    }
}