package xyz.illuminate.ui.Cells;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.card.MaterialCardView;
//import com.stfalcon.imageviewer.StfalconImageViewer;

import java.util.Arrays;
import java.util.HashMap;

import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Components.FontTextView;
import xyz.illuminate.ui.Components.SpanTextView;

public class AnnouncementsCell extends FrameLayout {

    MaterialCardView card;
    ImageView image;
    SpanTextView message;
    FontTextView title, time;
    View separator;
    View view;

    public AnnouncementsCell(@NonNull Context context) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.cell_announcements, this);
        title = view.findViewById(R.id.title);
        title.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        message = view.findViewById(R.id.message);
        message.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        time = view.findViewById(R.id.time);
        image = view.findViewById(R.id.image);
        separator = view.findViewById(R.id.linear_separator);
        separator.setBackgroundColor(Theme.getColor(Theme.key_divider));
        card = view.findViewById(R.id.card);
    }

    public void bind(HashMap<String, String> data) {
        title.setText(data.get("title"));
        message.setSpanText(data.get("message"));
        message.setMovementMethod(LinkMovementMethod.getInstance());
        // time.setText(FastDateFormat.getInstance().format(data.get("timestamps")));
        time.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(data.get("timestamps"))));
        message.setLinkTextColor(Theme.getColor(Theme.key_buttonBackgroundBlue));
        if (TextUtils.isEmpty(data.get("image_url"))) {
            card.setVisibility(GONE);
        } else {
            card.setVisibility(VISIBLE);
            Glide.with(image).load(data.get("image_url")).transition(DrawableTransitionOptions.withCrossFade()).placeholder(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundWhite))).centerCrop().into(image);
            //image.setOnClickListener(view -> new StfalconImageViewer.Builder(getContext(), Arrays.asList(data.get("image_url")), (imageView, image) -> {
            //    Glide.with(imageView).load(image).into(imageView);
            //}).allowZooming(true).allowSwipeToDismiss(true).withTransitionFrom(image).show());
        }
    }
}
