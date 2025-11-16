package com.example.login_sigup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account, container, false);

        LinearLayout layoutProfile = view.findViewById(R.id.profile);

        layoutProfile.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        LinearLayout layoutSetting = view.findViewById(R.id.setting);

        layoutSetting.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SettingFragment())
                    .addToBackStack(null)
                    .commit();
        });

        LinearLayout layoutIntroduce = view.findViewById(R.id.introduce);

        layoutIntroduce.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new IntroduceFragment())
                    .addToBackStack(null)
                    .commit();
        });

        LinearLayout layoutGroup = view.findViewById(R.id.Wallet_group);

        layoutGroup.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new Group())
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }

}
