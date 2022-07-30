package xyz.illuminate.ui.Components;

import android.text.TextPaint;

import xyz.illuminate.app.AndroidUtilities;

public class URLSpanNoUnderlineBold extends URLSpanNoUnderline {

    public URLSpanNoUnderlineBold(String url) {
        super(url != null ? url.replace('\u202E', ' ') : url);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        ds.setUnderlineText(false);
    }
}
