package xyz.illuminate.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.NotificationCenter;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Adapters.CategoryAdapter;
import xyz.illuminate.ui.Components.LayoutHelper;
import xyz.illuminate.ui.Components.RecyclerListView;
import xyz.illuminate.ui.Models.Category;
import xyz.illuminate.ui.Models.Project;

public class MainActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    public static String TAG = "MainActivity";
    ArrayList<Category> categories = new ArrayList<>();
    private Context context;
    private boolean ignoreRebuild = false;
    private View inflatedView;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private final int oriental = 0;
    private HomeActivity parentActivity;
    private SwipeRefreshLayout refreshLayout;
    private String type;
    private FloatingActionButton uploadButton;

    public MainActivity() {

    }

    public MainActivity(String type) {
        this.type = type;
    }

    @Override
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.didChangeTheme);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.didChangeTheme);
        getConnectionsManager().cancelAll(TAG);
        super.onFragmentDestroy();
    }

    @Override
    public View createView(Context context) {
        this.context = context;
        actionBar.setAddToContainer(false);
        fragmentView = new FrameLayout(context);
        TextView test = new TextView(context);
        test.setText(type);
        /*if (!type.equals("home")) {
            ((ViewGroup) fragmentView).addView(test, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
            return fragmentView;
        }*/
        inflatedView = LayoutInflater.from(context).inflate(R.layout.activity_home_main, (ViewGroup) fragmentView, false);
        ((FrameLayout) fragmentView).addView(inflatedView);
        if (!AndroidUtilities.isTablet()) {
            inflatedView.post(() -> {
               // inflatedView.setPadding(0, 0, 0, parentActivity.getBottomNavigationCell().getHeight());
            });
            layoutManager = new LinearLayoutManager(context);
            listView = new RecyclerListView(context);
            listView.setLayoutManager(layoutManager);
            if (categories.isEmpty()) {
                addCategories();
            } else {
                ignoreRebuild = true;
            }
            listView.setOverScrollMode(2);
            listView.setAdapter(new CategoryAdapter(this, categories));
            ((LinearLayout) inflatedView.findViewById(R.id.mainHomeContent)).addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
            uploadButton = inflatedView.findViewById(R.id.upload_fab);
            uploadButton.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56), Theme.getColor(Theme.key_fabColor), Theme.getColor(Theme.key_fabColor)));
            uploadButton.setImageTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_imageColorOnSurface)));
            uploadButton.setScaleType(ImageView.ScaleType.CENTER);
            uploadButton.setImageResource(R.drawable.add);
            uploadButton.setBackgroundTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_fabColor)));
            uploadButton.setOnClickListener(view -> {
            });
            refreshLayout = inflatedView.findViewById(R.id.refreshLayout);
            refreshLayout.setOnRefreshListener(() -> {
            });
            inflatedView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            refresh();
			
        }

        return fragmentView;
    }

    public void refresh() {
      //  for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(0);
            Project project = new Project();
            project.id = "1";
            project.downloads = "200";
            project.comments = "30";
            project.category = "Beta";
            project.isPublic = "0";
            project.isVerified = "1";
            project.isEditorChoice = "0";
            project.isLiked = "1";
            project.likes = "6";
            project.projectSize = "10";
            project.userName = "Cloudify User";
            project.title = "Cloudify™ App";
            project.projectType = "sk_pro";

            categories.set(0,category);
            listView.getAdapter().notifyItemChanged(0);
     //   }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        super.onTransitionAnimationStart(isOpen, backward);
       /// if (parentActivity != null && parentActivity.getBottomNavigationCell() != null) {
       //     parentActivity.getBottomNavigationCell().setAnimationInProgress(true);
       // }
    }

    @Override
    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        super.onTransitionAnimationEnd(isOpen, backward);
        //if (parentActivity != null || parentActivity.getBottomNavigationCell() != null) {
        //    parentActivity.getBottomNavigationCell().setAnimationInProgress(false);
        //}
        if (!isOpen) {
            return;
        }
        if (!ignoreRebuild) {
            //refresh here
        } else {
            ignoreRebuild = false;
        }
    }


    private void addCategories() {
        Category editor = new Category();
        editor.name = "Editor";
        editor.scope = "editor";
        editor.content = new ArrayList<>();
        editor.content_type = 5;
        categories.add(editor);

        Category recent = new Category();
        recent.name = "Recent";
        recent.scope = "recent";
        recent.content = new ArrayList<>();
        recent.content_type = 5;
        categories.add(recent);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.didChangeTheme) {
            ignoreRebuild = true;
        }
    }

    public void setHomeActivity(HomeActivity activity) {
        parentActivity = activity;
    }

    public void setIgnoreRebuild(boolean rebuild) {
        ignoreRebuild = rebuild;
    }

    public static class itemDecor extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int padding = AndroidUtilities.getProperPadding();
            outRect.left = padding / 2;
            outRect.right = padding / 2;
        }
    }

}