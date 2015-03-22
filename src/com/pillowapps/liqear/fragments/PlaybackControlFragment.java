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
import com.pillowapps.liqear.audio.deprecated.AudioTimeline;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PreferencesManager;
import com.pillowapps.liqear.helpers.Utils;

public class PlaybackControlFragment extends Fragment {

    private ImageButton playPauseButton;
    private SeekBar seekBar;
    private TextView timeTextView;
    private TextView timeDurationTextView;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private ImageButton shuffleButton;
    private ImageButton repeatButton;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        mainActivity.init();
        View v = inflater.inflate(R.layout.playback_controls_fragment_layout, null);
        playPauseButton = (ImageButton) v.findViewById(R.id.play_pause_button_playback_tab);
        seekBar = (SeekBar) v.findViewById(R.id.seek_bar_playback_tab);
        timeTextView = (TextView) v.findViewById(R.id.time_text_view_playback_tab);
        timeDurationTextView = (TextView) v.findViewById(R.id.time_inverted_text_view_playback_tab);
        nextButton = (ImageButton) v.findViewById(R.id.next_button_playback_tab);
        prevButton = (ImageButton) v.findViewById(R.id.prev_button_playback_tab);
        shuffleButton = (ImageButton) v.findViewById(R.id.shuffle_button_playback_tab);
        repeatButton = (ImageButton) v.findViewById(R.id.repeat_button_playback_tab);
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
                AudioTimeline.toggleShuffle();
                shuffleButton.setImageResource(Utils.getShuffleButtonImage());
                mainActivity.getMusicPlaybackService().updateWidgets();
            }
        });
        repeatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AudioTimeline.toggleRepeat();
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
