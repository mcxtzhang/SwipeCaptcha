package com.mcxtzhang.swipecaptcha;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * 介绍：
 * 作者：zhangxutong
 * 邮箱：mcxtzhang@163.com
 * 主页：http://blog.csdn.net/zxt0601
 * 时间： 2016/11/16.
 */

public class TestSinView extends View {
    public TestSinView(Context context) {
        super(context);
    }

    public TestSinView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //验证码 阴影、抠图的Path
    private Path mCaptchaPath = new Path();
    //验证码的左上角(起点)的x y
    private int mCaptchaX = 200;
    private int mCaptchaY = 200;
    //验证码的宽高
    private int mCaptchaWidth = 300;
    private int mCaptchaHeight = 300;

    @Override
    protected void onDraw(Canvas canvas) {


        mCaptchaPath.reset();
        //从左上角开始 绘制一个不规则的阴影
        mCaptchaPath.moveTo(mCaptchaX, mCaptchaY);


/*        mCaptchaPath.lineTo(mCaptchaX + gap, mCaptchaY);
        //画出凹凸 由于是多段Path 无法闭合，简直阿西吧
        int r = mCaptchaWidth / 2 - gap;
        RectF oval = new RectF(mCaptchaX + gap, mCaptchaY - (r), mCaptchaX + gap + r * 2, mCaptchaY + (r));
        mCaptchaPath.arcTo(oval, 180, 180);*/

        int gap = mCaptchaWidth / 3;
        mCaptchaPath.lineTo(mCaptchaX + gap, mCaptchaY);
        //利用正弦曲线方程 Y = A sin(wx+FAI)+k，将计算后的坐标传入Path.quadTo()方法（绘制贝塞尔曲线）中,构建波浪曲线。

        W = (float) (Math.PI / (mCaptchaWidth - 2 * gap));
        FAI = (float) (-W * (mCaptchaX + gap) + Math.PI);

        for (int x = mCaptchaX + gap; x < mCaptchaX + mCaptchaWidth - gap; x++) {
            float y = (float) (A * Math.sin(W * x + FAI) + K + mCaptchaY);
   /*         if (x == mCaptchaX + gap) {
                mCaptchaPath.moveTo(x, y);
            }*/
            mCaptchaPath.quadTo(x, y, x + 1, y);
        }


        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY);//节点

        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY + gap);

        W = (float) (Math.PI / (mCaptchaHeight - 2 * gap));
        FAI = (float) (-W * (mCaptchaY + gap) + Math.PI);

        for (int x = mCaptchaY + gap; x < mCaptchaY + mCaptchaHeight - gap; x++) {
            float y = (float) (A * Math.sin(W * x + FAI) + K + mCaptchaX + mCaptchaWidth);
   /*         if (x == mCaptchaX + gap) {
                mCaptchaPath.moveTo(x, y);
            }*/
            mCaptchaPath.quadTo(y, x, y + 1, x);
        }




        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mCaptchaPath, paint);
    }


    /**
     * 波浪圆X轴偏移
     */
    private float FAI = 0;
    /**
     * 波浪圆振幅
     */
    private float A = 50;
    /**
     * 波浪圆的周期
     */
    private float W;
    /**
     * 波浪圆Y轴偏移
     */
    private float K = 0;


    /**
     * 角度转换成弧度
     *
     * @param degree
     * @return
     */
    private double degreeToRad(double degree) {
        return degree * Math.PI / 180;
    }
}
