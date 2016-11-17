package com.mcxtzhang.captchalib;

/**
 * 介绍：对外暴漏的方法
 * 作者：zhangxutong
 * 邮箱：mcxtzhang@163.com
 * 主页：http://blog.csdn.net/zxt0601
 * 时间： 2016/11/17.
 */

public interface ISwipeCaptcha {
    public interface OnCaptchaMatchCallback {
        void matchSuccess(ISwipeCaptcha swipeCaptcha);

        void matchFailed(ISwipeCaptcha swipeCaptcha);
    }


    //生成一个新的验证码
    void createCaptcha();

    //重置这个验证码
    void resetCaptcha();

    //验证
    void matchCaptcha(OnCaptchaMatchCallback onCaptchaMatchCallback);


    //最大能拖拽的值，用作Seekbar的max
    int getMaxSwipeValue();

    //设置当前的Swipe值
    void setCurrentSwipeValue(int value);

}
