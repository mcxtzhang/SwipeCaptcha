package com.mcxtzhang.captchalib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

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
    //验证码的左上角(起点)的x y
    private int mCaptchaX;
    private int mCaptchaY;
    private Random mRandom;
    private Paint mPaint;
    //验证码 阴影、抠图的Path
    private Path mCaptchaPath;
    private PorterDuffXfermode mPorterDuffXfermode;

    //滑块Bitmap
    private Bitmap mMaspBitmap;
    //用于绘制阴影的Paint
    private Paint mMaskShadowPaint;
    private Bitmap mMaskShadowBitmap;
    //滑块的位移
    private int mDragerOffset;


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
        mPaint.setColor(0x77000000);
        //mPaint.setStyle(Paint.Style.STROKE);
        // 设置画笔遮罩滤镜
        mPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.SOLID));

        // 实例化画笔
        mMaskShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mMaskShadowPaint.setColor(Color.DKGRAY);
        mMaskShadowPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));

        mCaptchaPath = new Path();

        setClickable(true);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        post(new Runnable() {
            @Override
            public void run() {
                createCaptchaArea();
            }
        });
    }

    int mGap;

    //生成验证码区域
    public void createCaptchaArea() {

        mGap = mRandom.nextInt(mCaptchaWidth / 2);
        mGap = mCaptchaWidth / 4;

        mCaptchaX = mRandom.nextInt(mWidth - mCaptchaWidth - mGap);
        mCaptchaY = mRandom.nextInt(mHeight - mCaptchaHeight - mGap);
        Log.d(TAG, "createCaptchaArea() called mWidth:" + mWidth + ", mHeight:" + mHeight + ", mCaptchaX:" + mCaptchaX + ", mCaptchaY:" + mCaptchaY);

        mCaptchaPath.reset();
        mCaptchaPath.lineTo(0, 0);


        //从左上角开始 绘制一个不规则的阴影
        mCaptchaPath.moveTo(mCaptchaX, mCaptchaY);
        mCaptchaPath.lineTo(mCaptchaX + mGap, mCaptchaY);
        //画出凹凸 由于是多段Path 无法闭合，简直阿西吧
        int r = mCaptchaWidth / 2 - mGap;
        RectF oval = new RectF(mCaptchaX + mGap, mCaptchaY - (r), mCaptchaX + mGap + r * 2, mCaptchaY + (r));
        mCaptchaPath.arcTo(oval, 180, 180);
        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY);
        //凹的话，麻烦一点，要利用多次move
/*        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY + gap);
        oval = new RectF(mCaptchaX + mCaptchaWidth - r, mCaptchaY + gap, mCaptchaX + mCaptchaWidth + r, mCaptchaY + gap + r * 2);
        mCaptchaPath.arcTo(oval, 90, 180, true);
        mCaptchaPath.moveTo(mCaptchaX + mCaptchaWidth, mCaptchaY + gap + r * 2);*/
        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY + mCaptchaHeight);
        mCaptchaPath.lineTo(mCaptchaX, mCaptchaY + mCaptchaHeight);
        mCaptchaPath.close();

/*        RectF oval = new RectF(mCaptchaX + gap, mCaptchaY - (r), mCaptchaX + gap + r * 2, mCaptchaY + (r));
        mCaptchaPath.addArc(oval, 180,180);
        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY);
        //凹的话，麻烦一点，要利用多次move
        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY + gap);
        oval = new RectF(mCaptchaX + mCaptchaWidth - r, mCaptchaY + gap, mCaptchaX + mCaptchaWidth + r, mCaptchaY + gap + r * 2);
        mCaptchaPath.addArc(oval, 90, 180);
        mCaptchaPath.moveTo(mCaptchaX + mCaptchaWidth, mCaptchaY + gap + r * 2);*//*
        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY + mCaptchaHeight);
        mCaptchaPath.lineTo(mCaptchaX, mCaptchaY + mCaptchaHeight);
        mCaptchaPath.close();*/


        mMaspBitmap = getMaskBitmap(((BitmapDrawable) getDrawable()).getBitmap(), mCaptchaPath);
        mMaskShadowBitmap = mMaspBitmap.extractAlpha();
        mDragerOffset = 0;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCaptchaPath != null) {
            canvas.drawPath(mCaptchaPath, mPaint);
        }



/*        Rect mSrcRect = new Rect(0, 0, mWidth, mHeight);
        Rect mDstRect = new Rect(50, 50, 200, 200);


        int sc = canvas.saveLayer(0 + getPaddingLeft(), 0 + getPaddingTop(), mWidth - getPaddingRight(), mHeight - getPaddingBottom(), null,
                Canvas.ALL_SAVE_FLAG);
        canvas.translate(0,500);

        //设置遮罩模式为，先绘制DST,再绘制SRC,取交集，留下DST
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        //绘制波浪图形(图形上部是波浪，下部是矩形) （DST）
        canvas.drawPath(mCaptchaPath, mPaint);
        //设置遮罩模式(图像混合模式)
        mPaint.setXfermode(mPorterDuffXfermode);
        //绘制用于遮罩的圆形 (SRC)
        canvas.drawBitmap(((BitmapDrawable) getDrawable()).getBitmap(), mSrcRect, mSrcRect, mPaint);
        //设置遮罩模式为null
        mPaint.setXfermode(null);
        //将这个新图层绘制的bitmap，与上一个图层合并(显示)
        canvas.restoreToCount(sc);*/


/*        mPaint.setColor(Color.BLUE);

        int startX = 0 + mDragerOffset;
        mDragPath.reset();
        mDragPath.moveTo(startX, mCaptchaY);

        mDragPath.lineTo(startX + mGap, mCaptchaY);
        //画出凹凸 由于是多段Path 无法闭合，简直阿西吧
        int r = mCaptchaWidth / 2 - mGap;
        RectF oval = new RectF(startX + mGap, mCaptchaY - (r), startX + mGap + r * 2, mCaptchaY + (r));
        mDragPath.arcTo(oval, 180, 180);
        mDragPath.lineTo(startX + mCaptchaWidth, mCaptchaY);
        mDragPath.lineTo(startX + mCaptchaWidth, mCaptchaY + mCaptchaHeight);
        mDragPath.lineTo(startX, mCaptchaY + mCaptchaHeight);
        mDragPath.close();*/

        //canvas.drawPath(mDragPath, mPaint);


        if (null != mMaspBitmap && null != mMaskShadowBitmap) {
            // 先绘制阴影
            canvas.drawBitmap(mMaskShadowBitmap, -mCaptchaX + mDragerOffset, 0, mMaskShadowPaint);
            canvas.drawBitmap(mMaspBitmap, -mCaptchaX + mDragerOffset, 0, null);
        }

    }


    private int mFirstX;

    //模拟验证过程
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFirstX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                mDragerOffset = (int) (event.getX() - mFirstX);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                matchCaptcha();
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 校验
     */
    public void matchCaptcha() {
        if (Math.abs(mDragerOffset - mCaptchaX) < TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics())) {
            Log.d(TAG, "matchCaptcha() true: mDragerOffset:" + mDragerOffset + ", mCaptchaX:" + mCaptchaX);
            matchSuccess();
        } else {
            Log.e(TAG, "matchCaptcha() false: mDragerOffset:" + mDragerOffset + ", mCaptchaX:" + mCaptchaX);
            matchFailed();
        }
    }

    private void matchSuccess() {
        Toast.makeText(getContext(), "恭喜你啊 验证成功 可以搞事情了", Toast.LENGTH_SHORT).show();
        createCaptchaArea();
    }

    private void matchFailed() {
        Toast.makeText(getContext(), "你有80%的可能是机器人，现在走还来得及", Toast.LENGTH_SHORT).show();
        mDragerOffset = 0;
        invalidate();
    }


    //抠图
    private Bitmap getMaskBitmap(Bitmap mBitmap, Path mask) {
        Bitmap tempBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Log.e(TAG, " getMaskBitmap: width:" + mBitmap.getWidth() + ",  height:" + mBitmap.getHeight());
        Log.e(TAG, " View: width:" + mWidth + ",  height:" + mHeight);
        //把创建的位图作为画板
        Canvas mCanvas = new Canvas(tempBitmap);
        mCanvas.clipPath(mask);
        //考虑到scaleType等因素，要用Matrix对Bitmap进行缩放
        mCanvas.drawBitmap(mBitmap, getImageMatrix(), null);
        return tempBitmap;
    }
}
