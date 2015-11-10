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
import com.pillowapps.liqear.helpers.SharedPreferencesManager;
import com.pillowapps.liqear.helpers.ButtonStateUtils;
import com.pillowapps.liqear.helpers.TimeUtils;

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
        shuffleButton.setImageResource(ButtonStateUtils.getShuffleButtonImage());
        repeatButton.setImageResource(ButtonStateUtils.getRepeatButtonImage());
    }

    private void initListeners() {
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Timeline.getInstance().toggleShuffle();
                shuffleButton.setImageResource(ButtonStateUtils.getShuffleButtonImage());
                mainActivity.getMusicService().updateWidgets();
            }
        });
        repeatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Timeline.getInstance().toggleRepeat();
                repeatButton.setImageResource(ButtonStateUtils.getRepeatButtonImage());
                mainActivity.getMusicService().updateWidgets();
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
                mainActivity.getMusicService().next();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainActivity.getMusicService().prev();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar seekBar) {
                mainActivity.getMusicService().seekTo(seekBar.getProgress()
                        * mainActivity.getMusicService().getDuration() / 100);
                mainActivity.getMusicService().startPlayProgressUpdater();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                mainActivity.getMusicService().stopPlayProgressUpdater();
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                int timeFromBeginning = seekBar.getProgress() *
                        mainActivity.getMusicService().getDuration() / 100000;
                int timeToEnd = mainActivity.getMusicService().getDuration() / 1000
                        - timeFromBeginning;
                timeTextView.setText(String.format("-%s", TimeUtils.secondsToMinuteString(timeToEnd)));
            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = SharedPreferencesManager.getPreferences();
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
