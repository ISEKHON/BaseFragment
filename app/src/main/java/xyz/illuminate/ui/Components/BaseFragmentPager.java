package xyz.illuminate.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.NotificationCenter;
import xyz.illuminate.server.ConnectionsManager;
import xyz.illuminate.ui.ActionBar.ActionBarLayout;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.ActionBar.ThemeDescription;
import xyz.illuminate.ui.HomeActivity;
import xyz.illuminate.ui.LaunchActivity;
import xyz.illuminate.ui.MainActivity;
import xyz.illuminate.ui.NewsActivity;
import xyz.illuminate.ui.SettingsActivity;

@SuppressLint("ViewConstructor")
public class BaseFragmentPager extends FrameLayout implements ActionBarLayout.ActionBarLayoutDelegate, NotificationCenter.NotificationCenterDelegate {

    public static ActionBarLayout[] layouts = new ActionBarLayout[3];
    private final ArrayList<ArrayList<BaseFragment>> fragmentStacks = new ArrayList<>();
    private final ArrayList<BaseFragment> homeItems = new ArrayList<>();
    private final HomeActivity parentActivity;
    private int currentItem = 0;
    private MainActivity mainActivity;
    private OnPageChangedListener listener;


    public BaseFragmentPager(@NonNull Context context, HomeActivity parentActivity) {
        super(context);
        this.parentActivity = parentActivity;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didChangeTheme);
    }

    public void destroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didChangeTheme);
    }

    public void animateThemedValues(Theme.ThemeInfo themeInfo) {
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.didChangeTheme) {
            Theme.ThemeInfo themeInfo = (Theme.ThemeInfo) args[0];
            ConnectionsManager.getInstance(getContext()).setIgnoreRequests(true);
            if (AndroidUtilities.isTablet()) {
            } else {
                AndroidUtilities.runOnUIThread(() -> {
                    int n = 0;
                    while (true) {
                        ActionBarLayout[] layouts = BaseFragmentPager.layouts;
                        if (n >= 0) {
                            break;
                        }
                        ActionBarLayout actionBarLayout = layouts[n];
                        if (n == 0) {
                            ((MainActivity) actionBarLayout.fragmentsStack.get(0)).setHomeActivity(parentActivity);
                        }
                        actionBarLayout.animateThemedValues(themeInfo, 0, themeInfo.isDark, false);
                        actionBarLayout.rebuildAllFragmentViews(true, true);
                        n++;
                    }
                    AndroidUtilities.runOnUIThread(() -> {
                        ConnectionsManager.getInstance(getContext()).setIgnoreRequests(false);
                    });
                });
            }
        }
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(int currentItem) {
        for (int i = 0; i < layouts[currentItem].fragmentsStack.size() - 1; ++i) {
            layouts[currentItem].closeLastFragment(true);
        }
        int oldItem = this.currentItem;
        if (currentItem == oldItem) {
            return;
        }
        this.currentItem = currentItem;
        if (currentItem == 0 && layouts[0].fragmentsStack.size() <= 1) {
            ((LaunchActivity) parentActivity.getParentActivity()).getActionBarLayout().getDrawerLayoutContainer().setAllowOpenDrawer(false, true);
        } else {
            ((LaunchActivity) parentActivity.getParentActivity()).getActionBarLayout().getDrawerLayoutContainer().setAllowOpenDrawer(false, true);
        }
        layouts[currentItem].setLayerType(View.LAYER_TYPE_HARDWARE, null);
        layouts[oldItem].setLayerType(View.LAYER_TYPE_HARDWARE, null);
        layouts[currentItem].setScaleY(0.92f);
        layouts[currentItem].setScaleX(0.92f);
        layouts[currentItem].setAlpha(0.0f);
        layouts[currentItem].setVisibility(VISIBLE);
        layouts[oldItem].setAlpha(1.0f);
        layouts[oldItem].animate().alpha(0.0f).setDuration(80).withStartAction(() -> {
            listener.onPageChanged(currentItem);
        }).start();
        layouts[currentItem].animate().alpha(1.0f).withEndAction(() -> {
            layouts[currentItem].setLayerType(View.LAYER_TYPE_NONE, null);
            layouts[oldItem].setLayerType(LAYER_TYPE_NONE, null);
            layouts[currentItem].setVisibility(VISIBLE);
            layouts[oldItem].setVisibility(GONE);
        }).scaleX(1.0f).scaleY(1.0f).setDuration(220).setStartDelay(70).start();

    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> list = new ArrayList<>();
        ActionBarLayout[] layouts = BaseFragmentPager.layouts;
        for (ActionBarLayout layout : BaseFragmentPager.layouts) {
            for (BaseFragment fragment : layout.fragmentsStack) {
                list.addAll(fragment.getThemeDescriptions());
            }
        }
        return list;
    }

    public boolean isCurrentPageEmpty() {
        return layouts[currentItem].fragmentsStack.size() <= 1;
    }

    @Override
    public boolean onPreIme() {
        return false;
    }

    public void onBackPressed() {
        layouts[currentItem].onBackPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        layouts[currentItem].onKeyUp(keyCode, event);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ActionBarLayout layout) {
        if ((int) layout.getTag() == 0) {
            return fragment instanceof MainActivity || fragment instanceof NewsActivity || fragment instanceof SettingsActivity || parentActivity.presentFragment(fragment, removeLast, forceWithoutAnimation);
        }
        if ((int) layout.getTag() == 1) {
        }
        return true;
    }

    @Override
    public boolean needAddFragmentToStack(BaseFragment fragment, ActionBarLayout layout) {
        return true;
    }

    @Override
    public boolean needCloseLastFragment(ActionBarLayout layout) {
//        if (AndroidUtilities.isTablet()) {
//
//        } else if (!parentActivity.getBottomNavigationCell().isHidden() && layout.fragmentsStack.size() <= 1) {
//            parentActivity.getParentActivity().finish();
//        }
        return true;
    }

    @Override
    public void onRebuildAllFragments(ActionBarLayout layout, boolean last) {
    }

    public void populate(ArrayList<BaseFragment> list) {
        homeItems.clear();
        homeItems.addAll(list);
        fragmentStacks.clear();
        for (int i = 0; i < 3; ++i) {
            (layouts[i] = new ActionBarLayout(getContext())).setDelegate(this);
            fragmentStacks.add(new ArrayList<>());
            layouts[i].init(fragmentStacks.get(i));
            layouts[i].setTag(i);
            addView(layouts[i]);
            if (i >= 1) {
                layouts[i].setVisibility(GONE);
            }
            layouts[i].presentFragment(homeItems.get(i));
        }
    }

    public void setMainPage(MainActivity mainPage) {
        mainActivity = mainPage;
    }

    public void setOnPageChangedListener(OnPageChangedListener onPageChangedListener) {
        listener = onPageChangedListener;
    }

    public interface OnPageChangedListener {
        void onPageChanged(int item);
    }

}
