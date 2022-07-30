package xyz.illuminate.ui.Cells;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.SimpleTextView;
import xyz.illuminate.ui.ActionBar.Theme;

public class ProjectBannerCell extends FrameLayout {

    ImageView banner, icon;
    SimpleTextView title;
    TextView size, category;

    public ProjectBannerCell(@NonNull Context context) {
        super(context);

        View inflatedView = LayoutInflater.from(context).inflate(R.layout.projectpager, this);
        banner = inflatedView.findViewById(R.id.imageview_banner);
        icon = inflatedView.findViewById(R.id.icon);
        size = inflatedView.findViewById(R.id.size);
        category = inflatedView.findViewById(R.id.category);

        title = new SimpleTextView(context);
        title.setTextSize(16);
        title.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        title.setScrollNonFitText(true);
        title.setTypeface(AndroidUtilities.getTypeface("fonts/opensans_medium.ttf"));

        size.setTypeface(AndroidUtilities.getTypeface("fonts/opensans_medium.ttf"));
        size.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
    }

    public void setTitle(String proj_title) {
        title.setText(proj_title);
    }

    public void setCategory(String proj_category) {
        category.setText(proj_category);
    }

    public ImageView getIcon() {
        return icon;
    }

    public ImageView getBanner() {
        return banner;
    }

    public void setProjectSize(String proj_size) {
        size.setText(proj_size);
    }
}
