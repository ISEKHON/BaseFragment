package xyz.illuminate.ui;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.ApplicationLoader;
import xyz.illuminate.app.FileLog;
import xyz.illuminate.app.NotificationCenter;
import xyz.illuminate.app.R;
import xyz.illuminate.app.UserConfig;
import xyz.illuminate.server.ConnectionsManager;
import xyz.illuminate.ui.ActionBar.ActionBarLayout;
import xyz.illuminate.ui.ActionBar.AlertDialog;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.DrawerLayoutContainer;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Adapters.DrawerLayoutAdapter;
import xyz.illuminate.ui.Components.LayoutHelper;
import xyz.illuminate.ui.Components.RecyclerListView;
import xyz.illuminate.ui.Components.SideMenultItemAnimator;
import xyz.illuminate.ui.Components.ThemeEditorView;


public class LaunchActivity extends AppCompatActivity implements ActionBarLayout.ActionBarLayoutDelegate, NotificationCenter.NotificationCenterDelegate {

    private static final ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    private static final ArrayList<BaseFragment> layerFragmentsStack = new ArrayList<>();
    private static final ArrayList<BaseFragment> rightFragmentsStack = new ArrayList<>();
    private final ArrayList<BaseFragment> mainFragmentStack = new ArrayList<>();
    private final Intent intent = new Intent();
    public DrawerLayoutContainer drawerLayoutContainer;
    Theme.ThemeInfo applyTheme = null;
    long lastTime = 0, delay = 300;
    boolean animating;
    HomeActivity homeActivity;
    private ActionBarLayout layersActionBarLayout;
    private ActionBarLayout rightActionBarLayout;
    private FrameLayout shadowTablet;
    private FrameLayout shadowTabletSide;
    private View backgroundTablet;
    private DrawerLayoutAdapter drawerLayoutAdapter;
    private FrameLayout frameLayout;
    private boolean tabletFullSize;
    private RecyclerListView sideMenu;
    private SideMenultItemAnimator itemAnimator;
    private ActionBarLayout actionBarLayout;
    Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() > (lastTime + delay) - 500) {
                setTheme();
                animating = false;
            }
        }
    };
    private ActionMode visibleActionMode;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        AndroidUtilities.fillStatusBarHeight(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTheme(R.style.Theme_TMessages);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                setTaskDescription(new ActivityManager.TaskDescription(null, null, Theme.getColor(Theme.key_actionBarDefault) | 0xff000000));
            } catch (Exception ignore) {
            }

        }
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);


        Theme.createDialogDrawable(this);
        Theme.createCommonResources(this);

        actionBarLayout = new ActionBarLayout(this) {
            @Override
            public void setThemeAnimationValue(float value) {
                super.setThemeAnimationValue(value);
                drawerLayoutContainer.setBehindKeyboardColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
        };
        frameLayout = new FrameLayout(this);
        setContentView(frameLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        drawerLayoutContainer = new DrawerLayoutContainer(this);
        drawerLayoutContainer.setBehindKeyboardColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        frameLayout.addView(drawerLayoutContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        if (AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            RelativeLayout launchLayout = new RelativeLayout(this) {
                private boolean inLayout;

                @Override
                public void requestLayout() {
                    if (inLayout) {
                        return;
                    }
                    super.requestLayout();
                }

                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    inLayout = true;
                    int width = MeasureSpec.getSize(widthMeasureSpec);
                    int height = MeasureSpec.getSize(heightMeasureSpec);
                    setMeasuredDimension(width, height);

                    if (!AndroidUtilities.isInMultiwindow && (!AndroidUtilities.isSmallTablet() || getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
                        tabletFullSize = false;
                        int leftWidth = width / 100 * 35;
                        if (leftWidth < AndroidUtilities.dp(320)) {
                            leftWidth = AndroidUtilities.dp(320);
                        }
                        actionBarLayout.measure(MeasureSpec.makeMeasureSpec(leftWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                        shadowTabletSide.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                        rightActionBarLayout.measure(MeasureSpec.makeMeasureSpec(width - leftWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                    } else {
                        tabletFullSize = true;
                        actionBarLayout.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                    }
                    backgroundTablet.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                    shadowTablet.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                    layersActionBarLayout.measure(MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(530), width), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(528), height), MeasureSpec.EXACTLY));

                    inLayout = false;
                }

                @Override
                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    int width = r - l;
                    int height = b - t;

                    if (!AndroidUtilities.isInMultiwindow && (!AndroidUtilities.isSmallTablet() || getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
                        int leftWidth = width / 100 * 35;
                        if (leftWidth < AndroidUtilities.dp(320)) {
                            leftWidth = AndroidUtilities.dp(320);
                        }
                        shadowTabletSide.layout(leftWidth, 0, leftWidth + shadowTabletSide.getMeasuredWidth(), shadowTabletSide.getMeasuredHeight());
                        actionBarLayout.layout(0, 0, actionBarLayout.getMeasuredWidth(), actionBarLayout.getMeasuredHeight());
                        rightActionBarLayout.layout(leftWidth, 0, leftWidth + rightActionBarLayout.getMeasuredWidth(), rightActionBarLayout.getMeasuredHeight());
                    } else {
                        actionBarLayout.layout(0, 0, actionBarLayout.getMeasuredWidth(), actionBarLayout.getMeasuredHeight());
                    }
                    int x = (width - layersActionBarLayout.getMeasuredWidth()) / 2;
                    int y = (height - layersActionBarLayout.getMeasuredHeight()) / 2;
                    layersActionBarLayout.layout(x, y, x + layersActionBarLayout.getMeasuredWidth(), y + layersActionBarLayout.getMeasuredHeight());
                    backgroundTablet.layout(0, 0, backgroundTablet.getMeasuredWidth(), backgroundTablet.getMeasuredHeight());
                    shadowTablet.layout(0, 0, shadowTablet.getMeasuredWidth(), shadowTablet.getMeasuredHeight());
                }

            };
            drawerLayoutContainer.addView(actionBarLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            sideMenu = new RecyclerListView(this) {
                @Override
                public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    int restore = -1;
                    if (itemAnimator != null && itemAnimator.isRunning() && itemAnimator.isAnimatingChild(child)) {
                        restore = canvas.save();
                        canvas.clipRect(0, itemAnimator.getAnimationClipTop(), getMeasuredWidth(), getMeasuredHeight());
                    }
                    boolean result = super.drawChild(canvas, child, drawingTime);
                    if (restore >= 0) {
                        canvas.restoreToCount(restore);
                        invalidate();
                        invalidateViews();
                    }
                    return result;
                }
            };
            FileLog.d("UI create34 time = " + (SystemClock.elapsedRealtime() - ApplicationLoader.startTime));

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) sideMenu.getLayoutParams();
            Point screenSize = AndroidUtilities.getRealScreenSize();
            itemAnimator = new SideMenultItemAnimator(sideMenu);
            sideMenu.setItemAnimator(itemAnimator);
            sideMenu.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            sideMenu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            sideMenu.setAllowItemsInteractionDuringAnimation(false);
            sideMenu.setAdapter(drawerLayoutAdapter = new DrawerLayoutAdapter(this));
            drawerLayoutContainer.setDrawerLayout(sideMenu);
            layoutParams.width = AndroidUtilities.isTablet() ? AndroidUtilities.dp(320) : Math.min(AndroidUtilities.dp(320), Math.min(screenSize.x, screenSize.y) - AndroidUtilities.dp(56));
            layoutParams.height = LayoutHelper.MATCH_PARENT;
            sideMenu.setLayoutParams(layoutParams);
            sideMenu.setOnItemClickListener((view, position, x, y) -> {
                int id = drawerLayoutAdapter.getId(position);
                if (id == 2) {
                    Bundle args = new Bundle();
                    actionBarLayout.presentFragment(new DeveloperTools());
                    AndroidUtilities.showToast("ok");
                    drawerLayoutContainer.closeDrawer(false);
                } else if (id == 3) {
                    //presentFragment(new ContactsActivity(null));
                    AndroidUtilities.showToast("opop");
                    drawerLayoutContainer.closeDrawer(false);
                }
            });

            backgroundTablet = new View(this);
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.catstile);
            drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            backgroundTablet.setBackgroundDrawable(drawable);
            launchLayout.addView(backgroundTablet, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            launchLayout.addView(actionBarLayout);

            rightActionBarLayout = new ActionBarLayout(this);
            rightActionBarLayout.init(rightFragmentsStack);
            rightActionBarLayout.setDelegate(this);
            launchLayout.addView(rightActionBarLayout);

            shadowTabletSide = new FrameLayout(this);
            shadowTabletSide.setBackgroundColor(0x40295274);
            launchLayout.addView(shadowTabletSide);

            shadowTablet = new FrameLayout(this);
            shadowTablet.setVisibility(layerFragmentsStack.isEmpty() ? View.GONE : View.VISIBLE);
            shadowTablet.setBackgroundColor(0x7f000000);
            launchLayout.addView(shadowTablet);
            shadowTablet.setOnTouchListener((v, event) -> {
                if (!actionBarLayout.fragmentsStack.isEmpty() && event.getAction() == MotionEvent.ACTION_UP) {
                    float x = event.getX();
                    float y = event.getY();
                    int[] location = new int[2];
                    layersActionBarLayout.getLocationOnScreen(location);
                    int viewX = location[0];
                    int viewY = location[1];

                    if (layersActionBarLayout.checkTransitionAnimation() || x > viewX && x < viewX + layersActionBarLayout.getWidth() && y > viewY && y < viewY + layersActionBarLayout.getHeight()) {
                        return false;
                    } else {
                        if (!layersActionBarLayout.fragmentsStack.isEmpty()) {
                            for (int a = 0; a < layersActionBarLayout.fragmentsStack.size() - 1; a++) {
                                layersActionBarLayout.removeFragmentFromStack(layersActionBarLayout.fragmentsStack.get(0));
                                a--;
                            }
                            layersActionBarLayout.closeLastFragment(true);
                        }
                        return true;
                    }
                }
                return false;
            });

            shadowTablet.setOnClickListener(v -> {

            });

            layersActionBarLayout = new ActionBarLayout(this);
            layersActionBarLayout.setRemoveActionBarExtraHeight(true);
            layersActionBarLayout.setBackgroundView(shadowTablet);
            layersActionBarLayout.setUseAlphaAnimations(true);
            layersActionBarLayout.setBackgroundResource(R.drawable.boxshadow);
            layersActionBarLayout.init(layerFragmentsStack);
            layersActionBarLayout.setDelegate(this);
            layersActionBarLayout.setDrawerLayoutContainer(drawerLayoutContainer);
            layersActionBarLayout.setVisibility(layerFragmentsStack.isEmpty() ? View.GONE : View.VISIBLE);
            launchLayout.addView(layersActionBarLayout);

        } else {
            drawerLayoutContainer.addView(actionBarLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        sideMenu = new RecyclerListView(this) {
            @Override
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                int restore = -1;
                if (itemAnimator != null && itemAnimator.isRunning() && itemAnimator.isAnimatingChild(child)) {
                    restore = canvas.save();
                    canvas.clipRect(0, itemAnimator.getAnimationClipTop(), getMeasuredWidth(), getMeasuredHeight());
                }
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (restore >= 0) {
                    canvas.restoreToCount(restore);
                    invalidate();
                    invalidateViews();
                }
                return result;
            }
        };

        itemAnimator = new SideMenultItemAnimator(sideMenu);
        sideMenu.setItemAnimator(itemAnimator);
        sideMenu.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        sideMenu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        sideMenu.setAllowItemsInteractionDuringAnimation(false);
        sideMenu.setAdapter(drawerLayoutAdapter = new DrawerLayoutAdapter(this));
        drawerLayoutContainer.setDrawerLayout(sideMenu);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) sideMenu.getLayoutParams();
        Point screenSize = AndroidUtilities.getRealScreenSize();
        layoutParams.width = AndroidUtilities.isTablet() ? AndroidUtilities.dp(320) : Math.min(AndroidUtilities.dp(320), Math.min(screenSize.x, screenSize.y) - AndroidUtilities.dp(56));
        layoutParams.height = LayoutHelper.MATCH_PARENT;
        sideMenu.setLayoutParams(layoutParams);

        sideMenu.setOnItemClickListener((view, position, x, y) -> {
            int id = drawerLayoutAdapter.getId(position);
            if (id == 2) {
                Bundle args = new Bundle();
                actionBarLayout.presentFragment(new DeveloperTools());
                AndroidUtilities.showToast("");
                drawerLayoutContainer.closeDrawer(false);
            } else if (id == 3) {
                //presentFragment(new ContactsActivity(null));
                AndroidUtilities.showToast("opop");
                drawerLayoutContainer.closeDrawer(false);
            }
        });

        drawerLayoutContainer.setParentActionBarLayout(actionBarLayout);
        actionBarLayout.setDrawerLayoutContainer(drawerLayoutContainer);
        actionBarLayout.init(mainFragmentsStack);
        actionBarLayout.setDelegate(this);
        actionBarLayout.removeAllFragments();
        if (actionBarLayout.fragmentsStack.isEmpty()) {
            if (UserConfig.getInstance(this).isClientActivated()) {
                homeActivity = new HomeActivity();
            }

            actionBarLayout.addFragmentToStack(new CommentsActivity());
            drawerLayoutContainer.setAllowOpenDrawer(true, true);

            if (!AndroidUtilities.isTablet()) {
                actionBarLayout.showLastFragment();
            } else {
                layersActionBarLayout.showLastFragment();
            }
        } else {

        }
        checkLayout();
        AndroidUtilities.setLightStatusBar(getWindow(), !Theme.currentTheme.isDark);
        AndroidUtilities.setLightNavigationBar(getWindow(), !Theme.currentTheme.isDark);
        getWindow().setNavigationBarColor(Theme.getColor(Theme.key_navigationBarColor));
        actionBarLayout.animateThemedValues(Theme.currentTheme, 0, Theme.currentTheme.isDark, true);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayoutContainer.isDrawerOpened()) {
            drawerLayoutContainer.closeDrawer(false);
        } else {
            if (AndroidUtilities.isTablet()) {
                if (layersActionBarLayout.getVisibility() == View.VISIBLE) {
                    layersActionBarLayout.onBackPressed();
                } else {
                    boolean cancel = false;
                    if (rightActionBarLayout.getVisibility() == View.VISIBLE && !rightActionBarLayout.fragmentsStack.isEmpty()) {
                        BaseFragment fragment = rightActionBarLayout.fragmentsStack.get(rightActionBarLayout.fragmentsStack.size() - 1);
                        cancel = !fragment.onBackPressed();
                    }
                    if (!cancel) {
                        actionBarLayout.onBackPressed();
                    }
                }
            } else {
                actionBarLayout.onBackPressed();

            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        AndroidUtilities.checkDisplaySize(this, newConfig);
        super.onConfigurationChanged(newConfig);
        checkLayout();
       /* int layoutDirection=newConfig.getLayoutDirection();
        boolean isRTL=true;
        if (layoutDirection!=1){
            isRTL=false;
        }
        LocaleController.isRTL=isRTL;*/
        actionBarLayout.onConfigurationChanged(newConfig);
        ThemeEditorView instance = ThemeEditorView.getInstance();
        if (instance != null) {
            instance.onConfigurationChanged();
        }
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        AndroidUtilities.showToast("action mode started");
        visibleActionMode = mode;
        try {
            Menu menu = mode.getMenu();
            if (menu != null) {
                boolean extended = actionBarLayout.extendActionMode(menu);
                if (!extended && AndroidUtilities.isTablet()) {
                    extended = rightActionBarLayout.extendActionMode(menu);
                    if (!extended) {
                        layersActionBarLayout.extendActionMode(menu);
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (Build.VERSION.SDK_INT >= 23 && mode.getType() == ActionMode.TYPE_FLOATING) {
            return;
        }
        actionBarLayout.onActionModeStarted(mode);
        if (AndroidUtilities.isTablet()) {
            rightActionBarLayout.onActionModeStarted(mode);
            layersActionBarLayout.onActionModeStarted(mode);
        }
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        AndroidUtilities.showToast("action mode finished");
        if (visibleActionMode == mode) {
            visibleActionMode = null;
        }
        if (Build.VERSION.SDK_INT >= 23 && mode.getType() == ActionMode.TYPE_FLOATING) {
            return;
        }
        actionBarLayout.onActionModeFinished(mode);
        if (AndroidUtilities.isTablet()) {
            rightActionBarLayout.onActionModeFinished(mode);
            layersActionBarLayout.onActionModeFinished(mode);
        }
    }

    public void hideVisibleActionMode() {
        if (visibleActionMode == null) {
            return;
        }
        visibleActionMode.finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (AndroidUtilities.isTablet()) {
                if (layersActionBarLayout.getVisibility() == View.VISIBLE && !layersActionBarLayout.fragmentsStack.isEmpty()) {
                    layersActionBarLayout.onKeyUp(keyCode, event);
                } else if (rightActionBarLayout.getVisibility() == View.VISIBLE && !rightActionBarLayout.fragmentsStack.isEmpty()) {
                    rightActionBarLayout.onKeyUp(keyCode, event);
                } else {
                    actionBarLayout.onKeyUp(keyCode, event);
                }
            } else {
                if (actionBarLayout.fragmentsStack.size() == 1) {
                    if (!drawerLayoutContainer.isDrawerOpened()) {
                        if (getCurrentFocus() != null) {
                            AndroidUtilities.hideKeyboard(getCurrentFocus());
                        }
                        drawerLayoutContainer.openDrawer(false);
                    } else {
                        drawerLayoutContainer.closeDrawer(false);
                    }
                } else {
                    actionBarLayout.onKeyUp(keyCode, event);
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onPreIme() {
        return false;
    }

    @Override
    public boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ActionBarLayout layout) {
        if (AndroidUtilities.isTablet()) {
            if (!(fragment instanceof HomeActivity)) {
                ActionBarLayout actionBarLayout = layersActionBarLayout;
                if (layout != actionBarLayout) {
                    actionBarLayout.setVisibility(View.VISIBLE);
                    drawerLayoutContainer.setAllowOpenDrawer(false, false);
                }
                if (fragment instanceof LoginActivity) {
                    backgroundTablet.setVisibility(View.VISIBLE);
                    shadowTabletSide.setVisibility(View.GONE);
                    shadowTablet.setBackgroundColor(0);
                } else {
                    shadowTablet.setBackgroundColor(Theme.getColor(Theme.key_buttonBackgroundBlue));
                }
                actionBarLayout.addFragmentToStack(fragment);
                return false;
            }
            if (layout != layersActionBarLayout) {
                layersActionBarLayout.setVisibility(View.VISIBLE);
                drawerLayoutContainer.setAllowOpenDrawer(false, true);
                if (fragment instanceof HomeActivity) {
                    backgroundTablet.setVisibility(View.VISIBLE);
                    shadowTabletSide.setVisibility(View.GONE);
                    shadowTablet.setBackgroundColor(0x00000000);
                } else {
                    shadowTablet.setBackgroundColor(0x7f000000);
                }
                layersActionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, false, false);
                return false;
            }
        } else {
            boolean allow = true;
            if (fragment instanceof LoginActivity) {
                if (mainFragmentsStack.size() == 0) {
                    allow = false;
                }
            } else if (fragment instanceof HomeActivity) {
                if (mainFragmentsStack.size() == 1) {
                    allow = true;
                }
            } else if (fragment instanceof ChatActivity) {
                if (mainFragmentsStack.size() == 2) {
                    allow = true;
                }
            }
            drawerLayoutContainer.setAllowOpenDrawer(allow, false);
        }
        return true;
    }

    @Override
    public boolean needAddFragmentToStack(BaseFragment fragment, ActionBarLayout layout) {
        if (AndroidUtilities.isTablet()) {
            drawerLayoutContainer.setAllowOpenDrawer(true, true);
            if (!(fragment instanceof HomeActivity)) {
                ActionBarLayout actionBarLayout = layersActionBarLayout;
                if (layout != actionBarLayout) {
                    actionBarLayout.setVisibility(View.VISIBLE);
                    drawerLayoutContainer.setAllowOpenDrawer(false, false);
                }
                if (fragment instanceof LoginActivity) {
                    backgroundTablet.setVisibility(View.VISIBLE);
                    shadowTabletSide.setVisibility(View.GONE);
                    shadowTablet.setBackgroundColor(0);
                } else {
                    shadowTablet.setBackgroundColor(Theme.getColor(Theme.key_buttonBackgroundBlue));
                }
                actionBarLayout.addFragmentToStack(fragment);
                return false;
                   /* HomeActivity dialogsActivity = (HomeActivity) fragment;
                    if (/*dialogsActivity.isMainDialogList() && layout != actionBarLayout) {
                        actionBarLayout.removeAllFragments();
                        actionBarLayout.addFragmentToStack(fragment);
                        layersActionBarLayout.removeAllFragments();
                        layersActionBarLayout.setVisibility(View.GONE);
                        drawerLayoutContainer.setAllowOpenDrawer(true, false);
                        if (!tabletFullSize) {
                            shadowTabletSide.setVisibility(View.VISIBLE);
                            if (rightActionBarLayout.fragmentsStack.isEmpty()) {
                                backgroundTablet.setVisibility(View.VISIBLE);
                            }
                        }
                        return false;
                    }*/
            } else if (layout != layersActionBarLayout) {
                layersActionBarLayout.setVisibility(View.VISIBLE);
                drawerLayoutContainer.setAllowOpenDrawer(false, true);
                if (fragment instanceof HomeActivity) {
                    backgroundTablet.setVisibility(View.VISIBLE);
                    shadowTabletSide.setVisibility(View.GONE);
                    shadowTablet.setBackgroundColor(0x00000000);
                } else {
                    shadowTablet.setBackgroundColor(0x7f000000);
                }
                layersActionBarLayout.addFragmentToStack(fragment);
                return false;
            }
        } else {
            boolean allow = true;
            if (fragment instanceof LoginActivity) {
                if (mainFragmentsStack.size() == 0) {
                    allow = false;
                }
            } else if (fragment instanceof HomeActivity) {
                if (mainFragmentsStack.size() == 1) {
                    allow = false;
                }
            } else if (fragment instanceof ChatActivity) {
                if (mainFragmentsStack.size() == 2) {
                    allow = true;
                }
            }
            drawerLayoutContainer.setAllowOpenDrawer(allow, false);
        }
        return true;
    }

    @Override
    public boolean needCloseLastFragment(ActionBarLayout layout) {
        if (AndroidUtilities.isTablet()) {
            if (layout == actionBarLayout && layout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (layout == rightActionBarLayout) {
                if (!tabletFullSize) {
                    backgroundTablet.setVisibility(View.VISIBLE);
                }
            } else if (layout == layersActionBarLayout && actionBarLayout.fragmentsStack.isEmpty() && layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else {
            if (layout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            }
            if (layout.fragmentsStack.size() >= 2) {
                drawerLayoutContainer.setAllowOpenDrawer(true, false);
            }
        }
        return true;
    }

    public void rebuildAllFragments(boolean last) {
        if (layersActionBarLayout != null) {
            layersActionBarLayout.rebuildAllFragmentViews(last, last);
        } else {
            actionBarLayout.rebuildAllFragmentViews(last, last);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRebuildAllFragments(ActionBarLayout layout, boolean last) {
        if (AndroidUtilities.isTablet()) {
            if (layout == layersActionBarLayout) {
                rightActionBarLayout.rebuildAllFragmentViews(last, last);
                actionBarLayout.rebuildAllFragmentViews(last, last);
            }
        }
        drawerLayoutAdapter.notifyDataSetChanged();
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.didChangeTheme) {
            applyTheme = (Theme.ThemeInfo) args[0];
            AndroidUtilities.runOnUIThread(animationRunnable);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didChangeTheme);
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            actionBarLayout.onPause();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override
    protected void onResume() {
        actionBarLayout.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectionsManager.getInstance(this).cancelAll();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didChangeTheme);
        ThemeEditorView instance = ThemeEditorView.getInstance();
        if (instance != null) {
            instance.destroy();
        }
        //FileLoader.getInstance().deleteFile("/sdcard/EagleXCheats");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults == null) {
            grantResults = new int[0];
        }
        if (permissions == null) {
            permissions = new String[0];
        }
        boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if (requestCode == 4) {
            if (!granted) {
                showPermissionErrorAlert(getString(R.string.PermissionStorage));
            } else {
                FileLog.e("Storage permission granted");
            }
        }

        if (actionBarLayout.fragmentsStack.size() != 0) {
            BaseFragment fragment = actionBarLayout.fragmentsStack.get(actionBarLayout.fragmentsStack.size() - 1);
            fragment.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
            actionBarLayout.fragmentsStack.get(actionBarLayout.fragmentsStack.size() - 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (actionBarLayout.fragmentsStack.size() != 0) {
            BaseFragment fragment = actionBarLayout.fragmentsStack.get(actionBarLayout.fragmentsStack.size() - 1);
            fragment.onActivityResultFragment(requestCode, resultCode, data);
        }
    }

    private void showPermissionErrorAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.AppName));
        builder.setMessage(message);
        builder.setNegativeButton(getString(R.string.PermissionOpenSettings), (dialogInterface, i) -> {
            try {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package" + ApplicationLoader.applicationContext.getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
        builder.setPositiveButton(getString(R.string.OK), null);
        builder.show();

    }

    public void onFinish() {

    }

    public HomeActivity getHomeActivity() {
        return homeActivity;
    }

    public ActionBarLayout getActionBarLayout() {
        return actionBarLayout;
    }

    public ActionBarLayout getLayersActionBarLayout() {
        return layersActionBarLayout;
    }

    public ActionBarLayout getRightActionBarLayout() {
        return rightActionBarLayout;
    }

    private void setTheme() {
        actionBarLayout.animateThemedValues(applyTheme, 0, applyTheme.isDark, false);
        if (AndroidUtilities.isTablet()) {
            layersActionBarLayout.animateThemedValues(applyTheme, 0, applyTheme.isDark, true);
            rightActionBarLayout.animateThemedValues(applyTheme, 0, applyTheme.isDark, true);
        }
        AndroidUtilities.setLightNavigationBar(getWindow(), !applyTheme.isDark);
        AndroidUtilities.runOnUIThread(() -> {
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }, 300);
        getWindow().setNavigationBarColor(Theme.getColor(Theme.key_navigationBarColor));
        AndroidUtilities.setLightStatusBar(getWindow(), !applyTheme.isDark);
    }

    public void checkLayout() {
        if (!AndroidUtilities.isTablet() || rightActionBarLayout == null) {
            return;
        }

        if (!AndroidUtilities.isInMultiwindow && (!AndroidUtilities.isSmallTablet() || getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
            tabletFullSize = false;
            if (actionBarLayout.fragmentsStack.size() >= 2) {
                for (int a = 1; a < actionBarLayout.fragmentsStack.size(); a++) {
                    BaseFragment chatFragment = actionBarLayout.fragmentsStack.get(a);
                    chatFragment.onPause();
                    actionBarLayout.fragmentsStack.remove(a);
                    rightActionBarLayout.fragmentsStack.add(chatFragment);
                    a--;
                }
                actionBarLayout.showLastFragment();
                rightActionBarLayout.showLastFragment();
            }
            rightActionBarLayout.setVisibility(rightActionBarLayout.fragmentsStack.isEmpty() ? View.GONE : View.VISIBLE);
            backgroundTablet.setVisibility(rightActionBarLayout.fragmentsStack.isEmpty() ? View.VISIBLE : View.GONE);
            shadowTabletSide.setVisibility(!actionBarLayout.fragmentsStack.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            tabletFullSize = true;
            if (!rightActionBarLayout.fragmentsStack.isEmpty()) {
                for (int a = 0; a < rightActionBarLayout.fragmentsStack.size(); a++) {
                    BaseFragment chatFragment = rightActionBarLayout.fragmentsStack.get(a);
                    chatFragment.onPause();
                    rightActionBarLayout.fragmentsStack.remove(a);
                    actionBarLayout.fragmentsStack.add(chatFragment);
                    a--;
                }
                actionBarLayout.showLastFragment();
            }
            shadowTabletSide.setVisibility(View.GONE);
            rightActionBarLayout.setVisibility(View.GONE);
            backgroundTablet.setVisibility(!actionBarLayout.fragmentsStack.isEmpty() ? View.GONE : View.VISIBLE);
        }

    }

    public void _refresh() {
        DeveloperTools HomeActivity = new DeveloperTools();
        actionBarLayout.removeFragmentFromStack(0);
        actionBarLayout.presentFragment(HomeActivity);
        drawerLayoutContainer.setAllowOpenDrawer(false, false);

        if (AndroidUtilities.isTablet()) {
            layersActionBarLayout.showLastFragment();
            rightActionBarLayout.showLastFragment();
        }
    }


}
