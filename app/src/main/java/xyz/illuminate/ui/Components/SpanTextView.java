package xyz.illuminate.ui.Components;

import android.content.Context;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

import xyz.illuminate.app.FileLog;
import xyz.illuminate.app.MediaDataController;
import xyz.illuminate.server.TLRPC;

public class SpanTextView extends FontTextView {
    boolean disableWorkAround;

    public SpanTextView(Context context) {
        super(context);
        init();
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

    public boolean onTouchEvent(MotionEvent event) {
        Layout layout;
        if (!this.disableWorkAround && (layout = getLayout()) != null) {
            int offset = layout.getOffsetForHorizontal(layout.getLineForVertical((int) event.getY()), event.getX());
            if (getText() != null && (getText() instanceof Spanned)) {
                if (((Spanned) getText()).getSpans(offset, offset, ClickableSpan.class).length > 0) {
                    return SpanTextView.super.onTouchEvent(event);
                }
                return false;
            }
        }
        return SpanTextView.super.onTouchEvent(event);
    }

    public void setDisableWorkaround(boolean val) {
        this.disableWorkAround = val;
    }

    public void setSpanText(String text) {
        ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance().getEntities(new String[]{text}, true);
        MediaDataController.sortEntities(entities);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
        Object[] spansToRemove = stringBuilder.getSpans(0, stringBuilder.length(), Object.class);
        if (spansToRemove != null && spansToRemove.length > 0) {
            for (int a = 0; a < spansToRemove.length; a++) {
                stringBuilder.removeSpan(spansToRemove[a]);
            }
        }
        if (entities != null) {
            try {
                for (int a = 0; a < entities.size(); a++) {
                    TLRPC.MessageEntity entity = entities.get(a);
                    if (entity.offset + entity.length > stringBuilder.length()) {
                        continue;
                    }
                    if (entity instanceof TLRPC.TL_messageEntityMentionName) {
                        if (entity.offset + entity.length < stringBuilder.length() && stringBuilder.charAt(entity.offset + entity.length) == ' ') {
                            entity.length++;
                        }
                        stringBuilder.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_messageEntityMentionName) entity).user_id, 3), entity.offset, entity.offset + entity.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (entity instanceof TLRPC.TL_messageEntityCode || entity instanceof TLRPC.TL_messageEntityPre) {
                        stringBuilder.delete(entity.offset, entity.offset + 2);
                        stringBuilder.delete(entity.offset + entity.length, entity.offset + entity.length + 3);
                        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                        run.flags |= TextStyleSpan.FLAG_STYLE_MONO;
                        stringBuilder.setSpan(new TextStyleSpan(run), entity.offset, entity.offset + entity.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (entity instanceof TLRPC.TL_messageEntityBold) {
                        stringBuilder.delete(entity.offset, entity.offset + 2);
                        stringBuilder.delete(entity.offset + entity.length, entity.offset + entity.length + 2);
                        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                        run.flags |= TextStyleSpan.FLAG_STYLE_BOLD;
                        stringBuilder.setSpan(new TextStyleSpan(run), entity.offset, entity.offset + entity.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (entity instanceof TLRPC.TL_messageEntityItalic) {
                        stringBuilder.delete(entity.offset, entity.offset + 2);
                        stringBuilder.delete(entity.offset + entity.length, entity.offset + entity.length + 2);
                        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                        run.flags |= TextStyleSpan.FLAG_STYLE_ITALIC;
                        stringBuilder.setSpan(new TextStyleSpan(run), entity.offset, entity.offset + entity.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (entity instanceof TLRPC.TL_messageEntityStrike) {
                        stringBuilder.delete(entity.offset, entity.offset + 2);
                        stringBuilder.delete(entity.offset + entity.length, entity.offset + entity.length + 2);
                        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                        run.flags |= TextStyleSpan.FLAG_STYLE_STRIKE;
                        stringBuilder.setSpan(new TextStyleSpan(run), entity.offset, entity.offset + entity.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (entity instanceof TLRPC.TL_messageEntityUnderline) {
                        stringBuilder.delete(entity.offset, entity.offset + 2);
                        stringBuilder.delete(entity.offset + entity.length, entity.offset + entity.length + 2);
                        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                        run.flags |= TextStyleSpan.FLAG_STYLE_UNDERLINE;
                        stringBuilder.setSpan(new TextStyleSpan(run), entity.offset, entity.offset + entity.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (entity instanceof TLRPC.TL_messageEntityTextUrl) {
                        stringBuilder.setSpan(new URLSpanReplacement(entity.url), entity.offset, entity.offset + entity.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (entity instanceof TLRPC.TL_messageEntitySpoiler) {
                        stringBuilder.delete(entity.offset, entity.offset + 2);
                        stringBuilder.delete(entity.offset + entity.length, entity.offset + entity.length + 2);
                        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                        run.flags |= TextStyleSpan.FLAG_STYLE_SPOILER;
                        stringBuilder.setSpan(new TextStyleSpan(run), entity.offset, entity.offset + entity.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        setText(stringBuilder);

    }

}
