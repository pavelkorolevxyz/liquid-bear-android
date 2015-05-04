package com.pillowapps.liqear.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.activities.MainActivity;
import com.pillowapps.liqear.audio.Timeline;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.helpers.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PlaybackControlFragment extends Fragment {
    @InjectView(R.id.play_pause_button_playback_tab)
    protected ImageButton playPauseButton;
    @InjectView(R.id.seek_bar_playback_tab)
    protected SeekBar seekBar;
    @InjectView(R.id.time_text_view_playback_tab)
    protected TextView timeTextView;
    @InjectView(R.id.time_inverted_text_view_playback_tab)
    protected TextView timeDurationTextView;
    @InjectView(R.id.next_button_playback_tab)
    protected ImageButton nextButton;
    @InjectView(R.id.prev_button_playback_tab)
    protected ImageButton prevButton;
    @InjectView(R.id.shuffle_button_playback_tab)
    protected ImageButton shuffleButton;
    @InjectView(R.id.repeat_button_playback_tab)
    protected ImageButton repeatButton;

    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        mainActivity.init();
        View v = inflater.inflate(R.layout.playback_controls_fragment_layout, null, false);
        ButterKnife.inject(this, v);
        initListeners();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        shuffleButton.setImageResource(Utils.getShuffleButtonImage());
        repeatButton.setImageResource(Utils.getRepeatButtonImage());
    }

    private void initListeners() {
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Timeline.getInstance().toggleShuffle();
                shuffleButton.setImageResource(Utils.getShuffleButtonImage());
                mainActivity.getMusicPlaybackService().updateWidgets();
            }
        });
        repeatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Timeline.getInstance().toggleRepeat();
                repeatButton.setImageResource(Utils.getRepeatButtonImage());
                mainActivity.getMusicPlaybackService().updateWidgets();
            }
        });

        // Playback controlling.
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainActivity.playPause();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainActivity.getMusicPlaybackService().next();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainActivity.getMusicPlaybackService().prev();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar seekBar) {
                mainActivity.getMusicPlaybackService().seekTo(seekBar.getProgress()
                        * mainActivity.getMusicPlaybackService().getDuration() / 100);
                mainActivity.getMusicPlaybackService().startPlayProgressUpdater();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                mainActivity.getMusicPlaybackService().stopPlayProgressUpdater();
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                int timeFromBeginning = seekBar.getProgress() *
                        mainActivity.getMusicPlaybackService().getDuration() / 100000;
                int timeToEnd = mainActivity.getMusicPlaybackService().getDuration() / 1000
                        - timeFromBeginning;
                timeTextView.setText("-" + Utils.secondsToString(timeToEnd));
            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferencesManager.getPreferences();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Constants.TIME_INVERTED,
                        !preferences.getBoolean(Constants.TIME_INVERTED, false));
                editor.apply();
            }
        });
    }

    public ImageButton getPlayPauseButton() {
        return playPauseButton;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public TextView getTimeTextView() {
        return timeTextView;
    }

    public TextView getTimeDurationTextView() {
        return timeDurationTextView;
    }

    public ImageButton getNextButton() {
        return nextButton;
    }

    public ImageButton getPrevButton() {
        return prevButton;
    }

    public ImageButton getShuffleButton() {
        return shuffleButton;
    }

    public ImageButton getRepeatButton() {
        return repeatButton;
    }

}
