package com.pillowapps.liqear.components;

import android.content.Context;
import android.view.ContextThemeWrapper;

import com.pillowapps.liqear.R;

import de.cketti.library.changelog.ChangeLog;

public class DarkThemeChangeLog extends ChangeLog {
    public static final String DARK_THEME_CSS =
            "body { color: #ffffff; background-color: #282828; }" + "\n" + DEFAULT_CSS;

    public DarkThemeChangeLog(Context context) {
        super(new ContextThemeWrapper(context, R.style.DarkTheme), DARK_THEME_CSS);
    }
}