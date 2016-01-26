package com.pillowapps.liqear.listeners;

import android.view.MotionEvent;
import android.view.View;

import com.pillowapps.liqear.listeners.OnTopToBottomSwipeListener;

public class OnSwipeListener implements View.OnTouchListener {
    static final int MIN_DISTANCE = 100;
    private float downY;
    private OnTopToBottomSwipeListener listener;

    public OnSwipeListener(OnTopToBottomSwipeListener listener) {
        this.listener = listener;
    }

    public void onTopToBottomSwipe() {
        if (listener != null) listener.onTopToBottomSwipe();
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                float upY = event.getY();

                float deltaY = downY - upY;

                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    if (deltaY < 0) {
                        this.onTopToBottomSwipe();
                        return true;
                    }
                } else {
                    return false;
                }
                return true;
            }
            default:
                return false;
        }
    }

}