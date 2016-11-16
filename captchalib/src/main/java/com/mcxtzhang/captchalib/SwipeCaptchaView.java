package com.mcxtzhang.captchalib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

import static com.mcxtzhang.captchalib.DrawHelperUtils.drawPartCircle;

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
    private Bitmap mMaskBitmap;
    private Paint mMaskPaint;
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
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(0x77000000);
        //mPaint.setStyle(Paint.Style.STROKE);
        // 设置画笔遮罩滤镜
        mPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.SOLID));

        // 实例化画笔
        mMaskShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mMaskShadowPaint.setColor(Color.BLACK);
/*        mMaskShadowPaint.setStrokeWidth(50);
        mMaskShadowPaint.setTextSize(50);
        mMaskShadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);*/
        mMaskShadowPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));

        //滑块区域
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

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
                createCaptcha();
            }
        });
    }


    //生成验证码区域
    public void createCaptcha() {
        createCaptchaPath();
        craeteMask();
        invalidate();
    }

    //生成验证码Path
    private void createCaptchaPath() {
        int gap = mRandom.nextInt(mCaptchaWidth / 2);
        gap = mCaptchaWidth / 3;

        mCaptchaX = mRandom.nextInt(mWidth - mCaptchaWidth - gap);
        mCaptchaY = mRandom.nextInt(mHeight - mCaptchaHeight - gap);
        Log.d(TAG, "createCaptchaPath() called mWidth:" + mWidth + ", mHeight:" + mHeight + ", mCaptchaX:" + mCaptchaX + ", mCaptchaY:" + mCaptchaY);

        mCaptchaPath.reset();
        mCaptchaPath.lineTo(0, 0);


        //从左上角开始 绘制一个不规则的阴影
        mCaptchaPath.moveTo(mCaptchaX, mCaptchaY);//左上角


/*        mCaptchaPath.lineTo(mCaptchaX + gap, mCaptchaY);
        //画出凹凸 由于是多段Path 无法闭合，简直阿西吧
        int r = mCaptchaWidth / 2 - gap;
        RectF oval = new RectF(mCaptchaX + gap, mCaptchaY - (r), mCaptchaX + gap + r * 2, mCaptchaY + (r));
        mCaptchaPath.arcTo(oval, 180, 180);*/

        mCaptchaPath.lineTo(mCaptchaX + gap, mCaptchaY);
        //draw一个随机凹凸的圆
        drawPartCircle(new PointF(mCaptchaX + gap, mCaptchaY),
                new PointF(mCaptchaX + gap * 2, mCaptchaY),
                mCaptchaPath, mRandom.nextBoolean());


        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY);//右上角
        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY + gap);
        //draw一个随机凹凸的圆
        drawPartCircle(new PointF(mCaptchaX + mCaptchaWidth, mCaptchaY + gap),
                new PointF(mCaptchaX + mCaptchaWidth, mCaptchaY + gap * 2),
                mCaptchaPath, mRandom.nextBoolean());


        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY + mCaptchaHeight);//右下角
        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth - gap, mCaptchaY + mCaptchaHeight);
        //draw一个随机凹凸的圆
        drawPartCircle(new PointF(mCaptchaX + mCaptchaWidth - gap, mCaptchaY + mCaptchaHeight),
                new PointF(mCaptchaX + mCaptchaWidth - gap * 2, mCaptchaY + mCaptchaHeight),
                mCaptchaPath, mRandom.nextBoolean());


        mCaptchaPath.lineTo(mCaptchaX, mCaptchaY + mCaptchaHeight);//左下角
        mCaptchaPath.lineTo(mCaptchaX, mCaptchaY + mCaptchaHeight - gap);
        //draw一个随机凹凸的圆
        drawPartCircle(new PointF(mCaptchaX, mCaptchaY + mCaptchaHeight - gap),
                new PointF(mCaptchaX, mCaptchaY + mCaptchaHeight - gap * 2),
                mCaptchaPath, mRandom.nextBoolean());


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
    }

    //生成滑块
    private void craeteMask() {
        mMaskBitmap = getMaskBitmap(((BitmapDrawable) getDrawable()).getBitmap(), mCaptchaPath);
        mMaskShadowBitmap = mMaskBitmap.extractAlpha();
        mDragerOffset = 0;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCaptchaPath != null) {
            canvas.drawPath(mCaptchaPath, mPaint);
        }



/*
        //画出凹凸 由于是多段Path 无法闭合，简直阿西吧
        int r = mCaptchaWidth / 2 - mGap;
        RectF oval = new RectF(startX + mGap, mCaptchaY - (r), startX + mGap + r * 2, mCaptchaY + (r));
        mDragPath.arcTo(oval, 180, 180);
        mDragPath.lineTo(startX + mCaptchaWidth, mCaptchaY);
        mDragPath.lineTo(startX + mCaptchaWidth, mCaptchaY + mCaptchaHeight);
        mDragPath.lineTo(startX, mCaptchaY + mCaptchaHeight);
        mDragPath.close();*/


        if (null != mMaskBitmap && null != mMaskShadowBitmap) {
            // 先绘制阴影
            canvas.drawBitmap(mMaskShadowBitmap, -mCaptchaX + mDragerOffset, 0, mMaskShadowPaint);
            canvas.drawBitmap(mMaskBitmap, -mCaptchaX + mDragerOffset, 0, null);
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
        createCaptcha();
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
        //mCanvas.clipPath(mask);//有锯齿 且无法解决,所以换成XFermode的方法做
        // 抗锯齿
        mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        //绘制用于遮罩的圆形
        mCanvas.drawPath(mask, mMaskPaint);
        //设置遮罩模式(图像混合模式)
        mMaskPaint.setXfermode(mPorterDuffXfermode);
        //考虑到scaleType等因素，要用Matrix对Bitmap进行缩放
        mCanvas.drawBitmap(mBitmap, getImageMatrix(), mMaskPaint);
        mMaskPaint.setXfermode(null);
        return tempBitmap;
    }
}
