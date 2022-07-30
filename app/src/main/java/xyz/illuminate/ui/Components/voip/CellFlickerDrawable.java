package xyz.illuminate.ui.Components.voip;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import androidx.core.graphics.ColorUtils;

import xyz.illuminate.app.AndroidUtilities;

public class CellFlickerDrawable {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Shader gradientShader;

    private final Paint paintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Shader gradientShader2;
    public boolean drawFrame = true;
    public float repeatProgress = 1.2f;
    int size;
    int parentWidth;
    float progress;
    long lastUpdateTime;
    Matrix matrix = new Matrix();

    public CellFlickerDrawable() {
        this(64, 204);
    }

    public CellFlickerDrawable(int a1, int a2) {
        size = AndroidUtilities.dp(160);
        gradientShader = new LinearGradient(0, 0, size, 0, new int[]{Color.TRANSPARENT, ColorUtils.setAlphaComponent(Color.WHITE, a1), Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
        gradientShader2 = new LinearGradient(0, 0, size, 0, new int[]{Color.TRANSPARENT, ColorUtils.setAlphaComponent(Color.WHITE, a2), Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
        paint.setShader(gradientShader);
        paintOutline.setShader(gradientShader2);
        paintOutline.setStyle(Paint.Style.STROKE);
        paintOutline.setStrokeWidth(AndroidUtilities.dp(2));
    }

    public void draw(Canvas canvas, RectF rectF, float rad) {
        long currentTime = System.currentTimeMillis();
        if (lastUpdateTime != 0) {
            long dt = currentTime - lastUpdateTime;
            if (dt > 10) {
                progress += dt / 1200f;
                if (progress > repeatProgress) {
                    progress = 0;
                }
                lastUpdateTime = currentTime;
            }
        } else {
            lastUpdateTime = currentTime;
        }

        if (progress > 1f) {
            return;
        }

        float x = (parentWidth + size * 2) * progress - size;
        matrix.setTranslate(x, 0);
        gradientShader.setLocalMatrix(matrix);
        gradientShader2.setLocalMatrix(matrix);

        canvas.drawRoundRect(rectF, rad, rad, paint);
        if (drawFrame) {
            canvas.drawRoundRect(rectF, rad, rad, paintOutline);
        }
    }


    public void draw(Canvas canvas) {
        long currentTime = System.currentTimeMillis();
        if (lastUpdateTime != 0) {
            long dt = currentTime - lastUpdateTime;
            if (dt > 10) {
                progress += dt / 500f;
                if (progress > 4f) {
                    progress = 0;
                }
                lastUpdateTime = currentTime;
            }
        } else {
            lastUpdateTime = currentTime;
        }

        if (progress > 1f) {
            return;
        }

        float x = (parentWidth + size * 2) * progress - size;
        matrix.setTranslate(x, 0);
        gradientShader.setLocalMatrix(matrix);
        gradientShader2.setLocalMatrix(matrix);
    }

    public void setParentWidth(int parentWidth) {
        this.parentWidth = parentWidth;
    }
}
