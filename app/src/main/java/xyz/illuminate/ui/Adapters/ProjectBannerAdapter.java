package xyz.illuminate.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import xyz.illuminate.ui.Cells.ProjectBannerCell;
import xyz.illuminate.ui.Components.RecyclerListView;
import xyz.illuminate.ui.Models.Project;

public class ProjectBannerAdapter extends RecyclerListView.SelectionAdapter {

    Context mContext;
    ArrayList<Project> mData;

    public ProjectBannerAdapter(ArrayList<Project> data, Context context) {
        mContext = context;
        mData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerListView.Holder(new ProjectBannerCell(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Project project = mData.get(position);
        ProjectBannerCell cell = (ProjectBannerCell) holder.itemView;
        cell.setTitle(project.title);
        cell.setCategory(project.getCategory());
        cell.setProjectSize(project.getFileSize());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return true;
    }
}
