package xyz.illuminate.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.NotificationCenter;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.ActionBar;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.ActionBar.ThemeDescription;
import xyz.illuminate.ui.Adapters.HomeAdapter;
import xyz.illuminate.ui.Components.CubicBezierInterpolator;
import xyz.illuminate.ui.Models.ItemModal;

public class HomeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    String TAG = "HomeActivity";
    float slideFragmentProgress = 1f;
    boolean isSlideBackTransition;
    boolean isDrawerTransition;
    ValueAnimator slideBackTransitionAnimator;
    ArrayList<ItemModal> items = new ArrayList<ItemModal>();
    RecyclerView listView;
    View view;

    ItemModal itemModal;

    @Override
    protected ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setAddToContainer(true);
        actionBar.setTitle("Falcon");
        actionBar.setClipContent(true);
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
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        fragmentView.setOnTouchListener((v, event) -> true);
        view = LayoutInflater.from(context).inflate(R.layout.activity_home, (ViewGroup) fragmentView, false);

        itemModal = new Gson().fromJson((String) "[\n" +
                "  {\n" +
                "    \"name\": \"DialogUI\",\n" +
                "    \"lastEdited\": \"1 month ago\",\n" +
                "    \"type\": \"d\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ToastUI\",\n" +
                "    \"lastEdited\": \"10 month ago\",\n" +
                "    \"type\": \"t\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"My designed dialog\",\n" +
                "    \"lastEdited\": \"1 week ago\",\n" +
                "    \"type\": \"d\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ToastUI\",\n" +
                "    \"lastEdited\": \"10 month ago\",\n" +
                "    \"type\": \"t\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"DialogUI\",\n" +
                "    \"lastEdited\": \"1 month ago\",\n" +
                "    \"type\": \"d\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ToastUI\",\n" +
                "    \"lastEdited\": \"10 month ago\",\n" +
                "    \"type\": \"t\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"My designed dialog\",\n" +
                "    \"lastEdited\": \"1 week ago\",\n" +
                "    \"type\": \"d\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"ToastUI\",\n" +
                "    \"lastEdited\": \"10 month ago\",\n" +
                "    \"type\": \"t\"\n" +
                "  }\n" +
                "]", new TypeToken<ItemModal>() {
        }.getType());

        items.add(itemModal);
        listView = view.findViewById(R.id.list);
        listView.setAdapter(new HomeAdapter(context, items));


        ((ViewGroup) fragmentView).addView(view);


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
