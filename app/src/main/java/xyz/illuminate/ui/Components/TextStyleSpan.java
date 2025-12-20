package xyz.illuminate.ui.Components;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.util.Log;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.server.TLRPC;
import xyz.illuminate.ui.ActionBar.Theme;

public class TextStyleSpan extends MetricAffectingSpan {
    private static final String TAG = "TextStyleSpan";

    public final static int FLAG_STYLE_BOLD = 1;
    public final static int FLAG_STYLE_ITALIC = 2;
    public final static int FLAG_STYLE_MONO = 4;
    public final static int FLAG_STYLE_STRIKE = 8;
    public final static int FLAG_STYLE_UNDERLINE = 16;
    public final static int FLAG_STYLE_QUOTE = 32;
    public final static int FLAG_STYLE_MENTION = 64;
    public final static int FLAG_STYLE_URL = 128;
    public final static int FLAG_STYLE_SPOILER = 256;
    public final static int FLAG_STYLE_SPOILER_REVEALED = 512;
    public final static int FLAG_STYLE_BOLD_ITALIC = 1024;
    private int textSize;
    private int color;
    private final TextStyleRun style;
    public TextStyleSpan(TextStyleRun run) {
        this(run, 0, 0);
    }

    public TextStyleSpan(TextStyleRun run, int size) {
        this(run, size, 0);
    }

    public TextStyleSpan(TextStyleRun run, int size, int textColor) {
        style = run;
        if (size > 0) {
            textSize = size;
        }
        color = textColor;
    }

    public int getStyleFlags() {
        return style.flags;
    }

    public TextStyleRun getTextStyleRun() {
        return style;
    }

    public Typeface getTypeface() {
        return style.getTypeface();
    }

    public void setColor(int value) {
        color = value;
    }

    public boolean isSpoiler() {
        return (style.flags & FLAG_STYLE_SPOILER) > 0;
    }

    public void setSpoilerRevealed(boolean b) {
        if (b)
            style.flags |= FLAG_STYLE_SPOILER_REVEALED;
        else style.flags &= ~FLAG_STYLE_SPOILER_REVEALED;
    }

    public boolean isMono() {
        return style.getTypeface() == Typeface.MONOSPACE;
    }

    public boolean isBold() {
        return style.getTypeface() == AndroidUtilities.getTypeface("fonts/opensans_bold.ttf");
    }

    public boolean isItalic() {
        return style.getTypeface() == AndroidUtilities.getTypeface("fonts/ritalic.ttf");
    }

    public boolean isBoldItalic() {
        return style.getTypeface() == AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf");
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        if (textSize != 0) {
            p.setTextSize(textSize);
        }
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        style.applyStyle(p);
    }

    @Override
    public void updateDrawState(TextPaint p) {
        if (textSize != 0) {
            p.setTextSize(textSize);
        }
        if (color != 0) {
            p.setColor(color);
        }
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        style.applyStyle(p);
    }

    public static class TextStyleRun {

        public int flags;
        public int start;
        public int end;
        public TLRPC.MessageEntity urlEntity;

        public TextStyleRun() {
            flags = 0;
        }

        public TextStyleRun(TextStyleRun run) {
            flags = run.flags;
            start = run.start;
            end = run.end;
            urlEntity = run.urlEntity;
        }

        public void merge(TextStyleRun run) {
            flags |= run.flags;
        }

        public void replace(TextStyleRun run) {
            flags = run.flags;
        }

        public void applyStyle(TextPaint p) {
            try {
                if ((flags & FLAG_STYLE_BOLD) != 0) {
                    Typeface typeface = AndroidUtilities.getTypeface("fonts/opensans_bold.ttf");
                    if (typeface != null) {
                        p.setTypeface(typeface);
                    } else {
                        android.util.Log.e("TextStyleSpan", "Bold font not found, using default bold");
                        p.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                } else if ((flags & FLAG_STYLE_ITALIC) != 0) {
                    Typeface typeface = AndroidUtilities.getTypeface("fonts/ritalic.ttf");
                    if (typeface != null) {
                        p.setTypeface(typeface);
                    } else {
                        android.util.Log.e("TextStyleSpan", "Italic font not found, using default italic");
                        p.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                    }
                } else if ((flags & FLAG_STYLE_BOLD_ITALIC) != 0) {
                    Typeface typeface = AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf");
                    if (typeface != null) {
                        p.setTypeface(typeface);
                    } else {
                        android.util.Log.e("TextStyleSpan", "Bold-italic font not found, using default bold-italic");
                        p.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                    }
                } else if ((flags & FLAG_STYLE_MONO) != 0) {
                    p.setTypeface(Typeface.MONOSPACE);
                }

                if ((flags & FLAG_STYLE_STRIKE) != 0) {
                    p.setStrikeThruText(true);
                }

                if ((flags & FLAG_STYLE_UNDERLINE) != 0) {
                    p.setUnderlineText(true);
                }

                if ((flags & FLAG_STYLE_SPOILER) != 0) {
                    // Apply spoiler effect (gray background and text color)
                    p.setColor(0xFF808080); // Gray color
                    p.bgColor = 0xFFE0E0E0; // Light gray background
                }
            } catch (Exception e) {
                android.util.Log.e("TextStyleSpan", "Error applying text style", e);
            }
        }

        public Typeface getTypeface() {
            if ((flags & FLAG_STYLE_BOLD) > 0) {
                Typeface typeface = AndroidUtilities.getTypeface("fonts/opensans_bold.ttf");
                if (typeface != null) {
                    return typeface;
                } else {
                    Log.e(TAG, "Font file not found: fonts/opensans_bold.ttf");
                    return Typeface.DEFAULT_BOLD;
                }
            } else if ((flags & FLAG_STYLE_ITALIC) > 0) {
                Typeface typeface = AndroidUtilities.getTypeface("fonts/ritalic.ttf");
                if (typeface != null) {
                    return typeface;
                } else {
                    Log.e(TAG, "Font file not found: fonts/ritalic.ttf");
                    return Typeface.defaultFromStyle(Typeface.ITALIC);
                }
            } else if ((flags & FLAG_STYLE_MONO) > 0) {
                return Typeface.MONOSPACE;
            } else if ((flags & FLAG_STYLE_BOLD_ITALIC) > 0) {
                Typeface typeface = AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf");
                if (typeface != null) {
                    return typeface;
                } else {
                    Log.e(TAG, "Font file not found: fonts/rmediumitalic.ttf");
                    return Typeface.defaultFromStyle(Typeface.BOLD_ITALIC);
                }
            }
            return Typeface.DEFAULT;
        }
    }
}
