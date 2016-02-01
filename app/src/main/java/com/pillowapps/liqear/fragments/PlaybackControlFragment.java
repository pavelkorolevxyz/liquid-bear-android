package com.pillowapps.liqear.fragments;

import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pillowapps.liqear.R;

import butterknife.Bind;

public class PlaybackControlFragment extends Fragment {
    @Bind(R.id.play_pause_button_playback_tab)
    protected ImageButton playPauseButton;
    @Bind(R.id.seek_bar_playback_tab)
    protected SeekBar seekBar;
    @Bind(R.id.time_text_view_playback_tab)
    protected TextView timeTextView;
    @Bind(R.id.time_inverted_text_view_playback_tab)
    protected TextView timeDurationTextView;
    @Bind(R.id.next_button_playback_tab)
    protected ImageButton nextButton;
    @Bind(R.id.prev_button_playback_tab)
    protected ImageButton prevButton;
    @Bind(R.id.shuffle_button_playback_tab)
    protected ImageButton shuffleButton;
    @Bind(R.id.repeat_button_playback_tab)
    protected ImageButton repeatButton;
//
//    private MainActivity mainActivity;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        mainActivity = (MainActivity) getActivity();
//        mainActivity.register();
//        View v = inflater.inflate(R.layout.playback_controls_fragment_layout, null, false);
//        ButterKnife.inject(this, v);
//        initListeners();
//        return v;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        shuffleButton.setImageResource(ButtonStateUtils.getShuffleButtonImage());
//        repeatButton.setImageResource(ButtonStateUtils.getRepeatButtonImage());
//    }
//
//    private void initListeners() {
//        shuffleButton.setOnClickListener(v -> {
//            Timeline.getInstance().toggleShuffle();
//            shuffleButton.setImageResource(ButtonStateUtils.getShuffleButtonImage());
//            mainActivity.getMusicService().updateWidgets();
//        });
//        repeatButton.setOnClickListener(v -> {
//            Timeline.getInstance().toggleRepeat();
//            repeatButton.setImageResource(ButtonStateUtils.getRepeatButtonImage());
//            mainActivity.getMusicService().updateWidgets();
//        });
//
//        // Playback controlling.
//        playPauseButton.setOnClickListener(v -> mainActivity.playPause());
//
//        nextButton.setOnClickListener(v -> mainActivity.getMusicService().next());
//
//        prevButton.setOnClickListener(v -> mainActivity.getMusicService().prev());
//
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                mainActivity.getMusicService().seekTo(seekBar.getProgress()
//                        * mainActivity.getMusicService().getDuration() / 100);
//                mainActivity.getMusicService().startPlayProgressUpdater();
//            }
//
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                mainActivity.getMusicService().stopPlayProgressUpdater();
//            }
//
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (!fromUser) return;
//                int timeFromBeginning = seekBar.getProgress() *
//                        mainActivity.getMusicService().getDuration() / 100000;
//                int timeToEnd = mainActivity.getMusicService().getDuration() / 1000
//                        - timeFromBeginning;
//                timeTextView.setText(String.format("-%s", TimeUtils.secondsToMinuteString(timeToEnd)));
//            }
//        });
//
//        timeTextView.setOnClickListener(view -> {
//            SharedPreferences preferences = SharedPreferencesManager.getPreferences();
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putBoolean(Constants.TIME_INVERTED,
//                    !preferences.getBoolean(Constants.TIME_INVERTED, false));
//            editor.apply();
//        });
//    }
//
//    public ImageButton getPlayPauseButton() {
//        return playPauseButton;
//    }
//
//    public SeekBar getSeekBar() {
//        return seekBar;
//    }
//
//    public TextView getTimeTextView() {
//        return timeTextView;
//    }
//
//    public TextView getTimeDurationTextView() {
//        return timeDurationTextView;
//    }
//
//    public ImageButton getNextButton() {
//        return nextButton;
//    }
//
//    public ImageButton getPrevButton() {
//        return prevButton;
//    }
//
//    public ImageButton getShuffleButton() {
//        return shuffleButton;
//    }
//
//    public ImageButton getRepeatButton() {
//        return repeatButton;
//    }

}
