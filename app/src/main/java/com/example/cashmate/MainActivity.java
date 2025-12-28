package com.example.cashmate;

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
import com.example.cashmate.database.Category.CategoryHandle;
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
    private com.google.android.material.bottomnavigation.BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // ================= LOAD CATEGORY DEFAULT (RUN 1 TIME) =================
        CategoryHandle categoryHandle = new CategoryHandle(this);
        categoryHandle.insertDefaultCategoriesIfEmpty();

        // ================= INIT LOCAL STORAGE =================
        userHandle = new UserHandle(this);
        BudgetStorage.getInstance().init(this);

        // ================= SYNC FIREBASE USER =================
        syncFirebaseUserToLocal();

        boolean openMenu = getIntent().getBooleanExtra("open_menu", false);
        if (openMenu) {
            showMenuLayout();
        } else {
            showMainLayout();
        }
    }

    // ================= AUTH / INTRO LAYOUT =================
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
        btnSignup.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SignUp.class))
        );

        Button btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, Login.class))
        );
    }

    // ================= MAIN MENU LAYOUT =================
    private void showMenuLayout() {
        setContentView(R.layout.menu);

        bottomNav = findViewById(R.id.bottom_nav);

        // Fragment máº·c Ä‘á»‹nh
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

        // FAB â†’ PlusFragment
        FloatingActionButton fab = findViewById(R.id.btnAddTransaction);
        fab.setOnClickListener(v ->
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new PlusFragment())
                        .addToBackStack("plus")
                        .commit()
        );
    }

    // ================= NAV HELPER =================
    public void selectBottomTab(int menuItemId) {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(menuItemId);
        }
    }
    // ================= BOTTOM SHEET (OPTIONAL) =================
    private void showAddTransactionSheet() {
        View view = getLayoutInflater().inflate(R.layout.plus, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        Button btnSave = view.findViewById(R.id.btnSave);
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    // ================= UTIL =================
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // ================= FIREBASE â†’ LOCAL USER =================
    private void syncFirebaseUserToLocal() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        UserHandle userHandle = new UserHandle(this);
        String uid = firebaseUser.getUid();

        // Náº¿u local Ä‘Ã£ cÃ³ â†’ khÃ´ng insert
        if (userHandle.isUserExistsById(uid)) return;

        String fullName = firebaseUser.getDisplayName() != null
                ? firebaseUser.getDisplayName()
                : "User";

        String email = firebaseUser.getEmail();

        String avatarUrl = firebaseUser.getPhotoUrl() != null
                ? firebaseUser.getPhotoUrl().toString()
                : "";

        String password = "google";
        for (UserInfo info : firebaseUser.getProviderData()) {
            if ("password".equals(info.getProviderId())) {
                password = "";
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
