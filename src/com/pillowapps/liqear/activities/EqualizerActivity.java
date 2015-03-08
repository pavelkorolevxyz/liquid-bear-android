package com.pillowapps.liqear.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.audio.EqualizerManager;
import com.pillowapps.liqear.helpers.CompatEq;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class EqualizerActivity extends TrackedActivity {

    private static final List<SeekBar> seekBars = new ArrayList<SeekBar>();
    private static final int TEXT_SIZE = 14;
    private CompatEq equalizer;
    private BassBoost bassBoost;
    private LinearLayout mainLinearLayout;
    private Spinner spinner;
    private SharedPreferences preferences = PreferencesManager.getEqualizerPreferences();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setTitle(R.string.equalizer);
        setContentView(R.layout.equalizer_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mainLinearLayout = (LinearLayout) findViewById(R.id.main_layout);
        spinner = (Spinner) findViewById(R.id.spinner);
        equalizer = EqualizerManager.getEqualizer();
        bassBoost = EqualizerManager.getBassBoost();
        short numberOfPresets = equalizer.getNumberOfPresets();
        setupEqualizerFxAndUI(numberOfPresets == 0);

        checkEnabled();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long l) {
                TextView selectedText = (TextView) adapterView.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(getResources().getColor(R.color.primary_text));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void checkEnabled() {
        for (SeekBar seekBar : seekBars) {
            seekBar.setEnabled(preferences.getBoolean("enabled", true));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void setupEqualizerFxAndUI(boolean fromSaves) {
        final short bands = equalizer.getNumberOfBands();
        final SharedPreferences preferences = PreferencesManager.getEqualizerPreferences();
        if (fromSaves) {
            for (short i = 0; i < bands; i++) {
                equalizer.setBandLevel(i, (short) preferences.getInt(Constants.EQUALIZER + i, 0));
            }
            spinner.setVisibility(View.GONE);
            setupEqualizerSeeks();
        } else {
            short numberOfPresets = equalizer.getNumberOfPresets();
            String[] array = new String[numberOfPresets];
            for (int i = 0; i < numberOfPresets; i++) {
                array[i] = equalizer.getPresetName((short) i);
            }
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                    android.R.layout.simple_spinner_item, array);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection((short) preferences.getInt("selected", 0), false);
            if (!preferences.getBoolean("changed", false)) {
                setupEqualizerSeeks();
            } else {
                for (short i = 0; i < bands; i++) {
                    equalizer.setBandLevel(i, (short) preferences.getInt(
                            Constants.EQUALIZER + i, 0));
                }
                setupEqualizerSeeks();
            }

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view,
                                           int position, long l) {
                    Editor editor = PreferencesManager.getEqualizerPreferences().edit();
                    editor.putInt("selected", position);
                    editor.putBoolean("changed", false);
                    int presetIteration = 0;
                    while (presetIteration++ < 10) {
                        try {
                            equalizer.usePreset((short) position);
                        } catch (IllegalArgumentException e) {
                            continue;
                        }
                        break;
                    }
                    short bands = equalizer.getNumberOfBands();
                    for (short i = 0; i < bands; i++) {
                        editor.putInt(Constants.EQUALIZER + i, equalizer.getBandLevel(i));
                    }
                    editor.apply();
                    setupEqualizerSeeks();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    private void setupEqualizerSeeks() {
        short bands = equalizer.getNumberOfBands();

        short[] bandLevelRange = equalizer.getBandLevelRange();
        final short minEQLevel = bandLevelRange[0];
        final short maxEQLevel = bandLevelRange[1];
        mainLinearLayout.removeAllViews();
        final CheckBox checkBox = new CheckBox(this);
        checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        checkBox.setTextColor(getResources().getColor(R.color.primary_text));
        checkBox.setText(R.string.enable_equalizer);
        checkBox.setChecked(preferences.getBoolean("enabled", true));
        equalizer.setEnabled(checkBox.isChecked());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Editor editor = PreferencesManager.getEqualizerPreferences().edit();
                editor.putBoolean("enabled", isChecked);
                editor.apply();
                EqualizerManager.setEnabled(isChecked);
                checkEnabled();
            }
        });
        mainLinearLayout.addView(checkBox);
        for (short i = 0; i < bands; i++) {
            final short band = i;

            TextView freqTextView = new TextView(this);
            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            freqTextView.setTextSize(TEXT_SIZE);
            freqTextView.setTextColor(getResources().getColor(R.color.primary_text));
            freqTextView.setText((equalizer.getCenterFreq(band) / 1000) + " Hz");
            mainLinearLayout.addView(freqTextView);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            minDbTextView.setTextSize(TEXT_SIZE);
            minDbTextView.setTextColor(getResources().getColor(R.color.primary_text));
            minDbTextView.setText((minEQLevel / 100) + " dB");

            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            maxDbTextView.setText((maxEQLevel / 100) + " dB");
            maxDbTextView.setTextColor(getResources().getColor(R.color.primary_text));
            maxDbTextView.setTextSize(TEXT_SIZE);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            SeekBar bar = new SeekBar(this);
            bar.setLayoutParams(layoutParams);
            bar.setMax(maxEQLevel - minEQLevel);
            bar.setProgress(equalizer.getBandLevel(band) - minEQLevel);

            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) equalizer.setBandLevel(band, (short) (progress + minEQLevel));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    Editor editor = PreferencesManager.getEqualizerPreferences().edit();
                    editor.putInt(Constants.EQUALIZER + band, equalizer.getBandLevel(band));
                    editor.putBoolean("changed", true);
                    editor.apply();
                }
            });

            seekBars.add(bar);
            row.addView(minDbTextView);
            row.addView(bar);
            row.addView(maxDbTextView);

            mainLinearLayout.addView(row);
        }

        TextView bassBoostTextView = new TextView(this);
        bassBoostTextView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        bassBoostTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        bassBoostTextView.setTextSize(TEXT_SIZE);
        bassBoostTextView.setTextColor(getResources().getColor(R.color.primary_text));
        bassBoostTextView.setText("Bass boost");
        mainLinearLayout.addView(bassBoostTextView);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        if (bassBoost == null) return;

        SeekBar bar = new SeekBar(this);
        bar.setLayoutParams(layoutParams);
        bar.setMax(1000);
        bar.setProgress((int) bassBoost.getRoundedStrength());
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) bassBoost.setStrength((short) progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Editor editor = PreferencesManager.getEqualizerPreferences().edit();
                editor.putInt(Constants.EQUALIZER_BASS, (int) bassBoost.getRoundedStrength());
                editor.putBoolean("changed", true);
                editor.apply();
            }
        });
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.addView(bar);
        seekBars.add(bar);
        mainLinearLayout.addView(row);
    }
}
