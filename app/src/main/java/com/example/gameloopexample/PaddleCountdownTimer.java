package com.example.gameloopexample;

import android.os.CountDownTimer;

/**
 * Created by John on 4/4/2015.
 */
public class PaddleCountdownTimer extends CountDownTimer {
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public PaddleCountdownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public PaddleCountdownTimer(long millisInFuture) {
        super(millisInFuture, millisInFuture);
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {

    }
}
