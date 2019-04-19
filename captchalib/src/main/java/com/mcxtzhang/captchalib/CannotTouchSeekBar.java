package com.mcxtzhang.captchalib;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CannotTouchSeekBar extends AppCompatSeekBar {
    private static int left = 0;
    private static int right = 0;

    public CannotTouchSeekBar(Context context) {
       this(context,null);
    }

    public CannotTouchSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(left==0||right==0) {
            left = getThumb().getBounds().left;
            right = getThumb().getBounds().right;
        }


        int eventX = (int) event.getX();

        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(eventX<=left||eventX>=right){
                return false;
            }
        }


        return super.dispatchTouchEvent(event);
    }
}
