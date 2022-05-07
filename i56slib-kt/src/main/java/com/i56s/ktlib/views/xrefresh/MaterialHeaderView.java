package com.i56s.ktlib.views.xrefresh;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.i56s.ktlib.R;
import com.i56s.ktlib.utils.SizeUtils;

public class MaterialHeaderView extends FrameLayout implements BaseMaterialView {

    private final static String Tag = MaterialHeaderView.class.getSimpleName();
    private MaterialWaveView materialWaveView;
    private CircleProgressBar circleProgressBar;
    private int waveColor = 0x90ffffff;
    private int progressTextColor = Color.BLACK;
    private int[] progress_colors = {R.color.material_red, R.color.material_green, R.color.material_blue, R.color.material_yellow};
    private int progressStokeWidth = 3;
    private boolean isShowArrow = true, isShowProgressBg = true;
    private int progressValue = 0, progressValueMax = 100;
    private int textType = 1;
    private int progressBg = CircleProgressBar.DEFAULT_CIRCLE_BG_LIGHT;
    private int progressSize = 50;
    private static float density;

    public MaterialHeaderView(Context context) {
        this(context, null);
    }

    public MaterialHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    protected void init(AttributeSet attrs, int defStyle) {
        if (isInEditMode()) return;
        setClipToPadding(false);
        setWillNotDraw(false);
    }

    public int getWaveColor() {
        return waveColor;
    }

    public void setWaveColor(int waveColor) {
        this.waveColor = waveColor;
        if (null != materialWaveView) {
            materialWaveView.setColor(this.waveColor);
        }
    }

    public void setProgressSize(int progressSize) {
        this.progressSize = progressSize;
        LayoutParams layoutParams = new LayoutParams((int) density * progressSize, (int) density * progressSize);
        layoutParams.gravity = Gravity.CENTER;
        if (circleProgressBar != null)
            circleProgressBar.setLayoutParams(layoutParams);
    }

    public void setProgressBg(int progressBg) {
        this.progressBg = progressBg;
        if (circleProgressBar != null)
            circleProgressBar.setProgressBackGroundColor(progressBg);
    }

    public void setIsProgressBg(boolean isShowProgressBg) {
        this.isShowProgressBg = isShowProgressBg;
        if (circleProgressBar != null)
            circleProgressBar.setCircleBackgroundEnabled(isShowProgressBg);
    }

    public void setProgressTextColor(int textColor) {
        this.progressTextColor = textColor;
    }

    public void setProgressColors(int[] colors) {
        this.progress_colors = colors;
        if (circleProgressBar != null)
            circleProgressBar.setColorSchemeColors(progress_colors);
    }

    public void setTextType(int textType) {
        this.textType = textType;
    }

    public void setProgressValue(int value) {
        this.progressValue = value;
        this.post(new Runnable() {
            @Override
            public void run() {
                if (circleProgressBar != null) {
                    circleProgressBar.setProgress(progressValue);
                }
            }
        });

    }

    public void setProgressValueMax(int value) {
        this.progressValueMax = value;
    }

    public void setProgressStokeWidth(int w) {
        this.progressStokeWidth = w;
        if (circleProgressBar != null)
            circleProgressBar.setProgressStokeWidth(progressStokeWidth);
    }

    public void showProgressArrow(boolean isShowArrow) {
        this.isShowArrow = isShowArrow;
        if (circleProgressBar != null)
            circleProgressBar.setShowArrow(isShowArrow);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        density = getContext().getResources().getDisplayMetrics().density;
        materialWaveView = new MaterialWaveView(getContext());
        materialWaveView.setColor(waveColor);
        addView(materialWaveView);

        circleProgressBar = new CircleProgressBar(getContext());
        LayoutParams layoutParams = new LayoutParams((int) density * progressSize, (int) density * progressSize);
        layoutParams.gravity = Gravity.CENTER;
        circleProgressBar.setLayoutParams(layoutParams);
        circleProgressBar.setColorSchemeColors(progress_colors);
        circleProgressBar.setProgressStokeWidth(progressStokeWidth);
        circleProgressBar.setShowArrow(isShowArrow);
        circleProgressBar.setShowProgressText(textType == 0);
        circleProgressBar.setTextColor(progressTextColor);
        circleProgressBar.setProgress(progressValue);
        circleProgressBar.setMax(progressValueMax);
        circleProgressBar.setCircleBackgroundEnabled(isShowProgressBg);
        circleProgressBar.setProgressBackGroundColor(progressBg);
        addView(circleProgressBar);
    }

    @Override
    public void onComlete() {
        if (materialWaveView != null) {
            materialWaveView.onComlete();
        }
        if (circleProgressBar != null) {
            circleProgressBar.onComlete();
            ViewCompat.setTranslationY(circleProgressBar, 0);
            ViewCompat.setScaleX(circleProgressBar, 0);
            ViewCompat.setScaleY(circleProgressBar, 0);
        }

    }

    @Override
    public void onBegin() {
        if (materialWaveView != null) {
            materialWaveView.onBegin();
        }
        if (circleProgressBar != null) {
            ViewCompat.setScaleX(circleProgressBar, 0.001f);
            ViewCompat.setScaleY(circleProgressBar, 0.001f);
            circleProgressBar.onBegin();
        }
    }

    @Override
    public void onSlide(float fraction) {
        if (materialWaveView != null) {
            materialWaveView.onSlide(fraction);
        }
        if (circleProgressBar != null) {
            circleProgressBar.onSlide(fraction);
            float a = SizeUtils.limitValue(1, fraction);
            ViewCompat.setScaleX(circleProgressBar, a);
            ViewCompat.setScaleY(circleProgressBar, a);
            ViewCompat.setAlpha(circleProgressBar, a);
        }
    }

    @Override
    public void onRefreshing() {
        if (materialWaveView != null) {
            materialWaveView.onRefreshing();
        }
        if (circleProgressBar != null) {
            circleProgressBar.onRefreshing();
        }
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }
}
