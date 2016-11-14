package com.mcxtzhang.captchalib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

/**
 * 介绍：滑动验证码的View
 * 作者：zhangxutong
 * 邮箱：mcxtzhang@163.com
 * 主页：http://blog.csdn.net/zxt0601
 * 时间： 2016/11/14.
 */

public class SwipeCaptchaView extends ImageView {
    private static final String TAG = "zxt/" + SwipeCaptchaView.class.getName();
    //控件的宽高
    protected int mWidth;
    protected int mHeight;

    //验证码的宽高
    private int mCaptchaWidth;
    private int mCaptchaHeight;
    private int mCaptchaX;
    private int mCaptchaY;
    private Random mRandom;
    private Paint mPaint;

    public SwipeCaptchaView(Context context) {
        this(context, null);
    }

    public SwipeCaptchaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeCaptchaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        int defaultSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());
        mCaptchaHeight = defaultSize;
        mCaptchaWidth = defaultSize;
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SwipeCaptchaView, defStyleAttr, 0);
        int n = ta.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.SwipeCaptchaView_captchaHeight) {
                mCaptchaHeight = (int) ta.getDimension(attr, defaultSize);
            } else if (attr == R.styleable.SwipeCaptchaView_captchaWidth) {
                mCaptchaWidth = (int) ta.getDimension(attr, defaultSize);
            }
        }
        ta.recycle();


        mRandom = new Random(System.nanoTime());
        mPaint = new Paint();
        mPaint.setColor(0x88000000);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createCaptchaArea();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        createCaptchaArea();
    }

    //生成验证码区域
    private void createCaptchaArea() {


        mCaptchaX = mRandom.nextInt(mWidth - mCaptchaWidth);
        mCaptchaY = mRandom.nextInt(mHeight - mCaptchaHeight);
        Log.d(TAG, "createCaptchaArea() called mWidth:" + mWidth + ", mHeight:" + mHeight + ", mCaptchaX:" + mCaptchaX + ", mCaptchaY:" + mCaptchaY);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mCaptchaX, mCaptchaY, mCaptchaX + mCaptchaWidth, mCaptchaY + mCaptchaHeight, mPaint);
    }
}
