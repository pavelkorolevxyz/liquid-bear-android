package com.pillowapps.liqear.components.viewers;

import android.content.Context;
import android.view.View;
import android.widget.Spinner;

import com.pillowapps.liqear.R;

public class SpinnerLastfmArtistViewerPage extends LastfmArtistViewerPage {
    private Spinner spinner;
    private String period;

    public SpinnerLastfmArtistViewerPage(Context context, View view, String title) {
        super(context, view, title);
        spinner = (Spinner) view.findViewById(R.id.spinner);
    }

    public SpinnerLastfmArtistViewerPage(Context context, View view, int titleRes) {
        super(context, view, titleRes);
        spinner = (Spinner) view.findViewById(R.id.spinner);
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public void setSpinner(Spinner spinner) {
        this.spinner = spinner;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
