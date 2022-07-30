package xyz.illuminate.ui.Components;

import android.content.Context;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import xyz.illuminate.app.AndroidUtilities;

public class ProgressButton extends LinearLayout {

    TextView textView;
    RadialProgressView progressView;
    boolean isShown;

    public ProgressButton(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
        textView = new TextView(context);
        textView.setAllCaps(false);
        textView.setTextSize(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM, 14);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/opensans_regular.ttf"));
        addView(textView);

        progressView = new RadialProgressView(context);
        progressView.setVisibility(GONE);
        progressView.setSize(AndroidUtilities.dp(24));
        ViewHelper.setPadding(this, 8, 0, 8, 0);

        setMinimumHeight(AndroidUtilities.dp(42));
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setProgressColor(int color) {
        progressView.setProgressColor(color);
    }

    public void setProgress(int progress) {
        if (progress != 0) {
            progressView.setProgress((float) progress / 100);
        }
    }

    public RadialProgressView getProgressView() {
        return progressView;
    }

    public void showProgress(boolean show) {
        if (isShown != show) {
            TransitionManager.beginDelayedTransition(this, new TransitionSet().addTransition(new Fade()));
            if (show) {
                textView.setVisibility(GONE);
                progressView.setVisibility(VISIBLE);
            } else {
                textView.setVisibility(VISIBLE);
                progressView.setVisibility(GONE);
            }
            isShown = show;
        }
    }
}
