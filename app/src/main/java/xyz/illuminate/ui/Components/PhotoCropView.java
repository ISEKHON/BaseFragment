package xyz.illuminate.ui.Components;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.Property;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.MediaController;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Components.Crop.CropRotationWheel;
import xyz.illuminate.ui.Components.Crop.CropTransform;
import xyz.illuminate.ui.Components.Crop.CropView;

public class PhotoCropView extends FrameLayout {

    private PhotoCropViewDelegate delegate;
    private final CropView cropView;
    private final CropRotationWheel wheelView;
    private final boolean inBubbleMode;
    private boolean thumbImageVisible;
    private final boolean thumbImageVisibleOverride = true;
    private float thumbImageVisibleProgress;
    public final Property<PhotoCropView, Float> PROGRESS_VALUE = new AnimationProperties.FloatProperty<PhotoCropView>("thumbImageVisibleProgress") {
        @Override
        public void setValue(PhotoCropView object, float value) {
            thumbImageVisibleProgress = value;
            object.invalidate();
        }

        @Override
        public Float get(PhotoCropView object) {
            return thumbImageVisibleProgress;
        }
    };
    private float thumbAnimationProgress = 1.0f;
    public final Property<PhotoCropView, Float> ANIMATION_VALUE = new AnimationProperties.FloatProperty<PhotoCropView>("thumbAnimationProgress") {
        @Override
        public void setValue(PhotoCropView object, float value) {
            thumbAnimationProgress = value;
            object.invalidate();
        }

        @Override
        public Float get(PhotoCropView object) {
            return thumbAnimationProgress;
        }
    };
    private AnimatorSet thumbAnimation;
    private AnimatorSet thumbOverrideAnimation;
    private final float flashAlpha = 0.0f;
    private final Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PhotoCropView(Context context) {
        super(context);


        inBubbleMode = false;//context instanceof BubbleActivity;

        cropView = new CropView(context);
        cropView.setListener(new CropView.CropViewListener() {
            @Override
            public void onChange(boolean reset) {
                if (delegate != null) {
                    delegate.onChange(reset);
                }
            }

            @Override
            public void onUpdate() {
                if (delegate != null) {
                    delegate.onUpdate();
                }
            }

            @Override
            public void onAspectLock(boolean enabled) {
                wheelView.setAspectLock(enabled);
            }

            @Override
            public void onTapUp() {
                if (delegate != null) {
                    delegate.onTapUp();
                }
            }
        });
        cropView.setBottomPadding(AndroidUtilities.dp(64));
        addView(cropView);

        wheelView = new CropRotationWheel(context);
        wheelView.setListener(new CropRotationWheel.RotationWheelListener() {
            @Override
            public void onStart() {
                cropView.onRotationBegan();
            }

            @Override
            public void onChange(float angle) {
                cropView.setRotation(angle);
                if (delegate != null) {
                    delegate.onChange(false);
                }
            }

            @Override
            public void onEnd(float angle) {
                cropView.onRotationEnded();
            }

            @Override
            public void aspectRatioPressed() {
                cropView.showAspectRatioDialog();
            }

            @Override
            public boolean rotate90Pressed() {
                return rotate();
            }

            @Override
            public boolean mirror() {
                return PhotoCropView.this.mirror();
            }
        });
        addView(wheelView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER | Gravity.BOTTOM, 0, 0, 0, 0));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (thumbImageVisibleOverride && thumbImageVisible) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                delegate.onVideoThumbClick();
            }
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (thumbImageVisibleOverride && thumbImageVisible) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                delegate.onVideoThumbClick();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (thumbImageVisible && child == cropView) {
            RectF rect = cropView.getActualRect();
            int targetSize = AndroidUtilities.dp(32);
            int targetX = delegate.getVideoThumbX() - targetSize / 2 + AndroidUtilities.dp(2);
            int targetY = getMeasuredHeight() - AndroidUtilities.dp(156);
            float x = rect.left + (targetX - rect.left) * thumbAnimationProgress;
            float y = rect.top + (targetY - rect.top) * thumbAnimationProgress;
            float size = rect.width() + (targetSize - rect.width()) * thumbAnimationProgress;

            if (flashAlpha > 0.0f) {
                circlePaint.setColor(0xffffffff);
                circlePaint.setAlpha((int) (flashAlpha * 255));
                canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, circlePaint);
            }

            circlePaint.setColor(getThemedColor(Theme.key_dialogFloatingButton));
            circlePaint.setAlpha(Math.min(255, (int) (255 * thumbAnimationProgress * thumbImageVisibleProgress)));
            canvas.drawCircle(targetX + targetSize / 2, targetY + targetSize + AndroidUtilities.dp(8), AndroidUtilities.dp(3), circlePaint);
        }
        return result;
    }

    public boolean rotate() {
        if (wheelView != null) {
            wheelView.reset(false);
        }
        return cropView.rotate90Degrees();
    }

    public boolean mirror() {
        return cropView.mirror();
    }

    public void setBitmap(Bitmap bitmap, int rotation, boolean freeform, boolean update, CropTransform cropTransform, MediaController.CropState state) {
        requestLayout();

        thumbImageVisible = false;
        cropView.setBitmap(bitmap, rotation, freeform, update, cropTransform, state);
        wheelView.setFreeform(freeform);
        wheelView.reset(true);
        if (state != null) {
            wheelView.setRotation(state.cropRotate, false);
            wheelView.setRotated(state.transformRotation != 0);
            wheelView.setMirrored(state.mirrored);
        } else {
            wheelView.setRotated(false);
            wheelView.setMirrored(false);
        }
        wheelView.setVisibility(freeform ? VISIBLE : INVISIBLE);
    }

    public void cancelThumbAnimation() {
        if (thumbAnimation != null) {
            thumbAnimation.cancel();
            thumbAnimation = null;
            thumbImageVisible = false;
        }
    }

    public boolean isReady() {
        return cropView.isReady();
    }

    public void reset() {
        wheelView.reset(true);
        cropView.reset();
    }

    public void onAppear() {
        cropView.willShow();
    }

    public void setAspectRatio(float ratio) {
        cropView.setAspectRatio(ratio);
    }

    public void setFreeform(boolean freeform) {
        cropView.setFreeform(freeform);
    }

    public void onAppeared() {
        cropView.show();
    }

    public void onDisappear() {
        cropView.hide();
    }

    public void onShow() {
        cropView.onShow();
    }

    public void onHide() {
        cropView.onHide();
    }

    public float getRectX() {
        return cropView.getCropLeft() - AndroidUtilities.dp(14);
    }

    public float getRectY() {
        return cropView.getCropTop() - AndroidUtilities.dp(14) - (Build.VERSION.SDK_INT >= 21 && !inBubbleMode ? AndroidUtilities.statusBarHeight : 0);
    }

    public float getRectSizeX() {
        return cropView.getCropWidth();
    }

    public float getRectSizeY() {
        return cropView.getCropHeight();
    }

    public void makeCrop(MediaController.MediaEditState editState) {
        cropView.makeCrop(editState);
    }

    public void setDelegate(PhotoCropViewDelegate photoCropViewDelegate) {
        delegate = photoCropViewDelegate;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        cropView.updateLayout();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        cropView.invalidate();
    }

    private int getThemedColor(String key) {
        return Theme.getColor(key);
    }

    public interface PhotoCropViewDelegate {
        void onChange(boolean reset);

        void onUpdate();

        void onTapUp();

        int getVideoThumbX();

        void onVideoThumbClick();
    }
}
