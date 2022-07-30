package xyz.illuminate.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.DispatchQueue;
import xyz.illuminate.app.FileLog;
import xyz.illuminate.app.SharedConfig;
import xyz.illuminate.app.Utilities;
import xyz.illuminate.ui.ActionBar.ActionBar;
import xyz.illuminate.ui.ActionBar.ActionBarLayout;
import xyz.illuminate.ui.ActionBar.AdjustPanLayoutHelper;
import xyz.illuminate.ui.ActionBar.Theme;

public class SizeNotifierFrameLayout extends FrameLayout {

    private static DispatchQueue blurQueue;
    final BlurBackgroundTask blurBackgroundTask = new BlurBackgroundTask();
    private final float DOWN_SCALE = 12f;
    private final int TOP_CLIP_OFFSET = (int) (10 + DOWN_SCALE);
    public boolean needBlur;
    public boolean blurIsRunning;
    public boolean blurGeneratingTuskIsRunning;
    public ArrayList<BlurBitmap> unusedBitmaps = new ArrayList<>(10);
    public ArrayList<View> blurBehindViews = new ArrayList<>();
    public Paint blurPaintTop = new Paint();
    public Paint blurPaintTop2 = new Paint();
    public Paint blurPaintBottom = new Paint();
    public Paint blurPaintBottom2 = new Paint();
    public float blurCrossfadeProgress;
    public boolean invalidateBlur;
    protected int keyboardHeight;
    protected AdjustPanLayoutHelper adjustPanLayoutHelper;
    protected View backgroundView;
    SnowflakesEffect snowflakesEffect;
    BlurBitmap currentBitmap;
    BlurBitmap prevBitmap;
    Matrix matrix = new Matrix();
    Matrix matrix2 = new Matrix();
    ValueAnimator blurCrossfade;
    int count;
    int times;
    int count2;
    int times2;
    private final Rect rect = new Rect();
    private Drawable backgroundDrawable;
    private int bottomClip;
    private SizeNotifierFrameLayoutDelegate delegate;
    private boolean occupyStatusBar = true;
    private float translationX;
    private float translationY;
    private float bgAngle;
    private final float parallaxScale = 1.0f;
    private int backgroundTranslationY;
    private boolean paused = true;
    private Drawable oldBackgroundDrawable;
    private final ActionBarLayout parentLayout;
    private int emojiHeight;
    private float emojiOffset;
    private boolean animationInProgress;
    private boolean skipBackgroundDrawing;

    public SizeNotifierFrameLayout(Context context) {
        this(context, null);
    }

    public SizeNotifierFrameLayout(Context context, ActionBarLayout layout) {
        super(context);
        setWillNotDraw(false);
        parentLayout = layout;
        adjustPanLayoutHelper = createAdjustPanLayoutHelper();
        backgroundView = new View(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                if (backgroundDrawable == null || skipBackgroundDrawing) {
                    super.onDraw(canvas);
                    return;
                }
                checkLayerType();
                float themeAnimationValue = parentLayout != null ? parentLayout.getThemeAnimationValue() : 1.0f;
                for (int a = 0; a < 2; a++) {
                    Drawable drawable = a == 0 ? oldBackgroundDrawable : backgroundDrawable;
                    if (drawable == null) {
                        continue;
                    }
                    if (a == 1 && oldBackgroundDrawable != null && parentLayout != null) {
                        drawable.setAlpha((int) (255 * themeAnimationValue));
                    } else {
                        drawable.setAlpha(255);
                    }
                    if (drawable instanceof ColorDrawable) {
                        if (bottomClip != 0) {
                            canvas.save();
                            canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - bottomClip);
                        }
                        drawable.setBounds(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight());
                        drawable.draw(canvas);
                        checkSnowflake(canvas);
                        if (bottomClip != 0) {
                            canvas.restore();
                        }
                    } else if (drawable instanceof GradientDrawable) {
                        if (bottomClip != 0) {
                            canvas.save();
                            canvas.clipRect(0, 0, getMeasuredWidth(), getRootView().getMeasuredHeight() - bottomClip);
                        }
                        drawable.setBounds(0, backgroundTranslationY, getMeasuredWidth(), backgroundTranslationY + getRootView().getMeasuredHeight());
                        drawable.draw(canvas);
                        checkSnowflake(canvas);
                        if (bottomClip != 0) {
                            canvas.restore();
                        }
                    } else if (drawable instanceof BitmapDrawable) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                        if (bitmapDrawable.getTileModeX() == Shader.TileMode.REPEAT) {
                            canvas.save();
                            float scale = 2.0f / AndroidUtilities.density;
                            canvas.scale(scale, scale);
                            drawable.setBounds(0, 0, (int) Math.ceil(getMeasuredWidth() / scale), (int) Math.ceil(getRootView().getMeasuredHeight() / scale));
                            drawable.draw(canvas);
                            checkSnowflake(canvas);
                            canvas.restore();
                        } else {
                            int actionBarHeight = (isActionBarVisible() ? ActionBar.getCurrentActionBarHeight() : 0) + (Build.VERSION.SDK_INT >= 21 && occupyStatusBar ? AndroidUtilities.statusBarHeight : 0);
                            int viewHeight = getRootView().getMeasuredHeight() - actionBarHeight;
                            float scaleX = (float) getMeasuredWidth() / (float) drawable.getIntrinsicWidth();
                            float scaleY = (float) (viewHeight) / (float) drawable.getIntrinsicHeight();
                            float scale = Math.max(scaleX, scaleY);
                            int width = (int) Math.ceil(drawable.getIntrinsicWidth() * scale * parallaxScale);
                            int height = (int) Math.ceil(drawable.getIntrinsicHeight() * scale * parallaxScale);
                            int x = (getMeasuredWidth() - width) / 2 + (int) translationX;
                            int y = backgroundTranslationY + (viewHeight - height) / 2 + actionBarHeight + (int) translationY;
                            canvas.save();
                            canvas.clipRect(0, actionBarHeight, width, getMeasuredHeight() - bottomClip);
                            drawable.setBounds(x, y, x + width, y + height);
                            drawable.draw(canvas);
                            checkSnowflake(canvas);
                            canvas.restore();
                        }
                    }
                    if (a == 0 && oldBackgroundDrawable != null && themeAnimationValue >= 1.0f) {
                        oldBackgroundDrawable = null;
                        backgroundView.invalidate();
                    }
                }
            }
        };
        addView(backgroundView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    public void invalidateBlur() {
        invalidateBlur = true;
    }

    public void setBackgroundImage(Drawable bitmap, boolean motion) {
        if (backgroundDrawable == bitmap) {
            return;
        }
        backgroundDrawable = bitmap;
        checkLayerType();
        backgroundView.invalidate();
    }

    private void checkLayerType() {
//        if (parallaxEffect == null && backgroundDrawable instanceof MotionBackgroundDrawable && SharedConfig.getDevicePerformanceClass() == SharedConfig.PERFORMANCE_CLASS_HIGH) {
//            backgroundView.setLayerType(LAYER_TYPE_HARDWARE, null);
//        } else {
//            backgroundView.setLayerType(LAYER_TYPE_NONE, null);
//        }
    }

    public Drawable getBackgroundImage() {
        return backgroundDrawable;
    }

    public void setDelegate(SizeNotifierFrameLayoutDelegate delegate) {
        this.delegate = delegate;
    }

    public void setOccupyStatusBar(boolean value) {
        occupyStatusBar = value;
    }

    public void onPause() {
        paused = true;
    }

    public void onResume() {
        paused = false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        notifyHeightChanged();
    }

    public int measureKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(rect);
        if (rect.bottom == 0 && rect.top == 0) {
            return 0;
        }
        int usableViewHeight = rootView.getHeight() - (rect.top != 0 ? AndroidUtilities.statusBarHeight : 0) - AndroidUtilities.getViewInset(rootView);
        return keyboardHeight = Math.max(0, usableViewHeight - (rect.bottom - rect.top));
    }

    public int getKeyboardHeight() {
        return keyboardHeight;
    }

    public void notifyHeightChanged() {
        if (delegate != null) {
            keyboardHeight = measureKeyboardHeight();
            final boolean isWidthGreater = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y;
            post(() -> {
                if (delegate != null) {
                    delegate.onSizeChanged(keyboardHeight, isWidthGreater);
                }
            });
        }
    }

    public void setBottomClip(int value) {
        if (value != bottomClip) {
            bottomClip = value;
            backgroundView.invalidate();
        }
    }

    public void setBackgroundTranslation(int translation) {
        if (translation != backgroundTranslationY) {
            backgroundTranslationY = translation;
            backgroundView.invalidate();
        }
    }

    public int getBackgroundTranslationY() {
        return 0;
    }

    public int getBackgroundSizeY() {
        int offset = 0;
        return getMeasuredHeight() - offset;
    }

    public int getHeightWithKeyboard() {
        return keyboardHeight + getMeasuredHeight();
    }

    public void setEmojiKeyboardHeight(int height) {
        emojiHeight = height;
    }

    public void setEmojiOffset(boolean animInProgress, float offset) {
        emojiOffset = offset;
        animationInProgress = animInProgress;
    }

    private void checkSnowflake(Canvas canvas) {
        if (Theme.canStartHolidayAnimation()) {
            if (snowflakesEffect == null) {
                snowflakesEffect = new SnowflakesEffect(1);
            }
            snowflakesEffect.onDraw(this, canvas);
        }
    }

    protected boolean isActionBarVisible() {
        return true;
    }

    protected AdjustPanLayoutHelper createAdjustPanLayoutHelper() {
        return null;
    }

    public void setSkipBackgroundDrawing(boolean skipBackgroundDrawing) {
        if (this.skipBackgroundDrawing != skipBackgroundDrawing) {
            this.skipBackgroundDrawing = skipBackgroundDrawing;
            backgroundView.invalidate();
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == getBackgroundImage() || super.verifyDrawable(who);
    }

    public void startBlur() {
        if (!blurIsRunning || blurGeneratingTuskIsRunning || !invalidateBlur || !SharedConfig.chatBlurEnabled()) {
            return;
        }
        int blurAlpha = Color.alpha(Theme.getColor(Theme.key_chat_BlurAlpha));
        if (blurAlpha == 0) {
            return;
        }
        invalidateBlur = false;
        blurGeneratingTuskIsRunning = true;
        int lastW = getMeasuredWidth();
        int lastH = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight + AndroidUtilities.dp(100);

        int bitmapH = (int) (lastH / DOWN_SCALE) + TOP_CLIP_OFFSET;
        int bitmapW = (int) (lastW / DOWN_SCALE);

        long time = System.currentTimeMillis();
        BlurBitmap bitmap = null;
        if (unusedBitmaps.size() > 0) {
            bitmap = unusedBitmaps.remove(unusedBitmaps.size() - 1);
        }

        if (bitmap == null) {
            bitmap = new BlurBitmap();
            bitmap.topBitmap = Bitmap.createBitmap(bitmapW, bitmapH, Bitmap.Config.ARGB_8888);
            bitmap.topCanvas = new Canvas(bitmap.topBitmap);

            bitmap.bottomBitmap = Bitmap.createBitmap(bitmapW, bitmapH, Bitmap.Config.ARGB_8888);
            bitmap.bottomCanvas = new Canvas(bitmap.bottomBitmap);
        }
        bitmap.topBitmap.eraseColor(Color.TRANSPARENT);
        bitmap.bottomBitmap.eraseColor(Color.TRANSPARENT);

        BlurBitmap finalBitmap = bitmap;

        float sX = (float) finalBitmap.topBitmap.getWidth() / (float) lastW;
        float sY = (float) (finalBitmap.topBitmap.getHeight() - TOP_CLIP_OFFSET) / (float) lastH;
        finalBitmap.topCanvas.save();
        finalBitmap.pixelFixOffset = getScrollOffset() % (int) DOWN_SCALE;

        finalBitmap.topCanvas.clipRect(0, 10 * sY, finalBitmap.topBitmap.getWidth(), finalBitmap.topBitmap.getHeight());
        finalBitmap.topCanvas.scale(sX, sY);
        finalBitmap.topCanvas.translate(0, 10 * sY + finalBitmap.pixelFixOffset);

        finalBitmap.topScaleX = 1f / sX;
        finalBitmap.topScaleY = 1f / sY;

        drawList(finalBitmap.topCanvas, true);
        finalBitmap.topCanvas.restore();

        sX = (float) finalBitmap.bottomBitmap.getWidth() / (float) lastW;
        sY = (float) (finalBitmap.bottomBitmap.getHeight() - TOP_CLIP_OFFSET) / (float) lastH;
        finalBitmap.bottomOffset = getBottomOffset() - lastH;
        finalBitmap.bottomCanvas.save();
        finalBitmap.bottomCanvas.clipRect(0, 10 * sY, finalBitmap.bottomBitmap.getWidth(), finalBitmap.bottomBitmap.getHeight());
        finalBitmap.bottomCanvas.scale(sX, sY);
        finalBitmap.bottomCanvas.translate(0, 10 * sY - finalBitmap.bottomOffset + finalBitmap.pixelFixOffset);
        finalBitmap.bottomScaleX = 1f / sX;
        finalBitmap.bottomScaleY = 1f / sY;

        drawList(finalBitmap.bottomCanvas, false);
        finalBitmap.bottomCanvas.restore();


        times2 += System.currentTimeMillis() - time;
        count2++;
        if (count2 >= 20) {
            count2 = 0;
            times2 = 0;
        }

        if (blurQueue == null) {
            blurQueue = new DispatchQueue("BlurQueue");
        }
        blurBackgroundTask.radius = (int) (Math.max(6, Math.max(lastH, lastW) / 180) * 2.5f);
        blurBackgroundTask.finalBitmap = finalBitmap;
        blurQueue.postRunnable(blurBackgroundTask);
    }

    protected float getBottomOffset() {
        return getMeasuredHeight();
    }

    protected void drawList(Canvas blurCanvas, boolean top) {


    }

    protected int getScrollOffset() {
        return 0;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (blurIsRunning) {
            startBlur();
        }
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (needBlur && !blurIsRunning) {
            blurIsRunning = true;
            invalidateBlur = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        blurPaintTop.setShader(null);
        blurPaintTop2.setShader(null);
        blurPaintBottom.setShader(null);
        blurPaintBottom2.setShader(null);
        if (blurCrossfade != null) {
            blurCrossfade.cancel();
        }
        if (currentBitmap != null) {
            currentBitmap.recycle();
            currentBitmap = null;
        }
        for (int i = 0; i < unusedBitmaps.size(); i++) {
            if (unusedBitmaps.get(i) != null) {
                unusedBitmaps.get(i).recycle();
            }
        }
        unusedBitmaps.clear();
        blurIsRunning = false;
    }

    public void drawBlur(Canvas canvas, float y, Rect rectTmp, Paint blurScrimPaint, boolean top) {
        int blurAlpha = Color.alpha(Theme.getColor(Theme.key_chat_BlurAlpha));
        if (currentBitmap == null || !SharedConfig.chatBlurEnabled() || blurAlpha == 0) {
            canvas.drawRect(rectTmp, blurScrimPaint);
            return;
        }
        Paint blurPaint = top ? blurPaintTop : blurPaintBottom;
        Paint blurPaint2 = top ? blurPaintTop2 : blurPaintBottom2;

        if (blurPaint.getShader() != null) {
            matrix.reset();
            matrix2.reset();
            if (!top) {
                matrix.setTranslate(0, -y + currentBitmap.bottomOffset - currentBitmap.pixelFixOffset - TOP_CLIP_OFFSET);
                matrix.preScale(currentBitmap.bottomScaleX, currentBitmap.bottomScaleY);

                if (prevBitmap != null) {
                    matrix2.setTranslate(0, -y + prevBitmap.bottomOffset - prevBitmap.pixelFixOffset - TOP_CLIP_OFFSET);
                    matrix2.preScale(prevBitmap.bottomScaleX, prevBitmap.bottomScaleY);
                }
            } else {
                matrix.setTranslate(0, -y - currentBitmap.pixelFixOffset - TOP_CLIP_OFFSET);
                matrix.preScale(currentBitmap.topScaleX, currentBitmap.topScaleY);

                if (prevBitmap != null) {
                    matrix.setTranslate(0, -y - prevBitmap.pixelFixOffset - TOP_CLIP_OFFSET);
                    matrix.preScale(prevBitmap.topScaleX, prevBitmap.topScaleY);
                }
            }

            blurPaint.getShader().setLocalMatrix(matrix);
            if (blurPaint2.getShader() != null) {
                blurPaint2.getShader().setLocalMatrix(matrix);
            }
        }
        if (blurCrossfadeProgress != 1f && blurPaint2.getShader() != null) {
            canvas.drawRect(rectTmp, blurScrimPaint);
            canvas.drawRect(rectTmp, blurPaint2);
            canvas.saveLayerAlpha(rectTmp.left, rectTmp.top, rectTmp.right, rectTmp.bottom, (int) (blurCrossfadeProgress * 255), Canvas.ALL_SAVE_FLAG);
            canvas.drawRect(rectTmp, blurScrimPaint);
            canvas.drawRect(rectTmp, blurPaint);
            canvas.restore();
        } else {
            canvas.drawRect(rectTmp, blurScrimPaint);
            canvas.drawRect(rectTmp, blurPaint);
        }

        blurScrimPaint.setAlpha(blurAlpha);
        canvas.drawRect(rectTmp, blurScrimPaint);
    }

    public interface SizeNotifierFrameLayoutDelegate {
        void onSizeChanged(int keyboardHeight, boolean isWidthGreater);
    }

    private static class BlurBitmap {
        int pixelFixOffset;
        Canvas topCanvas;
        Bitmap topBitmap;
        float topScaleX, topScaleY;
        float bottomScaleX, bottomScaleY;
        float bottomOffset;

        Canvas bottomCanvas;
        Bitmap bottomBitmap;

        public void recycle() {
            topBitmap.recycle();
            bottomBitmap.recycle();
        }
    }

    private class BlurBackgroundTask implements Runnable {

        int radius;
        BlurBitmap finalBitmap;

        @Override
        public void run() {
            long time = System.currentTimeMillis();
            Utilities.stackBlurBitmap(finalBitmap.topBitmap, radius);
            Utilities.stackBlurBitmap(finalBitmap.bottomBitmap, radius);
            times += System.currentTimeMillis() - time;
            count++;
            if (count > 1000) {
                FileLog.d("chat blur generating average time" + (times / (float) count));
                count = 0;
                times = 0;
            }

            AndroidUtilities.runOnUIThread(() -> {
                prevBitmap = currentBitmap;
                BlurBitmap oldBitmap = currentBitmap;
                blurPaintTop2.setShader(blurPaintTop.getShader());
                blurPaintBottom2.setShader(blurPaintBottom.getShader());

                BitmapShader bitmapShader = new BitmapShader(finalBitmap.topBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                blurPaintTop.setShader(bitmapShader);

                bitmapShader = new BitmapShader(finalBitmap.bottomBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                blurPaintBottom.setShader(bitmapShader);

                blurCrossfadeProgress = 0;
                if (blurCrossfade != null) {
                    blurCrossfade.cancel();
                }
                blurCrossfade = ValueAnimator.ofFloat(0, 1f);
                blurCrossfade.addUpdateListener(valueAnimator -> {
                    blurCrossfadeProgress = (float) valueAnimator.getAnimatedValue();
                    for (int i = 0; i < blurBehindViews.size(); i++) {
                        blurBehindViews.get(i).invalidate();
                    }
                });
                blurCrossfade.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        blurCrossfadeProgress = 1f;
                        unusedBitmaps.add(oldBitmap);
                        super.onAnimationEnd(animation);
                    }
                });
                blurCrossfade.setDuration(50);
                blurCrossfade.start();
                for (int i = 0; i < blurBehindViews.size(); i++) {
                    blurBehindViews.get(i).invalidate();
                }
                currentBitmap = finalBitmap;

                AndroidUtilities.runOnUIThread(() -> {
                    blurGeneratingTuskIsRunning = false;
                    startBlur();
                }, 32);
            });
        }
    }

}
