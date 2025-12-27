package com.example.cashmate.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cashmate.R;
import com.example.cashmate.database.User.UserHandle;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    private EditText edtOldPass, edtNewPass, edtConfirmPass;
    private Button btnChange;
    private TextView Verify;

    private FirebaseUser user;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.change_password, container, false);

        // Back
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // Init
        edtOldPass = view.findViewById(R.id.o_pass);
        edtNewPass = view.findViewById(R.id.n_pass);
        edtConfirmPass = view.findViewById(R.id.confirm_pass);
        Verify = view.findViewById(R.id.tvVerify);
        btnChange = view.findViewById(R.id.btnChangepass);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Xác thực (re-auth + gửi mail)
        Verify.setOnClickListener(v -> verifyOldPasswordAndSendMail());

        // Đổi mật khẩu
        btnChange.setOnClickListener(v -> changePassword());

        return view;
    }


    // Re-auth + gửi mail

    private void verifyOldPasswordAndSendMail() {
        String oPass = edtOldPass.getText().toString().trim();

        if (oPass.isEmpty()) {
            toast("Vui lòng nhập mật khẩu cũ");
            return;
        }

        AuthCredential credential =
                EmailAuthProvider.getCredential(user.getEmail(), oPass);

        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> {
                    // Gửi mail xác thực
                    user.sendEmailVerification()
                            .addOnSuccessListener(unused2 ->
                                    toast("Đã gửi email xác thực, vui lòng kiểm tra email")
                            )
                            .addOnFailureListener(e ->
                                    toast("Gửi email xác thực thất bại")
                            );
                })
                .addOnFailureListener(e ->
                        toast("Mật khẩu cũ không đúng")
                );
    }


    // Đổi mật khẩu

    private void changePassword() {
        String newPass = edtNewPass.getText().toString().trim();
        String confirmPass = edtConfirmPass.getText().toString().trim();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            toast("Vui lòng nhập đầy đủ mật khẩu mới");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            toast("Mật khẩu mới không khớp");
            return;
        }

        user.reload().addOnSuccessListener(unused -> {
            if (!user.isEmailVerified()) {
                toast("Bạn chưa xác thực email");
                return;
            }

            // 🔥 Update Firebase
            user.updatePassword(newPass)
                    .addOnSuccessListener(unused2 -> {
                        updateLocalPassword(newPass);
                        toast("Đổi mật khẩu thành công");
                        requireActivity()
                                .getSupportFragmentManager()
                                .popBackStack();
                    })
                    .addOnFailureListener(e ->
                            toast("Đổi mật khẩu Firebase thất bại")
                    );
        });
    }


    //  Update local DB (PLAIN TEXT)

    private void updateLocalPassword(String newPass) {
        UserHandle db = new UserHandle(requireContext());
        db.updatePassword(user.getUid(), newPass); // plain text
    }

    private void toast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
