package xyz.illuminate.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.FrameLayout;

import androidx.annotation.CheckResult;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.BaseFragment;

public final class BulletinFactory {

    public static BulletinFactory of(BaseFragment fragment) {
        return new BulletinFactory(fragment);
    }

    public static BulletinFactory of(FrameLayout containerLayout) {
        return new BulletinFactory(containerLayout);
    }

    public static boolean canShowBulletin(BaseFragment fragment) {
        return fragment != null && fragment.getParentActivity() != null && fragment.getLayoutContainer() != null;
    }

    public static final int ICON_TYPE_NOT_FOUND = 0;
    public static final int ICON_TYPE_WARNING = 1;

    private final BaseFragment fragment;
    private final FrameLayout containerLayout;

    private BulletinFactory(BaseFragment fragment) {
        this.fragment = fragment;
        this.containerLayout = null;
    }

    private BulletinFactory(FrameLayout containerLayout) {
        this.containerLayout = containerLayout;
        this.fragment = null;
    }

    public Bulletin createSimpleBulletin(int iconRawId, String text) {
        final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext());
        layout.setAnimation(iconRawId, 36, 36);
        layout.textView.setText(text);
        layout.textView.setSingleLine(false);
        layout.textView.setMaxLines(2);
        return create(layout, Bulletin.DURATION_SHORT);
    }

    public Bulletin createSimpleBulletin(int iconRawId, CharSequence text, CharSequence subtext) {
        final Bulletin.TwoLineLottieLayout layout = new Bulletin.TwoLineLottieLayout(getContext());
        layout.setAnimation(iconRawId, 36, 36);
        layout.titleTextView.setText(text);
        layout.subtitleTextView.setText(subtext);
        return create(layout, Bulletin.DURATION_SHORT);
    }

    public Bulletin createSimpleBulletin(int iconRawId, CharSequence text, CharSequence button, Runnable onButtonClick) {
        final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext());
        layout.setAnimation(iconRawId, 36, 36);
        layout.textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        layout.textView.setSingleLine(false);
        layout.textView.setMaxLines(3);
        layout.textView.setText(text);
        layout.setButton(new Bulletin.UndoButton(getContext(), true).setText(button).setUndoAction(onButtonClick));
        return create(layout, Bulletin.DURATION_SHORT);
    }

    public Bulletin createSimpleBulletin(Drawable drawable, CharSequence text, String button, Runnable onButtonClick) {
        final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext());
        layout.imageView.setImageDrawable(drawable);
        layout.textView.setText(text);
        layout.textView.setSingleLine(false);
        layout.textView.setMaxLines(2);
        layout.setButton(new Bulletin.UndoButton(getContext(), true).setText(button).setUndoAction(onButtonClick));
        return create(layout, Bulletin.DURATION_LONG);
    }

    public Bulletin createErrorBulletin(CharSequence errorMessage) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext());
        layout.setAnimation(R.raw.chats_infotip);
        layout.textView.setText(errorMessage);
        layout.textView.setSingleLine(false);
        layout.textView.setMaxLines(2);
        return create(layout, Bulletin.DURATION_SHORT);
    }

    public Bulletin createErrorBulletinSubtitle(CharSequence errorMessage, CharSequence errorDescription) {
        Bulletin.TwoLineLottieLayout layout = new Bulletin.TwoLineLottieLayout(getContext());
        layout.setAnimation(R.raw.chats_infotip);
        layout.titleTextView.setText(errorMessage);
        layout.subtitleTextView.setText(errorDescription);
        return create(layout, Bulletin.DURATION_SHORT);
    }

    @CheckResult
    public Bulletin createCopyLinkBulletin() {
        return createCopyLinkBulletin(false);
    }

    @CheckResult
    public Bulletin createCopyBulletin(String message) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext());
        layout.setAnimation(R.raw.copy, 36, 36, "NULL ROTATION", "Back", "Front");
        layout.textView.setText(message);
        return create(layout, Bulletin.DURATION_SHORT);
    }

    @CheckResult
    public Bulletin createCopyLinkBulletin(boolean isPrivate) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        if (isPrivate) {
            final Bulletin.TwoLineLottieLayout layout = new Bulletin.TwoLineLottieLayout(getContext());
            layout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
            layout.titleTextView.setText("LinkCopied");
            layout.subtitleTextView.setText("LinkCopiedPrivateInfo");
            return create(layout, Bulletin.DURATION_LONG);
        } else {
            final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext());
            layout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
            layout.textView.setText("LinkCopied");
            return create(layout, Bulletin.DURATION_SHORT);
        }
    }

    @CheckResult
    public Bulletin createCopyLinkBulletin(String text) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext());
        layout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
        layout.textView.setText(text);
        return create(layout, Bulletin.DURATION_SHORT);
    }

    private Bulletin create(Bulletin.Layout layout, int duration) {
        if (fragment != null) {
            return Bulletin.make(fragment, layout, duration);
        } else {
            return Bulletin.make(containerLayout, layout, duration);
        }
    }

    private Context getContext() {
        return fragment != null ? fragment.getParentActivity() : containerLayout.getContext();
    }

//region Static Factory

    @CheckResult
    public static Bulletin createCopyLinkBulletin(BaseFragment fragment) {
        return of(fragment).createCopyLinkBulletin();
    }

    @CheckResult
    public static Bulletin createCopyLinkBulletin(FrameLayout containerView) {
        return of(containerView).createCopyLinkBulletin();
    }
//endregion
}
