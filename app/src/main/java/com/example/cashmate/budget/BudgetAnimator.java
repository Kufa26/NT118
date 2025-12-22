package com.example.login_sigup.budget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.DecimalFormat;

public class BudgetAnimator {

    public static void run(View rootView, int targetAmount, int totalBudget) {
        ProgressBar progressBar = rootView.findViewById(com.example.login_sigup.R.id.circularProgress);
        TextView tvAmount = rootView.findViewById(com.example.login_sigup.R.id.tvAmount);

        if (progressBar == null || tvAmount == null) return;

        int progressPercentage = (int) (((float) targetAmount / totalBudget) * 100);

        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, progressPercentage);
        progressAnimator.setDuration(2000);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.start();

        ValueAnimator numberAnimator = ValueAnimator.ofInt(0, targetAmount);
        numberAnimator.setDuration(2000);
        numberAnimator.setInterpolator(new DecelerateInterpolator());

        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");

        numberAnimator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            tvAmount.setText(decimalFormat.format(animatedValue));
        });

        numberAnimator.start();
    }
}