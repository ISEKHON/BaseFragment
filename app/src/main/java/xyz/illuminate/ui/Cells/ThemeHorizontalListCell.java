package xyz.illuminate.ui.Cells;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.ApplicationLoader;
import xyz.illuminate.app.FileLog;
import xyz.illuminate.app.FilePicker;
import xyz.illuminate.app.NotificationCenter;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.AlertDialog;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.BottomSheet;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Components.LayoutHelper;
import xyz.illuminate.ui.Components.RadioButton;
import xyz.illuminate.ui.Components.RecyclerListView;
import xyz.illuminate.ui.Components.ThemeEditorView;
import xyz.illuminate.ui.Components.ViewHelper;

public class ThemeHorizontalListCell extends RecyclerListView implements NotificationCenter.NotificationCenterDelegate {

    private final ArrayList<Theme.ThemeInfo> themes = Theme.themes;

    private final LinearLayoutManager horizontalLayoutManager;
    private final ThemesListAdapter adapter;
    private final boolean ignoreChange = false;
    private final BaseFragment parentFragment;
    private Theme.ThemeInfo exportTheme;

    public ThemeHorizontalListCell(BaseFragment parentFragment, Context context) {
        super(context);
        this.parentFragment = parentFragment;
        getThemes();

        //	setItemAnimator(null);
        //	setLayoutAnimation(null);
        horizontalLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        setPadding(0, 0, 0, 0);
        setClipToPadding(false);
        horizontalLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(horizontalLayoutManager);
        setAdapter(adapter = new ThemesListAdapter(context));

        setOnItemClickListener((view, position) -> {

            if (position == themes.size()) {
                BottomSheet.Builder builder = new BottomSheet.Builder(context);
                CharSequence[] itemsText = new CharSequence[]{"Add new", "Import theme"};
                int[] items = new int[]{R.drawable.poll_add_plus, R.drawable.ic_arrow_drop_down};

                builder.setItems(itemsText, items, (dg, item) -> {
                    switch (item) {
                        default:
                        case 0: {
                            AlertDialog.Builder abuilder = new AlertDialog.Builder(context);
                            abuilder.setTitle("Create new theme");
                            LinearLayout layout = new LinearLayout(context);
                            layout.setOrientation(LinearLayout.VERTICAL);

                            TextInputLayout til = (TextInputLayout) LayoutInflater.from(context).inflate(R.layout.design_textinput, null, false);//textinput_workaround
                            TextInputEditText themeName = til.findViewById(R.id.workaround_edittext);

                            themeName.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                            themeName.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
                            til.setHint("Theme name");
                            til.setHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2)));
                            til.setDefaultHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2)));

                            layout.addView(til, LayoutHelper.createLinear(-1, 58, Gravity.START, 16, 8, 16, 8));

                            TextInputLayout previewLayout = (TextInputLayout) LayoutInflater.from(context).inflate(R.layout.design_textinput, null, false);
                            TextInputEditText previewColor = previewLayout.findViewById(R.id.workaround_edittext);
                            previewColor.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                            previewColor.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
                            previewLayout.setHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2)));
                            previewLayout.setDefaultHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2)));
                            previewLayout.setHint("Preview Color (eg. #FFFFFF)");
                            layout.addView(previewLayout, LayoutHelper.createLinear(-1, 58, Gravity.START, 16, 8, 16, 8));

                            AndroidUtilities.setDefaultOutlineColor(til, previewLayout);
                            AndroidUtilities.setUpperHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader), til, previewLayout);

                            TextCheckCell darkMode = new TextCheckCell(context);
                            darkMode.setTextAndCheck("Dark mode", true, false);
                            darkMode.setOnClickListener((c) -> {
                                darkMode.setChecked(!darkMode.isChecked());
                            });
                            layout.addView(darkMode);

                            abuilder.setView(layout);

                            abuilder.setPositiveButton("CREATE", null);
                            abuilder.setNegativeButton("CANCEL", null);


                            AlertDialog dialog = abuilder.create();

                            dialog.show();

                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((v) -> {
                                previewLayout.setErrorEnabled(false);
                                if (previewColor.getText().toString().matches("#([A-Fa-f0-9]{6})")) {
                                    createTheme(themeName.getText().toString(), darkMode.isChecked(), Color.parseColor(previewColor.getText().toString()));
                                    dialog.dismiss();
                                } else {
                                    previewLayout.setError("Invalid color");
                                }
                            });
                            break;
                        }
                        case 1: {
                            FilePicker.pickSingleFile(parentFragment, "*/*", 69);
                            break;
                        }
                    }
                });

                builder.create().show();

            } else {

                if (!Theme.isAnimatingColor()) {
                    animateTheme(themes.get(position));
			    	/*ignoreChange = true;
			    	AndroidUtilities.runOnUIThread(() -> {
			    	    ignoreChange = false;
			    	}, 500);*/

                }
            }
        });

        setOnItemLongClickListener(((view, position) -> {
            if (position == themes.size()) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            CharSequence[] items = new CharSequence[3];
            if (themes.get(position).uuid.equals(Theme.currentTheme.uuid)) {
                items[0] = "Edit Theme";
            }
            items[1] = "Export Theme";
            if (!themes.get(position).isDefault) {
                items[2] = "Delete theme";
            }

            int[] icons = new int[]{
                    R.drawable.msg_edit,
                    R.drawable.msg_shareout,
                    R.drawable.msg_delete
            };

            builder.setItems(items, icons, (v, id) -> {
                if (id == 0) {
                    new ThemeEditorView().show(parentFragment.getParentActivity(), Theme.currentTheme);
                } else if (id == 1) {
                    exportTheme(themes.get(position));
                } else if (id == 2) {
                    deleteTheme(themes.get(position));
                    getThemes();
                    adapter.notifyDataSetChanged();
                }
            });
            builder.create().show();
            return true;
        }));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSelectFile);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didUpdateTheme);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didChangeTheme);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSelectFile);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdateTheme);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didChangeTheme);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.didSelectFile) {
            Uri uri = (Uri) args[0];
            String string = new Gson().toJson(exportTheme);
            try {
                FileOutputStream outputStream = (FileOutputStream) getContext().getContentResolver().openOutputStream(uri);
                outputStream.write(string.getBytes());
            } catch (FileNotFoundException e) {
                FileLog.e(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (id == NotificationCenter.didUpdateTheme) {
            getThemes();
            AndroidUtilities.showToast("theme updated");
            adapter.notifyDataSetChanged();
        } else if (id == NotificationCenter.didChangeTheme) {
		/*AndroidUtilities.runOnUIThread(() -> {
		    themes.clear();
		    themes.addAll(Theme.themes);
		    adapter.notifyDataSetChanged();
		}, 300);*/
            AndroidUtilities.showToast("theme changed");
        }
    }

    private void deleteTheme(Theme.ThemeInfo themeInfo) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", android.app.Activity.MODE_PRIVATE);
        String themesString = preferences.getString("themes", null);

        ArrayList<Theme.ThemeInfo> savedThemes = new Gson().fromJson(themesString, new TypeToken<ArrayList<Theme.ThemeInfo>>() {
        }.getType());

        savedThemes.remove(themeInfo);
        Theme.themes.remove(themeInfo);
        Theme.ThemeInfo themeToSet;

        if (Theme.currentTheme.equals(themeInfo)) {
            if (themeInfo.isDark) {
                themeToSet = Theme.themes.get(1);
            } else {
                themeToSet = Theme.themes.get(0);
            }
        } else {
            themeToSet = null;
        }

        preferences.edit().putString("themes", new Gson().toJson(savedThemes)).commit();
        if (themeToSet != null) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didChangeTheme, themeToSet);
        }
        //Theme.saveCurrentTheme(themeToSet, true, true, false);
    }

    private void exportTheme(Theme.ThemeInfo themeInfo) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(themeInfo.name, themeInfo.name + ".sh");
        parentFragment.startActivityForResult(intent, 400);

        exportTheme = themeInfo;
    }

    private void animateTheme(Theme.ThemeInfo themeInfo) {
        if (AndroidUtilities.isTablet()) {
            AndroidUtilities.runOnUIThread(() -> {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didChangeTheme, themeInfo);
            }, 50);
        }

        Theme.saveCurrentTheme(themeInfo, true, true, false);

        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didChangeTheme, themeInfo);
        //}
        //Theme.applyTheme(themes.get(position));
        //Theme.saveCurrentTheme(themeInfo, true, true, false);
    }

    private void createTheme(String name, boolean dark, int previewColor) {

        Theme.ThemeInfo theme = new Theme.ThemeInfo();
        theme.name = name;
        theme.uuid = java.util.UUID.randomUUID().toString();
        theme.isDark = dark;
        theme.isDefault = false;
        theme.previewColor = previewColor;
        theme.colors = new HashMap<>();
        theme.colors.putAll(Theme.getDefaultMap(dark));

        //Theme.themes.add(theme);


        animateTheme(theme);
        //Theme.saveCurrentTheme(theme, true, true, false);


        AndroidUtilities.runOnUIThread(() -> {
            getThemes();
            adapter.notifyDataSetChanged();

            AndroidUtilities.showToast("name: " + Theme.currentTheme.name + "windowBackgroundWhite " + Theme.currentTheme.colors.get(Theme.key_windowBackgroundWhite));
        }, 500);

        //nimateTheme(theme);
    }

    private void getThemes() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", android.app.Activity.MODE_PRIVATE);
        String themesString = preferences.getString("themes", null);

        if (themesString != null) {
            ArrayList<Theme.ThemeInfo> savedThemes = new Gson().fromJson(themesString, new TypeToken<ArrayList<Theme.ThemeInfo>>() {
            }.getType());
            themes.clear();
            themes.addAll(savedThemes);
        }

    }

    private static class InnerThemeView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {

        private final CardView cardview;
        private final RadioButton radioButton;
        private Theme.ThemeInfo theme;

        public InnerThemeView(Context context) {
            super(context);

            cardview = new CardView(context);
            cardview.setRadius(AndroidUtilities.dp(8));

            addView(cardview, LayoutHelper.createFrame(80, 100));

            ViewHelper.setPadding(this, 8, 0, 8, 0);

            radioButton = new RadioButton(context);
            radioButton.setCheckedColor(Theme.getColor(Theme.key_buttonBackgroundBlue));
            radioButton.setSize(AndroidUtilities.dp(24));
            radioButton.setChecked(true, false);

		/*	cardview.addView(radioButton, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					Gravity.CENTER));*/
        }

        public void setTheme(Theme.ThemeInfo theme) {
            this.theme = theme;
            cardview.setCardBackgroundColor(theme.previewColor);


        }

        public void setEnabled(boolean val) {
            if (val) {
                radioButton.setVisibility(View.VISIBLE);
            } else {
                radioButton.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            //NotificationCenter.getInstance().addObserver(this, NotificationCenter.didChangeTheme);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            //NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didChangeTheme);
        }

        @Override
        public void didReceivedNotification(int id, Object... args) {
            if (id == NotificationCenter.didChangeTheme) {
                Theme.ThemeInfo changedTheme = (Theme.ThemeInfo) args[0];

                TransitionManager.beginDelayedTransition(this);

                if (changedTheme.equals(theme)) {
                    radioButton.setVisibility(View.VISIBLE);
                } else {
                    radioButton.setVisibility(View.GONE);
                }
            }
        }
    }

    private class AddView extends FrameLayout {

        private final CardView cardView;
        private final ImageView plus;

        public AddView(Context context) {
            super(context);

            cardView = new CardView(context);
            cardView.setRadius(AndroidUtilities.dp(8));
            //cardView.setElevation(AndroidUtilities.dp(1));
            addView(cardView, LayoutHelper.createFrame(80, 100));

            plus = new ImageView(context);
            plus.setImageResource(R.drawable.poll_add_plus);
            plus.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_imageColor), PorterDuff.Mode.MULTIPLY));
            cardView.addView(plus, LayoutHelper.createCard(24, 24, Gravity.CENTER));

            ViewHelper.setPadding(this, 8, 0, 8, 0);
        }
    }

    private class ThemesListAdapter extends SelectionAdapter {

        private final Context mContext;

        ThemesListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new Holder(new InnerThemeView(mContext));
            }
            return new Holder(new AddView(mContext));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                InnerThemeView view = (InnerThemeView) holder.itemView;
                view.setTheme(themes.get(position));
            }

        }

        @Override
        public int getItemCount() {
            return themes.size() + 1;
        }

        @Override
        public int getItemViewType(int pos) {
            if (pos == themes.size()) {
                return 0;
            }
            return 1;
        }
    }
}