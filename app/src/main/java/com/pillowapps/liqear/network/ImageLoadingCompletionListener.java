package com.pillowapps.liqear.network;

import android.graphics.Bitmap;

public abstract class ImageLoadingCompletionListener implements ImageLoadingListener {
    public void onLoadingStarted() {

    }

    public void onLoadingFailed(String message) {

    }

    public abstract void onLoadingComplete(Bitmap bitmap);

    public void onLoadingCancelled() {

    }
}
