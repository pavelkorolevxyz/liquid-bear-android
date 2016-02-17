package com.pillowapps.liqear.helpers.home;

import android.support.annotation.Nullable;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.entities.Track;

public class ToolbarUtils {
    public static int getPlaybackToolbarMenuRes(@Nullable Track track) {
        if (track == null) {
            return R.menu.menu_play_tab_no_current_track;
        } else {
            return R.menu.menu_play_tab;
        }
    }
}
