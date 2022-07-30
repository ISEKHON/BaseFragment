package xyz.illuminate.ui.Components;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.SharedConfig;
import xyz.illuminate.ui.ActionBar.Theme;

public class URLSpanMono extends MetricAffectingSpan {

    private final CharSequence currentMessage;
    private final int currentStart;
    private final int currentEnd;
    private final byte currentType;
    private final TextStyleSpan.TextStyleRun style;

    public URLSpanMono(CharSequence message, int start, int end, byte type) {
        this(message, start, end, type, null);
    }

    public URLSpanMono(CharSequence message, int start, int end, byte type, TextStyleSpan.TextStyleRun run) {
        currentMessage = message;
        currentStart = start;
        currentEnd = end;
        currentType = type;
        style = run;
    }

    public void copyToClipboard() {
        AndroidUtilities.addToClipboard(currentMessage.subSequence(currentStart, currentEnd).toString());
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTextSize(AndroidUtilities.dp(SharedConfig.fontSize - 1));
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        if (style != null) {
            style.applyStyle(p);
        } else {
            p.setTypeface(Typeface.MONOSPACE);
        }
    }

    @Override
    public void updateDrawState(TextPaint p) {
        p.setTextSize(AndroidUtilities.dp(SharedConfig.fontSize - 1));
        if (currentType == 2) {
            p.setColor(0xffffffff);
        } else if (currentType == 1) {
            p.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        } else {
            p.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }
        if (style != null) {
            style.applyStyle(p);
        } else {
            p.setTypeface(Typeface.MONOSPACE);
            p.setUnderlineText(false);
        }
    }
}
