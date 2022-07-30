package xyz.illuminate.app;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.CharacterStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.illuminate.server.TLRPC;
import xyz.illuminate.ui.Components.TextStyleSpan;
import xyz.illuminate.ui.Components.URLSpanReplacement;
import xyz.illuminate.ui.Components.URLSpanUserMention;

@SuppressWarnings("unchecked")
public class MediaDataController extends BaseController {
    private static final Pattern BOLD_PATTERN = Pattern.compile("\\*\\*(.+?)\\*\\*");
    private static final Pattern ITALIC_PATTERN = Pattern.compile("''(.+?)''");
    private static final Pattern SPOILER_PATTERN = Pattern.compile("\\|\\|(.+?)\\|\\|");
    private static final Pattern STRIKE_PATTERN = Pattern.compile("--(.+?)--");
    private static final Pattern UNDERLINE_PATTERN = Pattern.compile("__(.+?)__");

    private static volatile MediaDataController Instance = new MediaDataController();
    private static final Comparator<TLRPC.MessageEntity> entityComparator = (entity1, entity2) -> {
        if (entity1.offset > entity2.offset) {
            return 1;
        } else if (entity1.offset < entity2.offset) {
            return -1;
        }
        return 0;
    };

    public MediaDataController() {
        super();
    }

    public static MediaDataController getInstance() {
        MediaDataController localInstance = Instance;
        if (localInstance == null) {
            synchronized (MediaDataController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new MediaDataController();
                }
            }
        }
        return localInstance;
    }

    public static void sortEntities(ArrayList<TLRPC.MessageEntity> entities) {
        Collections.sort(entities, entityComparator);
    }

    private static boolean checkInclusion(int index, List<TLRPC.MessageEntity> entities, boolean end) {
        if (entities == null || entities.isEmpty()) {
            return false;
        }
        int count = entities.size();
        for (int a = 0; a < count; a++) {
            TLRPC.MessageEntity entity = entities.get(a);
            if ((end ? entity.offset < index : entity.offset <= index) && entity.offset + entity.length > index) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkIntersection(int start, int end, List<TLRPC.MessageEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return false;
        }
        int count = entities.size();
        for (int a = 0; a < count; a++) {
            TLRPC.MessageEntity entity = entities.get(a);
            if (entity.offset > start && entity.offset + entity.length <= end) {
                return true;
            }
        }
        return false;
    }

    private static CharacterStyle createNewSpan(CharacterStyle baseSpan, TextStyleSpan.TextStyleRun textStyleRun, TextStyleSpan.TextStyleRun newStyleRun, boolean allowIntersection) {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun(textStyleRun);
        if (newStyleRun != null) {
            if (allowIntersection) {
                run.merge(newStyleRun);
            } else {
                run.replace(newStyleRun);
            }
        }
        if (baseSpan instanceof TextStyleSpan) {
            return new TextStyleSpan(run);
        } else if (baseSpan instanceof URLSpanReplacement) {
            URLSpanReplacement span = (URLSpanReplacement) baseSpan;
            return new URLSpanReplacement(span.getURL(), run);
        }
        return null;
    }

    public static void addStyleToText(TextStyleSpan span, int start, int end, Spannable editable, boolean allowIntersection) {
        try {
            CharacterStyle[] spans = editable.getSpans(start, end, CharacterStyle.class);
            if (spans != null && spans.length > 0) {
                for (int a = 0; a < spans.length; a++) {
                    CharacterStyle oldSpan = spans[a];
                    TextStyleSpan.TextStyleRun textStyleRun;
                    TextStyleSpan.TextStyleRun newStyleRun = span != null ? span.getTextStyleRun() : new TextStyleSpan.TextStyleRun();
                    if (oldSpan instanceof TextStyleSpan) {
                        TextStyleSpan textStyleSpan = (TextStyleSpan) oldSpan;
                        textStyleRun = textStyleSpan.getTextStyleRun();
                    } else if (oldSpan instanceof URLSpanReplacement) {
                        URLSpanReplacement urlSpanReplacement = (URLSpanReplacement) oldSpan;
                        textStyleRun = urlSpanReplacement.getTextStyleRun();
                        if (textStyleRun == null) {
                            textStyleRun = new TextStyleSpan.TextStyleRun();
                        }
                    } else {
                        continue;
                    }
                    if (textStyleRun == null) {
                        continue;
                    }
                    int spanStart = editable.getSpanStart(oldSpan);
                    int spanEnd = editable.getSpanEnd(oldSpan);
                    editable.removeSpan(oldSpan);
                    if (spanStart > start && end > spanEnd) {
                        editable.setSpan(createNewSpan(oldSpan, textStyleRun, newStyleRun, allowIntersection), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (span != null) {
                            editable.setSpan(new TextStyleSpan(new TextStyleSpan.TextStyleRun(newStyleRun)), spanEnd, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        end = spanStart;
                    } else {
                        int startTemp = start;
                        if (spanStart <= start) {
                            if (spanStart != start) {
                                editable.setSpan(createNewSpan(oldSpan, textStyleRun, null, allowIntersection), spanStart, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            if (spanEnd > start) {
                                if (span != null) {
                                    editable.setSpan(createNewSpan(oldSpan, textStyleRun, newStyleRun, allowIntersection), start, Math.min(spanEnd, end), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                                start = spanEnd;
                            }
                        }
                        if (spanEnd >= end) {
                            if (spanEnd != end) {
                                editable.setSpan(createNewSpan(oldSpan, textStyleRun, null, allowIntersection), end, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            if (end > spanStart && spanEnd <= startTemp) {
                                if (span != null) {
                                    editable.setSpan(createNewSpan(oldSpan, textStyleRun, newStyleRun, allowIntersection), spanStart, Math.min(spanEnd, end), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                                end = spanStart;
                            }
                        }
                    }
                }
            }
            if (span != null && start < end && start < editable.length()) {
                editable.setSpan(span, start, Math.min(editable.length(), end), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void addTextStyleRuns(ArrayList<TLRPC.MessageEntity> entities, CharSequence messageText, Spannable text) {
        addTextStyleRuns(entities, messageText, text, -1);
    }

    public static void addTextStyleRuns(ArrayList<TLRPC.MessageEntity> entities, CharSequence messageText, Spannable text, int allowedFlags) {
        for (TextStyleSpan prevSpan : text.getSpans(0, text.length(), TextStyleSpan.class))
            text.removeSpan(prevSpan);
        for (TextStyleSpan.TextStyleRun run : MediaDataController.getTextStyleRuns(entities, messageText, allowedFlags)) {
            MediaDataController.addStyleToText(new TextStyleSpan(run), run.start, run.end, text, true);
        }
    }

    public static ArrayList<TextStyleSpan.TextStyleRun> getTextStyleRuns(ArrayList<TLRPC.MessageEntity> entities, CharSequence text, int allowedFlags) {
        ArrayList<TextStyleSpan.TextStyleRun> runs = new ArrayList<>();
        ArrayList<TLRPC.MessageEntity> entitiesCopy = new ArrayList<>(entities);

        Collections.sort(entitiesCopy, (o1, o2) -> {
            if (o1.offset > o2.offset) {
                return 1;
            } else if (o1.offset < o2.offset) {
                return -1;
            }
            return 0;
        });
        for (int a = 0, N = entitiesCopy.size(); a < N; a++) {
            TLRPC.MessageEntity entity = entitiesCopy.get(a);
            if (entity.length <= 0 || entity.offset < 0 || entity.offset >= text.length()) {
                continue;
            } else if (entity.offset + entity.length > text.length()) {
                entity.length = text.length() - entity.offset;
            }

            TextStyleSpan.TextStyleRun newRun = new TextStyleSpan.TextStyleRun();
            newRun.start = entity.offset;
            newRun.end = newRun.start + entity.length;
            TLRPC.MessageEntity urlEntity = null;
            if (entity instanceof TLRPC.TL_messageEntitySpoiler) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_SPOILER;
            } else if (entity instanceof TLRPC.TL_messageEntityStrike) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_STRIKE;
            } else if (entity instanceof TLRPC.TL_messageEntityUnderline) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_UNDERLINE;
            } else if (entity instanceof TLRPC.TL_messageEntityBlockquote) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_QUOTE;
            } else if (entity instanceof TLRPC.TL_messageEntityBold) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_BOLD;
            } else if (entity instanceof TLRPC.TL_messageEntityItalic) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_ITALIC;
            } else if (entity instanceof TLRPC.TL_messageEntityCode || entity instanceof TLRPC.TL_messageEntityPre) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_MONO;
            } else if (entity instanceof TLRPC.TL_messageEntityMentionName) {
                newRun.flags = TextStyleSpan.FLAG_STYLE_MENTION;
                newRun.urlEntity = entity;
            } else {
                newRun.flags = TextStyleSpan.FLAG_STYLE_URL;
                newRun.urlEntity = entity;
            }

            newRun.flags &= allowedFlags;

            for (int b = 0, N2 = runs.size(); b < N2; b++) {
                TextStyleSpan.TextStyleRun run = runs.get(b);

                if (newRun.start > run.start) {
                    if (newRun.start >= run.end) {
                        continue;
                    }

                    if (newRun.end < run.end) {
                        TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(newRun);
                        r.merge(run);
                        b++;
                        N2++;
                        runs.add(b, r);

                        r = new TextStyleSpan.TextStyleRun(run);
                        r.start = newRun.end;
                        b++;
                        N2++;
                        runs.add(b, r);
                    } else {
                        TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(newRun);
                        r.merge(run);
                        r.end = run.end;
                        b++;
                        N2++;
                        runs.add(b, r);
                    }

                    int temp = newRun.start;
                    newRun.start = run.end;
                    run.end = temp;
                } else {
                    if (run.start >= newRun.end) {
                        continue;
                    }
                    int temp = run.start;
                    if (newRun.end == run.end) {
                        run.merge(newRun);
                    } else if (newRun.end < run.end) {
                        TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(run);
                        r.merge(newRun);
                        r.end = newRun.end;
                        b++;
                        N2++;
                        runs.add(b, r);

                        run.start = newRun.end;
                    } else {
                        TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(newRun);
                        r.start = run.end;
                        b++;
                        N2++;
                        runs.add(b, r);

                        run.merge(newRun);
                    }
                    newRun.end = temp;
                }
            }
            if (newRun.start < newRun.end) {
                runs.add(newRun);
            }
        }
        return runs;
    }

    public CharSequence substring(CharSequence source, int start, int end) {
        if (source instanceof SpannableStringBuilder) {
            return source.subSequence(start, end);
        } else if (source instanceof SpannedString) {
            return source.subSequence(start, end);
        } else {
            return TextUtils.substring(source, start, end);
        }
    }

    public void addStyle(int flags, int spanStart, int spanEnd, ArrayList<TLRPC.MessageEntity> entities) {
        if ((flags & TextStyleSpan.FLAG_STYLE_SPOILER) != 0)
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntitySpoiler(), spanStart, spanEnd));
        if ((flags & TextStyleSpan.FLAG_STYLE_BOLD) != 0)
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntityBold(), spanStart, spanEnd));
        if ((flags & TextStyleSpan.FLAG_STYLE_ITALIC) != 0)
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntityItalic(), spanStart, spanEnd));
        if ((flags & TextStyleSpan.FLAG_STYLE_MONO) != 0)
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntityCode(), spanStart, spanEnd));
        if ((flags & TextStyleSpan.FLAG_STYLE_STRIKE) != 0)
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntityStrike(), spanStart, spanEnd));
        if ((flags & TextStyleSpan.FLAG_STYLE_UNDERLINE) != 0)
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntityUnderline(), spanStart, spanEnd));
        if ((flags & TextStyleSpan.FLAG_STYLE_QUOTE) != 0)
            entities.add(setEntityStartEnd(new TLRPC.TL_messageEntityBlockquote(), spanStart, spanEnd));
    }

    private TLRPC.MessageEntity setEntityStartEnd(TLRPC.MessageEntity entity, int spanStart, int spanEnd) {
        entity.offset = spanStart;
        entity.length = spanEnd - spanStart;
        return entity;
    }

    public ArrayList<TLRPC.MessageEntity> getEntities(CharSequence[] message, boolean allowStrike) {
        if (message == null || message[0] == null) {
            return null;
        }
        ArrayList<TLRPC.MessageEntity> entities = null;
        int index;
        int start = -1;
        int lastIndex = 0;
        boolean isPre = false;
        final String mono = "`";
        final String pre = "```";
        while ((index = TextUtils.indexOf(message[0], !isPre ? mono : pre, lastIndex)) != -1) {
            if (start == -1) {
                isPre = message[0].length() - index > 2 && message[0].charAt(index + 1) == '`' && message[0].charAt(index + 2) == '`';
                start = index;
                lastIndex = index + (isPre ? 3 : 1);
            } else {
                if (entities == null) {
                    entities = new ArrayList<>();
                }
                for (int a = index + (isPre ? 3 : 1); a < message[0].length(); a++) {
                    if (message[0].charAt(a) == '`') {
                        index++;
                    } else {
                        break;
                    }
                }
                lastIndex = index + (isPre ? 3 : 1);
                if (isPre) {
                    int firstChar = start > 0 ? message[0].charAt(start - 1) : 0;
                    boolean replacedFirst = firstChar == ' ' || firstChar == '\n';
                    CharSequence startMessage = substring(message[0], 0, start - (replacedFirst ? 1 : 0));
                    CharSequence content = substring(message[0], start + 3, index);
                    firstChar = index + 3 < message[0].length() ? message[0].charAt(index + 3) : 0;
                    CharSequence endMessage = substring(message[0], index + 3 + (firstChar == ' ' || firstChar == '\n' ? 1 : 0), message[0].length());
                    if (startMessage.length() != 0) {
                        startMessage = AndroidUtilities.concat(startMessage, "\n");
                    } else {
                        replacedFirst = true;
                    }
                    if (endMessage.length() != 0) {
                        endMessage = AndroidUtilities.concat("\n", endMessage);
                    }
                    if (!TextUtils.isEmpty(content)) {
                        message[0] = AndroidUtilities.concat(startMessage, content, endMessage);
                        TLRPC.TL_messageEntityPre entity = new TLRPC.TL_messageEntityPre();
                        entity.offset = start + (replacedFirst ? 0 : 1);
                        entity.length = index - start - 3 + (replacedFirst ? 0 : 1);
                        entity.language = "";
                        entities.add(entity);
                        lastIndex -= 6;
                    }
                } else {
                    if (start + 1 != index) {
                        message[0] = AndroidUtilities.concat(substring(message[0], 0, start), substring(message[0], start + 1, index), substring(message[0], index + 1, message[0].length()));
                        TLRPC.TL_messageEntityCode entity = new TLRPC.TL_messageEntityCode();
                        entity.offset = start;
                        entity.length = index - start - 1;
                        entities.add(entity);
                        lastIndex -= 2;
                    }
                }
                start = -1;
                isPre = false;
            }
        }
        if (start != -1 && isPre) {
            message[0] = AndroidUtilities.concat(substring(message[0], 0, start), substring(message[0], start + 2, message[0].length()));
            if (entities == null) {
                entities = new ArrayList<>();
            }
            TLRPC.TL_messageEntityCode entity = new TLRPC.TL_messageEntityCode();
            entity.offset = start;
            entity.length = 1;
            entities.add(entity);
        }

        if (message[0] instanceof Spanned) {
            Spanned spannable = (Spanned) message[0];
            TextStyleSpan[] spans = spannable.getSpans(0, message[0].length(), TextStyleSpan.class);
            if (spans != null && spans.length > 0) {
                for (int a = 0; a < spans.length; a++) {
                    TextStyleSpan span = spans[a];
                    int spanStart = spannable.getSpanStart(span);
                    int spanEnd = spannable.getSpanEnd(span);
                    if (checkInclusion(spanStart, entities, false) || checkInclusion(spanEnd, entities, true) || checkIntersection(spanStart, spanEnd, entities)) {
                        continue;
                    }
                    if (entities == null) {
                        entities = new ArrayList<>();
                    }
                    addStyle(span.getStyleFlags(), spanStart, spanEnd, entities);
                }
            }

            URLSpanUserMention[] spansMentions = spannable.getSpans(0, message[0].length(), URLSpanUserMention.class);
            if (spansMentions != null && spansMentions.length > 0) {
                if (entities == null) {
                    entities = new ArrayList<>();
                }
            }

            URLSpanReplacement[] spansUrlReplacement = spannable.getSpans(0, message[0].length(), URLSpanReplacement.class);
            if (spansUrlReplacement != null && spansUrlReplacement.length > 0) {
                if (entities == null) {
                    entities = new ArrayList<>();
                }
                for (int b = 0; b < spansUrlReplacement.length; b++) {
                    TLRPC.TL_messageEntityTextUrl entity = new TLRPC.TL_messageEntityTextUrl();
                    entity.offset = spannable.getSpanStart(spansUrlReplacement[b]);
                    entity.length = Math.min(spannable.getSpanEnd(spansUrlReplacement[b]), message[0].length()) - entity.offset;
                    entity.url = spansUrlReplacement[b].getURL();
                    entities.add(entity);
                    TextStyleSpan.TextStyleRun style = spansUrlReplacement[b].getTextStyleRun();
                    if (style != null) {
                        addStyle(style.flags, entity.offset, entity.offset + entity.length, entities);
                    }
                }
            }
        }

        CharSequence cs = message[0];
        if (entities == null) entities = new ArrayList<>();
        cs = parsePattern(cs, BOLD_PATTERN, entities, obj -> new TLRPC.TL_messageEntityBold());
        cs = parsePattern(cs, ITALIC_PATTERN, entities, obj -> new TLRPC.TL_messageEntityItalic());
        cs = parsePattern(cs, SPOILER_PATTERN, entities, obj -> new TLRPC.TL_messageEntitySpoiler());
        cs = parsePattern(cs, UNDERLINE_PATTERN, entities, obj -> new TLRPC.TL_messageEntityUnderline());
        if (allowStrike) {
            cs = parsePattern(cs, STRIKE_PATTERN, entities, obj -> new TLRPC.TL_messageEntityStrike());
        }
        message[0] = cs;

        return entities;
    }

    private CharSequence parsePattern(CharSequence cs, Pattern pattern, List<TLRPC.MessageEntity> entities, GenericProvider<Void, TLRPC.MessageEntity> entityProvider) {
        Matcher m = pattern.matcher(cs);
        int offset = 0;
        while (m.find()) {
            if (checkInclusion(m.start(), entities, false) || checkIntersection(m.start(), m.end(), entities)) {

            }

            String gr = m.group(1);
            cs = cs.subSequence(0, m.start() - offset) + gr + cs.subSequence(m.end() - offset, cs.length());

            TLRPC.MessageEntity entity = entityProvider.provide(null);
            entity.offset = m.start() + offset;
            entity.length = gr.length();
            entities.add(entity);

            offset += m.end() - m.start() - gr.length();
        }
        return cs;
    }


}
