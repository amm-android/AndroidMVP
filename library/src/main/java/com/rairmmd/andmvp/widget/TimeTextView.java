package com.rairmmd.andmvp.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;

import com.allen.library.SuperButton;

/**
 * 获取验证码和倒计时TextView
 */
public class TimeTextView extends SuperButton {

    private String mTextString = "获取验证码";
    private int mCountDownTime = 60;
    private TimeCountDown mTimeCountDown;

    public TimeTextView(Context context) {
        this(context, null);
    }

    public TimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setText(mTextString);
        mTimeCountDown = new TimeCountDown(mCountDownTime * 1000, 1000);
    }

    public void setTextString(String text) {
        mTextString = text;
    }

    public void setCountDownTime(int mCountDownTime) {
        this.mCountDownTime = mCountDownTime;
    }

    public void start() {
        if (mTimeCountDown != null) {
            mTimeCountDown.start();
        }
    }

    public void cancel() {
        if (mTimeCountDown != null) {
            mTimeCountDown.cancel();
        }
        setClickable(true);
        setText(mTextString);
    }

    public void destory() {
        if (mTimeCountDown != null) {
            mTimeCountDown.cancel();
            mTimeCountDown = null;
        }
    }

    private class TimeCountDown extends CountDownTimer {

        TimeCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            setClickable(false);
            setText(String.format("%s秒", millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            setClickable(true);
            setText(mTextString);
        }
    }
}