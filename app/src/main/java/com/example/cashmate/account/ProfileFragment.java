package com.example.cashmate.account;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashmate.R;
import com.example.cashmate.database.User.User;
import com.example.cashmate.database.User.UserHandle;

public class ProfileFragment extends Fragment {

    private TextView txtFullName, txtDob, txtCountry, txtPhone, txtEmail;
    private RadioButton rbMale, rbFemale;
    private UserHandle userHandle;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.profile, container, false);

        userHandle = new UserHandle(requireContext());

        txtFullName = view.findViewById(R.id.txtFullName);
        txtDob = view.findViewById(R.id.txtDob);
        txtCountry = view.findViewById(R.id.txtCountry);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtEmail = view.findViewById(R.id.txtEmail);
        rbMale = view.findViewById(R.id.rbMale);
        rbFemale = view.findViewById(R.id.rbFemale);

        // BACK
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        Button btnEdit = view.findViewById(R.id.edit);
        btnEdit.setOnClickListener(v -> showEditInfoDialog());

        Button changepass = view.findViewById(R.id.changepass);
        changepass.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ChangePasswordFragment())
                        .addToBackStack(null)
                        .commit()
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
    }

    // LOAD USER INFO
    private void loadUserInfo() {
        String email = requireContext()
                .getSharedPreferences("USER_SESSION", 0)
                .getString("email", null);

        if (email == null) return;

        User user = userHandle.getUserByEmail(email);
        if (user == null) return;

        txtFullName.setText(
                user.getFullName() == null || user.getFullName().isEmpty()
                        ? "Chưa có thông tin"
                        : user.getFullName()
        );

        txtDob.setText(
                user.getDob() == null || user.getDob().isEmpty()
                        ? "Chưa có thông tin"
                        : user.getDob()
        );

        txtCountry.setText(
                user.getCountry() == null || user.getCountry().isEmpty()
                        ? "Chưa có thông tin"
                        : user.getCountry()
        );

        txtPhone.setText(
                user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()
                        ? "Chưa có thông tin"
                        : user.getPhoneNumber()
        );

        txtEmail.setText(user.getEmail());

        rbMale.setChecked(false);
        rbFemale.setChecked(false);

        if ("Male".equalsIgnoreCase(user.getGender())) {
            rbMale.setChecked(true);
        } else if ("Female".equalsIgnoreCase(user.getGender())) {
            rbFemale.setChecked(true);
        }

        // read-only
        rbMale.setClickable(false);
        rbFemale.setClickable(false);
    }

    // DIALOG SỬA THÔNG TIN
    private void showEditInfoDialog() {

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.add_info);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        EditText edtFullName = dialog.findViewById(R.id.edtFullName);
        EditText edtDob = dialog.findViewById(R.id.edtDob);
        EditText edtCountry = dialog.findViewById(R.id.edtCountry);
        EditText edtPhone = dialog.findViewById(R.id.edtPhone);
        EditText edtEmail = dialog.findViewById(R.id.edtEmail);
        RadioButton rbMaleDialog = dialog.findViewById(R.id.rbMale);
        RadioButton rbFemaleDialog = dialog.findViewById(R.id.rbFemale);
        Button btnSave = dialog.findViewById(R.id.btnSaveProfile);

        String email = requireContext().getSharedPreferences("USER_SESSION", 0).getString("email", null);

        if (email != null) {
            User user = userHandle.getUserByEmail(email);
            if (user != null) {
                edtFullName.setText(user.getFullName());
                edtDob.setText(user.getDob());
                edtCountry.setText(user.getCountry());
                edtPhone.setText(user.getPhoneNumber());
                edtEmail.setText(user.getEmail());

                if ("Male".equalsIgnoreCase(user.getGender())) {
                    rbMaleDialog.setChecked(true);
                } else if ("Female".equalsIgnoreCase(user.getGender())) {
                    rbFemaleDialog.setChecked(true);
                }
            }
        }

        btnSave.setOnClickListener(v -> {

            if (email == null) return;

            String gender = rbMaleDialog.isChecked() ? "Male"
                    : rbFemaleDialog.isChecked() ? "Female" : "";

            userHandle.updateUserInfo(
                    email,
                    edtFullName.getText().toString().trim(),
                    edtDob.getText().toString().trim(),
                    gender,
                    edtCountry.getText().toString().trim(),
                    edtPhone.getText().toString().trim()
            );

            Toast.makeText(
                    requireContext(),
                    "Cập nhật thông tin thành công",
                    Toast.LENGTH_SHORT
            ).show();

            dialog.dismiss();
            loadUserInfo(); // refresh ngay
        });

        dialog.show();
    }
}
