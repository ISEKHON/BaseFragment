package xyz.illuminate.ui.Components.blur3.source;

import android.graphics.Canvas;

import xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawable;

public interface BlurredBackgroundSource {
    BlurredBackgroundDrawable createDrawable();

    void draw(Canvas canvas, float left, float top, float right, float bottom);
}
