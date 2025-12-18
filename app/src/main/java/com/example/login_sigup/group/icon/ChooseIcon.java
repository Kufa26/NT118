package com.example.login_sigup.group.icon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_sigup.R;

public class ChooseIcon extends DialogFragment {

    public interface OnIconSelectedListener {
        void onIconSelected(int icon);
    }

    private final OnIconSelectedListener listener;

    public ChooseIcon(OnIconSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.choose_icon, container, false);

        RecyclerView rvIcons = view.findViewById(R.id.rvIcons);

        rvIcons.setLayoutManager(new GridLayoutManager(getContext(), 5));
        rvIcons.setAdapter(new IconAdapter(Icon.ICON_LIST, icon -> {
            listener.onIconSelected(icon);
            dismiss();
        }));

        return view;
    }
}
