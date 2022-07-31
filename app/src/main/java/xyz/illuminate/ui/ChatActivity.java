package xyz.illuminate.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.ActionBar;
import xyz.illuminate.ui.ActionBar.ActionBarMenu;
import xyz.illuminate.ui.ActionBar.ActionBarMenuItem;
import xyz.illuminate.ui.ActionBar.AdjustPanLayoutHelper;
import xyz.illuminate.ui.ActionBar.BackDrawable;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.Components.EditTextBoldCursor;
import xyz.illuminate.ui.Components.EditTextCaption;
import xyz.illuminate.ui.Components.LayoutHelper;

public class ChatActivity extends BaseFragment {

    private HomeActivity homeActivity;
    private FrameLayout contentView;
    private ActionBarMenuItem searchItem;

    @Override
    protected ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setAddToContainer(false);
        actionBar.setItemsColor(0xffffffff, false);
        actionBar.setBackgroundColor(0xfffe6262);
        actionBar.setTitle("ActionBar");
        return actionBar;
    }

    @Override
    public View createView(Context context) {
        ActionBarMenu menu = actionBar.createMenu();

        searchItem = menu.addItem(1,
                R.drawable.idk).setIsSearchField(true);
        searchItem.setSearchFieldHint("Search for messages");


        fragmentView = new FrameLayout(context) {
            final AdjustPanLayoutHelper adjustPanLayoutHelper = new AdjustPanLayoutHelper(this) {

                @Override
                public void onPanTranslationUpdate(float y, float progress, boolean keyboardVisible) {
                    contentView.setTranslationY(y);
                    actionBar.setTranslationY(y);
                    setFragmentPanTranslationOffset((int) y);
                }

                @Override
                public boolean heightAnimationEnabled() {
                    return !inPreviewMode && !inBubbleMode;
                }
            };


            @Override
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                adjustPanLayoutHelper.onAttach();
                // homeActivity.setAdjustPanLayoutHelper(adjustPanLayoutHelper);
            }


        };

        contentView = (FrameLayout) fragmentView;

        fragmentView.setOnTouchListener((view, ev) -> true);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.addView(actionBar);
        homeActivity = new HomeActivity();
        Button intro = new Button(context) {
            public boolean onTouchEvent(MotionEvent event) {
                float lastY;
                float startY = 0.0f;

                lastY = event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    finishPreviewFragment();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float dy = startY - lastY;
                    movePreviewFragment(dy);
                    if (dy < 0) {
                        startY = lastY;
                    }
                }
                return super.onTouchEvent(event);
            }
        };
        intro.setText("Long Press to open preview ");
        intro.setTextSize(18);
        intro.setOnLongClickListener(v -> {
            presentFragmentAsPreview(homeActivity);
            //finishFragment(true);
            return true;
        });

        ((ViewGroup) fragmentView).addView(root, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));
        EditTextCaption editText = new EditTextCaption(context);
        editText.setHint("EditTextCaption");

        EditTextBoldCursor boldCursor = new EditTextBoldCursor(context);
        boldCursor.setHintText("BOLD");
        root.addView(editText);
        root.addView(boldCursor);
        root.addView(intro);
        intro.setOnClickListener(view -> {
            intro.setText(editText.getText().toString());
            editText.requestLayout();
        });
        return fragmentView;
    }
}
