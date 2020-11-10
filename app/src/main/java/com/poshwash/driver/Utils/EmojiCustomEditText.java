package com.poshwash.driver.Utils;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by anandj on 9/27/2017.
 */

public class EmojiCustomEditText extends EditText {
    public EmojiCustomEditText(Context context) {
        super(context);
        init();
    }

    public EmojiCustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmojiCustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }

    private class EmojiExcludeFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    }

}
