package com.pillowapps.liqear.models;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pillowapps.liqear.network.ImageLoadingListener;

public class ImageModel {
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(500))
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public void loadImage(String url, ImageView imageView) {
        imageLoader.displayImage(url, imageView, options);
    }

    public void loadImage(String url, final ImageLoadingListener listener) {
        imageLoader.loadImage(url, options, new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                listener.onLoadingStarted();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "Input/Output error";
                        break;
                    case OUT_OF_MEMORY:
                        message = "Out Of Memory error";
                        break;
                    case UNKNOWN:
                        message = "Unknown error";
                        break;
                    default:
                        break;
                }
                listener.onLoadingFailed(message);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                listener.onLoadingComplete(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                listener.onLoadingCancelled();
            }
        });
    }

    public void loadImage(String url, ImageView imageView, final ImageLoadingListener listener) {
        imageLoader.displayImage(url, imageView, options, new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                listener.onLoadingStarted();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "Input/Output error";
                        break;
                    case OUT_OF_MEMORY:
                        message = "Out Of Memory error";
                        break;
                    case UNKNOWN:
                        message = "Unknown error";
                        break;
                    default:
                        break;
                }
                listener.onLoadingFailed(message);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                listener.onLoadingComplete(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                listener.onLoadingCancelled();
            }
        });
    }
}
