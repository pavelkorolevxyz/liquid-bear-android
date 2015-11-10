package com.pillowapps.liqear.helpers;


import com.pillowapps.liqear.R;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.entities.Track;

public class ButtonStateUtils {

    public static int getShuffleButtonImage() {
        switch (Timeline.getInstance().getShuffleMode()) {
            case SHUFFLE:
                return R.drawable.ic_shuffle_active;
            case DEFAULT:
            default:
                return R.drawable.ic_shuffle;
        }
    }

    public static int getRepeatButtonImage() {
        switch (Timeline.getInstance().getRepeatMode()) {
            case REPEAT:
                return R.drawable.ic_repeat_active;
            case REPEAT_PLAYLIST:
            default:
                return R.drawable.ic_repeat;
        }
    }

    public static int getLoveButtonImage() {
        Track track = Timeline.getInstance().getCurrentTrack();
        if (track == null) return R.drawable.ic_love_weight_centered;
        return track.isLoved()
                ? R.drawable.ic_love_active_weight_centered
                : R.drawable.ic_love_weight_centered;
    }

}
