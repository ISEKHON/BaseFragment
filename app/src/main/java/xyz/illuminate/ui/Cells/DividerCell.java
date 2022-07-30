package xyz.illuminate.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.core.graphics.ColorUtils;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.ui.ActionBar.Theme;

public class DividerCell extends View {

    private final Paint paint = new Paint();
    private boolean forceDarkTheme;

    public DividerCell(Context context) {
        super(context);
        setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), getPaddingTop() + getPaddingBottom() + 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (forceDarkTheme) {
            paint.setColor(ColorUtils.blendARGB(Color.BLACK, Theme.getColor(Theme.key_divider), 0.2f));
        } else {
            paint.setColor(Theme.getColor(Theme.key_divider));
        }

        canvas.drawLine(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getPaddingTop(), paint);
    }

    public void setForceDarkTheme(boolean forceDarkTheme) {
        this.forceDarkTheme = forceDarkTheme;
    }
}
