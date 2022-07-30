package xyz.illuminate.ui.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.CommentsActivity;
import xyz.illuminate.ui.Components.SpanTextView;

public class CommentsAdapter extends BaseAdapter {

    ArrayList<HashMap<String, String>> mData;
    CommentsActivity commentsActivity;

    ImageView profile_pic;
    SpanTextView comment_text;
    TextView user_name;
    TextView textView_error;
    ImageView userBadge;

    public CommentsAdapter(CommentsActivity commentsActivity, ArrayList<HashMap<String, String>> data) {
        mData = data;
        this.commentsActivity = commentsActivity;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public HashMap<String, String> getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View content = view;
        if (view == null) {
            content = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.coment_cell, null);
        }

        user_name = content.findViewById(R.id.user_name);
        comment_text = content.findViewById(R.id.comment_text);
        profile_pic = content.findViewById(R.id.profile_pic);
        userBadge = content.findViewById(R.id.badge);
        textView_error = content.findViewById(R.id.error);
        content.setBackground(Theme.getSelectorDrawable(Theme.currentTheme.isDark));
        comment_text.setPadding(comment_text.getPaddingLeft(), comment_text.getPaddingTop(), comment_text.getPaddingRight(), AndroidUtilities.dp(8));
        user_name.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        comment_text.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));

        comment_text.setSpanText(mData.get(i).get("message"));
        return content;
    }
}



/*extends RecyclerView.Adapter {

    public static final int TYPE_EMPTY = -2;
    public static final int TYPE_FAILED = 2;
    public static final int TYPE_SENDING = 1;
    public static final int TYPE_SENT = 0;

    public final CommentsActivity commentsActivity;
    OnItemLongClickListener listener;
    List<HashMap<String, String>> mData;

    public CommentsAdapter(ArrayList<HashMap<String, String>> data, CommentsActivity commentsActivity) {
        this.commentsActivity = commentsActivity;
        mData = data;
    }

    public interface OnItemLongClickListener {
        boolean OnItemLongClickListener(RecyclerView.ViewHolder viewHolder, int i);
    }

    public void update(List<HashMap<String, String>> data) {
        mData.clear();
        mData.addAll(data);
    }

    public void submitList(List<HashMap<String, String>> data) {
        mData.clear();
        mData.addAll(data);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        listener = longClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 4) {
            return new ViewHolder(new EmptyCell(parent.getContext()));
        }
        if (viewType == TYPE_EMPTY) {
            EmptyView emptyView = new EmptyView(parent.getContext(), AndroidUtilities.dp(50));
            emptyView.setText("No messages...");
            emptyView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return new ViewHolder(emptyView);
        }
        CommentCell cell = new CommentCell(parent.getContext(), commentsActivity);
        cell.setBackground(Theme.getSelectorDrawable(Theme.getColor(Theme.key_listSelector), Theme.key_windowBackgroundWhite));
        cell.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ViewHolder viewHolder = new ViewHolder(cell);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() != 4 && holder.getItemViewType() != -2) {
            CommentCell cell = (CommentCell) holder.itemView;
            switch (holder.getItemViewType()) {
                case 0:
                    cell.setBackground(Theme.getSelectorDrawable(Theme.getColor(Theme.key_listSelector), Theme.key_windowBackgroundWhite));
                    cell.setErrorEnabled(false);
                    break;
                case TYPE_SENDING:
                    cell.setBackground(Theme.getSelectorDrawable(Theme.getColor(Theme.key_listSelector), Theme.key_actionBarTabLine));
                    cell.setErrorEnabled(false);
                    break;
                case TYPE_FAILED:
                    cell.setBackground(Theme.getSelectorDrawable(Theme.getColor(Theme.key_listSelector), Theme.key_actionBarDefault));
                    cell.setErrorEnabled(false);
                    break;
            }
            cell.setData(mData.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData == null || mData.isEmpty()) {
            return TYPE_EMPTY;
        } else if (mData.get(position).get("type") == null) {
            return TYPE_SENT;
        } else {
            return Integer.parseInt(mData.get(position).get("type"));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private static class EmptyView extends LinearLayout {

        TextView text;

        public EmptyView(Context context, int paddingBottom) {
            super(context);
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setPadding(0, 0, 0, paddingBottom);
            text = new TextView(context);
            text.setText("");
            text.setTextSize(14);
            text.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            text.setTypeface(AndroidUtilities.getTypeface("fonts/opensans_medium.ttf"));

            addView(text, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        }

        public void setText(String text) {
            this.text.setText(text);
        }
    }

}*/