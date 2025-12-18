package com.example.login_sigup.Transaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login_sigup.R;
import com.example.login_sigup.database.transaction.Transaction;
import com.example.login_sigup.database.transaction.TransactionHandle;
import com.example.login_sigup.group.GroupFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    EditText etAmount;
    TextView tvGroup, etNote, tvDate;
    Button btnSave;

    Long selectedCategoryId = null;
    String selectedDate;

    TransactionHandle transactionHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plus);

        etAmount = findViewById(R.id.etAmount);
        tvGroup  = findViewById(R.id.tvGroup);
        etNote   = findViewById(R.id.etNote);
        tvDate   = findViewById(R.id.tvDate);
        btnSave  = findViewById(R.id.btnSave);

        transactionHandle = new TransactionHandle(this);

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        tvDate.setText(selectedDate);

        // ✅ BẤM CẢ DÒNG "CHỌN NHÓM"
        findViewById(R.id.layoutGroup).setOnClickListener(v -> openGroupFragment());

        btnSave.setOnClickListener(v -> saveTransaction());
    }

    // ================= OPEN GROUP FRAGMENT =================
    private void openGroupFragment() {
        View container = findViewById(R.id.fragment_container);
        container.setVisibility(View.VISIBLE);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, new GroupFragment())
                .addToBackStack(null)
                .commit();
    }

    // ================= BACK HANDLING =================
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            findViewById(R.id.fragment_container).setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    // ================= SAVE TRANSACTION =================
    private void saveTransaction() {

        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            etAmount.setError("Nhập số tiền");
            return;
        }

        if (selectedCategoryId == null) {
            Toast.makeText(this, "Chọn nhóm", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String note = etNote.getText().toString();

        Transaction t = new Transaction(
                1L,
                selectedCategoryId,
                amount,
                note,
                selectedDate,
                "EXPENSE"
        );

        long result = transactionHandle.insert(t);

        if (result > 0) {
            Toast.makeText(this, "Đã lưu", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
