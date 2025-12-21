package com.example.cashmate.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashmate.R;
import com.example.cashmate.database.User.User;
import com.example.cashmate.database.User.UserHandle;

public class ProfileFragment extends Fragment {

    private TextView txtFullName, txtDob, txtCountry, txtPhone, txtEmail;
    private RadioButton rbMale, rbFemale;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.profile, container, false);

        txtFullName = view.findViewById(R.id.txtFullName);
        txtDob = view.findViewById(R.id.txtDob);
        txtCountry = view.findViewById(R.id.txtCountry);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtEmail = view.findViewById(R.id.txtEmail);
        rbMale = view.findViewById(R.id.rbMale);
        rbFemale = view.findViewById(R.id.rbFemale);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

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

    private void loadUserInfo() {
        UserHandle userHandle = new UserHandle(requireContext());
        User user = userHandle.getCurrentUser();

        if (user == null) {
            txtFullName.setText("Chưa có thông tin");
            txtDob.setText("Chưa có thông tin");
            txtCountry.setText("Chưa có thông tin");
            txtPhone.setText("Chưa có thông tin");
            txtEmail.setText("Chưa có thông tin");
            rbMale.setChecked(false);
            rbFemale.setChecked(false);
            return;
        }

        txtFullName.setText(valueOrDefault(user.getFullName()));
        txtDob.setText(valueOrDefault(user.getDob()));
        txtCountry.setText(valueOrDefault(user.getCountry()));
        txtPhone.setText(valueOrDefault(user.getPhoneNumber()));
        txtEmail.setText(valueOrDefault(user.getEmail()));

        rbMale.setChecked(false);
        rbFemale.setChecked(false);
        if ("Nam".equalsIgnoreCase(user.getGender())) {
            rbMale.setChecked(true);
        } else if ("Nữ".equalsIgnoreCase(user.getGender())) {
            rbFemale.setChecked(true);
        }
    }

    private String valueOrDefault(String value) {
        return (value == null || value.trim().isEmpty())
                ? "Chưa có thông tin"
                : value;
    }
}
