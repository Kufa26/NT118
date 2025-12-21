package com.example.cashmate.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cashmate.R;
import com.example.cashmate.database.User.User;
import com.example.cashmate.database.User.UserHandle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnSignup;
    TextView txVerify;
    ImageButton btnBack;
    ImageView btnShowPassword;

    FirebaseAuth mAuth;
    UserHandle userHandle;

    boolean isShowPassword = false;
    boolean isVerificationSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignup = findViewById(R.id.btnSignup);
        txVerify = findViewById(R.id.tvVerify);
        btnBack = findViewById(R.id.btnBack);
        btnShowPassword = findViewById(R.id.btnShowPassword);

        mAuth = FirebaseAuth.getInstance();
        userHandle = new UserHandle(this);

        btnBack.setOnClickListener(v -> finish());

        btnShowPassword.setOnClickListener(v -> {
            if (isShowPassword) {
                edtPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                btnShowPassword.setImageResource(R.drawable.ic_eye_close);
            } else {
                edtPassword.setInputType(
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                btnShowPassword.setImageResource(R.drawable.ic_eye_open);
            }
            edtPassword.setSelection(edtPassword.getText().length());
            isShowPassword = !isShowPassword;
        });

        // ===== GỬI EMAIL XÁC THỰC =====
        txVerify.setOnClickListener(v -> sendVerifyEmail());

        // ===== ĐĂNG KÝ =====
        btnSignup.setOnClickListener(v -> registerIfVerified());

        TextView tvLogin = findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(SignUp.this, Login.class))
        );
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 6) return false;

        boolean hasUppercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        return hasUppercase && hasDigit && hasSpecial;
    }


    // ================= SEND VERIFY EMAIL =================
    private void sendVerifyEmail() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(
                    this,
                    "Mật khẩu phải ≥ 6 ký tự, có chữ hoa, chữ số và ký tự đặc biệt",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }


        if (userHandle.handleCheckEmailExists(email)) {
            Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user == null) return;

                    user.sendEmailVerification()
                            .addOnSuccessListener(unused -> {
                                isVerificationSent = true;
                                Toast.makeText(
                                        this,
                                        "Đã gửi email xác thực. Vui lòng kiểm tra Gmail",
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Lỗi: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // ================= REGISTER IF VERIFIED =================
    private void registerIfVerified() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null || !isVerificationSent) {
            Toast.makeText(this, "Vui lòng xác thực email để đăng ký", Toast.LENGTH_SHORT).show();
            return;
        }

        user.reload().addOnSuccessListener(unused -> {
            if (!user.isEmailVerified()) {
                Toast.makeText(this, "Vui lòng xác thực email để đăng ký", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ LƯU LOCAL VỚI UID + PASSWORD
            User localUser = new User(
                    user.getUid(),
                    "User",
                    user.getEmail(),
                    edtPassword.getText().toString(),
                    "01/01/2000",
                    "Unknown",
                    "Vietnam",
                    "",
                    ""
            );

            userHandle.handleSignUp(localUser);

            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

}
