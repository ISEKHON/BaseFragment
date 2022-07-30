package xyz.illuminate.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.LocaleController;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.ActionBar;
import xyz.illuminate.ui.ActionBar.BackDrawable;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Adapters.CommentsAdapter;
import xyz.illuminate.ui.Components.AlertsCreator;
import xyz.illuminate.ui.Components.EditTextCaption;
import xyz.illuminate.ui.Components.LayoutHelper;
import xyz.illuminate.ui.Components.SizeNotifierFrameLayout;
import xyz.illuminate.ui.Components.TextStyleSpan;
import xyz.illuminate.ui.Components.TypefaceSpan;

public class CommentsActivity extends BaseFragment {
    CommentsAdapter mAdapter;
    ArrayList<HashMap<String, String>> comments = new ArrayList<>();
    LinearLayout container;
    FrameLayout contentView;
    EditTextCaption commentEdit;
    ImageView sendComment;
    LinearLayoutManager layoutManager;
    ListView listView;

    @Override
    protected ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setCastShadows(true);
        actionBar.setAddToContainer(true);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setTitle("Title");
        actionBar.setSubtitle("Subtitle");
        return actionBar;
    }

    @Override
    public View createView(Context context) {
        fragmentView = new SizeNotifierFrameLayout(context);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                super.onItemClick(id);
                if (id == -1) {
                    finishFragment();
                }
            }
        });
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.comments, (ViewGroup) fragmentView, false);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        container = inflatedView.findViewById(R.id.linear_content);
        contentView = new FrameLayout(context);
        container.addView(contentView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        ((ViewGroup) fragmentView).addView(inflatedView);

        listView = new ListView(context);
        layoutManager = new LinearLayoutManager(context);
        mAdapter = new CommentsAdapter(this, comments);

        contentView.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        commentEdit = new EditTextCaption(context){
            @Override
            protected void extendActionMode(ActionMode actionMode, Menu menu) {
                fillActionModeMenu(menu);
            }
        };
        commentEdit.setHint("Enter something");
        commentEdit.setTextSize(14);
        commentEdit.setWindowView(getParentActivity().getWindow().getDecorView());
        commentEdit.setAllowTextEntitiesIntersection(true);
        commentEdit.setIncludeFontPadding(false);
        commentEdit.setMaxLines(6);
        commentEdit.setDelegate(() -> {
            commentEdit.invalidateEffects();
        });
        commentEdit.setBackgroundColor(Color.TRANSPARENT);
        commentEdit.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        ((LinearLayout) inflatedView.findViewById(R.id.comm)).addView(commentEdit, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        sendComment = inflatedView.findViewById(R.id.sendComment);
        sendComment.setImageTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_buttonBackgroundBlue)));
        sendComment.setBackground(Theme.getSelectorDrawable(!Theme.currentTheme.isDark));
        sendComment.setOnClickListener(view -> {
            sendMessage(commentEdit.getText().toString());
        });

        return fragmentView;
    }

    public void sendMessage(String message) {
        if (!TextUtils.isEmpty(message.trim()) && message.length() != 0) {
            if (message.length() == 1000) {
                AlertsCreator.showSimpleAlert(this, "Message too long cant send");
            }
            HashMap<String, String> draft = new HashMap<>();
            draft.put("message", message);
            draft.put("type", String.valueOf(1));
            comments.add(draft);
            listView.setAdapter(new CommentsAdapter(this, comments));
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            int position = comments.size();
            AndroidUtilities.showToast(String.valueOf(position));
        }
    }

    public void fillActionModeMenu(Menu menu) {
        if (menu.findItem(R.id.menu_bold) == null) {
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
    }

}
