package xyz.illuminate.ui.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.Theme;

public class FontTextView extends TextView {

    public FontTextView(Context context) {
        super(context);
        init(null, context, -1);
    }

    public FontTextView(Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context, -1);
    }

    public FontTextView(Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, context, defStyle);
    }

    public void init(AttributeSet attrs, final Context context, int defStyle) {
        String fontPath = "opensans_regular.ttf";
        String themeKey = null;
        TypedArray a = null;
        if (attrs != null) {
            a = context.obtainStyledAttributes(attrs, R.styleable.FontTextView);
            int indexCount = a.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int attr = a.getIndex(i);
                if (attr == 0) {
                    fontPath = (String) a.getText(attr);
                } else if (attr == 1) {
                    themeKey = (String) a.getText(attr);
                }
            }
        }
        setTypeface(AndroidUtilities.getTypeface("fonts/" + fontPath));
        if (themeKey != null) {
            setTextColor(Theme.getColor(themeKey));
        }
        if (a != null) {
            a.recycle();
        }
    }
}
