package com.example.cashmate;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cashmate.account.AccountFragment;
import com.example.cashmate.account.Login;
import com.example.cashmate.account.SignUp;
import com.example.cashmate.budget.BudgetFragment;
import com.example.cashmate.budget.BudgetStorage;
import com.example.cashmate.database.User.UserHandle;
import com.example.cashmate.Transaction.TransactionFragment;
import com.example.cashmate.home.HomeFragment;
import com.example.cashmate.home.ViewPagerAdapter;
import com.example.cashmate.plus.PlusFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class MainActivity extends AppCompatActivity {

    UserHandle userHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        userHandle = new UserHandle(this);
        BudgetStorage.getInstance().init(this);
        syncFirebaseUserToLocal();
        boolean openMenu = getIntent().getBooleanExtra("open_menu", false);
        if (openMenu) {
            showMenuLayout();
        } else {
            showMainLayout();
        }
    }

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
        btnSignup.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUp.class));
        });

        Button btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Login.class));
        });
    }

    private void showMenuLayout() {
        setContentView(R.layout.menu); // ⚠️ PHẢI LÀ menu.xml

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Fragment mặc định
        getSupportFragmentManager()
                .beginTransaction()
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
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selected)
                        .commit();
            }
            return true;
        });

        // FAB → PlusFragment (KHÔNG BottomSheet)
        FloatingActionButton fab = findViewById(R.id.btnAddTransaction);
        fab.setOnClickListener(v ->
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new PlusFragment())
                        .addToBackStack("plus")
                        .commit()
        );
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

    private void syncFirebaseUserToLocal() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        UserHandle userHandle = new UserHandle(this);

        String uid = firebaseUser.getUid();

        // Nếu local đã có → thôi
        if (userHandle.isUserExistsById(uid)) return;

        String fullName = firebaseUser.getDisplayName() != null
                ? firebaseUser.getDisplayName()
                : "User";

        String email = firebaseUser.getEmail();

        String avatarUrl = firebaseUser.getPhotoUrl() != null
                ? firebaseUser.getPhotoUrl().toString()
                : "";

        // Phân biệt provider
        String password = "google";
        for (UserInfo info : firebaseUser.getProviderData()) {
            if ("password".equals(info.getProviderId())) {
                password = ""; // hoặc mật khẩu nếu bạn đang giữ trong session
            }
        }

        userHandle.insertUserFromFirebase(
                uid,
                fullName,
                email,
                password,
                avatarUrl
        );
    }


}
