package xyz.illuminate.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import xyz.illuminate.app.ApplicationLoader;
import xyz.illuminate.app.NotificationCenter;
import xyz.illuminate.app.SharedConfig;
import xyz.illuminate.ui.ActionBar.ActionBar;
import xyz.illuminate.ui.ActionBar.AlertDialog;
import xyz.illuminate.ui.ActionBar.BackDrawable;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Cells.HeaderCell;
import xyz.illuminate.ui.Cells.TextCheckCell;
import xyz.illuminate.ui.Cells.TextDetailSettingsCell;
import xyz.illuminate.ui.Cells.ThemeHorizontalListCell;
import xyz.illuminate.ui.Components.LayoutHelper;
import xyz.illuminate.ui.Components.RecyclerListView;
import xyz.illuminate.ui.Components.SeekBarView;
import xyz.illuminate.ui.Components.ThemeEditorView;

public class SettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    private final SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("config", 0);
    SharedPreferences.Editor edit = preferences.edit();
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private AlertDialog progressDialog;
    @SuppressWarnings("FieldCanBeLocal")
    private LinearLayoutManager layoutManager;
    private int mainHeaderRow;
    private int logsRow;
    private int animationsEnableRow;
    private int animationsEnterDurationRow;
    private int animationsExitDurationRow;
    private int debugLinesRow;
    private int disableDrawerAnimationRow;
    private int advancedSectionRow;
    private int editorRow;
    private int themeHeaderRow;
    private int themeRow;
    private int rowCount;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    @Override
    public View createView(Context context) {
        actionBar.setCastShadows(false);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setOccupyStatusBar(true);
        actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        actionBar.setTitle("Settings");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setVerticalScrollBarEnabled(false);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((view, position) -> {
            if (!view.isEnabled()) {
                return;
            }
            SharedPreferences.Editor edit = preferences.edit();
            if (position == logsRow) {
                TextCheckCell textCheckCell = (TextCheckCell) view;
                edit.putBoolean("logs", textCheckCell.isChecked() ^ true).apply();
                textCheckCell.setChecked(preferences.getBoolean("logs", false));
                SharedConfig.loadConfig();
            } else if (position == animationsEnableRow) {
                TextCheckCell textCheckCell = (TextCheckCell) view;
                edit.putBoolean("animations", textCheckCell.isChecked() ^ true).commit();
                textCheckCell.setChecked(!textCheckCell.isChecked());
                SharedConfig.loadConfig();
            } else if (position == debugLinesRow) {
                TextCheckCell textCheckCell = (TextCheckCell) view;
                edit.putBoolean("debugLines", textCheckCell.isChecked() ^ true).commit();
                textCheckCell.setChecked(textCheckCell.isChecked());
                SharedConfig.loadConfig();
            } else if (position == animationsEnterDurationRow || position == animationsExitDurationRow) {
                int n2 = position == animationsEnterDurationRow ? SharedConfig.animationsEnterDuration : SharedConfig.animationsExitDuration;
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle("Change animation duration");
                LinearLayout layout2 = new LinearLayout(context);
                layout2.setOrientation(LinearLayout.HORIZONTAL);
                final TextView textView = new TextView(context);
                textView.setTextSize(16);
                SeekBarView seekBarView = new SeekBarView(context);
                seekBarView.setReportChanges(true);
                // textView.setText(n2); error
                seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                    @Override
                    public void onSeekBarDrag(boolean stop, float progress) {
                        textView.setText(Math.round(120 + progress * 1480));
                    }

                    @Override
                    public CharSequence getContentDescription() {
                        return "Test";
                    }

                    @Override
                    public int getStepsCount() {
                        return 1480;
                    }

                    @Override
                    public void onSeekBarPressed(boolean pressed) {
                    }
                });
                layout2.addView(seekBarView/*, LayoutHelper.createLinear(-1,38,1,515,5,0,0)*/);
                layout2.addView(textView, LayoutHelper.createLinear(50, 38, 17, 0, 10, 5, 0));
                seekBarView.setProgress(n2 - 120 / 1480);
                builder.setView(layout2);
                builder.setPositiveButton("Apply", (dialogInterface, i) -> {
                    if (position == animationsEnterDurationRow) {
                        edit.putInt("animationsEnterDurations", Integer.parseInt(textView.getText().toString())).commit();
                    } else {
                        edit.putInt("animationsExitDurations", Integer.parseInt(textView.getText().toString())).commit();
                    }
                    SharedConfig.loadConfig();
                });
                showDialog(builder.create());
            } else if (position == editorRow) {
                new ThemeEditorView().show(getParentActivity(), Theme.currentTheme);
            }
        });

        return fragmentView;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void updateRows() {
        rowCount = 0;
        mainHeaderRow = rowCount++;
        logsRow = rowCount++;
        advancedSectionRow = rowCount++;
        animationsEnableRow = rowCount++;
        animationsEnterDurationRow = rowCount++;
        animationsExitDurationRow = rowCount++;
        debugLinesRow = rowCount++;
        disableDrawerAnimationRow = rowCount++;
        editorRow = rowCount++;
        themeHeaderRow = rowCount++;
        themeRow = rowCount++;

        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private final Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == logsRow ||
                    position == debugLinesRow ||
                    position == animationsEnableRow ||
                    position == disableDrawerAnimationRow ||
                    position == animationsEnterDurationRow ||
                    position == animationsExitDurationRow ||
                    position == editorRow || position == themeRow;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new HeaderCell(mContext, 23);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                default:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new ThemeHorizontalListCell(SettingsActivity.this, mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:

                    TextCheckCell textCell = (TextCheckCell) holder.itemView;
                    if (position == logsRow) {
                        textCell.setTextAndValueAndCheck("Enable Logs", "Logs are saved to android/data/xyz.illuminate/logs", preferences.getBoolean("logs", false), true, false);
                    } else if (position == debugLinesRow) {
                        textCell.setTextAndValueAndCheck("Debug lines", "Preview the paths of animations", preferences.getBoolean("debugLines", false), true, false);
                    } else if (position == animationsEnableRow) {
                        textCell.setTextAndCheck("Toggle Animations", SharedConfig.animationsEnabled, false);
                    } else if (position == disableDrawerAnimationRow) {
                        textCell.setTextAndCheck("Disable Drawer Animations", SharedConfig.disableDrawerAnimations, false);
                    }
                    break;
                case 1:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == mainHeaderRow) {
                        headerCell.setText("Debugging");
                    } else if (position == advancedSectionRow) {
                        headerCell.setText("Animations");
                    } else if (position == themeHeaderRow) {
                        headerCell.setText("Theming");
                    }
                    break;
                case 2:

                    TextDetailSettingsCell settingsCell = (TextDetailSettingsCell) holder.itemView;
                    if (position == animationsEnterDurationRow) {
                        settingsCell.setTextAndValue("Enter Duration", "Change the duration of incoming animations", false);
                    } else if (position == animationsExitDurationRow) {
                        settingsCell.setTextAndValue("Exit Duration", "Change the duration of outgoing animations", false);
                    } else if (position == editorRow) {
                        settingsCell.setTextAndValue("Theme editor", "Tap to open the theme editor", false);
                    }
                    break;
                case 3:
                    ThemeHorizontalListCell themesHorizontalListCell = (ThemeHorizontalListCell) holder.itemView;
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == logsRow || position == debugLinesRow || position == animationsEnableRow || position == disableDrawerAnimationRow) {
                return 0;
            } else if (position == mainHeaderRow || position == advancedSectionRow || position == themeHeaderRow) {
                return 1;
            } else if (position == animationsEnterDurationRow || position == animationsExitDurationRow || position == editorRow) {
                return 2;
            } else if (position == themeRow) {
                return 3;
            }
            return 0;
        }
    }

   /* @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        return themeDescriptions;
    }*/
}
