package xyz.illuminate.ui.Components;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.CheckResult;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.LocaleController;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.BaseFragment;

public final class BulletinFactory {

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

    public static BulletinFactory of(BaseFragment fragment) {
        return new BulletinFactory(fragment);
    }

    public static BulletinFactory of(FrameLayout containerLayout) {
        return new BulletinFactory(containerLayout);
    }

    public static boolean canShowBulletin(BaseFragment fragment) {
        return fragment != null && fragment.getParentActivity() != null && fragment.getLayoutContainer() != null;
    }

    @CheckResult
    public static Bulletin createBanBulletin(BaseFragment fragment, boolean banned) {
        final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment.getParentActivity());
        final String text;
        if (banned) {
            layout.setAnimation(R.raw.ic_ban, "Hand");
            text = "User Blocked";
        } else {
            layout.setAnimation(R.raw.ic_unban, "Main", "Finger 1", "Finger 2", "Finger 3", "Finger 4");
            text = "User Unblocked";
        }
        layout.textView.setText(AndroidUtilities.replaceTags(text));
        return Bulletin.make(fragment, layout, Bulletin.DURATION_SHORT);
    }

    @CheckResult
    public static Bulletin createCopyLinkBulletin(BaseFragment fragment) {
        return of(fragment).createCopyLinkBulletin();
    }

    @CheckResult
    public static Bulletin createCopyLinkBulletin(FrameLayout containerView) {
        return of(containerView).createCopyLinkBulletin();
    }

    public Bulletin createSimpleBulletin(int iconRawId, String text) {
        final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext());
        layout.setAnimation(iconRawId, 36, 36);
        layout.textView.setText(text);
        layout.textView.setSingleLine(false);
        layout.textView.setMaxLines(2);
        return create(layout, Bulletin.DURATION_SHORT);
    }

    @CheckResult
    public Bulletin createDownloadBulletin(FileType fileType) {
        return createDownloadBulletin(fileType);
    }

    public Bulletin createReportSent() {
        final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext());
        layout.setAnimation(R.raw.chats_infotip);
        layout.textView.setText("Report Chat Sent");
        return create(layout, Bulletin.DURATION_SHORT);
    }

    public Bulletin createErrorBulletin(CharSequence errorMessage) {
        Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext());
        layout.setAnimation(R.raw.chats_infotip);
        layout.textView.setText(errorMessage);
        layout.textView.setSingleLine(false);
        layout.textView.setMaxLines(2);
        return create(layout, Bulletin.DURATION_SHORT);
    }

    @CheckResult
    public Bulletin createCopyBulletin(String message) {
        final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext());
        layout.setAnimation(R.raw.copy, 36, 36, "NULL ROTATION", "Back", "Front");
        layout.textView.setText(message);
        return create(layout, Bulletin.DURATION_SHORT);
    }

    @CheckResult
    public Bulletin createCopyLinkBulletin() {
        final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(getContext());
        layout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
        layout.textView.setText("Link Copied");
        return create(layout, Bulletin.DURATION_SHORT);
    }

    //region Static Factory


    /*@CheckResult
    public static Bulletin createDeleteMessagesBulletin(BaseFragment fragment, int count) {
        final Bulletin.LottieLayout layout = new Bulletin.LottieLayout(fragment.getParentActivity());
        layout.setAnimation(R.raw.ic_delete, "Envelope", "Cover", "Bucket");
        layout.textView.setText(LocaleController.formatPluralString("MessagesDeletedHint", count));
        return Bulletin.make(fragment, layout, Bulletin.DURATION_SHORT);
    }*/

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

    public enum FileType {

        UNKNOWN("FileSavedHint", R.string.FileSavedHint, Icon.SAVED_TO_DOWNLOADS),
        UNKNOWNS("FilesSavedHint", Icon.SAVED_TO_DOWNLOADS);

        private final String localeKey;
        private final int localeRes;
        private final boolean plural;
        private final Icon icon;

        FileType(String localeKey, int localeRes, Icon icon) {
            this.localeKey = localeKey;
            this.localeRes = localeRes;
            this.icon = icon;
            this.plural = false;
        }

        FileType(String localeKey, Icon icon) {
            this.localeKey = localeKey;
            this.icon = icon;
            this.localeRes = 0;
            this.plural = true;
        }

        private String getText() {
            return LocaleController.getString(localeKey, localeRes);
        }

        private enum Icon {

            SAVED_TO_DOWNLOADS(R.raw.ic_download, 2, "Box", "Arrow"),
            SAVED_TO_GALLERY(R.raw.ic_save_to_gallery, 0, "Box", "Arrow", "Mask", "Arrow 2", "Splash"),
            SAVED_TO_MUSIC(R.raw.ic_save_to_music, 2, "Box", "Arrow");

            private final int resId;
            private final String[] layers;
            private final int paddingBottom;

            Icon(int resId, int paddingBottom, String... layers) {
                this.resId = resId;
                this.paddingBottom = paddingBottom;
                this.layers = layers;
            }
        }
    }
    //endregion
}