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
import com.example.cashmate.group.ListGroupFragment;
import com.example.cashmate.database.budget.BudgetHandle;
import com.example.cashmate.database.User.UserHandle;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Add_Budget extends Fragment {

    private TextView tvWallet, tvDate;
    private ImageView imgGroupIcon;
    private EditText etAmount;

    private String tempSelectedDate = "";
    private int currentIconRes = R.drawable.ic_food;
    private int selectedCategoryId = 0;

    private final SimpleDateFormat sdf =
            new SimpleDateFormat("dd/MM", new Locale("vi", "VN"));
    private final SimpleDateFormat sdfFull =
            new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener(
                "select_group",
                this,
                (requestKey, bundle) -> {
                    // Nhận kết quả từ ListGroupFragment (dùng chung nhóm với phần Tài khoản)
                    String name = bundle.getString("name");
                    String iconName = bundle.getString("icon");
                    long idLong = bundle.getLong("idCategory", 0);

                    selectedCategoryId = (int) idLong;
                    currentIconRes = resolveIconRes(iconName);

                    if (tvWallet != null) tvWallet.setText(name);
                    if (imgGroupIcon != null) imgGroupIcon.setImageResource(currentIconRes);
                }
        );
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.create_a_budget, container, false);

        tvWallet = view.findViewById(R.id.tvWallet);
        imgGroupIcon = view.findViewById(R.id.imgGroupIcon);
        tvDate = view.findViewById(R.id.tvDate);
        etAmount = view.findViewById(R.id.tvGroup);
        Button btnSaveBudget = view.findViewById(R.id.btnSaveBudget);
        View layoutSelectDate = view.findViewById(R.id.layoutSelectDate);
        View btnBack = view.findViewById(R.id.btnBack);

        tvDate.setText("Tháng này (" + getMonthRange() + ")");

        btnSaveBudget.setOnClickListener(v -> saveBudget());
        layoutSelectDate.setOnClickListener(v -> showTimePickerDialog());

        tvWallet.setOnClickListener(v -> {
            ListGroupFragment fragment = new ListGroupFragment();
            Bundle args = new Bundle();
            args.putBoolean("expenseOnly", true);
            fragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    getParentFragmentManager().popBackStack()
            );
        }

        return view;
    }

    private void saveBudget() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amountStr);
        } catch (NumberFormatException e) {
            return;
        }

        String groupName = tvWallet.getText().toString();
        if (groupName.isEmpty()
                || groupName.equals("Chọn nhóm")
                || groupName.equals("Chưa chọn nhóm")) {
            Toast.makeText(getContext(), "Vui lòng chọn nhóm chi tiêu", Toast.LENGTH_SHORT).show();
            return;
        }

        String timeType = "MONTH";
        String startDate;
        String endDate;

        String dateText = tvDate.getText().toString().toLowerCase();
        if (dateText.contains("tuần")) {
            timeType = "WEEK";
            startDate = getWeekStartFull();
            endDate = getWeekEndFull();
        } else {
            startDate = getMonthStartFull();
            endDate = getMonthEndFull();
        }

        UserHandle userHandle = new UserHandle(requireContext());
        String userId = userHandle.getCurrentUser() != null
                ? userHandle.getCurrentUser().getIdUser()
                : "unknown";

        com.example.cashmate.database.budget.Budget budget = new com.example.cashmate.database.budget.Budget(
                userId,
                selectedCategoryId,
                groupName,
                amount,
                0,
                startDate,
                endDate,
                timeType
        );

        BudgetHandle db = new BudgetHandle(getContext());
        db.insertBudget(budget);

        BudgetStorage.getInstance().addItem(
                new BudgetItem(groupName, amount, 0, currentIconRes, timeType)
        );

        getParentFragmentManager().popBackStack();
    }

    private int resolveIconRes(String iconName) {
        if (iconName == null || iconName.isEmpty()) return R.drawable.ic_food;
        int resId = getResources().getIdentifier(iconName, "drawable", requireContext().getPackageName());
        return resId != 0 ? resId : R.drawable.ic_food;
    }

    private void showTimePickerDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.layout_time_picker, null);
        dialog.setContentView(view);

        TextView tvWeek = view.findViewById(R.id.tvThisWeek);
        TextView tvMonth = view.findViewById(R.id.tvThisMonth);
        TextView tvQuarter = view.findViewById(R.id.tvThisQuarter);
        TextView tvYear = view.findViewById(R.id.tvThisYear);

        View btnSave = view.findViewById(R.id.btnSave);
        View btnCancel = view.findViewById(R.id.btnCancel);

        tvWeek.setOnClickListener(v -> {
            tempSelectedDate = "Tuần này (" + getWeekRange() + ")";
            highlightSelection(view, tvWeek);
        });

        tvMonth.setOnClickListener(v -> {
            tempSelectedDate = "Tháng này (" + getMonthRange() + ")";
            highlightSelection(view, tvMonth);
        });

        tvQuarter.setOnClickListener(v -> {
            tempSelectedDate = "Quý này (" + getQuarterRange() + ")";
            highlightSelection(view, tvQuarter);
        });

        tvYear.setOnClickListener(v -> {
            tempSelectedDate = "Năm nay (" + getYearRange() + ")";
            highlightSelection(view, tvYear);
        });

        btnSave.setOnClickListener(v -> {
            if (!tempSelectedDate.isEmpty()) {
                tvDate.setText(tempSelectedDate);
            }
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void highlightSelection(View parent, TextView selected) {
        int normal = android.graphics.Color.BLACK;
        int active = android.graphics.Color.parseColor("#2E7D32");

        TextView[] views = {
                parent.findViewById(R.id.tvThisWeek),
                parent.findViewById(R.id.tvThisMonth),
                parent.findViewById(R.id.tvThisQuarter),
                parent.findViewById(R.id.tvThisYear)
        };

        for (TextView tv : views) {
            if (tv != null) tv.setTextColor(normal);
        }

        if (selected != null) selected.setTextColor(active);
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
        int q = cal.get(Calendar.MONTH) / 3;

        Calendar start = (Calendar) cal.clone();
        start.set(Calendar.MONTH, q * 3);
        start.set(Calendar.DAY_OF_MONTH, 1);

        Calendar end = (Calendar) cal.clone();
        end.set(Calendar.MONTH, q * 3 + 2);
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));

        return sdf.format(start.getTime()) + " - " + sdf.format(end.getTime());
    }

    private String getYearRange() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
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
