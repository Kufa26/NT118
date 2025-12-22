package com.example.cashmate.plus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashmate.R;

public class NoteFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.note, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        EditText edtNote = view.findViewById(R.id.edtNote);

        // Load note cũ
        if (getArguments() != null) {
            edtNote.setText(getArguments().getString("note", ""));
        }

        btnBack.setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString("note", edtNote.getText().toString());

            requireActivity()
                    .getSupportFragmentManager()
                    .setFragmentResult("note_result", result);

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack(); // QUAY LẠI PlusFragment
        });

        return view;
    }
}
