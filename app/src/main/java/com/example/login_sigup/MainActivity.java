package com.example.login_sigup;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.login_sigup.Transaction.TransactionFragment;
import com.example.login_sigup.account.AccountFragment;
import com.example.login_sigup.budget.BudgetFragment;
import com.example.login_sigup.database.User.User;
import com.example.login_sigup.database.User.UserHandle;
import com.example.login_sigup.home.HomeFragment;
import com.example.login_sigup.home.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    UserHandle userHandle;

    private static final String KEY_IS_IN_SETTINGS = "is_in_settings";
    private boolean isInSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Áp dụng Dark Mode (Giữ nguyên như trước)
        SharedPreferences prefs = getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        userHandle = new UserHandle(this);

        // 2. KIỂM TRA TRẠNG THÁI KHI RECREATE
        if (savedInstanceState != null) {
            isInSettings = savedInstanceState.getBoolean(KEY_IS_IN_SETTINGS, false);
        }

        if (savedInstanceState == null) {
            showMainLayout(); // Lần đầu mở app
        } else if (isInSettings) {
            // Nếu đang ở Setting khi nhấn Switch, nạp lại Menu và nhảy thẳng vào SettingFragment
            showMenuLayout();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new com.example.login_sigup.account.SettingFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
            showMenuLayout();
        }
    }

    // 3. LƯU TRẠNG THÁI TRƯỚC KHI RECREATE
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Kiểm tra xem Fragment hiện tại có phải là SettingFragment không
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof com.example.login_sigup.account.SettingFragment) {
            outState.putBoolean(KEY_IS_IN_SETTINGS, true);
        } else {
            outState.putBoolean(KEY_IS_IN_SETTINGS, false);
        }
    }

    // ================= WELCOME =================
    private void showMainLayout() {
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabIndicator);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setCustomView(
                        LayoutInflater.from(this)
                                .inflate(R.layout.item_tab_dot, null)
                )
        ).attach();

        Button btnSignup = findViewById(R.id.sign_up);
        btnSignup.setOnClickListener(v -> showAuthLayout(R.layout.signup));

        Button btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(v -> showAuthLayout(R.layout.login));
    }

    // ================= AUTH (LOGIN / SIGNUP) =================
    private void showAuthLayout(int layoutResId) {
        setContentView(layoutResId);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> showMainLayout());
        }

        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPassword = findViewById(R.id.edtPassword);
        ImageView btnShowPassword = findViewById(R.id.btnShowPassword);

        // SHOW / HIDE PASSWORD
        if (btnShowPassword != null && edtPassword != null) {
            final boolean[] isShow = {false};
            btnShowPassword.setOnClickListener(v -> {
                if (isShow[0]) {
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
                isShow[0] = !isShow[0];
            });
        }

        // ===== SIGN UP =====
        Button btnSignup = findViewById(R.id.btnSignup);
        if (btnSignup != null) {
            btnSignup.setOnClickListener(v -> {
                String email = edtEmail.getText().toString().trim();
                String pass = edtPassword.getText().toString().trim();

                if (email.isEmpty() || pass.isEmpty()) {
                    toast("Vui lòng nhập đầy đủ thông tin");
                    return;
                }

                if (userHandle.handleCheckEmailExists(email)) {
                    toast("Email đã tồn tại");
                    return;
                }

                User user = new User(
                        "User",
                        email,
                        pass,
                        "01/01/2000",
                        "Unknown",
                        "Vietnam",
                        "",
                        ""
                );

                if (userHandle.handleSignUp(user)) {
                    toast("Đăng ký thành công");
                    showAuthLayout(R.layout.login);
                } else {
                    toast("Đăng ký thất bại");
                }
            });
        }

        // ===== LOGIN =====
        Button btnLogin = findViewById(R.id.btnLogin);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                String email = edtEmail.getText().toString().trim();
                String pass = edtPassword.getText().toString().trim();

                if (email.isEmpty() || pass.isEmpty()) {
                    toast("Vui lòng nhập đầy đủ thông tin");
                    return;
                }

                User user = userHandle.handleLogin(email, pass);
                if (user != null) {
                    toast("Đăng nhập thành công");
                    showMenuLayout(); // ✅ CHỈ VÀO HOME Ở ĐÂY
                } else {
                    toast("Sai email hoặc mật khẩu");
                }
            });
        }

        // SWITCH LOGIN <-> SIGNUP
        TextView tvSignup = findViewById(R.id.tv_Signup);
        if (tvSignup != null) {
            tvSignup.setOnClickListener(v -> showAuthLayout(R.layout.signup));
        }

        TextView tvLogin = findViewById(R.id.tv_login);
        if (tvLogin != null) {
            tvLogin.setOnClickListener(v -> showAuthLayout(R.layout.login));
        }
    }

    // ================= HOME / MENU =================
    private void showMenuLayout() {
        setContentView(R.layout.menu);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) selected = new HomeFragment();
            else if (id == R.id.nav_transaction) selected = new TransactionFragment();
            else if (id == R.id.nav_budget) selected = new BudgetFragment();
            else if (id == R.id.nav_account) selected = new AccountFragment();

            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selected)
                        .commit();
            }
            return true;
        });

        FloatingActionButton fab = findViewById(R.id.btnAddTransaction);
        fab.setOnClickListener(v -> showAddTransactionSheet());
    }

    // ================= BOTTOM SHEET =================
    private void showAddTransactionSheet() {
        View view = getLayoutInflater().inflate(R.layout.plus, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        Button btnSave = view.findViewById(R.id.btnSave);
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                // TODO: xử lý lưu transaction ở đây nếu muốn
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
