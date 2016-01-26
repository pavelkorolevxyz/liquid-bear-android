package com.pillowapps.liqear.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.pillowapps.liqear.R;
import com.pillowapps.liqear.listeners.OnImageLoadedListener;

public class ImageModel {

    public void loadImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .dontAnimate()
                .into(imageView);
    }

    public void loadAvatarListImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.user_dark_list)
                .dontAnimate()
                .into(imageView);
    }

    public void loadGroupListImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.group_dark_list)
                .dontAnimate()
                .into(imageView);
    }

    public void loadArtistListImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(R.drawable.artist_dark_list)
                .centerCrop()
                .dontAnimate()
                .into(imageView);
    }

    public void loadAlbumListImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.album_dark_list)
                .dontAnimate()
                .into(imageView);
    }

    public void loadImage(Context context, String url, final OnImageLoadedListener listener) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        listener.onLoadingComplete(resource);
                    }
                });
    }

    public void loadImage(String url, ImageView imageView, final OnImageLoadedListener listener) {
        Glide.with(imageView.getContext())
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(resource);
                        listener.onLoadingComplete(resource);
                    }
                });
    }

}
