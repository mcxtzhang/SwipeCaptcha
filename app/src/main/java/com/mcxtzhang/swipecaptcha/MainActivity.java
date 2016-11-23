package com.mcxtzhang.swipecaptcha;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.mcxtzhang.captchalib.ISwipeCaptcha;

public class MainActivity extends AppCompatActivity {
    ISwipeCaptcha mSwipeCaptchaView;
    ISwipeCaptcha.OnCaptchaMatchCallback mOnCaptchaMatchCallback;

    SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeCaptchaView = (ISwipeCaptcha) findViewById(R.id.swipeCaptchaView);
        mSeekBar = (SeekBar) findViewById(R.id.dragBar);

        findViewById(R.id.btnChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeCaptchaView.createCaptcha();
            }
        });
        mOnCaptchaMatchCallback = new ISwipeCaptcha.OnCaptchaMatchCallback() {
            @Override
            public void matchSuccess(ISwipeCaptcha swipeCaptcha) {
                Toast.makeText(MainActivity.this, "恭喜你啊 验证成功 可以搞事情了", Toast.LENGTH_SHORT).show();
                //swipeCaptcha.createCaptcha();
                mSeekBar.setEnabled(false);
            }

            @Override
            public void matchFailed(ISwipeCaptcha swipeCaptcha) {
                Toast.makeText(MainActivity.this, "你有80%的可能是机器人，现在走还来得及", Toast.LENGTH_SHORT).show();
                swipeCaptcha.resetCaptcha();
                mSeekBar.setProgress(0);
            }
        };
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSwipeCaptchaView.setCurrentSwipeValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //随便放这里是因为控件
                mSeekBar.setMax(mSwipeCaptchaView.getMaxSwipeValue());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSwipeCaptchaView.matchCaptcha(mOnCaptchaMatchCallback);
            }
        });
    }
}
