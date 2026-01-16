package com.example.cashmate.account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import com.example.cashmate.MainActivity;
import com.example.cashmate.R;
import com.example.cashmate.database.User.UserHandle;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.exceptions.ClearCredentialException;

public class SettingFragment extends Fragment {

    private Switch mode_switch;
    private TextView modeStatus;

    private FirebaseAuth mAuth;
    private UserHandle userHandle;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.setting, container, false);

        mAuth = FirebaseAuth.getInstance();
        userHandle = new UserHandle(requireContext());

        // BACK
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // SIGN OUT
        Button btnSignout = view.findViewById(R.id.btnSignout);
        btnSignout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            CredentialManager credentialManager =
                    CredentialManager.create(requireContext());

            ClearCredentialStateRequest request =
                    new ClearCredentialStateRequest();

            credentialManager.clearCredentialStateAsync(
                    request,
                    null,
                    ContextCompat.getMainExecutor(requireContext()),
                    new CredentialManagerCallback<Void, ClearCredentialException>() {
                        @Override
                        public void onResult(Void result) {}

                        @Override
                        public void onError(ClearCredentialException e) {
                            e.printStackTrace();
                        }
                    }
            );

            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // DARK MODE
        mode_switch = view.findViewById(R.id.mode_switch);
        modeStatus = view.findViewById(R.id.dark_mode);

        mode_switch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {
                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_YES
                        );
                    } else {
                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_NO
                        );
                    }
                }
        );

        // DELETE ACCOUNT
        LinearLayout btnDelAcc = view.findViewById(R.id.btndelAcc);
        btnDelAcc.setOnClickListener(v -> showDeleteAccountDialog());

        return view;
    }


    private void showDeleteAccountDialog() {

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_del_account);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        EditText edtPassword = dialog.findViewById(R.id.edtConfirmPassword);
        TextView tvVerify = dialog.findViewById(R.id.tvVerify);
        Button btnDelete = dialog.findViewById(R.id.btnDeleteAccount);

        final boolean[] isVerifiedForDelete = {false};

        // SEND VERIFY EMAIL
        tvVerify.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) return;

            user.sendEmailVerification()
                    .addOnSuccessListener(unused -> {
                        isVerifiedForDelete[0] = true;
                        Toast.makeText(requireContext(),"Đã gửi email xác thực yêu cầu xóa tài khoản",Toast.LENGTH_LONG).show();
                    });
        });

        // CLICK DELETE
        btnDelete.setOnClickListener(v -> {

            if (!isVerifiedForDelete[0]) {
                Toast.makeText(requireContext(),"Vui lòng xác thực email trước khi xóa tài khoản",Toast.LENGTH_SHORT).show();
                return;
            }

            String password = edtPassword.getText().toString().trim();
            if (password.isEmpty()) {
                Toast.makeText(requireContext(),"Vui lòng nhập mật khẩu",Toast.LENGTH_SHORT).show();
                return;
            }

            showConfirmDeleteDialog(password, dialog);
        });

        dialog.show();
    }

    // CONFIRM DIALOG
    private void showConfirmDeleteDialog(String password, Dialog parentDialog) {

        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa tài khoản")
                .setMessage(
                        "Tài khoản sẽ bị xóa vĩnh viễn.\n" +
                                "Mọi dữ liệu sẽ không thể khôi phục.\n\n" +
                                "Bạn có chắc chắn muốn tiếp tục?"
                )
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteAccount(password);
                    parentDialog.dismiss();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    // DELETE FIREBASE + LOCAL
    private void deleteAccount(String password) {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String email = user.getEmail();

        AuthCredential credential =
                EmailAuthProvider.getCredential(email, password);

        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> {

                    user.delete()
                            .addOnSuccessListener(unused2 -> {

                                userHandle.deleteUserByEmail(email);
                                FirebaseAuth.getInstance().signOut();

                                Toast.makeText(
                                        requireContext(),
                                        "Đã xóa tài khoản thành công",
                                        Toast.LENGTH_LONG
                                ).show();

                                Intent intent =
                                        new Intent(requireContext(), MainActivity.class);
                                intent.setFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                                );
                                startActivity(intent);
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                requireContext(),
                                "Mật khẩu không đúng",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}
