package xyz.illuminate.ui.Adapters;

import android.content.Context;
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

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Components.LayoutHelper;
import xyz.illuminate.ui.Components.RecyclerListView;
import xyz.illuminate.ui.Models.Category;
import xyz.illuminate.ui.Models.Project;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Category> mData;
    private BaseFragment parentActivity;
    private RecyclerView parentRecycler;

    public CategoryAdapter(BaseFragment fragment, ArrayList<Category> data) {
        mData = data;
        parentActivity = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FrameLayout layout;
        if (viewType == 7) {
            layout = new FrameLayout(parent.getContext());
        } else {
            layout = new CategoryContainer(parent.getContext(), viewType);
        }
        layout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 7) {
            return;
        }
        CategoryContainer container = (CategoryContainer) holder.itemView;
        Category category = mData.get(position);
        container.setName(category.name);
        container.setData(category.content);
        if (container.getListView().getAdapter() != null) {
            container.getListView().getAdapter().notifyDataSetChanged();
        }
        container.init(category);
    }

    @Override
    public int getItemCount() {
        return mData == null || mData.isEmpty() ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).content_type;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView;
        }

        public void bind(Category category) {
            ((CategoryContainer) content).init(category);
        }
    }

    public class CategoryContainer extends FrameLayout {

        MaterialCardView cardView;
        LinearLayout content;
        RecyclerListView listView;
        ArrayList<Project> projectsData = new ArrayList<>();
        TextView name;
        ImageView view_more;

        public CategoryContainer(@NonNull Context context, int type) {
            super(context);
            LinearLayoutManager layoutManager;

            View inflatedView = LayoutInflater.from(context).inflate(R.layout.category_container, this, false);
            addView(inflatedView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            content = inflatedView.findViewById(R.id.category_content);
            cardView = inflatedView.findViewById(R.id.Card_box);
            view_more = inflatedView.findViewById(R.id.viewMore);
            name = inflatedView.findViewById(R.id.category_name);

            cardView.setCardBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            if (type == 8) {
            } else {
            }
            layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            listView.setAdapter(new ProjectBannerAdapter(projectsData, context));
            listView = new RecyclerListView(context);
            listView.setLayoutManager(layoutManager);
            listView.setNestedScrollingEnabled(false);
            listView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    if (type == 6) {
                        outRect.left += AndroidUtilities.dp(8);
                    }
                }
            });
            content.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            name.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));


        }

        public void init(Category category) {
            if (category.hideSeeMore) {
                view_more.setVisibility(GONE);
            } else {
                view_more.setOnClickListener(view -> {
                });
            }
        }

        public RecyclerListView getListView() {
            return listView;
        }

        public void setData(ArrayList<Project> projectArray) {
            projectsData.clear();
            projectsData.addAll(projectArray);
        }

        public void setName(String category_name) {
            name.setText(category_name);
        }
    }

}
