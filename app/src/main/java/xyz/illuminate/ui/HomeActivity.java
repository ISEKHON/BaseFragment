package xyz.illuminate.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.NotificationCenter;
import xyz.illuminate.ui.ActionBar.ActionBar;
import xyz.illuminate.ui.ActionBar.BackDrawable;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.ActionBar.ThemeDescription;
import xyz.illuminate.ui.Components.CubicBezierInterpolator;
import xyz.illuminate.ui.Components.LayoutHelper;

public class HomeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    String TAG = "HomeActivity";
    float slideFragmentProgress = 1f;
    boolean isSlideBackTransition;
    boolean isDrawerTransition;
    ValueAnimator slideBackTransitionAnimator;
    boolean animatingItem;
    private View view;
    private LinearLayout container;
    private Button button;
    private Button settings, openLogin, openHome;

    @Override
    protected ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context) {
            @Override
            public void setTranslationY(float translationY) {
                if (translationY != getTranslationY() && fragmentView != null) {
                    fragmentView.invalidate();
                }
                super.setTranslationY(translationY);
            }

            @Override
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (inPreviewMode) {
                    return false;
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        actionBar.setAddToContainer(true);
        actionBar.setTitle("Testing actionbar");
        actionBar.setClipContent(true);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        actionBar.setItemsColor(getThemedColor(Theme.key_actionBarDefaultIcon), false);
        return actionBar;
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didChangeTheme);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didChangeTheme);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.userInfoDidLoad);
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didChangeTheme);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didChangeTheme);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.userInfoDidLoad);
        getConnectionsManager().cancelAll(TAG);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "AppCompatCustomView"})
    @Override
    public View createView(Context context) {
        fragmentView = new FrameLayout(context);
        fragmentView.setOnTouchListener((v, event) -> true);
        container = new LinearLayout(context);
        container.setGravity(Gravity.CENTER);
        container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        ((ViewGroup) fragmentView).addView(container, 0, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));

        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);

        ChatActivity chatActivity = new ChatActivity();
        button = new Button(context) {
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
        button.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        button.setText("Open Next Fragment");
        button.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        button.setBackground(Theme.createRoundRectDrawable(15, Theme.getColor(Theme.key_buttonBackgroundBlue)));
        button.setOnLongClickListener(v -> {
            presentFragmentAsPreview(chatActivity);
            return true;
        });
        button.setOnClickListener(view -> presentFragment(new ChatActivity()));
        button.setFocusable(true);

        container.addView(button, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 20, 5, 20, 5));

        settings = new Button(context);
        settings.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        settings.setText("Open Settings");
        settings.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        settings.setBackground(Theme.createRoundRectDrawable(15, Theme.getColor(Theme.key_buttonBackgroundBlue)));
        settings.setOnClickListener(view -> presentFragment(new SettingsActivity()));
        settings.setOnLongClickListener(v -> {
            presentFragmentAsPreviewWithMenu(chatActivity, null);
            return true;
        });
        container.addView(settings, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 20, 5, 20, 5));

        /* openLogin = new Button(context) {
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
       openLogin.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        openLogin.setText("Open View Activity");
        openLogin.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        openLogin.setBackground(Theme.createRoundRectDrawable(15, Theme.getColor(Theme.key_buttonBackgroundBlue)));
        openLogin.setOnClickListener(view -> presentFragment(new ViewActivity(view)));
        openLogin.setOnLongClickListener(view -> {
            presentFragmentAsPreview(new Vieww(view));
            return false;
        });
        container.addView(openLogin, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 20, 5, 20, 5));
*/
        openHome = new Button(context);
        openHome.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        openHome.setText("Open DevTools");
        openHome.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        openHome.setBackground(Theme.createRoundRectDrawable(15, Theme.getColor(Theme.key_buttonBackgroundBlue)));
        openHome.setOnClickListener(view -> presentFragment(new DeveloperTools()));

        container.addView(openHome, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 20, 5, 20, 5));

        Button openCom = new Button(context);
        openCom.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        openCom.setText("Open Comments");
        openCom.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        openCom.setBackground(Theme.createRoundRectDrawable(15, Theme.getColor(Theme.key_buttonBackgroundBlue)));
        openCom.setOnClickListener(view -> presentFragment(new CommentsActivity()));

        container.addView(openCom, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 20, 5, 20, 5));

        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        return fragmentView;

    }


    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.didChangeTheme) {

        }
    }

    @Override
    protected void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();

    }


    @Override
    protected Animator getCustomSlideTransition(boolean topFragment, boolean backAnimation, float distanceToMove) {
        if (backAnimation) {
            slideBackTransitionAnimator = ValueAnimator.ofFloat(slideFragmentProgress, 1f);
            return slideBackTransitionAnimator;
        }
        int duration = (int) (Math.max((int) (200.0f / getLayoutContainer().getMeasuredWidth() * distanceToMove), 80) * 1.2f);
        slideBackTransitionAnimator = ValueAnimator.ofFloat(slideFragmentProgress, 1f);
        slideBackTransitionAnimator.addUpdateListener(valueAnimator -> setSlideTransitionProgress((float) valueAnimator.getAnimatedValue()));
        slideBackTransitionAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        slideBackTransitionAnimator.setDuration(duration);
        slideBackTransitionAnimator.start();
        return slideBackTransitionAnimator;
    }

    @Override
    protected void prepareFragmentToSlide(boolean topFragment, boolean beginSlide) {
        if (!topFragment && beginSlide) {
            isSlideBackTransition = true;
            setFragmentIsSliding(true);
        } else {
            slideBackTransitionAnimator = null;
            isSlideBackTransition = false;
            setFragmentIsSliding(false);
            setSlideTransitionProgress(1f);
        }
    }

    private void setFragmentIsSliding(boolean sliding) {
        /*if (SharedConfig.getDevicePerformanceClass() == SharedConfig.PERFORMANCE_CLASS_LOW) {
            return;
        }*/
        if (sliding) {
            if (actionBar != null) {
                actionBar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
            if (fragmentView != null) {
                fragmentView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                ((ViewGroup) fragmentView).setClipChildren(false);
                fragmentView.requestLayout();
            }
        } else {

            if (actionBar != null) {
                actionBar.setLayerType(View.LAYER_TYPE_NONE, null);
            }
            if (fragmentView != null) {
                fragmentView.setLayerType(View.LAYER_TYPE_NONE, null);
                ((ViewGroup) fragmentView).setClipChildren(true);
                fragmentView.requestLayout();
            }
        }
    }

    @Override
    protected void onSlideProgress(boolean isOpen, float progress) {
       /* if (SharedConfig.getDevicePerformanceClass() == SharedConfig.PERFORMANCE_CLASS_LOW) {
            return;
        }*/
        if (isSlideBackTransition && slideBackTransitionAnimator == null) {
            setSlideTransitionProgress(progress);
        }
    }

    private void setSlideTransitionProgress(float progress) {
       /* if (SharedConfig.getDevicePerformanceClass() == SharedConfig.PERFORMANCE_CLASS_LOW) {
            return;
        }*/
        slideFragmentProgress = progress;
        if (fragmentView != null) {
            fragmentView.invalidate();
        }

        if (fragmentView != null) {
            float s = 1f - 0.05f * (1f - slideFragmentProgress);
            fragmentView.setScaleX(s);
            fragmentView.setScaleY(s);
            fragmentView.setTranslationX((isDrawerTransition ? AndroidUtilities.dp(4) : -AndroidUtilities.dp(4)) * (1f - slideFragmentProgress));
            fragmentView.setPivotX(isDrawerTransition ? fragmentView.getMeasuredWidth() : 0);
            fragmentView.setPivotY(0);
            fragmentView.invalidate();
        }
    }

    @Override
    public void setProgressToDrawerOpened(float progress) {
        /*if (SharedConfig.getDevicePerformanceClass() == SharedConfig.PERFORMANCE_CLASS_LOW || isSlideBackTransition) {
            return;
        }*/
        boolean drawerTransition = progress > 0;
        if (drawerTransition != isDrawerTransition) {
            isDrawerTransition = drawerTransition;
            setFragmentIsSliding(isDrawerTransition);
            if (fragmentView != null) {
                fragmentView.requestLayout();
            }
        }
        setSlideTransitionProgress(1f - progress);
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        return themeDescriptions;
    }
}

/*settings = new Button(context){
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
        };*/
