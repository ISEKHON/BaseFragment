package xyz.illuminate.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;

public class BadgeImageView extends ImageView {

    private BadgeDrawable mBadgeDrawable;

    public BadgeImageView(Context context) {
        super(context);
        init();
    }

    public BadgeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BadgeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    public void attachBadge() {
        mBadgeDrawable.setVisible(true);
        BadgeUtils.attachBadgeDrawable(mBadgeDrawable, this);
    }

    public BadgeDrawable getOrCreateBadge() {
        if (mBadgeDrawable == null) {
            mBadgeDrawable = BadgeDrawable.create(getContext());
        }
        attachBadge();
        return mBadgeDrawable;
    }

    public void setBadgeVisibility(boolean visible) {
        mBadgeDrawable.setVisible(visible);
    }

}
