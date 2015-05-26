package com.pillowapps.liqear.entities.events;

public class BufferizationEvent {
    private int buffered;

    public BufferizationEvent(int buffered) {
        this.buffered = buffered;
    }

    public int getBuffered() {
        return buffered;
    }
}
