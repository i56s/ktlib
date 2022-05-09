package com.i56s.ktlib.views.xrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.i56s.ktlib.utils.SizeUtils;

import androidx.annotation.NonNull;

public class MaterialFoodView
        extends FrameLayout
        implements BaseMaterialView
{
    private MaterialWaveView  mMaterialWaveView;
    private CircleProgressBar circleProgressBar;
    private int               waveColor;
    private int               progressTextColor;
    private int[]             progress_colors;
    private int               progressStokeWidth;
    private boolean           isShowArrow, isShowProgressBg;
    private int progressValue, progressValueMax;
    private int              textType;
    private int              progressBg;
    private int              progressSize;
    private BaseMaterialView listener;


    public MaterialFoodView(Context context) {
        this(context, null);
    }

    public MaterialFoodView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialFoodView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    protected void init(AttributeSet attrs, int defStyle) {
        if (isInEditMode()) { return; }
        setClipToPadding(false);
        setWillNotDraw(false);
    }

    public int getWaveColor() {
        return waveColor;
    }

    public void setWaveColor(int waveColor) {
        this.waveColor = waveColor;
        if (null != mMaterialWaveView) {
            mMaterialWaveView.setColor(this.waveColor);
        }
    }

    public void setProgressSize(int progressSize) {
        this.progressSize = progressSize;
    }

    public void setProgressBg(int progressBg) {
        this.progressBg = progressBg;
    }

    public void setIsProgressBg(boolean isShowProgressBg) {
        this.isShowProgressBg = isShowProgressBg;
    }

    public void setProgressTextColor(int textColor) {
        this.progressTextColor = textColor;
    }

    public void setProgressColors(int[] colors) {
        this.progress_colors = colors;
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
                }
            }
        });

    }

    public void setProgressValueMax(int value) {
        this.progressValueMax = value;
    }

    public void setProgressStokeWidth(int w) {
        this.progressStokeWidth = w;
    }

    public void showProgressArrow(boolean isShowArrow) {
        this.isShowArrow = isShowArrow;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mMaterialWaveView = new MaterialWaveView(getContext());
        mMaterialWaveView.setColor(waveColor);
        addView(mMaterialWaveView);

        circleProgressBar = new CircleProgressBar(getContext());

        LayoutParams layoutParams = new LayoutParams((int) SizeUtils.dp2px(progressSize),
                                                     (int) SizeUtils.dp2px(progressSize));
        layoutParams.gravity = Gravity.CENTER;
        circleProgressBar.setLayoutParams(layoutParams);
        circleProgressBar.setColors(progress_colors);
        circleProgressBar.setProgressStokeWidth(progressStokeWidth);
        circleProgressBar.setShowArrow(isShowArrow);
        circleProgressBar.setCircleBackgroundEnabled(isShowProgressBg);
        circleProgressBar.setProgressBackGroundColor(progressBg);
        addView(circleProgressBar);
    }

    @Override
    public void onComlete() {
        if (mMaterialWaveView != null) {
            mMaterialWaveView.onComlete();
        }
        if (circleProgressBar != null) {
            circleProgressBar.onComlete();
            circleProgressBar.setTranslationY(0);
            circleProgressBar.setScaleX(0);
            circleProgressBar.setScaleY(0);
        }


    }

    @Override
    public void onBegin() {
        if (mMaterialWaveView != null) {
            mMaterialWaveView.onBegin();
        }
        if (circleProgressBar != null) {
            circleProgressBar.onBegin();
            circleProgressBar.setScaleX(1);
            circleProgressBar.setScaleY(1);
        }
    }

    @Override
    public void onSlide(float fraction) {
        if (mMaterialWaveView != null) {
            mMaterialWaveView.onSlide(fraction);
        }
        if (circleProgressBar != null) {
            circleProgressBar.onSlide(fraction);
            float a = SizeUtils.limitValue(1, fraction);
            circleProgressBar.setScaleX(1);
            circleProgressBar.setScaleY(1);
            circleProgressBar.setAlpha(a);
        }
    }

    @Override
    public void onRefreshing() {
        if (mMaterialWaveView != null) {
            mMaterialWaveView.onRefreshing();
        }
        if (circleProgressBar != null) {
            circleProgressBar.onRefreshing();
        }
    }

    @NonNull
    @Override
    public View getView() { return this; }
}


