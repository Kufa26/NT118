package com.example.cashmate.plus;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashmate.R;
import com.example.cashmate.database.transaction.Transaction;
import com.example.cashmate.database.transaction.TransactionHandle;
import com.example.cashmate.group.ListGroupFragment;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class PlusFragment extends Fragment {

    // ===== VIEW =====
    private ImageButton btnBack, btnPrevDate, btnNextDate;
    private TextView tvGroup, tvNote, tvDate;
    private ImageView imgGroup;
    private EditText etAmount;
    private Button btnSave;

    // ===== DATA =====
    private String noteText = "";
    private LocalDate selectedDate = LocalDate.now();

    // ===== GROUP =====
    private Long selectedCategoryId = null;
    private String selectedGroupName = null;
    private String selectedGroupIcon = null;
    private String selectedCategoryType = null;

    // ===== EDIT MODE =====
    private Long editingTransactionId = null;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.plus, container, false);

        // ===== BIND =====
        btnBack = view.findViewById(R.id.btnBack);
        tvGroup = view.findViewById(R.id.tvGroup);
        tvNote = view.findViewById(R.id.tvNote);
        tvDate = view.findViewById(R.id.tvDate);
        imgGroup = view.findViewById(R.id.imgGroupIcon);
        etAmount = view.findViewById(R.id.etAmount);
        btnSave = view.findViewById(R.id.btnSave);
        btnPrevDate = view.findViewById(R.id.btnPrevDate);
        btnNextDate = view.findViewById(R.id.btnNextDate);

        updateDateUI();
        updateNoteUI();

        // ===== BACK =====
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // ===== EDIT MODE (🔥 CỰC QUAN TRỌNG) =====
        if (getArguments() != null && getArguments().containsKey("idTransaction")) {
            editingTransactionId = getArguments().getLong("idTransaction");
            loadTransactionForEdit(editingTransactionId);
            btnSave.setText("Cập nhật");
        }

        // ===== DATE =====
        btnPrevDate.setOnClickListener(v -> {
            if (selectedDate.isAfter(LocalDate.of(2000, 1, 1))) {
                selectedDate = selectedDate.minusDays(1);
                updateDateUI();
            }
        });

        btnNextDate.setOnClickListener(v -> {
            if (selectedDate.isBefore(LocalDate.of(2050, 12, 31))) {
                selectedDate = selectedDate.plusDays(1);
                updateDateUI();
            }
        });

        tvDate.setOnClickListener(v -> openSpinnerDatePicker());

        // ===== GROUP =====
        view.findViewById(R.id.layoutGroup).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ListGroupFragment())
                        .addToBackStack("group")
                        .commit()
        );

        getParentFragmentManager()
                .setFragmentResultListener("select_group", this, (k, b) -> {
                    selectedCategoryId = b.getLong("idCategory");
                    selectedGroupName = b.getString("name");
                    selectedGroupIcon = b.getString("icon");
                    selectedCategoryType = b.getString("type");
                    updateGroupUI();
                });

        // ===== NOTE =====
        view.findViewById(R.id.layoutNote).setOnClickListener(v -> {
            NoteFragment f = new NoteFragment();
            Bundle b = new Bundle();
            b.putString("note", noteText);
            f.setArguments(b);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .hide(this)
                    .add(R.id.fragment_container, f)
                    .addToBackStack("note")
                    .commit();
        });

        getParentFragmentManager()
                .setFragmentResultListener("note_result", this, (k, b) -> {
                    noteText = b.getString("note", "");
                    updateNoteUI();
                });

        // ===== SAVE =====
        btnSave.setOnClickListener(v -> saveTransaction());

        return view;
    }

    // ================= SAVE =================
    private void saveTransaction() {

        if (selectedCategoryId == null) {
            Toast.makeText(getContext(), "Chưa chọn nhóm", Toast.LENGTH_SHORT).show();
            return;
        }

        if (etAmount.getText().toString().trim().isEmpty()) {
            etAmount.setError("Nhập số tiền");
            return;
        }

        double amount = Double.parseDouble(etAmount.getText().toString());
        String date = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String weekday = getThu(selectedDate.getDayOfWeek());

        Transaction t = new Transaction(
                1L,
                selectedCategoryId,
                amount,
                noteText,
                date,
                weekday,
                selectedCategoryType,
                System.currentTimeMillis()
        );

        TransactionHandle handle = new TransactionHandle(requireContext());

        if (editingTransactionId == null) {
            handle.insert(t);
            Toast.makeText(getContext(), "Đã thêm giao dịch", Toast.LENGTH_SHORT).show();
        } else {
            handle.update(editingTransactionId, t);
            Toast.makeText(getContext(), "Đã cập nhật giao dịch", Toast.LENGTH_SHORT).show();
        }

        requireActivity().getSupportFragmentManager().popBackStack();
    }

    // ================= LOAD EDIT =================
    private void loadTransactionForEdit(long id) {
        TransactionHandle handle = new TransactionHandle(requireContext());
        Cursor c = handle.getById(id);

        if (c != null && c.moveToFirst()) {

            etAmount.setText(String.valueOf(
                    c.getDouble(c.getColumnIndexOrThrow("amount"))
            ));

            noteText = c.getString(c.getColumnIndexOrThrow("note"));
            updateNoteUI();

            selectedCategoryId = c.getLong(c.getColumnIndexOrThrow("idCategory"));
            selectedGroupName = c.getString(c.getColumnIndexOrThrow("nameCategory"));
            selectedGroupIcon = c.getString(c.getColumnIndexOrThrow("iconCategory"));
            selectedCategoryType = c.getString(c.getColumnIndexOrThrow("typeTransaction"));
            updateGroupUI();

            selectedDate = LocalDate.parse(
                    c.getString(c.getColumnIndexOrThrow("date")),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
            );
            updateDateUI();
        }

        if (c != null) c.close();
    }

    // ================= DATE PICKER =================
    private void openSpinnerDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                (picker, y, m, d) -> {
                    selectedDate = LocalDate.of(y, m + 1, d);
                    updateDateUI();
                },
                selectedDate.getYear(),
                selectedDate.getMonthValue() - 1,
                selectedDate.getDayOfMonth()
        );

        dialog.getDatePicker().setCalendarViewShown(false);
        dialog.getDatePicker().setSpinnersShown(true);

        Calendar min = Calendar.getInstance();
        min.set(2000, Calendar.JANUARY, 1);

        Calendar max = Calendar.getInstance();
        max.set(2050, Calendar.DECEMBER, 31);

        dialog.getDatePicker().setMinDate(min.getTimeInMillis());
        dialog.getDatePicker().setMaxDate(max.getTimeInMillis());

        dialog.show();
    }

    // ================= UI =================
    private void updateDateUI() {
        tvDate.setText(
                getThu(selectedDate.getDayOfWeek()) + ", " +
                        selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
    }

    private String getThu(DayOfWeek d) {
        switch (d) {
            case MONDAY: return "Thứ Hai";
            case TUESDAY: return "Thứ Ba";
            case WEDNESDAY: return "Thứ Tư";
            case THURSDAY: return "Thứ Năm";
            case FRIDAY: return "Thứ Sáu";
            case SATURDAY: return "Thứ Bảy";
            case SUNDAY: return "Chủ Nhật";
            default: return "";
        }
    }

    private void updateGroupUI() {
        if (selectedGroupName != null) {
            tvGroup.setText(selectedGroupName);
            tvGroup.setTextColor(Color.BLACK);

            int res = getResources().getIdentifier(
                    selectedGroupIcon,
                    "drawable",
                    requireContext().getPackageName()
            );
            if (res != 0) imgGroup.setImageResource(res);
        }
    }

    private void updateNoteUI() {
        if (noteText == null || noteText.trim().isEmpty()) {
            tvNote.setText("Ghi chú");
            tvNote.setTextColor(Color.GRAY);
        } else {
            tvNote.setText(noteText);
            tvNote.setTextColor(Color.BLACK);
        }
    }
}
