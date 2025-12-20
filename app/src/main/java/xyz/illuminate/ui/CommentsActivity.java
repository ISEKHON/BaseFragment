package xyz.illuminate.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.ApplicationLoader;
import xyz.illuminate.app.LocaleController;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.ActionBar;
import xyz.illuminate.ui.ActionBar.BackDrawable;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Adapters.CommentsAdapter;
import xyz.illuminate.ui.Components.AlertsCreator;
import xyz.illuminate.ui.Components.EditTextCaption;
import xyz.illuminate.ui.Components.LayoutHelper;
import xyz.illuminate.ui.Components.SizeNotifierFrameLayout;
import xyz.illuminate.ui.Components.TextStyleSpan;
import xyz.illuminate.ui.Components.TypefaceSpan;

public class CommentsActivity extends BaseFragment {
    CommentsAdapter mAdapter;
    ArrayList<HashMap<String, String>> comments = new ArrayList<>();
    LinearLayout container;
    FrameLayout contentView;
    EditTextCaption commentEdit;
    ImageView sendComment;
    LinearLayoutManager layoutManager;
    ListView listView;
    FrameLayout activeBlurContainer; // Store blur container for scroll updates

    @Override
    protected ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setCastShadows(true);
        actionBar.setAddToContainer(true);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setTitle("Title");
        actionBar.setSubtitle("Subtitle");
        return actionBar;
    }

    @Override
    public View createView(Context context) {
        fragmentView = new SizeNotifierFrameLayout(context);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                super.onItemClick(id);
                if (id == -1) {
                    finishFragment();
                }
            }
        });
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.comments, (ViewGroup) fragmentView, false);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        container = inflatedView.findViewById(R.id.linear_content);
        contentView = new FrameLayout(context);
        container.addView(contentView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        ((ViewGroup) fragmentView).addView(inflatedView);

        listView = new ListView(context);
        layoutManager = new LinearLayoutManager(context);
        mAdapter = new CommentsAdapter(this, comments);

        contentView.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        commentEdit = new EditTextCaption(context){
            @Override
            protected void extendActionMode(ActionMode actionMode, Menu menu) {
                fillActionModeMenu(menu);
            }
        };
        commentEdit.setHint("Enter something");
        commentEdit.setTextSize(14);
        commentEdit.setWindowView(getParentActivity().getWindow().getDecorView());
        commentEdit.setAllowTextEntitiesIntersection(true);
        commentEdit.setIncludeFontPadding(false);
        commentEdit.setMaxLines(20);
        commentEdit.setDelegate(() -> {
            commentEdit.invalidateEffects();
        });
        commentEdit.setBackgroundColor(Color.TRANSPARENT);
        commentEdit.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        
        // Add TextWatcher for manual formatting
        commentEdit.addTextChangedListener(new android.text.TextWatcher() {
            private boolean isUpdating = false;
            private int selectionStart = 0;
            private int selectionEnd = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isUpdating) {
                    selectionStart = commentEdit.getSelectionStart();
                    selectionEnd = commentEdit.getSelectionEnd();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (isUpdating) return;
                
                try {
                    isUpdating = true;
                    CharSequence[] message = new CharSequence[]{s.toString()};
                    ArrayList<xyz.illuminate.server.TLRPC.MessageEntity> entities = 
                        xyz.illuminate.app.MediaDataController.getInstance().getEntities(message, true);
                    
                    if (entities != null && !entities.isEmpty()) {
                        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(message[0]);
                        
                        for (xyz.illuminate.server.TLRPC.MessageEntity entity : entities) {
                            if (entity.offset + entity.length > stringBuilder.length()) {
                                continue;
                            }

                            TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                            
                            if (entity instanceof xyz.illuminate.server.TLRPC.TL_messageEntityBold) {
                                run.flags |= TextStyleSpan.FLAG_STYLE_BOLD;
                            } else if (entity instanceof xyz.illuminate.server.TLRPC.TL_messageEntityItalic) {
                                run.flags |= TextStyleSpan.FLAG_STYLE_ITALIC;
                            } else if (entity instanceof xyz.illuminate.server.TLRPC.TL_messageEntityStrike) {
                                run.flags |= TextStyleSpan.FLAG_STYLE_STRIKE;
                            } else if (entity instanceof xyz.illuminate.server.TLRPC.TL_messageEntitySpoiler) {
                                run.flags |= TextStyleSpan.FLAG_STYLE_SPOILER;
                            } else if (entity instanceof xyz.illuminate.server.TLRPC.TL_messageEntityCode ||
                                     entity instanceof xyz.illuminate.server.TLRPC.TL_messageEntityPre) {
                                run.flags |= TextStyleSpan.FLAG_STYLE_MONO;
                            }

                            if (run.flags != 0) {
                                stringBuilder.setSpan(new TextStyleSpan(run), 
                                    entity.offset, entity.offset + entity.length, 
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                        
                        commentEdit.setText(stringBuilder);
                        if (selectionEnd > stringBuilder.length()) {
                            selectionEnd = stringBuilder.length();
                        }
                        commentEdit.setSelection(selectionStart, selectionEnd);
                    }
                } catch (Exception e) {
                    android.util.Log.e("CommentsActivity", "Error applying manual formatting", e);
                } finally {
                    isUpdating = false;
                }
            }
        });

        ((LinearLayout) inflatedView.findViewById(R.id.comm)).addView(commentEdit, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        sendComment = inflatedView.findViewById(R.id.sendComment);
        sendComment.setImageTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_buttonBackgroundBlue)));
        sendComment.setBackground(Theme.getSelectorDrawable(!Theme.currentTheme.isDark));
        sendComment.setOnClickListener(view -> {
            sendMessage(commentEdit.getText().toString());
        });
        
        // Add a test button
        ImageView testButton = new ImageView(context);
        testButton.setImageResource(R.drawable.photo_paint_text);
        testButton.setImageTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_buttonBackgroundBlue)));
        testButton.setBackground(Theme.getSelectorDrawable(!Theme.currentTheme.isDark));
        testButton.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        testButton.setOnClickListener(view -> {
            testTextFormatting();
        });
//        LinearLayout.LayoutParams testButtonParams = new LinearLayout.LayoutParams(AndroidUtilities.dp(48), AndroidUtilities.dp(48));
//        testButtonParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
//        testButtonParams.setMargins(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
//        ((LinearLayout) inflatedView.findViewById(R.id.testtt)).addView(testButton, testButtonParams);
//
        // Add a font check button
        ImageView fontCheckButton = new ImageView(context);
        fontCheckButton.setImageResource(R.drawable.chats_archive);
        fontCheckButton.setImageTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_buttonBackgroundBlue)));
        fontCheckButton.setBackground(Theme.getSelectorDrawable(!Theme.currentTheme.isDark));
        fontCheckButton.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        fontCheckButton.setOnClickListener(view -> {
            checkFontFiles();
        });

        // Add a blur test button
        ImageView blurTestButton = new ImageView(context);
        blurTestButton.setImageResource(R.drawable.msg_edit);
        blurTestButton.setImageTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_buttonBackgroundBlue)));
        blurTestButton.setBackground(Theme.getSelectorDrawable(!Theme.currentTheme.isDark));
        blurTestButton.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        blurTestButton.setOnClickListener(view -> {
            demonstrateBlur3Component(context);
        });
        LinearLayout.LayoutParams blurButtonParams = new LinearLayout.LayoutParams(AndroidUtilities.dp(48), AndroidUtilities.dp(48));
        blurButtonParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        blurButtonParams.setMargins(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
        ((LinearLayout) inflatedView.findViewById(R.id.view_test)).addView(blurTestButton, blurButtonParams);
        ((LinearLayout) inflatedView.findViewById(R.id.view_test)).addView(testButton, blurButtonParams);

        return fragmentView;
    }

    public void sendMessage(String message) {
        if (!TextUtils.isEmpty(message.trim()) && message.length() != 0) {
            if (message.length() == 1000) {
                AlertsCreator.showSimpleAlert(this, "Message too long cant send");
                return;
            }

            // Get the current text with all its formatting
            Spanned spannedText = commentEdit.getText();
            TextStyleSpan[] styleSpans = spannedText.getSpans(0, spannedText.length(), TextStyleSpan.class);
            
            // Convert spans back to markdown syntax
            StringBuilder markdownText = new StringBuilder(message);
            ArrayList<FormattingSpan> formattingSpans = new ArrayList<>();
            
            // Collect all spans and their positions
            for (TextStyleSpan span : styleSpans) {
                int start = spannedText.getSpanStart(span);
                int end = spannedText.getSpanEnd(span);
                int flags = span.getTextStyleRun().flags;
                formattingSpans.add(new FormattingSpan(start, end, flags));
            }
            
            // Sort spans by start position in reverse order (to handle nested spans)
            Collections.sort(formattingSpans, (a, b) -> b.start - a.start);
            
            // Apply markdown syntax
            for (FormattingSpan span : formattingSpans) {
                String prefix = "", suffix = "";
                
                if ((span.flags & TextStyleSpan.FLAG_STYLE_BOLD_ITALIC) != 0) {
                    prefix = "**__";
                    suffix = "__**";
                } else if ((span.flags & TextStyleSpan.FLAG_STYLE_BOLD) != 0) {
                    prefix = "**";
                    suffix = "**";
                } else if ((span.flags & TextStyleSpan.FLAG_STYLE_ITALIC) != 0) {
                    prefix = "__";
                    suffix = "__";
                } else if ((span.flags & TextStyleSpan.FLAG_STYLE_STRIKE) != 0) {
                    prefix = "~~";
                    suffix = "~~";
                } else if ((span.flags & TextStyleSpan.FLAG_STYLE_SPOILER) != 0) {
                    prefix = "||";
                    suffix = "||";
                } else if ((span.flags & TextStyleSpan.FLAG_STYLE_MONO) != 0) {
                    if (span.end - span.start > 1 && markdownText.charAt(span.start - 1) == '\n') {
                        prefix = "```";
                        suffix = "```";
                    } else {
                        prefix = "`";
                        suffix = "`";
                    }
                }
                
                if (!prefix.isEmpty()) {
                    markdownText.insert(span.end, suffix);
                    markdownText.insert(span.start, prefix);
                }
            }

            HashMap<String, String> draft = new HashMap<>();
            draft.put("message", markdownText.toString());
            draft.put("type", String.valueOf(1));
            comments.add(draft);
            listView.setAdapter(new CommentsAdapter(this, comments));
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            
            // Clear the input field
            commentEdit.setText("");
            
            int position = comments.size();
            AndroidUtilities.showToast(String.valueOf(position));
        }
    }

    private static class FormattingSpan {
        int start;
        int end;
        int flags;

        FormattingSpan(int start, int end, int flags) {
            this.start = start;
            this.end = end;
            this.flags = flags;
        }
    }

    public void testTextFormatting() {
        // Test various text formatting patterns
        String[] testMessages = {
            "This is **bold text**",
            "This is __italic text__",
            "This is ||spoiler text||",
            "This is ~~strikethrough text~~",
            "This is `monospace text`",
            "This is ```code block text```",
            "This is **__bold-italic text__**"
        };
        
        for (String testMessage : testMessages) {
            HashMap<String, String> draft = new HashMap<>();
            draft.put("message", testMessage);
            draft.put("type", String.valueOf(1));
            comments.add(draft);
        }
        
        listView.setAdapter(new CommentsAdapter(this, comments));
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    public void checkFontFiles() {
        android.util.Log.d("CommentsActivity", "Checking font files...");
        
        String[] fontFiles = {
            "fonts/opensans_bold.ttf",
            "fonts/ritalic.ttf",
            "fonts/rmediumitalic.ttf"
        };
        
        for (String fontFile : fontFiles) {
            try {
                java.io.File file = new java.io.File(ApplicationLoader.applicationContext.getFilesDir(), fontFile);
                if (file.exists()) {
                    android.util.Log.d("CommentsActivity", "Font file exists: " + fontFile);
                } else {
                    android.util.Log.e("CommentsActivity", "Font file does not exist: " + fontFile);
                }
            } catch (Exception e) {
                android.util.Log.e("CommentsActivity", "Error checking font file: " + fontFile, e);
            }
        }
        
        // Also check if the fonts can be loaded
        try {
            android.graphics.Typeface boldTypeface = xyz.illuminate.app.AndroidUtilities.getTypeface("fonts/opensans_bold.ttf");
            android.util.Log.d("CommentsActivity", "Bold font loaded: " + (boldTypeface != null));
            
            android.graphics.Typeface italicTypeface = xyz.illuminate.app.AndroidUtilities.getTypeface("fonts/ritalic.ttf");
            android.util.Log.d("CommentsActivity", "Italic font loaded: " + (italicTypeface != null));
            
            android.graphics.Typeface boldItalicTypeface = xyz.illuminate.app.AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf");
            android.util.Log.d("CommentsActivity", "Bold-Italic font loaded: " + (boldItalicTypeface != null));
        } catch (Exception e) {
            android.util.Log.e("CommentsActivity", "Error loading fonts", e);
        }
    }

    public void fillActionModeMenu(Menu menu) {
        if (menu.findItem(R.id.menu_bold) == null) {
            if (Build.VERSION.SDK_INT >= 23) {
                menu.removeItem(android.R.id.shareText);
            }
            int order = 6;
            menu.add(R.id.menu_groupbolditalic, R.id.menu_spoiler, order++, LocaleController.getString("Spoiler", R.string.Spoiler));
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(LocaleController.getString("Bold", R.string.Bold));
            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            menu.add(R.id.menu_groupbolditalic, R.id.menu_bold, order++, stringBuilder);
            stringBuilder = new SpannableStringBuilder(LocaleController.getString("Italic", R.string.Italic));
            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            menu.add(R.id.menu_groupbolditalic, R.id.menu_italic, order++, stringBuilder);

            stringBuilder = new SpannableStringBuilder(LocaleController.getString("Mono", R.string.Mono));
            stringBuilder.setSpan(new TypefaceSpan(Typeface.MONOSPACE), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            menu.add(R.id.menu_groupbolditalic, R.id.menu_mono, order++, stringBuilder);

            stringBuilder = new SpannableStringBuilder(LocaleController.getString("Strike", R.string.Strike));
            TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
            run.flags |= TextStyleSpan.FLAG_STYLE_STRIKE;
            stringBuilder.setSpan(new TextStyleSpan(run), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            menu.add(R.id.menu_groupbolditalic, R.id.menu_strike, order++, stringBuilder);
            stringBuilder = new SpannableStringBuilder(LocaleController.getString("Underline", R.string.Underline));
            run = new TextStyleSpan.TextStyleRun();
            run.flags |= TextStyleSpan.FLAG_STYLE_UNDERLINE;
            stringBuilder.setSpan(new TextStyleSpan(run), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            menu.add(R.id.menu_groupbolditalic, R.id.menu_underline, order++, stringBuilder);
            menu.add(R.id.menu_groupbolditalic, R.id.menu_link, order++, LocaleController.getString("CreateLink", R.string.CreateLink));
            menu.add(R.id.menu_groupbolditalic, R.id.menu_regular, order++, LocaleController.getString("Regular", R.string.Regular));
        }
    }

    /**
     * Demonstrates progressive blur effect on the ListView content
     * Now updates dynamically as you scroll!
     */
    public void demonstrateBlur3Component(Context context) {
        android.util.Log.d("CommentsActivity", "Demonstrating progressive blur with ListView content");

        // Make sure we have some content in the ListView to blur
        if (comments.isEmpty()) {
            // Add sample content for demonstration
            addSampleComments();
        }

        // Progressive blur effect on ListView (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            createActualBlurEffectOnListView(context);
            setupScrollListenerForBlur();
            AndroidUtilities.showToast("Live progressive blur enabled - scroll to see!");
        } else {
            AndroidUtilities.showToast("Blur requires Android 12+ (API 31)");
        }
    }

    /**
     * Sets up scroll listener to update blur in real-time as user scrolls
     */
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.S)
    private void setupScrollListenerForBlur() {
        listView.setOnScrollListener(new android.widget.AbsListView.OnScrollListener() {
            private long lastUpdateTime = 0;
            private static final long UPDATE_INTERVAL = 16; // ~60 FPS
            private boolean isScrolling = false;

            @Override
            public void onScrollStateChanged(android.widget.AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    isScrolling = false;
                    // Final update when scroll stops for crisp result
                    if (activeBlurContainer != null) {
                        updateBlurEffect();
                    }
                } else {
                    isScrolling = true;
                }
            }

            @Override
            public void onScroll(android.widget.AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (activeBlurContainer != null && isScrolling) {
                    long currentTime = System.currentTimeMillis();

                    // Throttle updates to prevent excessive calls
                    if (currentTime - lastUpdateTime >= UPDATE_INTERVAL) {
                        lastUpdateTime = currentTime;
                        updateBlurEffect();
                    }
                }
            }
        });
    }

    /**
     * Updates the blur effect by recapturing the ListView content
     * Optimized for smooth real-time updates during scrolling
     */
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.S)
    private void updateBlurEffect() {
        if (activeBlurContainer == null || listView == null) return;

        try {
            // Get position of blur container
            int[] location = new int[2];
            activeBlurContainer.getLocationInWindow(location);
            int[] contentLocation = new int[2];
            contentView.getLocationInWindow(contentLocation);

            int relativeX = location[0] - contentLocation[0];
            int relativeY = location[1] - contentLocation[1];

            // Recapture the ListView content
            android.graphics.Bitmap capturedBitmap = captureBitmapFromView(
                listView,
                relativeX,
                relativeY,
                activeBlurContainer.getWidth(),
                activeBlurContainer.getHeight()
            );

            if (capturedBitmap != null) {
                // Store reference to text view to keep it
                android.widget.TextView textView = null;
                View overlayView = null;

                // Find and keep text and overlay
                for (int i = 0; i < activeBlurContainer.getChildCount(); i++) {
                    View child = activeBlurContainer.getChildAt(i);
                    if (child instanceof android.widget.TextView) {
                        textView = (android.widget.TextView) child;
                    } else if (!(child instanceof android.widget.ImageView)) {
                        overlayView = child;
                    }
                }

                // Remove ALL children
                activeBlurContainer.removeAllViews();

                // Recreate blur layers with new content (faster recreation)
                createProgressiveBlurLayers(getParentActivity(), activeBlurContainer, capturedBitmap);

                // Add overlay back
                if (overlayView != null) {
                    activeBlurContainer.addView(overlayView);
                }

                // Add text back on top
                if (textView != null) {
                    activeBlurContainer.addView(textView);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("CommentsActivity", "Error updating blur effect", e);
        }
    }

    /**
     * Adds sample comments to the ListView for demonstration
     */
    private void addSampleComments() {
        String[] sampleMessages = {
            "This is a **bold message**",
            "This is an __italic message__",
            "This is a ||spoiler message||",
            "This is ~~strikethrough text~~",
            "This is `code text`",
            "Normal text message here",
            "Another **bold** and __italic__ message",
            "Final test message with formatting"
        };

        for (String message : sampleMessages) {
            HashMap<String, String> draft = new HashMap<>();
            draft.put("message", message);
            draft.put("type", String.valueOf(1));
            comments.add(draft);
        }

        listView.setAdapter(new CommentsAdapter(this, comments));
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
    }


    /**
     * Example 4: Creates PROGRESSIVE blur effect on the ListView
     * Blur intensity increases from edges to center for a dynamic effect
     */
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.S)
    private void createActualBlurEffectOnListView(Context context) {
        android.util.Log.d("CommentsActivity", "Creating PROGRESSIVE blur effect on ListView...");

        // Create a container for the progressive blur effect
        final FrameLayout blurContainer = new FrameLayout(context);
        activeBlurContainer = blurContainer; // Store reference for scroll updates
        blurContainer.setElevation(AndroidUtilities.dp(12));

        // Set rounded corners
        blurContainer.setClipToOutline(true);
        blurContainer.setOutlineProvider(new android.view.ViewOutlineProvider() {
            @Override
            public void getOutline(android.view.View view, android.graphics.Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(),
                        AndroidUtilities.dp(20));
            }
        });

        // Position the container first
        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(
                AndroidUtilities.dp(280),
                AndroidUtilities.dp(140)
        );
        containerParams.gravity = Gravity.CENTER;
        contentView.addView(blurContainer, containerParams);

        // Post to ensure layout is complete before capturing
        blurContainer.post(() -> {
                    // Wait for ListView to be properly laid out
                    listView.post(() -> {
                        try {
                            android.util.Log.d("CommentsActivity", "Starting blur capture...");
                            android.util.Log.d("CommentsActivity", "ListView size: " + listView.getWidth() + "x" + listView.getHeight());
                            android.util.Log.d("CommentsActivity", "BlurContainer size: " + blurContainer.getWidth() + "x" + blurContainer.getHeight());

                            // Get the position of the blur container relative to ListView
                            int[] blurLocation = new int[2];
                            blurContainer.getLocationInWindow(blurLocation);
                            int[] listLocation = new int[2];
                            listView.getLocationInWindow(listLocation);

                            int relativeX = blurLocation[0] - listLocation[0];
                            int relativeY = blurLocation[1] - listLocation[1];

                            android.util.Log.d("CommentsActivity", "Blur position in window: " + blurLocation[0] + "," + blurLocation[1]);
                            android.util.Log.d("CommentsActivity", "ListView position in window: " + listLocation[0] + "," + listLocation[1]);
                            android.util.Log.d("CommentsActivity", "Relative position: " + relativeX + "," + relativeY);

                            // Ensure coordinates are valid
                            if (relativeX < 0) relativeX = 0;
                            if (relativeY < 0) relativeY = 0;

                            // Capture the ListView content
                            android.graphics.Bitmap capturedBitmap = captureBitmapFromView(
                                    listView,
                                    relativeX,
                                    relativeY,
                                    blurContainer.getWidth(),
                                    blurContainer.getHeight()
                            );

                            android.util.Log.d("CommentsActivity", "Captured bitmap: " + (capturedBitmap != null ? capturedBitmap.getWidth() + "x" + capturedBitmap.getHeight() : "null"));

                            if (capturedBitmap != null) {
                                // Create multiple layers for progressive blur effect
                                createProgressiveBlurLayers(context, blurContainer, capturedBitmap);

                                android.util.Log.d("CommentsActivity", "Progressive blur applied successfully!");
                            } else {
                                // Fallback
                                blurContainer.setBackgroundColor(0xCCFFFFFF);
                                android.widget.TextView textView = new android.widget.TextView(context);
                                textView.setText("Blur Failed\nShowing Fallback");
                                textView.setTextColor(0xFF000000);
                                textView.setGravity(Gravity.CENTER);
                                blurContainer.addView(textView, LayoutHelper.createFrame(
                                        LayoutHelper.MATCH_PARENT,
                                        LayoutHelper.WRAP_CONTENT,
                                        Gravity.CENTER
                                ));
                            }
                        } catch (Exception e) {
                            android.util.Log.e("CommentsActivity", "Error creating progressive blur", e);
                        }
                    });

                }
        );
    }


    /**
     * Creates progressive blur layers with increasing blur intensity
     * INCREASED DENSITY: More layers with stronger blur for denser effect
     */
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.S)
    private void createProgressiveBlurLayers(Context context, FrameLayout container, android.graphics.Bitmap baseBitmap) {
        // Create 5 layers with MUCH stronger blur amounts for denser effect
        float[] blurRadii = {10f, 25f, 45f, 70f, 100f}; // INCREASED: Much stronger progressive blur
        float[] alphas = {0.25f, 0.25f, 0.25f, 0.15f, 0.1f}; // Balanced opacity across more layers

        for (int i = 0; i < blurRadii.length; i++) {
            android.widget.ImageView blurLayer = new android.widget.ImageView(context);
            blurLayer.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            blurLayer.setImageBitmap(baseBitmap);

            // Apply STRONGER progressive blur
            blurLayer.setRenderEffect(
                android.graphics.RenderEffect.createBlurEffect(
                    blurRadii[i], blurRadii[i],
                    android.graphics.Shader.TileMode.CLAMP
                )
            );

            // Set alpha for layering effect
            blurLayer.setAlpha(alphas[i]);

            container.addView(blurLayer, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT
            ));
        }

        // Add a stronger white overlay for better text visibility with denser blur
        View overlay = new View(context);
        overlay.setBackgroundColor(0x50FFFFFF); // 31% white - increased for denser blur
        container.addView(overlay, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT,
            LayoutHelper.MATCH_PARENT
        ));

    }

    /**
     * Helper method to capture a bitmap from a specific area of a view
     */
    private android.graphics.Bitmap captureBitmapFromView(View view, int x, int y, int width, int height) {
        try {
            android.util.Log.d("CommentsActivity", "=== CAPTURE START ===");
            android.util.Log.d("CommentsActivity", "View: " + view.getClass().getSimpleName());
            android.util.Log.d("CommentsActivity", "View size: " + view.getWidth() + "x" + view.getHeight());
            android.util.Log.d("CommentsActivity", "Capture region: x=" + x + " y=" + y + " w=" + width + " h=" + height);

            if (view.getWidth() == 0 || view.getHeight() == 0) {
                android.util.Log.e("CommentsActivity", "View has zero size!");
                return null;
            }

            // Force view to layout and draw
            view.measure(
                View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY)
            );
            view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

            // Create full bitmap of ListView
            android.graphics.Bitmap fullBitmap = android.graphics.Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                android.graphics.Bitmap.Config.ARGB_8888
            );
            android.graphics.Canvas canvas = new android.graphics.Canvas(fullBitmap);

            // Draw with background
            android.graphics.drawable.Drawable bgDrawable = view.getBackground();
            if (bgDrawable != null) {
                bgDrawable.draw(canvas);
            } else {
                canvas.drawColor(android.graphics.Color.WHITE);
            }

            view.draw(canvas);

            android.util.Log.d("CommentsActivity", "Full bitmap created: " + fullBitmap.getWidth() + "x" + fullBitmap.getHeight());

            // Crop to the specific area
            if (x + width > fullBitmap.getWidth()) {
                width = fullBitmap.getWidth() - x;
            }
            if (y + height > fullBitmap.getHeight()) {
                height = fullBitmap.getHeight() - y;
            }

            if (x < 0) x = 0;
            if (y < 0) y = 0;

            if (width <= 0 || height <= 0) {
                android.util.Log.e("CommentsActivity", "Invalid crop dimensions: w=" + width + " h=" + height);
                fullBitmap.recycle();
                return null;
            }

            android.util.Log.d("CommentsActivity", "Cropping to: x=" + x + " y=" + y + " w=" + width + " h=" + height);

            android.graphics.Bitmap croppedBitmap = android.graphics.Bitmap.createBitmap(
                fullBitmap,
                x,
                y,
                width,
                height
            );

            fullBitmap.recycle();

            android.util.Log.d("CommentsActivity", "=== CAPTURE SUCCESS === Result: " + croppedBitmap.getWidth() + "x" + croppedBitmap.getHeight());

            return croppedBitmap;
        } catch (Exception e) {
            android.util.Log.e("CommentsActivity", "=== CAPTURE FAILED ===", e);
            e.printStackTrace();
            return null;
        }
    }

}
