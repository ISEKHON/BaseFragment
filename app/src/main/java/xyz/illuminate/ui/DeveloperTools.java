package xyz.illuminate.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.CallBackTask;
import xyz.illuminate.app.FileLog;
import xyz.illuminate.app.LocaleController;
import xyz.illuminate.app.NotificationCenter;
import xyz.illuminate.app.R;
import xyz.illuminate.app.SingleCopyAsyncTask;
import xyz.illuminate.ui.ActionBar.ActionBar;
import xyz.illuminate.ui.ActionBar.AlertDialog;
import xyz.illuminate.ui.ActionBar.BackDrawable;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.ActionBar.ThemeDescription;
import xyz.illuminate.ui.Cells.TextSettingsCell;
import xyz.illuminate.ui.Cells.ThemeHorizontalListCell;
import xyz.illuminate.ui.Components.BulletinFactory;
import xyz.illuminate.ui.Components.EditTextCaption;
import xyz.illuminate.ui.Components.LayoutHelper;
import xyz.illuminate.ui.Components.RecyclerListView;
import xyz.illuminate.ui.Components.SizeNotifierFrameLayout;
import xyz.illuminate.ui.Components.TextStyleSpan;
import xyz.illuminate.ui.Components.ThemeEditorView;
import xyz.illuminate.ui.Components.TypefaceSpan;
import xyz.illuminate.ui.Components.voip.CellFlickerDrawable;

public class DeveloperTools extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    RecyclerListView list;
    ListAdapter listAdapter;
    LinearLayoutManager layoutManager;
    HomeActivity parentActivity;

    int rowCount;
    int homeRow;
    int themeEditorRow;
    int themeRow;

    @Override
    protected ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setCastShadows(true);
        actionBar.setTitle("Developer Tools");
        actionBar.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });
        return actionBar;
    }

    @Override
    public View createView(Context context) {
        parentActivity = new HomeActivity();
        fragmentView = new SizeNotifierFrameLayout(context, getParentLayout());
        fragmentView.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
        SizeNotifierFrameLayout root = (SizeNotifierFrameLayout) fragmentView;

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        listAdapter = new ListAdapter(context);
        list = new RecyclerListView(context);
        layoutManager = new LinearLayoutManager(context);
        list.setLayoutManager(layoutManager);
        list.setBackground(null);
        list.setNestedScrollingEnabled(true);
        list.setAdapter(listAdapter);
        layout.addView(list, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        list.setOnItemClickListener((view, position) -> {
            if (!view.isEnabled()) {
                return;
            }
            if (position == homeRow) {
                presentFragment(new HomeActivity());
            } else if (position == themeEditorRow) {
                new ThemeEditorView().show(getParentActivity(), Theme.currentTheme);
                BulletinFactory.createCopyLinkBulletin(DeveloperTools.this);
            }
        });
        EditTextCaption caption = new EditTextCaption(context);
        caption.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));

        caption.setCaption("||test|| **bold** http://btc `jsu` ~loda~  @ok_ok");
        caption.requestLayout();

        layout.addView(caption, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        TextView textView = new TextView(context) {
            CellFlickerDrawable cellFlickerDrawable;

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (cellFlickerDrawable == null) {
                    cellFlickerDrawable = new CellFlickerDrawable();
                    cellFlickerDrawable.drawFrame = false;
                    cellFlickerDrawable.repeatProgress = 2f;
                }
                cellFlickerDrawable.setParentWidth(getMeasuredWidth());
                AndroidUtilities.rectTmp.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                cellFlickerDrawable.draw(canvas, AndroidUtilities.rectTmp, AndroidUtilities.dp(4),null);
                invalidate();
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                int size = MeasureSpec.getSize(widthMeasureSpec);
                if (size > AndroidUtilities.dp(260)) {
                    super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(320), MeasureSpec.EXACTLY), heightMeasureSpec);
                } else {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            }
        };
        textView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4), Theme.getColor(Theme.key_buttonBackgroundBlue), getThemedColor(Theme.key_divider)));
        textView.setPadding(AndroidUtilities.dp(34), 0, AndroidUtilities.dp(34), 0);
        textView.setText("show dialog");
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(v -> {
            // MediaController.saveFile("/sdcard/test.apk", getParentActivity(), 2, "test.apk", ".apk", () -> {
            AlertDialog.Builder d = new AlertDialog.Builder(context);
            d.setTitle("Basefragment");
            d.setMessage(caption.getText().toString());
            AlertDialog show = d.create();
            show.show();

            BulletinFactory.of(DeveloperTools.this).createErrorBulletin("There was an error on server").show();
        });
        layout.addView(textView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 42, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 36, 0, 36, 76));
        root.addView(layout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return fragmentView;
    }

    @Override
    public boolean extendActionMode(Menu menu) {
        fillActionModeMenu(menu);
        return true;
    }

    public void fillActionModeMenu(Menu menu) {
        if (menu.findItem(R.id.menu_bold) != null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            menu.removeItem(android.R.id.shareText);
        }
        int order = 6;
        menu.add(R.id.menu_groupbolditalic, R.id.menu_spoiler, order++, LocaleController.getString("Spoiler", R.string.Spoiler));
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(LocaleController.getString("Bold", R.string.Bold));
        stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        menu.add(R.id.menu_groupbolditalic, R.id.menu_bold, order++, stringBuilder);
        stringBuilder = new SpannableStringBuilder(LocaleController.getString("Italic", R.string.Italic));
        stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        menu.add(R.id.menu_groupbolditalic, R.id.menu_italic, order++, stringBuilder);

        stringBuilder = new SpannableStringBuilder(LocaleController.getString("Mono", R.string.Mono));
        stringBuilder.setSpan(new TypefaceSpan(Typeface.MONOSPACE), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        menu.add(R.id.menu_groupbolditalic, R.id.menu_mono, order++, stringBuilder);

        stringBuilder = new SpannableStringBuilder(LocaleController.getString("Strike", R.string.Strike));
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= TextStyleSpan.FLAG_STYLE_STRIKE;
        stringBuilder.setSpan(new TextStyleSpan(run), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        menu.add(R.id.menu_groupbolditalic, R.id.menu_strike, order++, stringBuilder);
        stringBuilder = new SpannableStringBuilder(LocaleController.getString("Underline", R.string.Underline));
        run = new TextStyleSpan.TextStyleRun();
        run.flags |= TextStyleSpan.FLAG_STYLE_UNDERLINE;
        stringBuilder.setSpan(new TextStyleSpan(run), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        menu.add(R.id.menu_groupbolditalic, R.id.menu_underline, order++, stringBuilder);
        menu.add(R.id.menu_groupbolditalic, R.id.menu_link, order++, LocaleController.getString("CreateLink", R.string.CreateLink));
        menu.add(R.id.menu_groupbolditalic, R.id.menu_regular, order++, LocaleController.getString("Regular", R.string.Regular));
    }


    @Override
    public boolean onFragmentCreate() {
        updateRows();
        return true;
    }


    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        AndroidUtilities.showToast(data.getData().toString());
        if (requestCode == 69) {
            Uri uri = data.getData();
            if (uri != null) {
                new SingleCopyAsyncTask(uri, getParentActivity(), new CallBackTask() {
                    @Override
                    public void onCopyPreExecute() {

                    }

                    @Override
                    public void onCopyProgressUpdate(int progress) {

                    }

                    @Override
                    public void onCopyPostExecute(String path, boolean wasSuccessful, String reason) {
                        if (wasSuccessful) {
                            try {
                                Theme.ThemeInfo theme = new Gson().fromJson(getFileLoader().readFile(path), Theme.ThemeInfo.class);
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didChangeTheme, theme);
                                Theme.importTheme(theme);
                                AndroidUtilities.showToast("Theme imported");
                            } catch (Exception e) {
                                FileLog.e(e);
                                AndroidUtilities.showToast("error" + "\n\n" + reason);
                            }
                        }
                    }
                }).execute();
            } else {
                AndroidUtilities.showToast("uri null,");
            }
        } else if (requestCode == 400 && data != null) {
            AndroidUtilities.showToast("post");
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSelectFile, data.getData());
        } else {
            AndroidUtilities.showToast("nothing");
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }

        if (id == NotificationCenter.didChangeTheme) {
            AndroidUtilities.showToast("theme changed");
        } else if (id == NotificationCenter.didUpdateTheme) {
            AndroidUtilities.showToast("update theme");
        } else if (id == NotificationCenter.didSelectFile) {
            AndroidUtilities.showToast("file selected");
        }
    }

    @Override
    public int getThemedColor(String key) {
        return Theme.getColor(key);
    }

    public void updateRows() {
        rowCount = 0;
        homeRow = rowCount++;
        themeEditorRow = rowCount++;
        themeRow = rowCount++;

        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(list, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(list, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(list, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(list, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        return themeDescriptions;
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {

        private final Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                default:
                    view = new TextSettingsCell(mContext);
                    // view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new ThemeHorizontalListCell(DeveloperTools.this, mContext);
                    view.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                    break;

            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell detailSettingsCell = (TextSettingsCell) holder.itemView;
                    if (position == homeRow) {
                        detailSettingsCell.setText("Open HomeActivity", true);
                    } else if (position == themeEditorRow) {
                        detailSettingsCell.setText("Open Theme Editor", true);
                    }
                    break;
                case 1:
                    ThemeHorizontalListCell themesHorizontalListCell = (ThemeHorizontalListCell) holder.itemView;
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == themeEditorRow ||
                    position == themeRow ||
                    position == homeRow;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == homeRow || position == themeEditorRow) {
                return 0;
            } else if (position == themeRow) {
                return 1;
            }
            return 0;
        }
    }
}
