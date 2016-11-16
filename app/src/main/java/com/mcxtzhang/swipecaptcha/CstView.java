package com.mcxtzhang.swipecaptcha;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


/**
 * 介绍：
 * 作者：zhangxutong
 * 邮箱：mcxtzhang@163.com
 * 主页：http://blog.csdn.net/zxt0601
 * 时间： 2016/11/15.
 */

public class CstView extends View {
    private static final String TAG = "zxt";
    ;

    public CstView(Context context) {
        super(context);
    }

    public CstView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CstView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), (R.drawable.pic11)), 0, 0, new Paint());
        Path path = new Path();
        path.moveTo(100, 100);
        path.lineTo(200, 100);
        path.lineTo(200, 200);
        path.lineTo(200, 300);
        path.close();
        Bitmap rightBitmap = getMaskBitmap(BitmapFactory.decodeResource(getResources(), (R.drawable.pic11)), path);
        canvas.drawBitmap(rightBitmap, -100, -100, new Paint());

    }

    //生成右边的背景
    private Bitmap getMaskBitmap(Bitmap mBitmap, Path mask) {
        Bitmap bgBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Log.e(TAG, " getRightBitmap: " + bgBitmap.getWidth());
        //把创建的位图作为画板
        Canvas mCanvas = new Canvas(bgBitmap);
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        //mCanvas.save();
        //先将canvas保存
        //把canvas修剪成指定的路径区域
        //mCanvas.translate(100, 100);

        mCanvas.clipPath(mask);
        mCanvas.drawBitmap(mBitmap, 0, 0, mPaint);

        Log.e(TAG, "getRightBitmap: " + bgBitmap.getWidth());
        return bgBitmap;
    }

    private Bitmap leftBitmap;

/*    //生成左边的滑块的背景
    private void getLeftBitmap(Bitmap mBitmap) {
        Bitmap bgBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //把创建的位图作为画板
        Canvas mCanvas = new Canvas(bgBitmap);
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mCanvas.save();
        mCanvas.translate(point.x, point.y);
        mCanvas.clipPath(path);
        mCanvas.drawColor(Color.WHITE);
        mCanvas.restore();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        //先将canvas保存
        //把canvas修剪成指定的路径区域
        mCanvas.drawBitmap(mBitmap, 0, 0, mPaint);
        leftBitmap = Bitmap.createBitmap(bgBitmap, point.x, point.y, w, w);
    }*/
}
