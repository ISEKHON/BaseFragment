package xyz.illuminate.ui.Cells;

import android.content.Context;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import java.util.HashMap;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.CommentsActivity;
import xyz.illuminate.ui.Components.SpanTextView;

public class CommentCell extends FrameLayout {

    ImageView badge;
    Chip role;
    CommentsActivity parentCommentsAct;
    ImageView profile_pic;
    boolean showError;
    SpanTextView comment_text;
    TextView user_name;
    TextView textView_time;
    TextView textView_error;
    ImageView userBadge;

    public CommentCell(Context context, CommentsActivity commentsActivity) {
        super(context);
        parentCommentsAct = commentsActivity;
        View content = LayoutInflater.from(context).inflate(R.layout.coment_cell, this);
        content.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((ViewGroup) content).getChildAt(0).setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        user_name = content.findViewById(R.id.user_name);
        comment_text = content.findViewById(R.id.comment_text);
        profile_pic = content.findViewById(R.id.profile_pic);
        userBadge = content.findViewById(R.id.badge);
        textView_error = content.findViewById(R.id.error);
        setBackground(Theme.getSelectorDrawable(Theme.currentTheme.isDark));
        comment_text.setPadding(comment_text.getPaddingLeft(), comment_text.getPaddingTop(), getPaddingRight(), AndroidUtilities.dp(8));
        badge.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(24), Theme.getColor(Theme.key_badge_background), Color.WHITE));
        init();
    }

    public void init() {
        setWillNotDraw(false);
        user_name.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        comment_text.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    public void setErrorEnabled(boolean enabled) {
        showError = enabled;
        textView_error.setVisibility(enabled ? VISIBLE : GONE);
    }

    public void setErrorInfo(String text){
        textView_error.setVisibility(VISIBLE);
        textView_error.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        textView_error.setText(text);
    }

    public void setData(HashMap<String,String> data){
        if (data.containsKey("error_message")){
            textView_error.setText(data.get("error_message"));
        }

        comment_text.setSpanText(data.get("message"));
        comment_text.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
