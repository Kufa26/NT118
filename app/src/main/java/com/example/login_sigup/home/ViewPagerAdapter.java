package com.example.login_sigup.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.login_sigup.mainActivity.Page1Fragment;
import com.example.login_sigup.mainActivity.Page2Fragment;
import com.example.login_sigup.mainActivity.Page3Fragment;
import com.example.login_sigup.mainActivity.Page4Fragment;
import com.example.login_sigup.mainActivity.Page5Fragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Page1Fragment();
            case 1:
                return new Page2Fragment();
            case 2:
                return new Page4Fragment();
            case 3:
                return new Page3Fragment();
            default:
                return new Page5Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
