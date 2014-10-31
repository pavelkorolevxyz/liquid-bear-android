package com.pillowapps.liqear.components;

import android.graphics.Bitmap;

public class ProbablyCancelledBitmap{
    private Bitmap bitmap;
    private boolean cancelled = false;

    public ProbablyCancelledBitmap(Bitmap bitmap, boolean cancelled) {
        this.bitmap = bitmap;
        this.cancelled = cancelled;
    }

    public ProbablyCancelledBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.cancelled = false;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}