package com.pillowapps.liqear.helpers;


import com.pillowapps.liqear.R;
import com.pillowapps.liqear.entities.RepeatMode;
import com.pillowapps.liqear.entities.ShuffleMode;
import com.pillowapps.liqear.entities.Track;

public class ButtonStateUtils {

    private ButtonStateUtils() {
        // no-op
    }

    public static int getShuffleButtonImage(ShuffleMode shuffleMode) {
        switch (shuffleMode) {
            case SHUFFLE:
                return R.drawable.ic_shuffle_active;
            case DEFAULT:
            default:
                return R.drawable.ic_shuffle;
        }
    }

    public static int getRepeatButtonImage(RepeatMode repeatMode) {
        switch (repeatMode) {
            case REPEAT:
                return R.drawable.ic_repeat_active;
            case REPEAT_PLAYLIST:
            default:
                return R.drawable.ic_repeat;
        }
    }

    public static int getLoveButtonImage(Track currentTrack) {
        return currentTrack != null && currentTrack.isLoved()
                ? R.drawable.ic_love_active_weight_centered
                : R.drawable.ic_love_weight_centered;
    }

}
