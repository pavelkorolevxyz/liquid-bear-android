package com.pillowapps.liqear.network;

import android.graphics.Bitmap;

public interface ImageLoadingListener {
    void onLoadingStarted();

    void onLoadingFailed(String message);

    void onLoadingComplete(Bitmap bitmap);

    void onLoadingCancelled();
}
