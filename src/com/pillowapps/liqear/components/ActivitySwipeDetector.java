package com.pillowapps.liqear.components;

import android.view.MotionEvent;
import android.view.View;
import com.pillowapps.liqear.activities.MainActivity;

public class ActivitySwipeDetector implements View.OnTouchListener {
    static final int MIN_DISTANCE = 100;
    private MainActivity activity;
    private float downX, downY, upX, upY;

    public ActivitySwipeDetector(MainActivity activity) {
        this.activity = activity;
    }
    public void onTopToBottomSwipe() {
        activity.openDropButton();
    }

    public void onBottomToTopSwipe() {
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaY = downY - upY;

                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    if (deltaY < 0) {
                        this.onTopToBottomSwipe();
                        return true;
                    }
                    if (deltaY > 0) {
                        this.onBottomToTopSwipe();
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