package com.mcxtzhang.swipecaptcha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mcxtzhang.captchalib.SwipeCaptchaView;

public class MainActivity extends AppCompatActivity {
    SwipeCaptchaView mSwipeCaptchaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeCaptchaView = (SwipeCaptchaView) findViewById(R.id.swipeCaptchaView);

        findViewById(R.id.btnChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeCaptchaView.createCaptchaArea();
            }
        });
    }
}
