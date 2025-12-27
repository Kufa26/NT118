package com.example.cashmate.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashmate.R;
import com.example.cashmate.group.chooseGroup.Group;
import com.google.android.material.bottomsheet.BottomSheetDialog;

// Import database
import com.example.cashmate.database.budget.Budget;
import com.example.cashmate.database.budget.BudgetHandle;
import com.example.cashmate.database.User.UserHandle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Add_Budget extends Fragment {

    private TextView tvWallet, tvDate;
    private ImageView imgGroupIcon;
    private EditText etAmount;

    private String tempSelectedDate = "";
    private int currentIconRes = R.drawable.ic_food;
    private int selectedCategoryId = 0; // Mặc định 0

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", new Locale("vi", "VN"));
    private SimpleDateFormat sdfFull = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("select_group", this, (requestKey, bundle) -> {
            String name = bundle.getString("groupName");
            int iconRes = bundle.getInt("groupIcon");
            long idLong = bundle.getLong("idCategory", 0);

            this.selectedCategoryId = (int) idLong;

            if (tvWallet != null) tvWallet.setText(name);
            if (imgGroupIcon != null) {
                imgGroupIcon.setImageResource(iconRes);
            }
            this.currentIconRes = iconRes;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        View view = inflater.inflate(R.layout.create_a_budget, container, false);

        tvWallet = view.findViewById(R.id.tvWallet);
        imgGroupIcon = view.findViewById(R.id.imgGroupIcon);
        tvDate = view.findViewById(R.id.tvDate);
        View layoutSelectDate = view.findViewById(R.id.layoutSelectDate);
        etAmount = view.findViewById(R.id.tvGroup);
        Button btnSaveBudget = view.findViewById(R.id.btnSaveBudget);

        tvDate.setText("Tháng này (" + getMonthRange() + ")");

        btnSaveBudget.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            long amount = 0;

            if (!amountStr.isEmpty()) {
                try {
                    amount = Long.parseLong(amountStr);
                } catch (NumberFormatException e) { return; }
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                return;
            }

            String groupName = tvWallet.getText().toString();

            // --- ĐÃ SỬA: Kiểm tra tên thay vì ID để tránh lỗi ---
            if (groupName.isEmpty() || groupName.equals("Chọn nhóm") || groupName.equals("Chưa chọn nhóm")) {
                Toast.makeText(getContext(), "Vui lòng chọn nhóm chi tiêu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tính toán thời gian
            String currentDateText = tvDate.getText().toString();
            String timeType = "MONTH";
            String startDateStr = "";
            String endDateStr = "";

            if (currentDateText.toLowerCase().contains("tuần")) {
                timeType = "WEEK";
                startDateStr = getWeekStartFull();
                endDateStr = getWeekEndFull();
            } else if (currentDateText.toLowerCase().contains("tháng")) {
                timeType = "MONTH";
                startDateStr = getMonthStartFull();
                endDateStr = getMonthEndFull();
            } else {
                timeType = "MONTH";
                startDateStr = getMonthStartFull();
                endDateStr = getMonthEndFull();
            }

            UserHandle userHandle = new UserHandle(requireContext());
            String currentUserId = (userHandle.getCurrentUser() != null) ? userHandle.getCurrentUser().getIdUser() : "unknown";

            // Lưu vào DB
            Budget newBudget = new Budget(
                    currentUserId,
                    selectedCategoryId,
                    groupName,
                    amount,
                    0,
                    startDateStr,
                    endDateStr,
                    timeType
            );

            BudgetHandle dbHandle = new BudgetHandle(getContext());
            dbHandle.insertBudget(newBudget);

            // Cập nhật list hiển thị
            BudgetStorage.getInstance().addItem(new BudgetItem(groupName, amount, 0, currentIconRes, timeType));

            if (getActivity() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        layoutSelectDate.setOnClickListener(v -> showTimePickerDialog());

        tvWallet.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new Group())
                    .addToBackStack(null)
                    .commit();
        });

        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    // ... (Giữ nguyên các hàm showTimePickerDialog và tính ngày tháng bên dưới) ...
    private void showTimePickerDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.layout_time_picker, null);
        bottomSheetDialog.setContentView(dialogView);

        TextView tvWeek = dialogView.findViewById(R.id.tvThisWeek);
        TextView tvMonth = dialogView.findViewById(R.id.tvThisMonth);
        TextView tvQuarter = dialogView.findViewById(R.id.tvThisQuarter);
        TextView tvYear = dialogView.findViewById(R.id.tvThisYear);
        View btnSave = dialogView.findViewById(R.id.btnSave);
        View btnCancel = dialogView.findViewById(R.id.btnCancel);

        tvWeek.setOnClickListener(v -> {
            tempSelectedDate = "Tuần này (" + getWeekRange() + ")";
            highlightSelection(dialogView, tvWeek);
        });

        tvMonth.setOnClickListener(v -> {
            tempSelectedDate = "Tháng này (" + getMonthRange() + ")";
            highlightSelection(dialogView, tvMonth);
        });

        tvQuarter.setOnClickListener(v -> {
            tempSelectedDate = "Quý này (" + getQuarterRange() + ")";
            highlightSelection(dialogView, tvQuarter);
        });

        tvYear.setOnClickListener(v -> {
            tempSelectedDate = "Năm nay (" + getYearRange() + ")";
            highlightSelection(dialogView, tvYear);
        });

        btnSave.setOnClickListener(v -> {
            if (!tempSelectedDate.isEmpty()) {
                tvDate.setText(tempSelectedDate);
            }
            bottomSheetDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    private void highlightSelection(View parent, TextView selected) {
        int defaultColor = android.graphics.Color.BLACK;
        int selectedColor = android.graphics.Color.parseColor("#2E7D32");

        TextView[] views = {
                parent.findViewById(R.id.tvThisWeek),
                parent.findViewById(R.id.tvThisMonth),
                parent.findViewById(R.id.tvThisQuarter),
                parent.findViewById(R.id.tvThisYear)
        };
        for (TextView view : views) {
            if (view != null) view.setTextColor(defaultColor);
        }
        if (selected != null) selected.setTextColor(selectedColor);
    }

    private String getWeekRange() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        String start = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String end = sdf.format(cal.getTime());
        return start + " - " + end;
    }

    private String getMonthRange() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String start = sdf.format(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String end = sdf.format(cal.getTime());
        return start + " - " + end;
    }

    private String getQuarterRange() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int quarter = (month / 3);
        Calendar startCal = (Calendar) cal.clone();
        startCal.set(Calendar.MONTH, quarter * 3);
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        Calendar endCal = (Calendar) cal.clone();
        endCal.set(Calendar.MONTH, (quarter * 3) + 2);
        endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return sdf.format(startCal.getTime()) + " - " + sdf.format(endCal.getTime());
    }

    private String getYearRange() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return "01/01/" + year + " - 31/12/" + year;
    }

    private String getWeekStartFull() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return sdfFull.format(cal.getTime());
    }

    private String getWeekEndFull() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        return sdfFull.format(cal.getTime());
    }

    private String getMonthStartFull() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return sdfFull.format(cal.getTime());
    }

    private String getMonthEndFull() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return sdfFull.format(cal.getTime());
    }
}