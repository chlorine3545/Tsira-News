package com.java.liyao;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private TextView welcome_countdown;
    private ImageView welcome_logo;
    private TextView welcome_text;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 3000; // 设置倒计时时长，单位为毫秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        welcome_countdown = findViewById(R.id.welcome_countdown);
        welcome_logo = findViewById(R.id.welcome_logo);
        welcome_text = findViewById(R.id.welcome_text);

        startCountdown();

        setFadeInAnimation(welcome_logo);
        setFadeInAnimation(welcome_text);
    }

    private void setFadeInAnimation(View view) {
        // 初始状态为不可见
        view.setVisibility(View.INVISIBLE);

        // 创建AlphaAnimation
        AlphaAnimation fadeInAnimation = new AlphaAnimation(0, 1); // 从完全透明到完全不透明
        fadeInAnimation.setDuration(1000); // 设置动画持续时间

        // 开始动画
        view.startAnimation(fadeInAnimation);

        // 动画结束后设置为可见
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                welcome_countdown.setText(secondsRemaining + " s");
            }

            @Override
            public void onFinish() {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();

            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}