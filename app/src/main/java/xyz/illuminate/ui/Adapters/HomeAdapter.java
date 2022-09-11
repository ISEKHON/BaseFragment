package xyz.illuminate.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.ui.Cells.UserCell;
import xyz.illuminate.ui.Components.RecyclerListView;
import xyz.illuminate.ui.Models.ItemModal;

public class HomeAdapter extends RecyclerView.Adapter {

    Context mContext;
    ArrayList<ItemModal> itemModal;

    public HomeAdapter(Context context, ArrayList<ItemModal> items) {
        super();
        itemModal = items;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserCell cell = new UserCell(mContext, AndroidUtilities.dp(8), 1, true);
        return new RecyclerListView.Holder(cell);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserCell cell = (UserCell) holder.itemView;
        cell.setData(itemModal.get(position));
    }

    @Override
    public int getItemCount() {
        return itemModal.size();
    }
}
