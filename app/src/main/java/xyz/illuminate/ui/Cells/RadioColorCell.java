package xyz.illuminate.ui.Cells;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.LocaleController;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Components.LayoutHelper;
import xyz.illuminate.ui.Components.RadioButton;


public class RadioColorCell extends FrameLayout {

    private final TextView textView;
    private final RadioButton radioButton;

    public RadioColorCell(Context context) {
        super(context);

        radioButton = new RadioButton(context);
        radioButton.setSize(AndroidUtilities.dp(20));
        radioButton.setColor(Theme.getColor(Theme.key_dialogRadioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
        addView(radioButton, LayoutHelper.createFrame(22, 22, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, (LocaleController.isRTL ? 0 : 18), 14, (LocaleController.isRTL ? 18 : 0), 0));

        textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, (LocaleController.isRTL ? 21 : 51), 13, (LocaleController.isRTL ? 51 : 21), 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50), MeasureSpec.EXACTLY));
    }

    public void setCheckColor(int color1, int color2) {
        radioButton.setColor(color1, color2);
    }

    public void setTextAndValue(String text, boolean checked) {
        textView.setText(text);
        radioButton.setChecked(checked, false);
    }

    public void setChecked(boolean checked, boolean animated) {
        radioButton.setChecked(checked, animated);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.RadioButton");
        info.setCheckable(true);
        info.setChecked(radioButton.isChecked());
    }
}
