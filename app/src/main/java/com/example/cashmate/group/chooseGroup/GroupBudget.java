package com.example.cashmate.group.chooseGroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmate.R;

import java.util.ArrayList;

public class GroupBudget extends Fragment {

    ArrayList<GroupItemBudget> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_group, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        View btnAddGroup = view.findViewById(R.id.btnAddGroup);
        btnAddGroup.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddGroupBudget.class);
            startActivity(intent);
        });

        RecyclerView rcv = view.findViewById(R.id.rcvGroups);
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv.setClipToPadding(false);
        rcv.setPadding(0, 0, 0, 40);

        loadGroups();
        GroupAdapterBudget adapter = new GroupAdapterBudget(list, item -> {
            // Tạo Bundle để đóng gói dữ liệu
            Bundle result = new Bundle();
            result.putString("groupName", item.getName());
            result.putInt("groupIcon", item.getIcon());

            // Gửi kết quả về qua FragmentManager
            getParentFragmentManager().setFragmentResult("select_group", result);

            // Quay lại màn hình Add_Budget
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        rcv.setAdapter(adapter);

        return view;
    }


    private void loadGroups() {
        list.add(new GroupItemBudget("Ăn uống", R.drawable.ic_food));
        list.add(new GroupItemBudget("Bảo hiểm", R.drawable.ic_insurance));
        list.add(new GroupItemBudget("Đầu tư", R.drawable.ic_bills));
        list.add(new GroupItemBudget("Di chuyển", R.drawable.ic_move));
        list.add(new GroupItemBudget("Bảo dưỡng xe", R.drawable.ic_maintenance));
        list.add(new GroupItemBudget("Vật nuôi", R.drawable.ic_pets));
        list.add(new GroupItemBudget("Sửa và trang trí nhà", R.drawable.ic_tool));
        list.add(new GroupItemBudget("Giải trí", R.drawable.ic_entertainment));
        list.add(new GroupItemBudget("Công việc", R.drawable.ic_work));
        list.add(new GroupItemBudget("Vui chơi", R.drawable.ic_sports));
        list.add(new GroupItemBudget("Giáo dục", R.drawable.ic_education));
        list.add(new GroupItemBudget("Hóa đơn tiện ích", R.drawable.ic_bills));
        list.add(new GroupItemBudget("Hóa đơn điện", R.drawable.ic_electric));
        list.add(new GroupItemBudget("Hóa đơn xăng", R.drawable.ic_fuel));
        list.add(new GroupItemBudget("Hóa đơn Internet", R.drawable.ic_internet));
        list.add(new GroupItemBudget("Hóa đơn nước", R.drawable.ic_water));
        list.add(new GroupItemBudget("Hóa đơn điện thoại", R.drawable.ic_phone));
        list.add(new GroupItemBudget("Mua sắm", R.drawable.ic_shopping));
        list.add(new GroupItemBudget("Đồ dùng cá nhân", R.drawable.ic_personal_items));
        list.add(new GroupItemBudget("Thuế", R.drawable.ic_tax));
        list.add(new GroupItemBudget("Làm đẹp", R.drawable.ic_jewelry));
        list.add(new GroupItemBudget("Vườn", R.drawable.ic_garden));
        list.add(new GroupItemBudget("Sức khỏe", R.drawable.ic_health));
        list.add(new GroupItemBudget("Lương", R.drawable.ic_salary));
        list.add(new GroupItemBudget("Trả nợ", R.drawable.ic_debt_repayment));
        list.add(new GroupItemBudget("Thu nợ", R.drawable.ic_debt_collection));
    }
}
