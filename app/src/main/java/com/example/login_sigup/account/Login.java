package com.example.login_sigup.account;

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

import com.example.login_sigup.R;
import com.example.login_sigup.database.User.User;
import com.example.login_sigup.database.User.UserHandle;

public class Login extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    ImageButton btnBack;
    ImageView btnShowPassword;
    TextView tvSignup;

    UserHandle userHandle;
    boolean isShowPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); // XML login

        // ===== ÁNH XẠ =====
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);
        btnShowPassword = findViewById(R.id.btnShowPassword);
        tvSignup = findViewById(R.id.tv_Signup);

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

        // ===== CHUYỂN SIGNUP =====
        tvSignup.setOnClickListener(v ->
                startActivity(new Intent(Login.this, SignUp.class))
        );

        // ===== LOGIN =====
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = userHandle.handleLogin(email, password);
            if (user != null) {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(this, MainActivity.class));
            } else {
                Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
