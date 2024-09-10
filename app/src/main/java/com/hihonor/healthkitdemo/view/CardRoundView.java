/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.healthkitdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.hihonor.healthkitdemo.R;

/**
 * 自定义圆角布局
 *
 * @author lW0037320
 * @since 2024-08-01
 */
public class CardRoundView extends RelativeLayout {
    private static final String TAG = "CardRoundView";
    private static final int RADIUS_DEFAULT = 35;
    private Path mPath = new Path();
    private int mRadius = RADIUS_DEFAULT;

    private Paint mPaint;

    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private RectF mRectF;

    public CardRoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CardRoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CardRoundView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CardRoundRadius);

        mRadius = ta.getDimensionPixelSize(R.styleable.CardRoundRadius_radius,
                getContext().getResources().getDimensionPixelSize(R.dimen.magic_corner_radius_large));

        ta.recycle();

        setWillNotDraw(false);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRectF = new RectF(0, 0, getWidth(), getHeight());
        mPath.reset();
        mPath.addRoundRect(mRectF, mRadius, mRadius, Path.Direction.CCW);
        canvas.clipPath(mPath);
        canvas.drawColor(getResources().getColor(R.color.white));
    }
}
