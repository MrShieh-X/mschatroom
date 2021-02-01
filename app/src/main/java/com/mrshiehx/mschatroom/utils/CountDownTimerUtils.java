package com.mrshiehx.mschatroom.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;

import com.mrshiehx.mschatroom.R;

import java.lang.ref.WeakReference;

//发送验证码倒数类
public class CountDownTimerUtils extends CountDownTimer {
    private WeakReference<Button> mButton;
    Context context;
    boolean running;

    public CountDownTimerUtils(Context context, Button mButton, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.context = context;
        this.mButton = new WeakReference(mButton);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (mButton.get() == null) {
            cancle();
            return;
        }
        mButton.get().setEnabled(false);
        mButton.get().setText(context.getResources().getString(R.string.button_get_captcha) + "(" + millisUntilFinished / 999 + ")");
        this.running = true;
        //RegisterScreen.gettingCaptcha=true;
        //  mTextView.setBackgroundResource(R.drawable.bg_identify_code_press); //设置按钮为灰
        //SpannableString spannableString = new SpannableString(mButton.get().getText().toString());
        //ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
        //spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        //mButton.get().setText(spannableString);
    }

    @Override
    public void onFinish() {
        if (mButton.get() == null) {
            cancle();
            return;
        }
        mButton.get().setText(context.getResources().getString(R.string.button_get_captcha));
        mButton.get().setEnabled(true);
        this.running = false;
        //RegisterScreen.gettingCaptcha=false;
    }

    public void cancle() {
        if (this != null) {
            this.cancel();
            this.running = false;
        }
    }

    public boolean isRunning() {
        return running;
    }
}