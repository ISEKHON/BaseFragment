package xyz.illuminate.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import xyz.illuminate.app.MediaDataController;
import xyz.illuminate.server.TLRPC;
import xyz.illuminate.ui.Components.spoilers.SpoilerEffect;

public class SpanTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final String TAG = "SpanTextView";
    boolean disableWorkAround;
    private final List<SpoilerEffect> spoilers = new ArrayList<>();
    private final Stack<SpoilerEffect> spoilersPool = new Stack<>();
    private final Path path = new Path();
    private boolean isSpoilersRevealed;

    public SpanTextView(Context context) {
        super(context);
        init();
        // Enable hardware acceleration for proper spoiler effect rendering
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    public SpanTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpanTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Handle spoiler effects first
        if (event.getAction() == MotionEvent.ACTION_DOWN && !spoilers.isEmpty()) {
            float x = event.getX();
            float y = event.getY();
            
            for (SpoilerEffect eff : spoilers) {
                if (eff.getBounds().contains((int)x, (int)y)) {
                    eff.setOnRippleEndCallback(() -> {
                        setSpoilersRevealed(true);
                    });

                    float rad = (float) Math.sqrt(Math.pow(getWidth(), 2) + Math.pow(getHeight(), 2));
                    for (SpoilerEffect e : spoilers) {
                        e.startRipple(x, y, rad);
                    }
                    return true;
                }
            }
        }

        // Handle clickable spans
        Layout layout;
        if (!this.disableWorkAround && (layout = getLayout()) != null) {
            int offset = layout.getOffsetForHorizontal(layout.getLineForVertical((int) event.getY()), event.getX());
            if (getText() != null && (getText() instanceof Spanned)) {
                if (((Spanned) getText()).getSpans(offset, offset, ClickableSpan.class).length > 0) {
                    return super.onTouchEvent(event);
                }
                return false;
            }
        }
        return super.onTouchEvent(event);
    }

    public void setDisableWorkaround(boolean val) {
        this.disableWorkAround = val;
    }

    public void setSpanText(String text) {
        if (text == null) {
            setText("");
            return;
        }

        Log.d(TAG, "Setting text: " + text);
        CharSequence[] message = new CharSequence[]{text};
        ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance().getEntities(message, true);
        Log.d(TAG, "Found " + (entities != null ? entities.size() : 0) + " entities");

        if (entities != null && !entities.isEmpty()) {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(message[0]);
            
            // Remove existing spans
            Object[] spans = stringBuilder.getSpans(0, stringBuilder.length(), Object.class);
            if (spans != null) {
                for (Object span : spans) {
                    stringBuilder.removeSpan(span);
                }
            }

            // Sort entities by offset
            Collections.sort(entities, (e1, e2) -> Integer.compare(e1.offset, e2.offset));
            
            for (int a = 0; a < entities.size(); a++) {
                TLRPC.MessageEntity entity = entities.get(a);
                if (entity.offset + entity.length > stringBuilder.length()) {
                    Log.e(TAG, "Entity out of bounds: offset=" + entity.offset + 
                        " length=" + entity.length + " text length=" + stringBuilder.length());
                    continue;
                }

                try {
                    TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();

                    if (entity instanceof TLRPC.TL_messageEntityBold) {
                        // Check for bold-italic combination
                        boolean hasBoldItalic = false;
                        for (TLRPC.MessageEntity nextEntity : entities) {
                            if (nextEntity instanceof TLRPC.TL_messageEntityItalic &&
                                nextEntity.offset == entity.offset &&
                                nextEntity.length == entity.length) {
                                hasBoldItalic = true;
                                break;
                            }
                        }
                        
                        if (hasBoldItalic) {
                            run.flags |= TextStyleSpan.FLAG_STYLE_BOLD_ITALIC;
                            // Skip the next italic entity
                            a++;
                        } else {
                            run.flags |= TextStyleSpan.FLAG_STYLE_BOLD;
                        }
                    } else if (entity instanceof TLRPC.TL_messageEntityItalic) {
                        // Skip if already handled as part of bold-italic
                        boolean wasHandled = false;
                        for (int i = 0; i < a; i++) {
                            TLRPC.MessageEntity prevEntity = entities.get(i);
                            if (prevEntity instanceof TLRPC.TL_messageEntityBold &&
                                prevEntity.offset == entity.offset &&
                                prevEntity.length == entity.length) {
                                wasHandled = true;
                                break;
                            }
                        }
                        
                        if (!wasHandled) {
                            run.flags |= TextStyleSpan.FLAG_STYLE_ITALIC;
                        } else {
                            continue;
                        }
                    } else if (entity instanceof TLRPC.TL_messageEntityStrike) {
                        run.flags |= TextStyleSpan.FLAG_STYLE_STRIKE;
                    } else if (entity instanceof TLRPC.TL_messageEntityUnderline) {
                        run.flags |= TextStyleSpan.FLAG_STYLE_UNDERLINE;
                    } else if (entity instanceof TLRPC.TL_messageEntitySpoiler) {
                        run.flags |= TextStyleSpan.FLAG_STYLE_SPOILER;
                    } else if (entity instanceof TLRPC.TL_messageEntityCode || 
                             entity instanceof TLRPC.TL_messageEntityPre) {
                        run.flags |= TextStyleSpan.FLAG_STYLE_MONO;
                        
                        // Add background for code blocks
                        if (entity instanceof TLRPC.TL_messageEntityPre) {
                            // Add padding for code blocks
                            if (entity.offset > 0) {
                                stringBuilder.insert(entity.offset, "\n");
                                entity.offset++;
                            }
                            if (entity.offset + entity.length < stringBuilder.length()) {
                                stringBuilder.insert(entity.offset + entity.length, "\n");
                            }
                        }
                        
                        // Apply background color
                        stringBuilder.setSpan(new android.text.style.BackgroundColorSpan(0xFFE3E3E3),
                            entity.offset, entity.offset + entity.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    if (run.flags != 0) {
                        stringBuilder.setSpan(new TextStyleSpan(run),
                            entity.offset, entity.offset + entity.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        Log.d(TAG, "Applied span with flags: " + run.flags + 
                            " at offset " + entity.offset + " length " + entity.length);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error applying span for entity: " + 
                        entity.getClass().getSimpleName(), e);
                }
            }
            setText(stringBuilder);
            Log.d(TAG, "Final text set: " + stringBuilder);
            invalidateSpoilers();
        } else {
            setText(text);
        }
    }

    private void setSpoilersRevealed(boolean revealed) {
        if (isSpoilersRevealed == revealed) return;
        
        isSpoilersRevealed = revealed;
        Spannable text = (Spannable) getText();
        if (text != null) {
            TextStyleSpan[] spans = text.getSpans(0, text.length(), TextStyleSpan.class);
            boolean textChanged = false;
            
            for (TextStyleSpan span : spans) {
                if (span.isSpoiler()) {
                    span.setSpoilerRevealed(revealed);
                    textChanged = true;
                }
            }
            
            if (textChanged) {
                // Force refresh the text to update spoiler states
                setText(text);
                invalidateSpoilers();
            }
        }
    }

    private void invalidateSpoilers() {
        spoilersPool.addAll(spoilers);
        spoilers.clear();
        
        if (getLayout() != null && getText() instanceof Spanned) {
            SpoilerEffect.addSpoilers(this, getLayout(), spoilersPool, spoilers);
            
            // Update spoiler states
            if (isSpoilersRevealed) {
                Spannable text = (Spannable) getText();
                TextStyleSpan[] spans = text.getSpans(0, text.length(), TextStyleSpan.class);
                for (TextStyleSpan span : spans) {
                    if (span.isSpoiler()) {
                        span.setSpoilerRevealed(true);
                    }
                }
            }
        }
        
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateSpoilers();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (spoilers.isEmpty() || isSpoilersRevealed) {
            super.onDraw(canvas);
            return;
        }

        canvas.save();
        path.rewind();
        
        for (SpoilerEffect eff : spoilers) {
            Rect bounds = eff.getBounds();
            path.addRect(bounds.left, bounds.top, bounds.right, bounds.bottom, Path.Direction.CW);
        }
        
        canvas.clipPath(path, Region.Op.DIFFERENCE);
        super.onDraw(canvas);
        canvas.restore();

        canvas.save();
        canvas.clipPath(path);
        path.rewind();
        
        if (!spoilers.isEmpty()) {
            spoilers.get(0).getRipplePath(path);
        }
        
        canvas.clipPath(path);
        super.onDraw(canvas);
        canvas.restore();

        for (SpoilerEffect eff : spoilers) {
            eff.draw(canvas);
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!isSpoilersRevealed) {
            super.setText(text, type);
        } else {
            // If spoilers are revealed, make sure to keep them revealed
            if (text instanceof Spannable) {
                Spannable spannable = (Spannable) text;
                TextStyleSpan[] spans = spannable.getSpans(0, spannable.length(), TextStyleSpan.class);
                for (TextStyleSpan span : spans) {
                    if (span.isSpoiler()) {
                        span.setSpoilerRevealed(true);
                    }
                }
            }
            super.setText(text, type);
        }
    }
}
