package com.example.cashmate.group.icon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashmate.R;

public class ChooseIconFragment extends Fragment {

    private final int[] ICONS = new int[]{
            R.drawable.ic_bills,
            R.drawable.ic_debt_collection,
            R.drawable.ic_debt_repayment,
            R.drawable.ic_education,
            R.drawable.ic_electric,
            R.drawable.ic_entertainment,
            R.drawable.ic_food,
            R.drawable.ic_fuel,
            R.drawable.ic_garden,
            R.drawable.ic_health,
            R.drawable.ic_insurance,
            R.drawable.ic_internet,
            R.drawable.ic_jewelry,
            R.drawable.ic_maintenance,
            R.drawable.ic_money,
            R.drawable.ic_move,
            R.drawable.ic_other_expenses,
            R.drawable.ic_personal_items,
            R.drawable.ic_pets,
            R.drawable.ic_phone,
            R.drawable.ic_piggybank,
            R.drawable.ic_salary,
            R.drawable.ic_savecash,
            R.drawable.ic_shopping,
            R.drawable.ic_sports,
            R.drawable.ic_tax,
            R.drawable.ic_tool,
            R.drawable.ic_wallet,
            R.drawable.ic_water,
            R.drawable.ic_work,
            R.drawable.ic_medicine,
            R.drawable.ic_money_transferred,
            R.drawable.ic_travel,
            R.drawable.ic_saving,
            R.drawable.ic_house,
            R.drawable.ic_appliance,
            R.drawable.ic_cosmetics,
            R.drawable.ic_game,
            R.drawable.ic_income1,
            R.drawable.ic_money_received,
            R.drawable.ic_loan,
            R.drawable.ic_payment
    };

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.choose_icon, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        RecyclerView rvIcons = view.findViewById(R.id.rvIcons);
        rvIcons.setLayoutManager(new GridLayoutManager(getContext(), 5));

        rvIcons.setAdapter(new IconAdapter(ICONS, iconRes -> {

            // ✅ LẤY TÊN ICON (ic_food, ic_wallet...)
            String iconName = getResources().getResourceEntryName(iconRes);

            Bundle result = new Bundle();
            result.putString("icon", iconName);

            requireActivity()
                    .getSupportFragmentManager()
                    .setFragmentResult("choose_icon", result);

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        }));

        return view;
    }
}
