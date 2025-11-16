package com.example.login_sigup;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class Group extends Fragment {
    ArrayList<GroupItem> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.select_group, container, false);

        ImageButton btnBack= view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        RecyclerView rcv = view.findViewById(R.id.rcvGroups);
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv.setClipToPadding(false);
        rcv.setPadding(0, 0, 0, 40);

        loadGroups();
        GroupAdapter adapter = new GroupAdapter(list, item -> {
        });
        rcv.setAdapter(adapter);
        return view;
    }

    private void loadGroups() {
        list.add(new GroupItem("Ăn uống", R.drawable.ic_food));
        list.add(new GroupItem("Bảo hiểm", R.drawable.ic_insurance));
        list.add(new GroupItem("Đầu tư", R.drawable.ic_bills));
        list.add(new GroupItem("Di chuyển", R.drawable.ic_move));
        list.add(new GroupItem("Bảo dưỡng xe", R.drawable.ic_insurance));
        list.add(new GroupItem("Vật nuôi", R.drawable.ic_pets));
        list.add(new GroupItem("Sửa và trang trí nhà", R.drawable.ic_tool));
        list.add(new GroupItem("Giải trí", R.drawable.ic_entertainment));
        list.add(new GroupItem("Công việc", R.drawable.ic_work));
        list.add(new GroupItem("Vui chơi", R.drawable.ic_sports));
        list.add(new GroupItem("Giáo dục", R.drawable.ic_education));
        list.add(new GroupItem("Hóa đơn tiện ích", R.drawable.ic_bills));
        list.add(new GroupItem("Hóa đơn điện", R.drawable.ic_electric));
        list.add(new GroupItem("Hóa đơn xăng", R.drawable.ic_fuel));
        list.add(new GroupItem("Hóa đơn Internet", R.drawable.ic_internet));
        list.add(new GroupItem("Hóa đơn nước", R.drawable.ic_water));
        list.add(new GroupItem("Hóa đơn điện thoại", R.drawable.ic_phone));
        list.add(new GroupItem("Mua sắm", R.drawable.ic_shopping));
        list.add(new GroupItem("Đồ dùng cá nhân", R.drawable.ic_personal_items));
        list.add(new GroupItem("Thuế", R.drawable.ic_tax));
        list.add(new GroupItem("Làm đẹp", R.drawable.ic_jewelry));
        list.add(new GroupItem("Vườn", R.drawable.ic_garden));
        list.add(new GroupItem("Sức khỏe", R.drawable.ic_health));
        list.add(new GroupItem("Lương", R.drawable.ic_salary));
        list.add(new GroupItem("Trả nợ", R.drawable.ic_debt_repayment));
        list.add(new GroupItem("Thu nợ", R.drawable.ic_debt_collection));
    }
}
