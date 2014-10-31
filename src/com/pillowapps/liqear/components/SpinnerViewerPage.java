package com.pillowapps.liqear.components;

import android.view.View;
import android.widget.Spinner;
import com.pillowapps.liqear.R;

public class SpinnerViewerPage<T> extends ViewerPage<T> {
    private Spinner spinner;

    public SpinnerViewerPage(View tab) {
        super(tab);
        spinner = (Spinner) tab.findViewById(R.id.spinner);
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public void setSpinner(Spinner spinner) {
        this.spinner = spinner;
    }

}
