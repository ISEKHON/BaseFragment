package xyz.illuminate.ui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.Cells.DrawerActionCell;
import xyz.illuminate.ui.Cells.DrawerProfileCell;
import xyz.illuminate.ui.Cells.EmptyCell;
import xyz.illuminate.ui.Components.RecyclerListView;

public class DrawerLayoutAdapter extends RecyclerListView.SelectionAdapter {

    private final Context mContext;
    private final ArrayList<Item> items = new ArrayList<>(1);

    public DrawerLayoutAdapter(Context context) {
        mContext = context;
        resetItems();
    }

    @Override
    public int getItemCount() {
        return items.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == 1) {
            return 1;
        }

        return 2;
    }

    public int getId(int position) {
        position -= 2;
        if (position < 0 || position >= items.size()) {
            return -1;
        }
        Item item = items.get(position);
        return item != null ? item.id : -1;
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup view = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                view = new DrawerProfileCell(mContext);
                view.getChildAt(0).setPadding(AndroidUtilities.dp(8), AndroidUtilities.statusBarHeight + AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));
                break;

            case 2:
                view = new DrawerActionCell(mContext);
                break;

            case 1:
            default:
                view = new EmptyCell(mContext, AndroidUtilities.dp(8));
                break;
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new RecyclerListView.Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 2:
                position -= 2;
                DrawerActionCell cell = (DrawerActionCell) holder.itemView;
                items.get(position).bind(cell);
                break;
        }
    }

    private void resetItems() {
        /*if (!UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            return;
        }*/
        items.add(new Item(2, "Developer Tools", R.drawable.photo_tools));
        items.add(new Item(1, "Settings", R.drawable.tool_blur));
    }

    private static class Item {

        public int icon;
        public String text;
        public int id;

        public Item(int id, String text, int icon) {
            this.icon = icon;
            this.id = id;
            this.text = text;
        }

        public void bind(DrawerActionCell actionCell) {
            actionCell.setTextAndIcon(text, icon);
        }
    }
}
