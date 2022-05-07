package com.i56s.ktlib.views.xrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.i56s.ktlib.utils.SizeUtils;

import androidx.annotation.NonNull;

public class MaterialHeadView extends FrameLayout implements BaseMaterialView {
    private MaterialWaveView materialWaveView;
    private CircleProgressBar circleProgressBar;
    private int waveColor;
    private int progressTextColor;
    private int[] progress_colors;
    private int progressStokeWidth;
    private boolean isShowArrow,isShowProgressBg;
    private int progressValue,progressValueMax;
    private int textType;
    private int progressBg;
    private int progressSize;
    private BaseMaterialView listener;


    public MaterialHeadView(Context context) {
        this(context, null);
    }

    public MaterialHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialHeadView(Context context, AttributeSet attrs, int defStyle) {
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
        if(null!= materialWaveView)
        {
            materialWaveView.setColor( this.waveColor );
        }
    }

    public void setProgressSize(int progressSize)
    {
        this.progressSize = progressSize;
    }

    public void setProgressBg(int progressBg)
    {
        this.progressBg = progressBg;
    }

    public void setIsProgressBg(boolean isShowProgressBg)
    {
        this.isShowProgressBg = isShowProgressBg;
    }

    public void setProgressTextColor(int textColor)
    {
        this.progressTextColor = textColor;
    }

    public void setProgressColors(int[] colors)
    {
        this.progress_colors = colors;
    }

    public void setTextType(int textType)
    {
        this.textType = textType;
    }

    public void setProgressValue(int value)
    {
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

    public void setProgressValueMax(int value)
    {
        this.progressValueMax = value;
    }

    public void setProgressStokeWidth(int w)
    {
        this.progressStokeWidth = w;
    }

    public void showProgressArrow(boolean isShowArrow)
    {
        this.isShowArrow = isShowArrow;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        materialWaveView = new MaterialWaveView(getContext());
        materialWaveView.setColor(waveColor);
        addView(materialWaveView);

        circleProgressBar = new CircleProgressBar(getContext());
        LayoutParams layoutParams = new LayoutParams((int)SizeUtils.dp2px(progressSize), (int)SizeUtils.dp2px(progressSize));
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
        if(materialWaveView != null)
        {
            materialWaveView.onComlete();
        }
        if(circleProgressBar != null)
        {
            circleProgressBar.onComlete();
            circleProgressBar.setTranslationY(0);
            circleProgressBar.setScaleX( 0);
            circleProgressBar.setScaleY(0);
        }

    }

    @Override
    public void onBegin() {
        if(materialWaveView != null)
        {
            materialWaveView.onBegin();
        }
        if(circleProgressBar != null)
        {
            circleProgressBar.onBegin();
        }
    }

    @Override
    public void onSlide( float fraction) {
        if(materialWaveView != null)
        {
            materialWaveView.onSlide(fraction);
        }
        if(circleProgressBar != null)
        {
            circleProgressBar.onSlide( fraction);
            float a = SizeUtils.limitValue(1,fraction);
            circleProgressBar.setScaleX( 1);
            circleProgressBar.setScaleY( 1);
            circleProgressBar.setAlpha( a);
        }
    }

    @Override
    public void onRefreshing() {
        if(materialWaveView != null)
        {
            materialWaveView.onRefreshing();
        }
        if(circleProgressBar != null)
        {
            circleProgressBar.onRefreshing();
        }
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }


//    public void scaleView(View v,float a,float b) {
//        ObjectAnimator ax = ObjectAnimator.ofFloat(v,"scaleX",a,b);
//        ObjectAnimator ay = ObjectAnimator.ofFloat(v,"scaleY",a,b);
//        AnimatorSet animSet = new AnimatorSet();
//        animSet.play(ax).with(ay);
//        animSet.setDuration(200);
//        animSet.start();
//    }
}
