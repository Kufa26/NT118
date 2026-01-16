package com.example.cashmate.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashmate.R;
import com.example.cashmate.database.Category.Category;
import com.example.cashmate.database.Category.CategoryHandle;
import com.example.cashmate.group.icon.ChooseIconFragment;

public class AddGroupFragment extends Fragment {

    private ImageView imgGroupIcon;
    private EditText edtGroupName;
    private RadioButton rbExpense, rbIncome;
    private Button btnSave;

    // icon mặc định
    private String selectedIcon = "ic_food";

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.add_group, container, false);

        imgGroupIcon = view.findViewById(R.id.imgGroupIcon);
        edtGroupName = view.findViewById(R.id.edtGroupName);
        rbExpense = view.findViewById(R.id.rbExpense);
        rbIncome = view.findViewById(R.id.rbIncome);
        btnSave = view.findViewById(R.id.btnSave);
        ImageButton btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // CHỌN ICON
        imgGroupIcon.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ChooseIconFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // NHẬN ICON
        requireActivity()
                .getSupportFragmentManager()
                .setFragmentResultListener(
                        "choose_icon",
                        this,
                        (key, bundle) -> {
                            selectedIcon = bundle.getString("icon");

                            int resId = getResources().getIdentifier(
                                    selectedIcon,
                                    "drawable",
                                    requireContext().getPackageName()
                            );

                            imgGroupIcon.setImageResource(resId);
                        }
                );

        btnSave.setOnClickListener(v -> saveCategory());

        return view;
    }

    private void saveCategory() {
        String name = edtGroupName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập tên nhóm", Toast.LENGTH_SHORT).show();
            return;
        }

        String type;
        if (rbIncome.isChecked()) {
            type = "INCOME";
        } else if (rbExpense.isChecked()) {
            type = "EXPENSE";
        } else {
            Toast.makeText(getContext(), "Chọn loại nhóm", Toast.LENGTH_SHORT).show();
            return;
        }

        Category category = new Category(name, type, selectedIcon);
        CategoryHandle handle = new CategoryHandle(requireContext());
        handle.insertCategory(category);

        Toast.makeText(getContext(), "Đã thêm nhóm", Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
