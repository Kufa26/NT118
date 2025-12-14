package com.example.login_sigup;

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

import com.example.login_sigup.database.User.User;
import com.example.login_sigup.database.User.UserHandle;

public class SignUp extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnSignup;
    ImageButton btnBack;
    ImageView btnShowPassword;
    TextView tvLogin;

    UserHandle userHandle;
    boolean isShowPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup); // XML signup

        // ===== ÁNH XẠ =====
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnBack = findViewById(R.id.btnBack);
        btnShowPassword = findViewById(R.id.btnShowPassword);
        tvLogin = findViewById(R.id.tv_login);

        userHandle = new UserHandle(this);

        // ===== BACK =====
        btnBack.setOnClickListener(v -> finish());

        // ===== SHOW / HIDE PASSWORD =====
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

        // ===== CHUYỂN LOGIN =====
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUp.this, Login.class));
            finish();
        });

        // ===== SIGN UP =====
        btnSignup.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userHandle.handleCheckEmailExists(email)) {
                Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(
                    "User",
                    email,
                    password,
                    "01/01/2000",
                    "Unknown",
                    "Vietnam",
                    "",
                    ""
            );

            if (userHandle.handleSignUp(user)) {
                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
