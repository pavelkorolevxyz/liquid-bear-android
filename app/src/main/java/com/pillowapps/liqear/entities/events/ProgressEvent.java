package com.pillowapps.liqear.entities.events;

public class ProgressEvent {
    private boolean show;

    public ProgressEvent(boolean show) {
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }
}
