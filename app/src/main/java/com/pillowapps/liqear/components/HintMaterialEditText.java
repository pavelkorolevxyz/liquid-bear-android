package com.pillowapps.liqear.components;

import android.content.Context;
import android.util.AttributeSet;

import com.rengwuxian.materialedittext.MaterialEditText;

public class HintMaterialEditText extends MaterialEditText {
    public HintMaterialEditText(Context context) {
        super(context);
        updateHint();
    }

    public HintMaterialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateHint();
    }

    public HintMaterialEditText(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        updateHint();
    }

    public void updateHint(String hint) {
        setHint(hint);
        setFloatingLabelText(hint);
    }

    public void updateHint() {
        CharSequence hint = getHint();
        updateHint(hint.toString());
    }
}
