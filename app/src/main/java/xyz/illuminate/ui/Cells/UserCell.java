package xyz.illuminate.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.LocaleController;
import xyz.illuminate.app.NotificationCenter;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.ActionBar.SimpleTextView;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Components.CheckBox;
import xyz.illuminate.ui.Components.CheckBoxSquare;
import xyz.illuminate.ui.Components.LayoutHelper;
import xyz.illuminate.ui.Components.SpanTextView;
import xyz.illuminate.ui.Models.ItemModal;

public class UserCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {

    private View avatarImageView;
    private SimpleTextView nameTextView;
    private SpanTextView statusTextView;
    private ImageView imageView;
    private CheckBox checkBox;
    private CheckBoxSquare checkBoxBig;
    private TextView adminTextView;
    private TextView addButton;

    private CharSequence currentName;
    private CharSequence currentStatus;

    private String lastName;
    private int lastStatus;

    private int statusColor;
    private int statusOnlineColor;

    private boolean needDivider=true;

    public UserCell(Context context, int padding, int checkbox, boolean admin) {
        this(context, padding, checkbox, admin, false);
    }

    public UserCell(Context context, int padding, int checkbox, boolean admin, boolean needAddButton) {
        super(context);

        int additionalPadding;
      //  if (needAddButton) {
            addButton = new TextView(context);
            addButton.setGravity(Gravity.CENTER);
            addButton.setTextColor(Color.WHITE);
            addButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            addButton.setBackgroundDrawable(Theme.AdaptiveRipple.filledRect(Color.parseColor("#4da0eb"), 4));
            addButton.setText("Preview");
            addButton.setPadding(AndroidUtilities.dp(17), 0, AndroidUtilities.dp(17), 0);
            addView(addButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 28, Gravity.TOP | (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT), LocaleController.isRTL ? 14 : 0, 15, LocaleController.isRTL ? 0 : 14, 0));
            additionalPadding = (int) Math.ceil((addButton.getPaint().measureText(addButton.getText().toString()) + AndroidUtilities.dp(34 + 14)) / AndroidUtilities.density);
      //  } else {
       //     additionalPadding = 0;
     //   }

        statusColor = Color.parseColor("#838C96");
        statusOnlineColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText);

        avatarImageView = new View(context);
        avatarImageView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(50),Color.parseColor("#118df5")));
        addView(avatarImageView, LayoutHelper.createFrame(46, 46, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, LocaleController.isRTL ? 0 : 7 + padding, 6, LocaleController.isRTL ? 7 + padding : 0, 0));

        nameTextView = new SimpleTextView(context);
        nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        nameTextView.setTextSize(16);
        nameTextView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 20, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, LocaleController.isRTL ? 28 + (checkbox == 2 ? 18 : 0) + additionalPadding : (64 + padding), 10, LocaleController.isRTL ? (64 + padding) : 28 + (checkbox == 2 ? 18 : 0) + additionalPadding, 0));

        statusTextView = new SpanTextView(context);
        statusTextView.setTextSize(15);
		statusTextView.setTextColor(statusColor);
        statusTextView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(statusTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 20, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, LocaleController.isRTL ? 28 + additionalPadding : (64 + padding), 32, LocaleController.isRTL ? (64 + padding) : 28 + additionalPadding, 0));

        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
        imageView.setVisibility(GONE);
        addView(imageView, LayoutHelper.createFrame(LayoutParams.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, LocaleController.isRTL ? 0 : 16, 0, LocaleController.isRTL ? 16 : 0, 0));

        if (checkbox == 2) {
            checkBoxBig = new CheckBoxSquare(context, false);
            addView(checkBoxBig, LayoutHelper.createFrame(18, 18, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, LocaleController.isRTL ? 19 : 0, 0, LocaleController.isRTL ? 0 : 19, 0));
        } else if (checkbox == 1) {
            checkBox = new CheckBox(context, R.drawable.round_check2);
            checkBox.setVisibility(INVISIBLE);
            checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
            addView(checkBox, LayoutHelper.createFrame(22, 22, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, LocaleController.isRTL ? 0 : 37 + padding, 40, LocaleController.isRTL ? 37 + padding : 0, 0));
        }

        if (admin) {
            adminTextView = new TextView(context);
            adminTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            adminTextView.setTextColor(Color.parseColor("#118df5"));			
            addView(adminTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP, LocaleController.isRTL ? 23 : 0, 10, LocaleController.isRTL ? 0 : 23, 0));
        }

        setFocusable(true);
		setWillNotDraw(!needDivider);
    }

    public void setAvatarPadding(int padding) {
        LayoutParams layoutParams = (LayoutParams) avatarImageView.getLayoutParams();
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0 : 7 + padding);
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 7 + padding : 0);
        avatarImageView.setLayoutParams(layoutParams);

        layoutParams = (LayoutParams) nameTextView.getLayoutParams();
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 28 + (checkBoxBig != null ? 18 : 0) : (64 + padding));
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? (64 + padding) : 28 + (checkBoxBig != null ? 18 : 0));

        layoutParams = (LayoutParams) statusTextView.getLayoutParams();
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 28 : (64 + padding));
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? (64 + padding) : 28);

        if (checkBox != null) {
            layoutParams = (LayoutParams) checkBox.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0 : 37 + padding);
            layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 37 + padding : 0);
        }
    }

    public void setAddButtonVisible(boolean value) {
        if (addButton == null) {
            return;
        }
        addButton.setVisibility(value ? VISIBLE : GONE);
    }

    public void setRole(String role) {
        if (adminTextView == null) {
            return;
        }
        adminTextView.setVisibility(role != null ? VISIBLE : GONE);
        adminTextView.setText(role);
        if (role != null) {
            CharSequence text = adminTextView.getText();
            int size = (int) Math.ceil(adminTextView.getPaint().measureText(text, 0, text.length()));
            nameTextView.setPadding(LocaleController.isRTL ? size + AndroidUtilities.dp(6) : 0, 0, !LocaleController.isRTL ? size + AndroidUtilities.dp(6) : 0, 0);
        } else {
            nameTextView.setPadding(0, 0, 0, 0);
        }
    }

    public CharSequence getName() {
        return nameTextView.getText();
    }

    public void setData(String name,String last, String type) {
        nameTextView.setText(name);
        statusTextView.setSpanText(last);
        setRole(type.equals("d")?"Dialog":"Toast");
    }


    public void setNameTypeface(Typeface typeface) {
        nameTextView.setTypeface(typeface);
    }

    public void setChecked(boolean checked, boolean animated) {
        if (checkBox != null) {
            if (checkBox.getVisibility() != VISIBLE) {
                checkBox.setVisibility(VISIBLE);
            }
            checkBox.setChecked(checked, animated);
        } else if (checkBoxBig != null) {
            if (checkBoxBig.getVisibility() != VISIBLE) {
                checkBoxBig.setVisibility(VISIBLE);
            }
            checkBoxBig.setChecked(checked, animated);
        }
    }

    public void setCheckDisabled(boolean disabled) {
        if (checkBoxBig != null) {
            checkBoxBig.setDisabled(disabled);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    public void setStatusColors(int color, int onlineColor) {
        statusColor = color;
        statusOnlineColor = onlineColor;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (checkBoxBig != null) {
            checkBoxBig.invalidate();
        }
    }

    public void update(int mask) {

    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0 : AndroidUtilities.dp(68), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (checkBoxBig != null && checkBoxBig.getVisibility() == VISIBLE) {
            info.setCheckable(true);
            info.setChecked(checkBoxBig.isChecked());
            info.setClassName("android.widget.CheckBox");
        } else if (checkBox != null && checkBox.getVisibility() == VISIBLE) {
            info.setCheckable(true);
            info.setChecked(checkBox.isChecked());
            info.setClassName("android.widget.CheckBox");
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiLoaded) {
            nameTextView.invalidate();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }
}
