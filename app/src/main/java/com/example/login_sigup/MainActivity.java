package com.example.login_sigup;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        showMainLayout();
    }
    private void showMainLayout() {
        setContentView(R.layout.activity_main);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabIndicator);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            View tabView = LayoutInflater.from(this).inflate(R.layout.item_tab_dot, null);
            tab.setCustomView(tabView);
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    View tabView = tabLayout.getTabAt(i).getCustomView();
                    if (tabView instanceof ImageView) {
                        ((ImageView) tabView).setImageResource(
                                i == position ? R.drawable.dot_active : R.drawable.dot_inactive
                        );
                    }
                }
            }
        });

        Button btnSignUp = findViewById(R.id.sign_up);
        btnSignUp.setOnClickListener(v -> showAuthLayout(R.layout.signup));

//        Button btnLogin = findViewById(R.id.login);
//        btnLogin.setOnClickListener(v -> showAuthLayout(R.layout.login));

        Button btnTestMenu = findViewById(R.id.login);
        if (btnTestMenu != null) {
            btnTestMenu.setOnClickListener(v -> showMenuLayout());
        }
    }

    public void showAuthLayout(int layoutResId) {
        setContentView(layoutResId);
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> showMainLayout());
        }

        TextView btnSignup = findViewById(R.id.tv_Signup);
        if (btnSignup != null) {
            btnSignup.setOnClickListener(v -> showAuthLayout(R.layout.signup));
        }

        TextView btnLogin = findViewById(R.id.tv_login);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> showAuthLayout(R.layout.login));
        }

        EditText etPassword = findViewById(R.id.edtPassword);
        ImageView btnShowPassword = findViewById(R.id.btnShowPassword);

        if (etPassword != null && btnShowPassword != null) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnShowPassword.setImageResource(R.drawable.ic_eye_close);

            final boolean[] isPasswordVisible = {false};

            btnShowPassword.setOnClickListener(v -> {
                if (isPasswordVisible[0]) {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnShowPassword.setImageResource(R.drawable.ic_eye_close);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnShowPassword.setImageResource(R.drawable.ic_eye_open);
                }
                etPassword.setSelection(etPassword.getText().length());
                isPasswordVisible[0] = !isPasswordVisible[0];
            });
        }
    }

    private void showAddTransactionSheet() {
        View view = getLayoutInflater().inflate(R.layout.plus, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(R.drawable.bg_bottomsheet_rounded);

            bottomSheet.post(() -> {
                com.google.android.material.bottomsheet.BottomSheetBehavior<View> behavior =
                        com.google.android.material.bottomsheet.BottomSheetBehavior.from(bottomSheet);

                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                int targetHeight = (int) (screenHeight * 0.9);
                bottomSheet.getLayoutParams().height = targetHeight;
                bottomSheet.requestLayout();

                behavior.setPeekHeight(targetHeight);
                behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED);
                behavior.setSkipCollapsed(false);
            });
        }

        Button btnSave = view.findViewById(R.id.btnSave);
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> dialog.dismiss());
        }

        Button btnShowDetails = view.findViewById(R.id.tvAddDetail);
        ScrollView scrollDetails = view.findViewById(R.id.scrollDetails);

        if (btnShowDetails != null && scrollDetails != null) {
            scrollDetails.setVisibility(View.GONE);

            Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

            final boolean[] isVisible = {false};

            btnShowDetails.setOnClickListener(v -> {
                if (isVisible[0]) {
                    scrollDetails.startAnimation(slideUp);
                    scrollDetails.setVisibility(View.GONE);
                } else {
                    scrollDetails.setVisibility(View.VISIBLE);
                    scrollDetails.startAnimation(slideDown);
                }
                isVisible[0] = !isVisible[0];
            });
        }

        dialog.show();
    }
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

        FloatingActionButton btnAddTransaction = findViewById(R.id.btnAddTransaction);
        if (btnAddTransaction != null) {
            btnAddTransaction.setOnClickListener(v -> showAddTransactionSheet());
        }
    }
}
