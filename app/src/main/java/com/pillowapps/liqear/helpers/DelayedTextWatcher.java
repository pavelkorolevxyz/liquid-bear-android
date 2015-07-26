package com.pillowapps.liqear.helpers;

import android.os.Handler;
import android.text.TextWatcher;

public abstract class DelayedTextWatcher implements TextWatcher {
    private static final long DELAY = 200;
    private Handler handler = new Handler();

    public void runTaskWithDelay(Runnable runnable) {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(runnable, DELAY);
    }
}
