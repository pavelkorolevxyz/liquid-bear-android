package com.pillowapps.liqear.models;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.network.ImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImageModel {
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new FadeInBitmapDisplayer(500))
            .build();

    private DisplayImageOptions avatarListOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.user_dark_list)
            .showImageForEmptyUri(R.drawable.user_dark_list)
            .showImageOnFail(R.drawable.user_dark_list)
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private DisplayImageOptions groupListOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.group_dark_list)
            .showImageForEmptyUri(R.drawable.group_dark_list)
            .showImageOnFail(R.drawable.group_dark_list)
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private DisplayImageOptions artistListOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.artist_dark_list)
            .showImageForEmptyUri(R.drawable.artist_dark_list)
            .showImageOnFail(R.drawable.artist_dark_list)
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private DisplayImageOptions albumListOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.album_dark_list)
            .showImageForEmptyUri(R.drawable.album_dark_list)
            .showImageOnFail(R.drawable.album_dark_list)
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public void loadImage(String url, ImageView imageView) {
        imageLoader.displayImage(url, imageView, options);
    }

    public void loadAvatarListImage(String url, ImageView imageView) {
        AnimateFirstDisplayListener animateFirstDisplayListener = new AnimateFirstDisplayListener();
        imageLoader.displayImage(url, imageView, avatarListOptions, animateFirstDisplayListener);
    }

    public void loadGroupListImage(String url, ImageView imageView) {
        AnimateFirstDisplayListener animateFirstDisplayListener = new AnimateFirstDisplayListener();
        imageLoader.displayImage(url, imageView, groupListOptions, animateFirstDisplayListener);
    }

    public void loadArtistListImage(String url, ImageView imageView) {
        AnimateFirstDisplayListener animateFirstDisplayListener = new AnimateFirstDisplayListener();
        imageLoader.displayImage(url, imageView, artistListOptions, animateFirstDisplayListener);
    }

    public void loadAlbumListImage(String url, ImageView imageView) {
        AnimateFirstDisplayListener animateFirstDisplayListener = new AnimateFirstDisplayListener();
        imageLoader.displayImage(url, imageView, albumListOptions, animateFirstDisplayListener);
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


    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
