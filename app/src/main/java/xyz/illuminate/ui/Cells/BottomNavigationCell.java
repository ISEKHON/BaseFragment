package xyz.illuminate.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Components.BadgeImageView;
import xyz.illuminate.ui.Components.BaseFragmentPager;
import xyz.illuminate.ui.Components.CubicBezierInterpolator;
import xyz.illuminate.ui.Components.LayoutHelper;

public class BottomNavigationCell extends FrameLayout {

    private final int ITEMS_SIZE;
    private final LinearLayout divider;
    private final LinearLayout[] itemsContainer;
    private final ImageView[] itemsIcon;
    private final TextView[] itemsText;
    private final View view;
    ValueAnimator.AnimatorUpdateListener animatorUpdateListener;
    ValueAnimator.AnimatorUpdateListener open;
    BaseFragmentPager pager;
    ValueAnimator valueAnimator;
    private int backgroundColor;
    private int currentPosition;
    private int disabledColor;
    private int height;
    private boolean inProgress;
    private boolean isHidden;
    private int selectedColor;

    public BottomNavigationCell(@NonNull Context context, BaseFragmentPager pager) {
        super(context);
        ITEMS_SIZE = 3;
        isHidden = false;
        currentPosition = 0;
        open = valueAnimator -> {
            LayoutParams layoutParams = (LayoutParams) getLayoutParams();
            layoutParams.height = (int) (height * valueAnimator.getAnimatedFraction());
            setLayoutParams(layoutParams);
        };
        animatorUpdateListener = valueAnimator -> {
            LayoutParams layoutParams = (LayoutParams) getLayoutParams();
            layoutParams.height = (int) (height * -valueAnimator.getAnimatedFraction());
        };
        itemsIcon = new ImageView[3];
        itemsText = new TextView[3];
        itemsContainer = new LinearLayout[3];
        selectedColor = Theme.getColor(Theme.key_imageColorPrimary);
        disabledColor = Theme.getColor(Theme.key_imageColorDisabled);
        backgroundColor = Theme.getColor(Theme.key_surfaceBackground);
        view = LayoutInflater.from(context).inflate(R.layout.design_bottom_nav, this);
        this.pager = pager;
        itemsIcon[0] = (ImageView) view.findViewById(R.id.homeIcon);
        itemsIcon[1] = (ImageView) view.findViewById(R.id.newsIcon);
        itemsIcon[2] = (ImageView) view.findViewById(R.id.profleIcon);
        itemsIcon[0].setImageResource(R.drawable.msg_home);
        itemsIcon[1].setImageResource(R.drawable.msg_discussion);
        itemsIcon[2].setImageResource(R.drawable.menu_contacts);
        itemsContainer[0] = (LinearLayout) view.findViewById(R.id.home_tab);
        itemsContainer[1] = (LinearLayout) view.findViewById(R.id.news_tab);
        itemsContainer[2] = (LinearLayout) view.findViewById(R.id.profile_tab);
        itemsText[0] = (TextView) view.findViewById(R.id.homeTxt);
        itemsText[1] = (TextView) view.findViewById(R.id.newsTxt);
        itemsText[2] = (TextView) view.findViewById(R.id.profileTxt);

        //view.setVisibility(GONE);
        divider = new LinearLayout(context);
        divider.setBackgroundColor(Theme.getColor(Theme.key_divider));
        addView(divider, LayoutHelper.createFrame(-1, 1, 48));
        update(0);
        init();
    }

    private void init() {
        for (int i = 0; i < 3; ++i) {
            int item = i;
            itemsText[i].setVisibility(GONE);
            itemsContainer[i].setOnClickListener(view -> post(() -> pager.setCurrentItem(item)));
            itemsContainer[i].setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector), 4, 4));
            itemsIcon[i].setColorFilter(new PorterDuffColorFilter(disabledColor, PorterDuff.Mode.SRC_ATOP));
            itemsIcon[i].animate().scaleX(0.8f).scaleY(0.8f).start();
            itemsContainer[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
            itemsText[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
        }
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setAnimationInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public void setBadgeVisibility(int id, boolean visible) {
        ImageView[] itemsIcon = this.itemsIcon;
        if (itemsIcon[id] instanceof BadgeImageView) {
            ((BadgeImageView) itemsIcon[id]).getOrCreateBadge().setVisible(visible);
        }
    }

    public void setBgColor(int color) {
        backgroundColor = color;
        view.setBackgroundColor(color);
    }

    public void setDisabledColor(int disabledColor) {
        this.disabledColor = disabledColor;
        update(currentPosition);
    }

    public void setPrimaryColor(int primaryColor) {
        selectedColor = primaryColor;
        update(currentPosition);
    }

    public void show() {
        boolean isHidden = this.isHidden;
        if (isHidden) {
            this.isHidden = !isHidden;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(open);
            ofFloat.start();
        }
    }

    public void hide() {
        boolean isHidden = this.isHidden;
        if (!isHidden) {
            this.isHidden = !isHidden;
            height = getMeasuredHeight();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
            ofFloat.addUpdateListener(animatorUpdateListener);
            ofFloat.start();
        }
    }

    public void update(int currentPosition) {
        //ValueAnimator valueAnimator = this.valueAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        for (int i = 0; i < 3; ++i) {
            itemsIcon[this.currentPosition].animate().scaleX(0.8f).scaleY(0.8f).setDuration(180).start();
            itemsIcon[currentPosition].animate().scaleX(1.0f).scaleY(1.0f).start();
            itemsIcon[i].setColorFilter(new PorterDuffColorFilter(disabledColor, PorterDuff.Mode.SRC_ATOP));
            itemsText[i].setTextColor(disabledColor);
            if (currentPosition == i) {
                (this.valueAnimator = ValueAnimator.ofArgb(disabledColor, selectedColor)).start();
                this.valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.valueAnimator.addUpdateListener(valueAnimator1 -> {
                    itemsIcon[currentPosition].setColorFilter(new PorterDuffColorFilter((int) valueAnimator1.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
                    itemsText[currentPosition].setTextColor((int) valueAnimator1.getAnimatedValue());
                });
                this.valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        inProgress = false;
                        BottomNavigationCell.this.currentPosition = currentPosition;
                    }
                });
            }
        }
    }
}
