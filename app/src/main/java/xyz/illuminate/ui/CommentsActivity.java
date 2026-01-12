package xyz.illuminate.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
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
import xyz.illuminate.ui.ActionBar.ThemeDescription;
import xyz.illuminate.ui.Adapters.CommentsAdapter;
import xyz.illuminate.ui.Cells.TextSettingsCell;
import xyz.illuminate.ui.Components.AlertsCreator;
import xyz.illuminate.ui.Components.EditTextCaption;
import xyz.illuminate.ui.Components.LayoutHelper;
import xyz.illuminate.ui.Components.SizeNotifierFrameLayout;
import xyz.illuminate.ui.Components.TextStyleSpan;
import xyz.illuminate.ui.Components.TypefaceSpan;
import xyz.illuminate.ui.Components.blur3.DownscaleScrollableNoiseSuppressor;
import xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawableRenderNode;
import xyz.illuminate.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider;
import xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode;

import android.widget.AbsListView;
import android.widget.TextView;

/**
 * CommentsActivity - Demonstrates the Telegram blur3 system for liquid glass effects
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * LIQUID GLASS EFFECT IMPLEMENTATION GUIDE
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * The blur3 system from Telegram provides a sophisticated multi-layer blur and
 * liquid glass effect. Here's how to use it:
 * 
 * ╔═══════════════════════════════════════════════════════════════════════╗
 * ║ 1. REQUIREMENTS                                                       ║
 * ╚═══════════════════════════════════════════════════════════════════════╝
 * 
 *   • Android 12+ (API 31) for blur effects (RenderEffect)
 *   • Android 13+ (API 33) for liquid glass shader (RuntimeShader with AGSL)
 *   • Hardware acceleration enabled on Canvas
 *   • liquid_glass_shader.agsl file in res/raw/
 * 
 * ╔═══════════════════════════════════════════════════════════════════════╗
 * ║ 2. BASIC USAGE - Simple Blur                                         ║
 * ╚═══════════════════════════════════════════════════════════════════════╝
 * 
 *   // Create blur source
 *   BlurredBackgroundSourceRenderNode source = 
 *       new BlurredBackgroundSourceRenderNode(fallbackColorSource);
 *   
 *   // Create drawable from source
 *   BlurredBackgroundDrawableRenderNode drawable = 
 *       (BlurredBackgroundDrawableRenderNode) source.createDrawable();
 *   
 *   // Configure appearance
 *   drawable.setRadius(dp(16));  // Corner radius
 *   drawable.setColorProvider(colorProvider);
 * 
 * ╔═══════════════════════════════════════════════════════════════════════╗
 * ║ 3. LIQUID GLASS EFFECT - Advanced                                    ║
 * ╚═══════════════════════════════════════════════════════════════════════╝
 * 
 *   // Enable liquid glass shader (Android 13+)
 *   drawable.setLiquidGlassEffectAllowed();
 *   
 *   // Configure glass properties
 *   drawable.setThickness(dp(11));      // Glass thickness
 *   drawable.setIntensity(0.75f);       // Refraction intensity (0.0-1.0)
 * 
 * ╔═══════════════════════════════════════════════════════════════════════╗
 * ║ 4. RENDERING PIPELINE                                                 ║
 * ╚═══════════════════════════════════════════════════════════════════════╝
 * 
 *   In your view's dispatchDraw():
 *   
 *   1. Record content to blur:
 *      RecordingCanvas rc = source.beginRecording(width, height);
 *      // Draw background content here
 *      source.endRecording();
 *   
 *   2. Set drawable bounds:
 *      drawable.setBounds(0, 0, width, height);
 *   
 *   3. Update colors (if needed):
 *      drawable.updateColors();
 *   
 *   4. Draw the blurred result:
 *      drawable.draw(canvas);
 * 
 * ╔═══════════════════════════════════════════════════════════════════════╗
 * ║ 5. ADVANCED - DownscaleScrollableNoiseSuppressor                     ║
 * ╚═══════════════════════════════════════════════════════════════════════╝
 * 
 *   For complex scrollable content with multiple blur regions:
 *   
 *   DownscaleScrollableNoiseSuppressor suppressor = 
 *       new DownscaleScrollableNoiseSuppressor();
 *   
 *   // Setup render nodes for blur regions
 *   ArrayList<RectF> positions = new ArrayList<>();
 *   positions.add(new RectF(0, 0, width, height));
 *   suppressor.setupRenderNodes(positions, 1);
 *   
 *   // Record content
 *   RecordingCanvas rc = suppressor.beginRecordingRect(0);
 *   // Draw content
 *   suppressor.endRecordingRect();
 *   
 *   // Invalidate and draw
 *   suppressor.invalidateResultRenderNodes(width, height);
 *   suppressor.draw(canvas, DRAW_GLASS);  // or DRAW_FROSTED_GLASS
 * 
 * ╔═══════════════════════════════════════════════════════════════════════╗
 * ║ 6. COLOR PROVIDER                                                     ║
 * ╚═══════════════════════════════════════════════════════════════════════╝
 * 
 *   Implement BlurredBackgroundColorProvider for theming:
 *   
 *   - getBackgroundColor(): Base color (semi-transparent for glass effect)
 *   - getShadowColor(): Drop shadow color
 *   - getStrokeColorTop(): Top highlight stroke
 *   - getStrokeColorBottom(): Bottom shadow stroke
 * 
 * ╔═══════════════════════════════════════════════════════════════════════╗
 * ║ 7. PERFORMANCE TIPS                                                   ║
 * ╚═══════════════════════════════════════════════════════════════════════╝
 * 
 *   • Use setLayerType(LAYER_TYPE_HARDWARE, null) on container views
 *   • The blur uses downsampling for performance (4x/16x downscale)
 *   • Liquid glass mode uses weak blur + strong blur combination
 *   • Only update display list when content actually changes
 *   • Use RenderNode caching to avoid redundant rendering
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * See the methods below for complete working examples:
 *   - demonstrateBlur3Component(): Main entry point
 *   - createLiquidGlassEffect(): Full liquid glass implementation
 *   - createAdvancedLiquidGlassEffect(): Using DownscaleScrollableNoiseSuppressor
 */

public class CommentsActivity extends BaseFragment {
    private static final int REQUEST_IMAGE_PICK = 1001;
    
    CommentsAdapter mAdapter;
    ArrayList<HashMap<String, String>> comments = new ArrayList<>();
    LinearLayout container;
    FrameLayout contentView;
    EditTextCaption commentEdit;
    ImageView sendComment;
    ImageView attachImageButton;
    LinearLayoutManager layoutManager;
    ListView listView;
    FrameLayout activeBlurContainer; // Store blur container for scroll updates
    
    // Navigation and message input containers
    private LinearLayout messageInputContainer;
    private LinearLayout navigationBarContainer;
    private ImageView toggleButton;
    private boolean showingMessageInput = true;
    private boolean navigationBarBlurApplied = false;
    
    // Glass stroke buttons for theme updates
    private android.widget.Button blurTestButton;
    private android.widget.Button frostedGlassButton;
    private android.widget.Button combinedBlurButton;
    private android.widget.Button glassStrokeButton;

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
        contentView.setClipToPadding(false);
        container.addView(contentView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        ((ViewGroup) fragmentView).addView(inflatedView);

        listView = new ListView(context);
        layoutManager = new LinearLayoutManager(context);
        mAdapter = new CommentsAdapter(this, comments);
        
        // Add bottom padding so content doesn't hide behind floating input
        listView.setClipToPadding(false);
        listView.setPadding(0, 0, 0, AndroidUtilities.dp(80)); // Reserve space for input

        contentView.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        commentEdit = new EditTextCaption(context){
            @Override
            protected void extendActionMode(ActionMode actionMode, Menu menu) {
                fillActionModeMenu(menu);
            }
        };
        commentEdit.setHint("Message");
        commentEdit.setHintTextColor(Theme.multAlpha(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), 0.4f));
        commentEdit.setTextSize(16);
        commentEdit.setWindowView(getParentActivity().getWindow().getDecorView());
        commentEdit.setAllowTextEntitiesIntersection(true);
        commentEdit.setIncludeFontPadding(false);
        commentEdit.setMaxLines(5);
        commentEdit.setDelegate(() -> {
            commentEdit.invalidateEffects();
        });
        commentEdit.setBackgroundColor(Color.TRANSPARENT);
        commentEdit.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));

        addSampleComments();
        
        // Apply Telegram-style blur to message input after layout
        fragmentView.post(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                createMessageInputBlur(context, inflatedView);
            }
        });
        
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

        // Store reference to message input container
        messageInputContainer = inflatedView.findViewById(R.id.lin_shadow);
        
        // Create navigation bar (initially hidden)
        createNavigationBar(context, inflatedView);
        
        ((LinearLayout) inflatedView.findViewById(R.id.comm)).addView(commentEdit, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        // Add attach image button
        attachImageButton = new ImageView(context);
        attachImageButton.setImageResource(R.drawable.input_attach);
        attachImageButton.setImageTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_buttonBackgroundBlue)));
        attachImageButton.setBackground(Theme.getSelectorDrawable(!Theme.currentTheme.isDark));
        attachImageButton.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        attachImageButton.setOnClickListener(view -> {
            openImagePicker();
        });
        // Don't add here - will be added to button container below

        sendComment = inflatedView.findViewById(R.id.sendComment);
        sendComment.setImageTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_buttonBackgroundBlue)));
        sendComment.setBackground(Theme.getSelectorDrawable(!Theme.currentTheme.isDark));
        sendComment.setOnClickListener(view -> {
            sendMessage(commentEdit.getText().toString());
        });
        
        // Add toggle button to switch between message input and navigation
        toggleButton = new ImageView(context);
        toggleButton.setImageResource(R.drawable.input_bot2);
        toggleButton.setImageTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_buttonBackgroundBlue)));
        toggleButton.setBackground(Theme.getSelectorDrawable(!Theme.currentTheme.isDark));
        toggleButton.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        toggleButton.setOnClickListener(view -> {
            toggleInputView();
        });
       // ((LinearLayout) inflatedView.findViewById(R.id.comm)).addView(toggleButton, LayoutHelper.createLinear(AndroidUtilities.dp(48), AndroidUtilities.dp(48)));
        
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
        blurTestButton = createGlassStrokeButton(context, "Liquid Glass");
        blurTestButton.setOnClickListener(view -> {
            demonstrateBlur3Component(context);
        });
        
        // Add a frosted glass button (alternative blur style)
        frostedGlassButton = createGlassStrokeButton(context, "Frosted Glass");
        frostedGlassButton.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                createAdvancedLiquidGlassEffect(context);
                AndroidUtilities.showToast("Advanced frosted glass with noise suppression!");
            } else {
                AndroidUtilities.showToast("Requires Android 12+");
            }
        });
        
        // Add a combined liquid glass + blur button (best of both)
        combinedBlurButton = createGlassStrokeButton(context, "Premium Glass");
        combinedBlurButton.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                createCombinedLiquidGlassWithBlur(context);
                AndroidUtilities.showToast("Premium liquid glass with enhanced blur!");
            } else {
                AndroidUtilities.showToast("Requires Android 12+");
            }
        });
        
        // Add a blur with glass stroke button (emphasized borders)
        glassStrokeButton = createGlassStrokeButton(context, "Glass Stroke");
        glassStrokeButton.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                createBlurWithGlassStroke(context);
                AndroidUtilities.showToast("Glass blur with prominent strokes!");
            } else {
                AndroidUtilities.showToast("Requires Android 12+");
            }
        });
        
        // Create a horizontal scrollable container for the buttons
        android.widget.HorizontalScrollView buttonScrollView = new android.widget.HorizontalScrollView(context);
        buttonScrollView.setHorizontalScrollBarEnabled(false);
        buttonScrollView.setFillViewport(false);
        
        LinearLayout buttonContainer = new LinearLayout(context);
        buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonContainer.setGravity(Gravity.CENTER_VERTICAL);
        
        LinearLayout.LayoutParams blurButtonParams = new LinearLayout.LayoutParams(
            LayoutHelper.WRAP_CONTENT, 
            LayoutHelper.WRAP_CONTENT
        );
        blurButtonParams.gravity = Gravity.CENTER_VERTICAL;
        blurButtonParams.setMargins(AndroidUtilities.dp(4), AndroidUtilities.dp(4), AndroidUtilities.dp(4), AndroidUtilities.dp(4));
        
        buttonContainer.addView(toggleButton, blurButtonParams);
         buttonContainer.addView(attachImageButton, blurButtonParams);
        buttonContainer.addView(glassStrokeButton, blurButtonParams);
        buttonContainer.addView(combinedBlurButton, blurButtonParams);
        buttonContainer.addView(frostedGlassButton, blurButtonParams);
        buttonContainer.addView(blurTestButton, blurButtonParams);
        buttonContainer.addView(testButton, blurButtonParams);
        
        buttonScrollView.addView(buttonContainer);
        
        ((LinearLayout) inflatedView.findViewById(R.id.view_test)).addView(buttonScrollView, LayoutHelper.createLinear(
            LayoutHelper.MATCH_PARENT, 
            LayoutHelper.WRAP_CONTENT
        ));

        return fragmentView;
    }

    /**
     * Creates a button with actual blur3 glass stroke effect and rounded corners (Telegram style)
     */
    private android.widget.Button createGlassStrokeButton(Context context, String text) {
        // Create blur source with fallback
        xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceColor fallbackSource = 
            new xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceColor();
        fallbackSource.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        
        xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode blurSource = 
            new xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode(fallbackSource);
        
        // Create the blur drawable
        xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawableRenderNode blurDrawable = 
            (xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawableRenderNode) blurSource.createDrawable();
        
        // Configure glass properties
        blurDrawable.setRadius(AndroidUtilities.dp(20)); // Rounded corners
        
        // Set color provider for glass effect
        blurDrawable.setColorProvider(new xyz.illuminate.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider() {
            @Override
            public int getBackgroundColor() {
                // Semi-transparent blue background
                return Theme.multAlpha(Theme.getColor(Theme.key_buttonBackgroundBlue), 0.85f);
            }

            @Override
            public int getShadowColor() {
                return 0x30000000;
            }

            @Override
            public int getStrokeColorTop() {
                // Bright white top stroke for glass shine
                return 0x60FFFFFF;
            }

            @Override
            public int getStrokeColorBottom() {
                // Darker bottom stroke
                return 0x30000000;
            }
        });
        
        // Create button with blur3 background
        android.widget.Button button = new android.widget.Button(context) {
            private boolean isRecording = false;
            
            @Override
            protected void onDraw(Canvas canvas) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && canvas.isHardwareAccelerated() && !isRecording) {
                    // Record background content for blur
                    if (blurSource.needUpdateDisplayList(getWidth(), getHeight())) {
                        isRecording = true;
                        android.graphics.RecordingCanvas recordingCanvas = blurSource.beginRecording(getWidth(), getHeight());
                        recordingCanvas.save();
                        recordingCanvas.translate(-getLeft(), -getTop());
                        View parent = (View) getParent();
                        if (parent != null) {
                            int oldVisibility = getVisibility();
                            setVisibility(View.INVISIBLE);
                            parent.draw(recordingCanvas);
                            setVisibility(oldVisibility);
                        }
                        recordingCanvas.restore();
                        blurSource.endRecording();
                        isRecording = false;
                    }

                    // Draw blurred background with glass effect
                    blurDrawable.setBounds(0, 0, getWidth(), getHeight());
                    blurDrawable.updateColors();
                    blurDrawable.draw(canvas);
                } else {
                    // Fallback for older Android versions
                    canvas.drawColor(Theme.getColor(Theme.key_buttonBackgroundBlue));
                }
                
                super.onDraw(canvas);
            }
        };
        
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(12);
        button.setTypeface(null, Typeface.BOLD);
        button.setAllCaps(false);
        button.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(10), AndroidUtilities.dp(16), AndroidUtilities.dp(10));
        button.setBackground(null); // Remove default background
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setElevation(AndroidUtilities.dp(2));
            button.setStateListAnimator(null);
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            button.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        
        return button;
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
    
    /**
     * Opens image picker to select an image from gallery
     */
    private void openImagePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            getParentActivity().startActivityForResult(intent, REQUEST_IMAGE_PICK);
        } catch (Exception e) {
            android.util.Log.e("CommentsActivity", "Error opening image picker", e);
            AndroidUtilities.showToast("Failed to open image picker");
        }
    }
    
    /**
     * Creates navigation bar with blur effect (same style as message input)
     */
    private void createNavigationBar(Context context, View rootView) {
        // Use fragmentView as parent instead of linear_content so we can position at bottom
        ViewGroup parentView = (ViewGroup) rootView; // rootView is fragmentView (FrameLayout)
        
        // Create navigation bar container - transparent, blur wrapper will handle styling
        navigationBarContainer = new LinearLayout(context);
        navigationBarContainer.setOrientation(LinearLayout.HORIZONTAL);
        navigationBarContainer.setGravity(Gravity.CENTER);
        navigationBarContainer.setBackgroundColor(Color.TRANSPARENT);
        navigationBarContainer.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        navigationBarContainer.setVisibility(View.GONE); // Initially hidden
        
        // Initialize toggle state - message input shown by default
        showingMessageInput = true;
        
        // Create icon-based navigation buttons
        ImageView homeButton = createIconNavButton(context, R.drawable.msg_home, "Home");
        homeButton.setColorFilter(0xFF007AFF, android.graphics.PorterDuff.Mode.SRC_IN); // Blue color for home
        homeButton.setOnClickListener(v -> {
            animateButtonClick(homeButton);
            AndroidUtilities.showToast("Home clicked");
        });
        
        ImageView libraryButton = createIconNavButton(context, R.drawable.msg_callback, "Library");
        libraryButton.setOnClickListener(v -> {
            animateButtonClick(libraryButton);
            AndroidUtilities.showToast("Library clicked");
        });
        
        ImageView settingsButton = createIconNavButton(context, R.drawable.menu_settings, "Settings");
        settingsButton.setOnClickListener(v -> {
            animateButtonClick(settingsButton);
            toggleInputView(); // Toggle back to message input
        });
        
        // Add buttons to navigation bar with equal weight
        LinearLayout.LayoutParams navButtonParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        navButtonParams.setMargins(AndroidUtilities.dp(4), 0, AndroidUtilities.dp(4), 0);
        
        navigationBarContainer.addView(homeButton, navButtonParams);
        navigationBarContainer.addView(libraryButton, navButtonParams);
        navigationBarContainer.addView(settingsButton, navButtonParams);
        
        // Add navigation bar to fragmentView (FrameLayout) so it overlays at bottom
        // Get screen width and set navbar to 70% of screen width
        android.util.DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int navBarWidth = (int) (screenWidth * 0.7f);
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            navBarWidth,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
        );
        params.setMargins(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        parentView.addView(navigationBarContainer, params);
    }
    
    /**
     * Creates an icon-based navigation button (iOS style)
     */
    private ImageView createIconNavButton(Context context, int iconRes, String contentDescription) {
        ImageView button = new ImageView(context);
        button.setImageResource(iconRes);
        button.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), android.graphics.PorterDuff.Mode.SRC_IN);
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        button.setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(12), AndroidUtilities.dp(12), AndroidUtilities.dp(12));
        button.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 1));
        button.setContentDescription(contentDescription);
        
        return button;
    }
    
    /**
     * Creates a navigation button with icon
     */
    private ImageView createNavButton(Context context, int iconRes, String contentDescription) {
        ImageView button = new ImageView(context);
        button.setLayoutParams(new ViewGroup.LayoutParams(
            AndroidUtilities.dp(56),
            AndroidUtilities.dp(56)
        ));
        button.setImageResource(iconRes);
        // Use solid dark color for maximum visibility
        button.setColorFilter(0xFF000000, android.graphics.PorterDuff.Mode.SRC_IN);
        // Add a background to see if button is there
        button.setBackgroundColor(0x20FF0000); // Light red tint for debugging
        button.setPadding(AndroidUtilities.dp(12), AndroidUtilities.dp(12), AndroidUtilities.dp(12), AndroidUtilities.dp(12));
        button.setContentDescription(contentDescription);
        button.setScaleType(ImageView.ScaleType.FIT_CENTER);
        
        android.util.Log.d("CommentsActivity", "Creating nav button: " + contentDescription + " with icon: " + iconRes);
        
        return button;
    }
    
    /**
     * Animates button click with scale and alpha effect
     */
    private void animateButtonClick(View button) {
        button.animate()
            .scaleX(0.85f)
            .scaleY(0.85f)
            .alpha(0.6f)
            .setDuration(100)
            .withEndAction(() -> {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(100)
                    .start();
            })
            .start();
    }
    
    /**
     * Toggles between message input and navigation bar
     */
    private void toggleInputView() {
        showingMessageInput = !showingMessageInput;
        
        if (showingMessageInput) {
            // Show message input, hide navigation bar
            navigationBarContainer.setVisibility(View.GONE);
            
            // Hide the navigation bar wrapper (the blur wrapper if applied)
            View navBarParent = (View) navigationBarContainer.getParent();
            if (navBarParent != null && navBarParent != fragmentView) {
                navBarParent.setVisibility(View.GONE);
            }
            
            // Show the message input container directly (blur is a drawable now, not a wrapper)
            messageInputContainer.setVisibility(View.VISIBLE);
            messageInputContainer.setAlpha(0f);
            messageInputContainer.animate().alpha(1f).setDuration(200).start();
            
            toggleButton.setImageResource(R.drawable.input_bot2);
        } else {
            // Hide message input, show navigation bar
            messageInputContainer.setVisibility(View.GONE);
            
            // Show navigation bar container first
            navigationBarContainer.setVisibility(View.VISIBLE);
            
            // Show the navigation bar wrapper (the blur wrapper if applied)
            View navBarParent = (View) navigationBarContainer.getParent();
            if (navBarParent != null && navBarParent != fragmentView) {
                navBarParent.setVisibility(View.VISIBLE);
                navBarParent.setAlpha(0f);
                navBarParent.animate().alpha(1f).setDuration(200).start();
            } else {
                // If not wrapped yet, animate the navbar directly
                navigationBarContainer.setAlpha(0f);
                navigationBarContainer.animate().alpha(1f).setDuration(200).start();
            }
            
            toggleButton.setImageResource(R.drawable.input_keyboard);
            
            // Apply blur to navigation bar
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                applyBlurToNavigationBar(navigationBarContainer);
            }
        }
    }
    
    /**
     * Applies Telegram-style blur to navigation bar (same as message input)
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void applyBlurToNavigationBar(View navBar) {
        // Check if blur has already been applied
        if (navigationBarBlurApplied) {
            return;
        }
        
        ViewGroup parent = (ViewGroup) navBar.getParent();
        if (parent == null) return;
        
        // Remove from parent
        int index = parent.indexOfChild(navBar);
        ViewGroup.LayoutParams originalParams = navBar.getLayoutParams();
        parent.removeView(navBar);
        
        // Create blur wrapper (same pattern as message input)
        final xyz.illuminate.ui.Components.blur3.DownscaleScrollableNoiseSuppressor noiseSuppressor =
            new xyz.illuminate.ui.Components.blur3.DownscaleScrollableNoiseSuppressor();
        
        final android.graphics.Paint paintStrokeTop = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        paintStrokeTop.setStyle(android.graphics.Paint.Style.STROKE);
        paintStrokeTop.setStrokeWidth(AndroidUtilities.dpf2(1));
        paintStrokeTop.setColor(0x60FFFFFF);
        
        final android.graphics.Paint paintStrokeBottom = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        paintStrokeBottom.setStyle(android.graphics.Paint.Style.STROKE);
        paintStrokeBottom.setStrokeWidth(AndroidUtilities.dpf2(2 / 3f));
        paintStrokeBottom.setColor(0x40000000);
        
        final android.graphics.Paint paintShadow = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        paintShadow.setColor(0x30000000);
        
        final float cornerRadius = AndroidUtilities.dp(50);
        final float[] radii = new float[8];
        java.util.Arrays.fill(radii, cornerRadius);
        
        FrameLayout blurWrapper = new FrameLayout(navBar.getContext()) {
            private boolean isRecording = false;
            private final android.graphics.Path clipPath = new android.graphics.Path();
            private final android.graphics.RectF clipRect = new android.graphics.RectF();
            
            @Override
            protected void dispatchDraw(Canvas canvas) {
                if (!canvas.isHardwareAccelerated()) {
                    super.dispatchDraw(canvas);
                    return;
                }
                
                if (isRecording) {
                    return;
                }

                if (noiseSuppressor.getRenderNodesCount() == 0) {
                    java.util.ArrayList<android.graphics.RectF> positions = new java.util.ArrayList<>();
                    positions.add(new android.graphics.RectF(0, 0, getWidth(), getHeight()));
                    noiseSuppressor.setupRenderNodes(positions, 1);
                }

                    isRecording = true;
                try {
                    android.graphics.RecordingCanvas recordingCanvas = noiseSuppressor.beginRecordingRect(0);
                    recordingCanvas.save();
                    recordingCanvas.translate(-getLeft(), -getTop());
                    View parentView = (View) getParent();
                    if (parentView != null) {
                        int oldVisibility = getVisibility();
                        setVisibility(View.INVISIBLE);
                        parentView.draw(recordingCanvas);
                        setVisibility(oldVisibility);
                    }
                    recordingCanvas.restore();
                    noiseSuppressor.endRecordingRect();
                } catch (Exception e) {
                    android.util.Log.e("CommentsActivity", "Error recording blur", e);
                } finally {
                    isRecording = false;
                }

                int width = getWidth();
                int height = getHeight();
                clipRect.set(0, 0, width, height);
                clipPath.reset();
                clipPath.addRoundRect(clipRect, radii, android.graphics.Path.Direction.CW);
                canvas.save();
                canvas.clipPath(clipPath);

                noiseSuppressor.invalidateResultRenderNodes(width, height);
                noiseSuppressor.draw(canvas, xyz.illuminate.ui.Components.blur3.DownscaleScrollableNoiseSuppressor.DRAW_FROSTED_GLASS);

                // Draw semi-transparent background - 30% opacity same as message input
                int backgroundColor = Theme.getColor(Theme.key_windowBackgroundWhite);
                canvas.drawColor(Theme.multAlpha(backgroundColor, 0.30f));
                
                // Draw glass strokes using helper method
                xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawable.drawStroke(
                    canvas, 0, 0, width, height, radii,
                    AndroidUtilities.dpf2(1), true, paintStrokeTop
                );
                
                xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawable.drawStroke(
                    canvas, 0, 0, width, height, radii,
                    AndroidUtilities.dpf2(2 / 3f), false, paintStrokeBottom
                );

                super.dispatchDraw(canvas);
                canvas.restore();
            }
        };

        blurWrapper.setWillNotDraw(false);
        blurWrapper.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        blurWrapper.setPadding(
            AndroidUtilities.dp(5),
            AndroidUtilities.dp(5),
            AndroidUtilities.dp(5),
            AndroidUtilities.dp(5)
        );
        
        blurWrapper.addView(navBar, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        
        parent.addView(blurWrapper, index, originalParams);
        
        // Mark blur as applied
        navigationBarBlurApplied = true;
    }
    
    /**
     * Handles the result from image picker
     */
    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                sendImageComment(imageUri);
            }
        }
    }
    
    /**
     * Sends a comment with an image
     */
    private void sendImageComment(Uri imageUri) {
        try {
            HashMap<String, String> draft = new HashMap<>();
            draft.put("message", ""); // Empty message for image-only comments
            draft.put("type", String.valueOf(2)); // Type 2 for image comments
            draft.put("imageUri", imageUri.toString());
            
            comments.add(draft);
            listView.setAdapter(new CommentsAdapter(this, comments));
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            
            // Scroll to the new comment
            listView.smoothScrollToPosition(comments.size() - 1);
            
            AndroidUtilities.showToast("Image added to comments");
        } catch (Exception e) {
            android.util.Log.e("CommentsActivity", "Error sending image comment", e);
            AndroidUtilities.showToast("Failed to add image");
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
     * Demonstrates the liquid glass effect with blur3 system from Telegram
     * This creates a beautiful frosted glass effect with dynamic blur
     */
    public void demonstrateBlur3Component(Context context) {
        android.util.Log.d("CommentsActivity", "Demonstrating liquid glass effect with blur3 system");

        // Remove old glass panel if exists
        if (activeBlurContainer != null && activeBlurContainer.getParent() != null) {
            ((ViewGroup) activeBlurContainer.getParent()).removeView(activeBlurContainer);
            activeBlurContainer = null;
        }

        // Make sure we have some content in the ListView to blur
        if (comments.isEmpty()) {
            addSampleComments();
        }

        // Liquid glass effect (Android 12+ for blur, Android 13+ for liquid glass shader)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            createLiquidGlassEffect(context);
            AndroidUtilities.showToast("Liquid glass effect enabled!");
        } else {
            AndroidUtilities.showToast("Blur requires Android 12+ (API 31)");
        }
    }

    /**
     * Creates a beautiful liquid glass effect using the blur3 system from Telegram
     * This demonstrates the full power of the ported blur3 components
     */
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.S)
    private void createLiquidGlassEffect(Context context) {
        android.util.Log.d("CommentsActivity", "Creating liquid glass effect with blur3 system");

        // Step 1: Create the blur source from the content view
        xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceColor fallbackSource = 
            new xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceColor();
        fallbackSource.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        
        xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode blurSource = 
            new xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode(fallbackSource);

        // Step 2: Create the drawable that will render the blurred background
        xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawableRenderNode blurDrawable = 
            (xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawableRenderNode) blurSource.createDrawable();
        
        // Step 3: Enable liquid glass effect if on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            blurDrawable.setLiquidGlassEffectAllowed();
            android.util.Log.d("CommentsActivity", "Liquid glass shader enabled!");
        }

        // Step 4: Configure the blur drawable
        blurDrawable.setRadius(AndroidUtilities.dp(16)); // Rounded corners
        blurDrawable.setThickness(AndroidUtilities.dp(11)); // Glass thickness
        blurDrawable.setIntensity(0.75f); // Refraction intensity
        
        // Step 5: Set color provider for theming
        blurDrawable.setColorProvider(new xyz.illuminate.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider() {
            @Override
            public int getBackgroundColor() {
                // Semi-transparent white for glass effect
                return 0x40FFFFFF;
            }

            @Override
            public int getShadowColor() {
                // Subtle shadow
                return 0x20000000;
            }

            @Override
            public int getStrokeColorTop() {
                // Light top stroke for glass reflection
                return 0x30FFFFFF;
            }

            @Override
            public int getStrokeColorBottom() {
                // Darker bottom stroke
                return 0x20000000;
            }
        });

        // Step 6: Create a view to hold the glass effect
        FrameLayout glassContainer = new FrameLayout(context) {
            private boolean isRecording = false;
            
            @Override
            protected void dispatchDraw(Canvas canvas) {
                if (!canvas.isHardwareAccelerated()) {
                    super.dispatchDraw(canvas);
                    return;
                }
                
                // Prevent recursive drawing during recording
                if (isRecording) {
                    return;
                }
                
                // First, capture the background content into the blur source
                if (blurSource.needUpdateDisplayList(getWidth(), getHeight())) {
                    isRecording = true;
                    android.graphics.RecordingCanvas recordingCanvas = blurSource.beginRecording(getWidth(), getHeight());
                    
                    // Draw the content behind this view (ListView)
                    recordingCanvas.save();
                    // Translate by both X and Y to capture exact background position
                    recordingCanvas.translate(-getLeft(), -getTop());
                    View parent = (View) getParent();
                    if (parent != null) {
                        // Temporarily hide this view to avoid recursive drawing
                        int oldVisibility = getVisibility();
                        setVisibility(View.INVISIBLE);
                        parent.draw(recordingCanvas);
                        setVisibility(oldVisibility);
                    }
                    recordingCanvas.restore();
                    
                    blurSource.endRecording();
                    isRecording = false;
                }

                // Set bounds for the drawable
                blurDrawable.setBounds(0, 0, getWidth(), getHeight());
                blurDrawable.updateColors();
                
                // Draw the blurred background
                blurDrawable.draw(canvas);
                
                // Draw children on top
                super.dispatchDraw(canvas);
            }
        };

        glassContainer.setWillNotDraw(false);
        glassContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // Step 7: Add content to the glass container
        android.widget.TextView titleView = new android.widget.TextView(context);
        titleView.setText("Liquid Glass Effect");
        titleView.setTextColor(0xFF000000);
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(20);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setShadowLayer(4, 0, 0, Color.WHITE);
        
        android.widget.TextView descView = new android.widget.TextView(context);
        descView.setText("Using blur3 system from Telegram\n" +
                       (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? 
                        "with custom AGSL shader" : "with standard blur"));
        descView.setTextColor(0xFF666666);
        descView.setGravity(Gravity.CENTER);
        descView.setTextSize(12);
        descView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(8), AndroidUtilities.dp(16), 0);

        LinearLayout textContainer = new LinearLayout(context);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setGravity(Gravity.CENTER);
        textContainer.addView(titleView);
        textContainer.addView(descView);

        glassContainer.addView(textContainer, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER
        ));

        // Step 8: Add to content view
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            AndroidUtilities.dp(300), 
            AndroidUtilities.dp(180)
        );
        params.gravity = Gravity.CENTER;
        contentView.addView(glassContainer, params);

        // Step 9: Animate the glass effect
        glassContainer.setScaleX(0.8f);
        glassContainer.setScaleY(0.8f);
        glassContainer.setAlpha(0f);
        glassContainer.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(300)
            .start();

        activeBlurContainer = glassContainer;
        android.util.Log.d("CommentsActivity", "Liquid glass effect created successfully!");
    }

    /**
     * Alternative implementation using DownscaleScrollableNoiseSuppressor
     * This is the advanced multi-layer blur system used in Telegram
     */
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.S)
    private void createAdvancedLiquidGlassEffect(Context context) {
        // Remove old glass panel if exists
        if (activeBlurContainer != null && activeBlurContainer.getParent() != null) {
            ((ViewGroup) activeBlurContainer.getParent()).removeView(activeBlurContainer);
            activeBlurContainer = null;
        }

        // Create the downscale suppressor with liquid glass enabled
        xyz.illuminate.ui.Components.blur3.DownscaleScrollableNoiseSuppressor noiseSuppressor =
            new xyz.illuminate.ui.Components.blur3.DownscaleScrollableNoiseSuppressor();

        // Create container
        FrameLayout advancedGlassContainer = new FrameLayout(context) {
            private boolean isRecording = false;
            
            @Override
            protected void dispatchDraw(Canvas canvas) {
                if (!canvas.isHardwareAccelerated()) {
                    super.dispatchDraw(canvas);
                    return;
                }
                
                // Prevent recursive drawing during recording
                if (isRecording) {
                    return;
                }

                // Setup render nodes if needed
                if (noiseSuppressor.getRenderNodesCount() == 0) {
                    java.util.ArrayList<android.graphics.RectF> positions = new java.util.ArrayList<>();
                    positions.add(new android.graphics.RectF(0, 0, getWidth(), getHeight()));
                    noiseSuppressor.setupRenderNodes(positions, 1);
                }

                // Record content to blur
                isRecording = true;
                android.graphics.RecordingCanvas recordingCanvas = noiseSuppressor.beginRecordingRect(0);
                recordingCanvas.save();
                // Translate by both X and Y to capture exact background position
                recordingCanvas.translate(-getLeft(), -getTop());
                View parent = (View) getParent();
                if (parent != null) {
                    // Temporarily hide this view to avoid recursive drawing
                    int oldVisibility = getVisibility();
                    setVisibility(View.INVISIBLE);
                    parent.draw(recordingCanvas);
                    setVisibility(oldVisibility);
                }
                recordingCanvas.restore();
                noiseSuppressor.endRecordingRect();
                isRecording = false;

                // Invalidate and draw
                noiseSuppressor.invalidateResultRenderNodes(getWidth(), getHeight());
                noiseSuppressor.draw(canvas, xyz.illuminate.ui.Components.blur3.DownscaleScrollableNoiseSuppressor.DRAW_GLASS);

                super.dispatchDraw(canvas);
            }
        };

        advancedGlassContainer.setWillNotDraw(false);
        advancedGlassContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        android.widget.TextView label = new android.widget.TextView(context);
        label.setText("Advanced Liquid Glass\nwith Noise Suppression");
        label.setTextColor(0xFF000000);
        label.setGravity(Gravity.CENTER);
        label.setTextSize(16);
        label.setTypeface(null, android.graphics.Typeface.BOLD);

        advancedGlassContainer.addView(label, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER
        ));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            AndroidUtilities.dp(280), 
            AndroidUtilities.dp(140)
        );
        params.gravity = Gravity.CENTER;
        contentView.addView(advancedGlassContainer, params);
    }

    /**
     * Premium implementation combining liquid glass shader with enhanced blur
     * This creates the most premium glass effect with both refraction and strong blur
     */
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.S)
    private void createCombinedLiquidGlassWithBlur(Context context) {
        android.util.Log.d("CommentsActivity", "Creating COMBINED liquid glass + enhanced blur");

        // Remove old glass panel if exists
        if (activeBlurContainer != null && activeBlurContainer.getParent() != null) {
            ((ViewGroup) activeBlurContainer.getParent()).removeView(activeBlurContainer);
            activeBlurContainer = null;
        }

        // Step 1: Create blur source with fallback
        xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceColor fallbackSource = 
            new xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceColor();
        fallbackSource.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        
        xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode blurSource = 
            new xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode(fallbackSource);
        
        // Step 2: Apply additional blur to the source itself (Android 12+)
        blurSource.setBlur(30f); // Pre-blur before liquid glass shader
        
        // Step 3: Create drawable
        xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawableRenderNode blurDrawable = 
            (xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawableRenderNode) blurSource.createDrawable();
        
        // Step 4: Enable liquid glass shader on top of blur (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            blurDrawable.setLiquidGlassEffectAllowed();
            android.util.Log.d("CommentsActivity", "Premium: Liquid glass shader + blur!");
        }

        // Step 5: Configure premium glass properties
        blurDrawable.setRadius(AndroidUtilities.dp(20)); // Larger corners
        blurDrawable.setThickness(AndroidUtilities.dp(15)); // Thicker glass
        blurDrawable.setIntensity(0.9f); // Higher refraction intensity
        
        // Step 6: Premium color provider with gradient-like effect
        blurDrawable.setColorProvider(new xyz.illuminate.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider() {
            @Override
            public int getBackgroundColor() {
                // Very subtle white tint to let blur show through
                return 0x30FFFFFF;
            }

            @Override
            public int getShadowColor() {
                // Stronger shadow for depth
                return 0x40000000;
            }

            @Override
            public int getStrokeColorTop() {
                // Bright top highlight for glass shine
                return 0x50FFFFFF;
            }

            @Override
            public int getStrokeColorBottom() {
                // Darker bottom for depth
                return 0x30000000;
            }
        });

        // Step 7: Create premium container
        FrameLayout premiumGlassContainer = new FrameLayout(context) {
            private boolean isRecording = false;
            
            @Override
            protected void dispatchDraw(Canvas canvas) {
                if (!canvas.isHardwareAccelerated()) {
                    super.dispatchDraw(canvas);
                    return;
                }
                
                if (isRecording) {
                    return;
                }
                
                // Record and blur background
                if (blurSource.needUpdateDisplayList(getWidth(), getHeight())) {
                    isRecording = true;
                    android.graphics.RecordingCanvas recordingCanvas = blurSource.beginRecording(getWidth(), getHeight());
                    
                    recordingCanvas.save();
                    // Translate by both X and Y to capture exact background position
                    recordingCanvas.translate(-getLeft(), -getTop());
                    View parent = (View) getParent();
                    if (parent != null) {
                        int oldVisibility = getVisibility();
                        setVisibility(View.INVISIBLE);
                        parent.draw(recordingCanvas);
                        setVisibility(oldVisibility);
                    }
                    recordingCanvas.restore();
                    
                    blurSource.endRecording();
                    isRecording = false;
                }

                blurDrawable.setBounds(0, 0, getWidth(), getHeight());
                blurDrawable.updateColors();
                blurDrawable.draw(canvas);
                
                super.dispatchDraw(canvas);
            }
        };

        premiumGlassContainer.setWillNotDraw(false);
        premiumGlassContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        premiumGlassContainer.setElevation(AndroidUtilities.dp(16)); // Higher elevation

        // Step 8: Premium content
        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setGravity(Gravity.CENTER);
        contentLayout.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(16), AndroidUtilities.dp(20), AndroidUtilities.dp(16));

        android.widget.TextView titleView = new android.widget.TextView(context);
        titleView.setText("Glass");
        titleView.setTextColor(0xFF000000);
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(22);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setShadowLayer(6, 0, 0, Color.WHITE);
        
        android.widget.TextView descView = new android.widget.TextView(context);
        descView.setText("Liquid Glass Shader + Enhanced Blur\n" +
                       (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? 
                        "AGSL Refraction + RenderEffect Blur " : 
                        "Enhanced Blur Effect"));
        descView.setTextColor(0xFF555555);
        descView.setGravity(Gravity.CENTER);
        descView.setTextSize(13);
        descView.setPadding(0, AndroidUtilities.dp(8), 0, 0);

        contentLayout.addView(titleView);
        contentLayout.addView(descView);
        
        premiumGlassContainer.addView(contentLayout, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER
        ));

        // Step 9: Add with premium animation
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            AndroidUtilities.dp(320), 
            AndroidUtilities.dp(200)
        );
        params.gravity = Gravity.CENTER;
        params.topMargin = AndroidUtilities.dp(-50); // Offset upwards
        contentView.addView(premiumGlassContainer, params);

        // Premium entrance animation
        premiumGlassContainer.setScaleX(0.7f);
        premiumGlassContainer.setScaleY(0.7f);
        premiumGlassContainer.setAlpha(0f);
        premiumGlassContainer.setRotation(-5f);
        premiumGlassContainer.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .rotation(0f)
            .setDuration(400)
            .setInterpolator(new android.view.animation.OvershootInterpolator(1.2f))
            .start();

        activeBlurContainer = premiumGlassContainer;
        android.util.Log.d("CommentsActivity", "Premium combined glass effect created!");
    }

    /**
     * Creates a blur effect with prominent glass strokes/borders
     * Emphasizes the top highlight and bottom shadow strokes for a framed glass look
     */
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.S)
    private void createBlurWithGlassStroke(Context context) {
        android.util.Log.d("CommentsActivity", "Creating blur with prominent glass strokes");

        // Remove old glass panel if exists
        if (activeBlurContainer != null && activeBlurContainer.getParent() != null) {
            ((ViewGroup) activeBlurContainer.getParent()).removeView(activeBlurContainer);
            activeBlurContainer = null;
        }

        // Step 1: Create blur source
        xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceColor fallbackSource = 
            new xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceColor();
        fallbackSource.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        
        xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode blurSource = 
            new xyz.illuminate.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode(fallbackSource);
        
        // Apply moderate blur
        blurSource.setBlur(20f);
        
        // Step 2: Create drawable
        xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawableRenderNode blurDrawable = 
            (xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawableRenderNode) blurSource.createDrawable();
        
        // Step 3: Configure for stroke emphasis
        blurDrawable.setRadius(AndroidUtilities.dp(16));
        
        // Step 4: Color provider with PROMINENT strokes
        blurDrawable.setColorProvider(new xyz.illuminate.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider() {
            @Override
            public int getBackgroundColor() {
                // More transparent to let blur show
                return 0x25FFFFFF;
            }

            @Override
            public int getShadowColor() {
                // Strong shadow
                return 0x60000000;
            }

            @Override
            public int getStrokeColorTop() {
                // BRIGHT white top stroke - the star of the show!
                return 0xAAFFFFFF;
            }

            @Override
            public int getStrokeColorBottom() {
                // Strong dark bottom stroke for contrast
                return 0x80000000;
            }
        });

        // Step 5: Create container with stroke emphasis
        FrameLayout strokeGlassContainer = new FrameLayout(context) {
            private boolean isRecording = false;
            
            @Override
            protected void dispatchDraw(Canvas canvas) {
                if (!canvas.isHardwareAccelerated()) {
                    super.dispatchDraw(canvas);
                    return;
                }
                
                if (isRecording) {
                    return;
                }
                
                // Record background
                if (blurSource.needUpdateDisplayList(getWidth(), getHeight())) {
                    isRecording = true;
                    android.graphics.RecordingCanvas recordingCanvas = blurSource.beginRecording(getWidth(), getHeight());
                    
                    recordingCanvas.save();
                    // Translate by both X and Y to capture exact background position
                    recordingCanvas.translate(-getLeft(), -getTop());
                    View parent = (View) getParent();
                    if (parent != null) {
                        int oldVisibility = getVisibility();
                        setVisibility(View.INVISIBLE);
                        parent.draw(recordingCanvas);
                        setVisibility(oldVisibility);
                    }
                    recordingCanvas.restore();
                    
                    blurSource.endRecording();
                    isRecording = false;
                }

                blurDrawable.setBounds(0, 0, getWidth(), getHeight());
                blurDrawable.updateColors();
                blurDrawable.draw(canvas);
                
                super.dispatchDraw(canvas);
            }
        };

        strokeGlassContainer.setWillNotDraw(false);
        strokeGlassContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        strokeGlassContainer.setElevation(AndroidUtilities.dp(12));

        // Step 6: Content that emphasizes the frame
        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setGravity(Gravity.CENTER);
        contentLayout.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(12), AndroidUtilities.dp(16), AndroidUtilities.dp(12));

        android.widget.TextView titleView = new android.widget.TextView(context);
        titleView.setText("Glass Frame");
        titleView.setTextColor(0xFF000000);
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(20);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setShadowLayer(4, 0, 0, Color.WHITE);
        
        android.widget.TextView descView = new android.widget.TextView(context);
        descView.setText("Blur with Prominent Strokes\nBright Top | Dark Bottom");
        descView.setTextColor(0xFF666666);
        descView.setGravity(Gravity.CENTER);
        descView.setTextSize(12);
        descView.setPadding(0, AndroidUtilities.dp(8), 0, 0);

        contentLayout.addView(titleView);
        contentLayout.addView(descView);
        
        strokeGlassContainer.addView(contentLayout, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER
        ));

        // Step 7: Position and add
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            AndroidUtilities.dp(300), 
            AndroidUtilities.dp(160)
        );
        params.gravity = Gravity.CENTER;
        params.topMargin = AndroidUtilities.dp(50); // Offset downwards
        contentView.addView(strokeGlassContainer, params);

        // Entrance animation with bounce
        strokeGlassContainer.setScaleX(0.5f);
        strokeGlassContainer.setScaleY(0.5f);
        strokeGlassContainer.setAlpha(0f);
        strokeGlassContainer.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(350)
            .setInterpolator(new android.view.animation.BounceInterpolator())
            .start();

        activeBlurContainer = strokeGlassContainer;
        android.util.Log.d("CommentsActivity", "Blur with glass strokes created!");
    }


    /**
     * Adds sample comments to the ListView for demonstration
     */
    private void addSampleComments() {
        String[] sampleMessages = {
            "This is a **bold message** with blur effect",
            "This is an __italic message__ under glass",
            "This is a ||spoiler message|| with frosted glass",
            "This is ~~strikethrough text~~ demonstrating blur",
            "This is `code text` with liquid glass",
            "Normal text message with beautiful blur background",
            "Another **bold** and __italic__ message showing glass effect",
            "Final test message with formatting and liquid glass",
            "The blur3 system provides smooth, efficient blur",
            "It uses RenderNode and RenderEffect for performance"
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
     * Creates Telegram-style frosted glass blur effect for the message input field.
     * This applies a beautiful blur to the comment input container at the bottom.
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void createMessageInputBlur(Context context, View rootView) {
        // Find the message input container (already floating in layout)
        final LinearLayout messageContainer = rootView.findViewById(R.id.lin_shadow);
        if (messageContainer == null) return;
        
        // Create the downscale suppressor for smooth blur (like Telegram uses)
        final xyz.illuminate.ui.Components.blur3.DownscaleScrollableNoiseSuppressor noiseSuppressor =
            new xyz.illuminate.ui.Components.blur3.DownscaleScrollableNoiseSuppressor();
        
        // Create paints for glass strokes
        final android.graphics.Paint paintStrokeTop = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        paintStrokeTop.setStyle(android.graphics.Paint.Style.STROKE);
        paintStrokeTop.setStrokeWidth(AndroidUtilities.dpf2(1));
        paintStrokeTop.setColor(0x60FFFFFF); // Bright white highlight
        
        final android.graphics.Paint paintStrokeBottom = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        paintStrokeBottom.setStyle(android.graphics.Paint.Style.STROKE);
        paintStrokeBottom.setStrokeWidth(AndroidUtilities.dpf2(2 / 3f));
        paintStrokeBottom.setColor(0x40000000); // Subtle shadow
        
        final android.graphics.Paint paintShadow = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        paintShadow.setColor(0x30000000);
        
        final float cornerRadius = AndroidUtilities.dp(24);
        final float[] radii = new float[8];
        java.util.Arrays.fill(radii, cornerRadius);
        
        // Set blur as background drawable instead of wrapping - this doesn't interfere with EditText
        android.graphics.drawable.Drawable blurDrawable = new android.graphics.drawable.Drawable() {
            private boolean isRecording = false;
            private final android.graphics.Path clipPath = new android.graphics.Path();
            private final android.graphics.RectF clipRect = new android.graphics.RectF();
            
            @Override
            public void draw(Canvas canvas) {
                if (!canvas.isHardwareAccelerated()) {
                    return;
                }
                
                if (isRecording) {
                    return;
                }

                // Setup render nodes if needed
                if (noiseSuppressor.getRenderNodesCount() == 0) {
                    java.util.ArrayList<android.graphics.RectF> positions = new java.util.ArrayList<>();
                    android.graphics.Rect bounds = getBounds();
                    positions.add(new android.graphics.RectF(bounds));
                    noiseSuppressor.setupRenderNodes(positions, 1);
                }

                // Record content to blur
                isRecording = true;
                try {
                    android.graphics.RecordingCanvas recordingCanvas = noiseSuppressor.beginRecordingRect(0);
                    recordingCanvas.save();
                    
                    // Translate to capture background behind message container
                    int[] location = new int[2];
                    messageContainer.getLocationOnScreen(location);
                    recordingCanvas.translate(-messageContainer.getLeft(), -messageContainer.getTop());
                    
                    // Draw parent to capture the list content
                    View parentView = (View) messageContainer.getParent();
                    if (parentView != null) {
                        int oldVisibility = messageContainer.getVisibility();
                        messageContainer.setVisibility(View.INVISIBLE);
                        parentView.draw(recordingCanvas);
                        messageContainer.setVisibility(oldVisibility);
                    }
                    
                    recordingCanvas.restore();
                    noiseSuppressor.endRecordingRect();
                } catch (Exception e) {
                    android.util.Log.e("CommentsActivity", "Error recording blur", e);
                } finally {
                    isRecording = false;
                }

                // Clip canvas to rounded rectangle shape
                android.graphics.Rect bounds = getBounds();
                clipRect.set(bounds);
                clipPath.reset();
                clipPath.addRoundRect(clipRect, radii, android.graphics.Path.Direction.CW);
                canvas.save();
                canvas.clipPath(clipPath);

                // Draw the blurred result
                noiseSuppressor.invalidateResultRenderNodes(bounds.width(), bounds.height());
                noiseSuppressor.draw(canvas, xyz.illuminate.ui.Components.blur3.DownscaleScrollableNoiseSuppressor.DRAW_FROSTED_GLASS);

                // Draw semi-transparent background
                int backgroundColor = Theme.getColor(Theme.key_windowBackgroundWhite);
                canvas.drawColor(Theme.multAlpha(backgroundColor, 0.30f));
                
                // Draw glass strokes
                xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawable.drawStroke(
                    canvas, 0, 0, bounds.width(), bounds.height(), radii,
                    AndroidUtilities.dpf2(1), true, paintStrokeTop
                );
                
                xyz.illuminate.ui.Components.blur3.drawable.BlurredBackgroundDrawable.drawStroke(
                    canvas, 0, 0, bounds.width(), bounds.height(), radii,
                    AndroidUtilities.dpf2(2 / 3f), false, paintStrokeBottom
                );

                canvas.restore();
            }
            
            @Override
            public void setAlpha(int alpha) {}
            
            @Override
            public void setColorFilter(android.graphics.ColorFilter colorFilter) {}
            
            @Override
            public int getOpacity() {
                return android.graphics.PixelFormat.TRANSLUCENT;
            }
        };
        
        messageContainer.setBackground(blurDrawable);
        messageContainer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        
        // Make inner views transparent
        View linearComments = messageContainer.findViewById(R.id.linear_comments);
        if (linearComments != null) {
            linearComments.setBackgroundColor(Color.TRANSPARENT);
        }
        
        // Update blur when scrolling
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                messageContainer.invalidate();
            }
        });
        
        android.util.Log.d("CommentsActivity", "Telegram-style blur applied as background drawable!");
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        
        // Glass stroke buttons - they need to invalidate on theme change to update blur colors
        if (blurTestButton != null) {
            themeDescriptions.add(new ThemeDescription(blurTestButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_buttonBackgroundBlue));
        }
        if (frostedGlassButton != null) {
            themeDescriptions.add(new ThemeDescription(frostedGlassButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_buttonBackgroundBlue));
        }
        if (combinedBlurButton != null) {
            themeDescriptions.add(new ThemeDescription(combinedBlurButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_buttonBackgroundBlue));
        }
        if (glassStrokeButton != null) {
            themeDescriptions.add(new ThemeDescription(glassStrokeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_buttonBackgroundBlue));
        }
        
        return themeDescriptions;
    }
}
