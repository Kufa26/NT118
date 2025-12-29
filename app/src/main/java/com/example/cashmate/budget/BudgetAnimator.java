package com.example.cashmate.budget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class BudgetAnimator extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private RectF rectF;
    private float sweepAngle = 0;

    private static final float STROKE_WIDTH = 40f;
    private static final int COLOR_PROGRESS = Color.parseColor("#27AE60");
    private static final int COLOR_BACKGROUND = Color.parseColor("#E0E0E0");

    public BudgetAnimator(Context context) {
        super(context);
        init();
    }

    public BudgetAnimator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BudgetAnimator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(COLOR_BACKGROUND);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(STROKE_WIDTH);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint = new Paint();
        progressPaint.setColor(COLOR_PROGRESS);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(STROKE_WIDTH);
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float padding = STROKE_WIDTH / 2;
        if (getWidth() > padding * 2 && getHeight() > padding * 2) {
            rectF.set(padding, padding, getWidth() - padding, getHeight() - padding);

            canvas.drawArc(rectF, 135, 270, false, backgroundPaint);

            if (sweepAngle > 0) {
                canvas.drawArc(rectF, 135, sweepAngle, false, progressPaint);
            }
        }
    }

    public static void run(View parentView, int remaining, int total) {
        View graphView = parentView.findViewById(com.example.cashmate.R.id.circularGraph);
        if (graphView instanceof BudgetAnimator) {
            ((BudgetAnimator) graphView).startAnimation(remaining, total);
        }
    }

    public void startAnimation(int remaining, int total) {
        if (total <= 0) {
            sweepAngle = 0;
            invalidate();
            return;
        }

        float percentage = (float) remaining / total;
        if (percentage > 1) percentage = 1;
        if (percentage < 0) percentage = 0;

        float targetAngle = 270 * percentage;

        ValueAnimator animator = ValueAnimator.ofFloat(sweepAngle, targetAngle);

        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            sweepAngle = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }
}