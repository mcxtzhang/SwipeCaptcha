package com.mcxtzhang.swipecaptcha;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 介绍：
 * 作者：zhangxutong
 * 邮箱：mcxtzhang@163.com
 * 主页：http://blog.csdn.net/zxt0601
 * 时间： 2016/11/16.
 */

public class TestThridBerserView extends View {
    public TestThridBerserView(Context context) {
        super(context);
    }

    public TestThridBerserView(Context context, AttributeSet attrs) {
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
        mCaptchaPath.moveTo(mCaptchaX, mCaptchaY);

        int gap = mCaptchaWidth / 3;
        mCaptchaPath.lineTo(mCaptchaX + gap, mCaptchaY);
        int left = mCaptchaX + gap;
        int right = mCaptchaX + gap * 2;
        PointF start = new PointF(left, mCaptchaY);
        PointF end = new PointF(right, mCaptchaY);
        drawPartCircle(start, end, mCaptchaPath, false);


        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY);//节点
        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY + gap);

        drawPartCircle(new PointF(mCaptchaX + mCaptchaWidth, mCaptchaY + gap),
                new PointF(mCaptchaX + mCaptchaWidth, mCaptchaY + gap * 2),
                mCaptchaPath, false);

        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth, mCaptchaY + mCaptchaHeight);//节点
        mCaptchaPath.lineTo(mCaptchaX + mCaptchaWidth - gap, mCaptchaY + mCaptchaHeight);

        drawPartCircle(new PointF(mCaptchaX + mCaptchaWidth - gap, mCaptchaY + mCaptchaHeight),
                new PointF(mCaptchaX + mCaptchaWidth - gap - gap, mCaptchaY + mCaptchaHeight),
                mCaptchaPath, false);

        mCaptchaPath.lineTo(mCaptchaX, mCaptchaY + mCaptchaHeight);//节点
        mCaptchaPath.lineTo(mCaptchaX, mCaptchaY + mCaptchaHeight - gap);

        drawPartCircle(new PointF(mCaptchaX, mCaptchaY + mCaptchaHeight - gap),
                new PointF(mCaptchaX, mCaptchaY + mCaptchaHeight - gap * 2),
                mCaptchaPath, false);


        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mCaptchaPath, paint);
    }


    /**
     * 传入起点、终点 坐标、凹凸和Path。
     * 会自动绘制凹凸的半圆弧
     *
     * @param start 起点坐标
     * @param end   终点坐标
     * @param path  半圆会绘制在这个path上
     * @param outer 是否凸半圆
     */
    private void drawPartCircle(PointF start, PointF end, Path path, boolean outer) {
        float c = 0.551915024494f;
        //中点
        PointF middle = new PointF(start.x + (end.x - start.x) / 2, start.y + (end.y - start.y) / 2);
        //半径
        float r1 = (float) Math.sqrt(Math.pow((middle.x - start.x), 2) + Math.pow((middle.y - start.y), 2));
        //gap值
        float gap1 = r1 * c;

        if (start.x == end.x) {
            //绘制竖直方向的

            //是否是从上到下
            boolean topToBottom = end.y - start.y > 0 ? true : false;
            //以下是我写出了所有的计算公式后推的，不要问我过程，只可意会。
            int flag;//旋转系数
            if (topToBottom) {
                flag = 1;
            } else {
                flag = -1;
            }
            if (outer) {
                //凸的 两个半圆
                path.cubicTo(start.x + gap1 * flag, start.y,
                        middle.x + r1 * flag, middle.y - gap1 * flag,
                        middle.x + r1 * flag, middle.y);
                path.cubicTo(middle.x + r1 * flag, middle.y + gap1 * flag,
                        end.x + gap1 * flag, end.y,
                        end.x, end.y);
            } else {
                //凹的 两个半圆
                path.cubicTo(start.x - gap1 * flag, start.y,
                        middle.x - r1 * flag, middle.y - gap1 * flag,
                        middle.x - r1 * flag, middle.y);
                path.cubicTo(middle.x - r1 * flag, middle.y + gap1 * flag,
                        end.x - gap1 * flag, end.y,
                        end.x, end.y);
            }
        } else {
            //绘制水平方向的

            //是否是从左到右
            boolean leftToRight = end.x - start.x > 0 ? true : false;
            //以下是我写出了所有的计算公式后推的，不要问我过程，只可意会。
            int flag;//旋转系数
            if (leftToRight) {
                flag = 1;
            } else {
                flag = -1;
            }
            if (outer) {
                //凸 两个半圆
                path.cubicTo(start.x, start.y - gap1 * flag,
                        middle.x - gap1 * flag, middle.y - r1 * flag,
                        middle.x, middle.y - r1 * flag);
                path.cubicTo(middle.x + gap1 * flag, middle.y - r1 * flag,
                        end.x, end.y - gap1 * flag,
                        end.x, end.y);
            } else {
                //凹 两个半圆
                path.cubicTo(start.x, start.y + gap1 * flag,
                        middle.x - gap1 * flag, middle.y + r1 * flag,
                        middle.x, middle.y + r1 * flag);
                path.cubicTo(middle.x + gap1 * flag, middle.y + r1 * flag,
                        end.x, end.y + gap1 * flag,
                        end.x, end.y);
            }


/*
            没推导之前的公式在这里
            if (start.x < end.x) {
                if (outer) {
                    //上左半圆 顺时针
                    path.cubicTo(start.x, start.y - gap1,
                            middle.x - gap1, middle.y - r1,
                            middle.x, middle.y - r1);

                    //上右半圆:顺时针
                    path.cubicTo(middle.x + gap1, middle.y - r1,
                            end.x, end.y - gap1,
                            end.x, end.y);
                } else {
                    //下左半圆 逆时针
                    path.cubicTo(start.x, start.y + gap1,
                            middle.x - gap1, middle.y + r1,
                            middle.x, middle.y + r1);

                    //下右半圆 逆时针
                    path.cubicTo(middle.x + gap1, middle.y + r1,
                            end.x, end.y + gap1,
                            end.x, end.y);
                }
            } else {
                if (outer) {
                    //下右半圆 顺时针
                    path.cubicTo(start.x, start.y + gap1,
                            middle.x + gap1, middle.y + r1,
                            middle.x, middle.y + r1);
                    //下左半圆 顺时针
                    path.cubicTo(middle.x - gap1, middle.y + r1,
                            end.x, end.y + gap1,
                            end.x, end.y);
                }
            }*/
        }
    }
}
