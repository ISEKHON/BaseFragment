package xyz.illuminate.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.FileLog;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Components.LayoutHelper;

public class DrawerActionCell extends FrameLayout {

    private final TextView textView;

    public DrawerActionCell(Context context) {
        super(context);

        textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_drawerTextColor));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        textView.setCompoundDrawablePadding(AndroidUtilities.dp(29));
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP, 19, 0, 16, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        textView.setTextColor(Theme.getColor(Theme.key_drawerTextColor));
    }

    public void setTextAndIcon(String text, int resId) {
        try {
            textView.setText(text);
            Drawable drawable = getResources().getDrawable(resId).mutate();
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_drawerImageColor), PorterDuff.Mode.MULTIPLY));
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }
}
