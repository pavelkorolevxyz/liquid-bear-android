package com.pillowapps.liqear.components;

public class CancellableThread extends Thread{
    private boolean cancelled = false;

    public CancellableThread(Runnable runnable) {
        super(runnable);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
