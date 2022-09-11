package xyz.illuminate.ui.ActionBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.text.TextPaint;
import android.util.StateSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.ApplicationLoader;
import xyz.illuminate.app.NotificationCenter;
import xyz.illuminate.app.R;
import xyz.illuminate.ui.Components.CombinedDrawable;

public class Theme {

    public static final int ACTION_BAR_PHOTO_VIEWER_COLOR = 0x7f000000;
    public static final int ACTION_BAR_MEDIA_PICKER_COLOR = 0xff333333;
    public static final int ACTION_BAR_VIDEO_EDIT_COLOR = 0xff000000;
    public static final int ACTION_BAR_PLAYER_COLOR = 0xffffffff;
    public static final int ACTION_BAR_PICKER_SELECTOR_COLOR = 0xff3d3d3d;
    public static final int ACTION_BAR_WHITE_SELECTOR_COLOR = 0x40ffffff;
    public static final int ACTION_BAR_AUDIO_SELECTOR_COLOR = 0x2f000000;
    public static final int ARTICLE_VIEWER_MEDIA_PROGRESS_COLOR = 0xffffffff;


    public static final String key_progressCircle = "progressCircle";
    public static final String key_emptyListPlaceholder = "emptyListPlaceholder";
    //color keys
    public static final String key_actionBarDefault = "actionBarDefault";
    public static final String key_actionBarDefaultSelector = "actionBarDefaultSelector";
    public static final String key_actionBarWhiteSelector = "actionBarWhiteSelector";
    public static final String key_actionBarDefaultIcon = "actionBarDefaultIcon";
    public static final String key_actionBarTipBackground = "actionBarTipBackground";
    public static final String key_actionBarActionModeDefault = "actionBarActionModeDefault";
    public static final String key_actionBarActionModeDefaultTop = "actionBarActionModeDefaultTop";
    public static final String key_actionBarActionModeDefaultIcon = "actionBarActionModeDefaultIcon";
    public static final String key_actionBarActionModeDefaultSelector = "actionBarActionModeDefaultSelector";
    public static final String key_actionBarDefaultTitle = "actionBarDefaultTitle";
    public static final String key_actionBarDefaultSubtitle = "actionBarDefaultSubtitle";
    public static final String key_actionBarDefaultSubmenuItem = "actionBarDefaultSubmenuItem";
    public static final String key_actionBarDefaultSubmenuItemIcon = "actionBarDefaultSubmenuItemIcon";
    public static final String key_actionBarDefaultSubmenuBackground = "actionBarDefaultSubmenuBackground";
    public static final String key_actionBarDefaultSearch = "actionBarDefaultSearch";
    public static final String key_actionBarDefaultSearchPlaceholder = "actionBarDefaultSearchPlaceholder";

    public static final String key_actionBarTabActiveText = "actionBarTabActiveText";
    public static final String key_actionBarTabUnactiveText = "actionBarTabUnactiveText";
    public static final String key_actionBarTabLine = "actionBarTabLine";
    public static final String key_actionBarTabSelector = "actionBarTabSelector";


    public static final String key_avatar_border = "avatar_border";
    public static final String key_avatar_text = "avatar_text";
    public static final String key_avatar_backgroundSaved = "avatar_backgroundSaved";
    public static final String key_avatar_backgroundArchived = "avatar_backgroundArchived";
    public static final String key_avatar_backgroundArchivedHidden = "avatar_backgroundArchivedHidden";
    public static final String key_avatar_backgroundRed = "avatar_backgroundRed";
    public static final String key_avatar_backgroundOrange = "avatar_backgroundOrange";
    public static final String key_avatar_backgroundViolet = "avatar_backgroundViolet";
    public static final String key_avatar_backgroundGreen = "avatar_backgroundGreen";
    public static final String key_avatar_backgroundCyan = "avatar_backgroundCyan";
    public static final String key_avatar_backgroundBlue = "avatar_backgroundBlue";
    public static final String key_avatar_backgroundPink = "avatar_backgroundPink";

    public static final String key_avatar_actionBarIconBlue = "avatar_actionBarIconBlue";
    public static final String key_groupcreate_spanBackground = "groupcreate_spanBackground";
    public static final String key_fabColor = "fabColor";

    public static final String key_buttonBackgroundBlue = "buttonBackgroundBlue";
    public static final String key_buttonTextColor = "buttonTextColor";
    public static final String key_navigationBarColor = "navigationBarColor";
    public static final String key_outlineColor = "outlineColor";
    public static final String key_imageColor = "imageColor";

    public static final String key_colorPrimaryTextSurface = "colorPrimaryTextSurface";
    public static final String key_colorPrimaryTextWhite = "colorPrimaryTextWhite";

    public static final String key_drawerBackground = "drawerBackground";
    public static final String key_drawerImageColor = "drawerImageColor";
    public static final String key_drawerTextColor = "drawerTextColor";
    public static final String key_drawerHeaderBackground = "drawerHeaderBackground";
    public static final String key_drawerHeaderStroke = "drawerHeaderStroke";

    public static final String key_seekbarColor = "seekbarColor";
    public static final String key_seekbarBackground = "seekbarBackground";

    public static final String key_listSelector = "listSelectorSDK21";
    public static final String key_windowBackgroundWhite = "windowBackgroundWhite";
    public static final String key_windowBackgroundGray = "windowBackgroundGray";
    public static final String key_windowBackgroundWhiteBlackText = "windowBackgroundWhiteBlackText";
    public static final String key_windowBackgroundWhiteGrayText2 = "windowBackgroundWhiteGrayText2";
    public static final String key_windowBackgroundWhiteGrayIcon = "windowBackgroundWhiteGrayIcon";
    public static final String key_windowBackgroundWhiteBlueText3 = "windowBackgroundWhiteBlueText3";
    public static final String key_windowBackgroundWhiteBlueText4 = "windowBackgroundWhiteBlueText4";
    public static final String key_windowBackgroundWhiteGrayText3 = "windowBackgroundWhiteGrayText3";
    public static final String key_windowBackgroundWhiteBlueHeader = "windowBackgroundWhiteBlueHeader";
    public static final String key_windowBackgroundWhiteBlueText = "windowBackgroundWhiteBlueText";
    public static final String key_windowBackgroundWhiteBlueLink = "windowBackgroundWhiteBlueLink";
    public static final String key_windowBackgroundWhiteRedText = "windowBackgroundWhiteRedText";

    public static final String key_fastScrollActive = "fastScrollActive";
    public static final String key_fastScrollInactive = "fastScrollInactive";
    public static final String key_fastScrollText = "fastScrollText";
    public static final String key_profile_title = "profile_title";
    public static final String key_profile_actionIcon = "profile_actionIcon";
    public static final String key_profile_actionBackground = "profile_actionBackground";
    public static final String key_fab_actionPressedBackground = "profile_actionPressedBackground";

    public static final String key_divider = "divider";
    public static final String key_graySection = "graySection";
    public static final String key_graySectionText = "key_graySectionText";

    public static final String key_checkboxCheck = "checkboxCheck";
    //public static final String key_chat_attachPhotoBackground = "chat_attachPhotoBackground";
    public static final String key_checkbox = "checkbox";
    public static final String key_checkboxDisabled = "checkboxDisabled";
    public static final String key_dialogTextBlack = "dialogTextBlack";
    public static final String key_dialogTextLink = "dialogTextLink";
    public static final String key_dialogGrayLine = "dialogGrayLine";
    public static final String key_dialogTextBlue4 = "dialogTextBlue4";
    public static final String key_windowBackgroundWhiteLinkText = "windowBackgroundWhiteLinkText";
    public static final String key_dialogProgressCircle = "dialogProgressCircle";
    public static final String key_dialogTextBlue = "dialogTextBlue";
    public static final String key_windowBackgroundWhiteValueText = "windowBackgroundWhiteValueText";
    public static final String key_dialogBackground = "dialogBackground";
    public static final String key_dialogBackgroundGray = "dialogBackgroundGray";

    public static final String key_dialogButton = "dialogButton";
    public static final String key_dialogButtonSelector = "dialogButtonSelector";
    public static final String key_dialogIcon = "dialogIcon";
    public static final String key_dialogLineProgress = "dialogLineProgress";
    public static final String key_dialogLineProgressBackground = "dialogLineProgressBackground";
    public static final String key_dialogScrollGlow = "dialogScrollGlow";
    public static final String key_dialogCheckboxSquareBackground = "dialogCheckboxSquareBackground";
    public static final String key_dialogCheckboxSquareCheck = "dialogCheckboxSquareCheck";
    public static final String key_dialogCheckboxSquareUnchecked = "dialogCheckboxSquareUnchecked";
    public static final String key_dialogCheckboxSquareDisabled = "dialogCheckboxSquareDisabled";
    public static final String key_dialog_inlineProgressBackground = "dialog_inlineProgressBackground";
    public static final String key_dialog_inlineProgress = "dialog_inlineProgress";
    public static final String key_dialogRadioBackground = "dialogRadioBackground";
    public static final String key_dialogRadioBackgroundChecked = "dialogRadioBackgroundChecked";

    public static final String key_dialogFloatingButton = "dialogFloatingButton";
    public static final String key_dialogFloatingButtonPressed = "dialogFloatingButtonPressed";
    public static final String key_dialogFloatingIcon = "dialogFloatingIcon";

    public static final String key_radioBackground = "radioBackground";
    public static final String key_radioBackgroundChecked = "radioBackgroundChecked";
    public static final String key_dialogTextGray2 = "dialogTextGray2";
    public static final String key_dialogTextGray3 = "dialogTextGray3";
    public static final String key_dialogTextGray4 = "dialogTextGray4";
    public static final String key_switchTrack = "switchTrack";
    public static final String key_switchTrackChecked = "switchTrackChecked";
    public static final String key_switchTrackBlue = "switchTrackBlue";
    public static final String key_switchTrackBlueChecked = "switchTrackBlueChecked";
    public static final String key_switchTrackBlueThumb = "switchTrackBlueThumb";
    public static final String key_switchTrackBlueThumbChecked = "switchTrackBlueThumbChecked";
    public static final String key_switchTrackBlueSelector = "switchTrackBlueSelector";
    public static final String key_switchTrackBlueSelectorChecked = "switchTrackBlueSelectorChecked";
    public static final String key_switch2Track = "switch2Track";
    public static final String key_checkboxSquareBackground = "checkboxSquareBackground";
    public static final String key_checkboxSquareCheck = "checkboxSquareCheck";
    public static final String key_checkboxSquareUnchecked = "checkboxSquareUnchecked";
    public static final String key_checkboxSquareDisabled = "checkboxSquareDisabled";

    public static final String key_switch2TrackChecked = "switch2TrackChecked";
    public static final String key_imageColorOnSurface = "imageColorSurface";
    public static final String key_tabSelectedIndicator = "tabSelectedIndicator";
    public static final String key_tabSelectedBlueText = "tabSelectedBlueText";
    public static final String key_chip_privateBackground = "chip_privateBackground";

    public static final String key_badge_background = "badge_background";
    public static final String key_badge_verified = "badge_verified";
    public static final String key_badge_betaTester = "badge_betaTester";
    public static final String key_badge_premium = "badge_premium";
    public static final String key_badge_vip = "badge_vip";
    public static final String key_badge_moderator = "badge_moderator";
    public static final String key_badge_admin = "badge_admin";
    public static final String key_badge_developer = "badge_developer";

    public static final String key_windowBackgroundWhitePlacholder = "windowBackgroundWhitePlaceholder";
    public static final String key_windowBackgroundWhiteCursor = "windowBackgroundWhiteCursor";
    public static final String key_badge_public = "badge_public";
    public static final String key_dialogTextGray = "dialogTextGray";
    public static final String key_imageColorDisabled = "imageColorDisabled";

    public static final String key_windowBackgroundWhiteText = "windowBackgroundWhiteText";
    public static final String key_windowBackgroundBlueGrayText = "windowBackgroundBlueGrayText";
    public static final String key_surfaceBackground = "surfaceBackground";
    public static final String key_imageColorPrimary = "imageColorPrimary";
    private static final Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final HashMap<String, Integer> currentColors = new HashMap<>();
    private static final HashMap<String, Integer> defaultColors = new HashMap<>();
    private static final HashMap<String, Integer> defaultColorsDark = new HashMap<>();
    private static final HashMap<String, ThemeInfo> themesDict;
    public static String key_windowBackgroundWhiteVerifiedText = "windowBackgroundWhiteVerifiedText";
    public static String key_windowBackgroundWhiteOrangeText = "windowBackgroundWhiteOrangeText";
    public static String key_undo_background = "undo_background";
    public static String key_undo_infoColor = "undo_infoColor";
    public static String key_undo_cancelColor = "undo_cancelColor";
    public static Drawable chat_composeShadowDrawable;
    public static Paint chat_composeBackgroundPaint = new Paint();
    public static Paint dividerPaint;
    public static Paint checkboxSquare_eraserPaint;
    public static Paint checkboxSquare_checkPaint;
    public static Paint checkboxSquare_backgroundPaint;
    public static ArrayList<ThemeInfo> themes;
    public static ThemeInfo currentTheme;
    public static Paint chat_statusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public static PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(0xfe6262, PorterDuff.Mode.MULTIPLY);

    public static Paint chat_composeDrawablePaint = new Paint();
    public static Drawable chat_composeDrawableDrawable;
    public static Drawable listSelector;
    public static boolean canStartHolidayAnimation;

    public static String key_chat_BlurAlpha = "chat_BlurAlpha";
    public static String key_chats_tabUnreadActiveBackground;
    public static String key_chats_tabUnreadUnactiveBackground;
    public static TextPaint chat_msgTextPaint;
    public static Drawable moveUpDrawable;
    public static Paint dialogs_onlineCirclePaint = new Paint();
    public static TextPaint dialogs_countTextPaint;
    public static Drawable dialogs_holidayDrawable;
    private static HashMap<String, Integer> animatingColors;
    private static Method StateListDrawable_getStateDrawableMethod;
    private static int dialogs_holidayDrawableOffsetX;
    private static int dialogs_holidayDrawableOffsetY;
    private static long lastHolidayCheckTime;

    static {

        defaultColors.put(key_undo_background, 0xea272f38);
        defaultColors.put(key_undo_infoColor, 0xff85caff);
        defaultColors.put(key_undo_cancelColor, 0xffffffff);


        defaultColors.put(key_dialogBackground, 0xffffffff);
        defaultColors.put(key_dialogBackgroundGray, 0xfff0f0f0);
        defaultColors.put(key_dialogLineProgress, 0xff527da3);
        defaultColors.put(key_dialogLineProgressBackground, 0xffdbdbdb);
        defaultColors.put(key_dialog_inlineProgressBackground, 0xf6f0f2f5);
        defaultColors.put(key_dialog_inlineProgress, 0xff6b7378);
        defaultColors.put(key_dialogButton, 0xff4991cc);
        defaultColors.put(key_dialogButtonSelector, 0x0f000000);
        defaultColors.put(key_dialogScrollGlow, 0xfff5f6f7);
        defaultColors.put(key_dialogProgressCircle, 0xff289deb);
        defaultColors.put(key_dialogTextBlue, 0xff2f8cc9);
        defaultColors.put(key_dialogIcon, 0xff616161);
        defaultColors.put(key_dialogTextBlack, 0xff222222);
        defaultColors.put(key_dialogTextLink, 0xff1976d2);
        defaultColors.put(key_dialogGrayLine, 0xffd2d2d2);
        defaultColors.put(key_dialogTextBlue4, 0xff19a7e8);
        defaultColors.put(key_dialogFloatingButton, 0xff4cb4f5);
        defaultColors.put(key_dialogFloatingButtonPressed, 0x0f000000);
        defaultColors.put(key_dialogFloatingIcon, 0xffffffff);


        defaultColors.put(key_checkbox, 0xff5ec245);
        defaultColors.put(key_checkboxCheck, 0xffffffff);
        defaultColors.put(key_checkboxDisabled, 0xffb0b9c2);

        defaultColors.put(key_avatar_backgroundBlue, 0xff549cdd);
        defaultColors.put(key_groupcreate_spanBackground, 0xfff2f2f2);
        defaultColors.put(key_avatar_actionBarIconBlue, 0xffffffff);
        defaultColors.put(key_fab_actionPressedBackground, 0xfff2f2f2);

        defaultColors.put(key_windowBackgroundWhiteValueText, 0xff666666);
        defaultColors.put(key_windowBackgroundWhiteLinkText, 0xff2196f3);
        defaultColors.put(key_windowBackgroundWhite, 0xffffffff);

        defaultColors.put(key_windowBackgroundWhiteText, 0xffffffff);

        defaultColors.put(key_windowBackgroundWhiteBlackText, 0xff222222);
        defaultColors.put(key_windowBackgroundWhiteRedText, 0xffcd5a5a);
        defaultColors.put(key_windowBackgroundWhiteGrayText2, 0xff666666);
        defaultColors.put(key_windowBackgroundWhiteGrayIcon, 0xff81868b);
        defaultColors.put(key_windowBackgroundGray, 0xfff0f0f0);
        defaultColors.put(key_windowBackgroundWhiteBlueHeader, 0xff2196f3);
        defaultColors.put(key_windowBackgroundWhiteBlueText, 0xff2196f3);
        defaultColors.put(key_windowBackgroundWhiteBlueText3, 0xff2678b6);
        defaultColors.put(key_windowBackgroundWhiteBlueText4, 0xff1c93e3);
        defaultColors.put(key_windowBackgroundWhiteGrayText3, 0xff999999);
        defaultColors.put(key_windowBackgroundWhiteBlueLink, 0xff2196f3);


        defaultColors.put(key_avatar_border, 0xff2196f3);

        defaultColors.put(key_avatar_text, 0xffffffff);

        defaultColors.put(key_avatar_backgroundSaved, 0xff66bffa);
        defaultColors.put(key_avatar_backgroundArchived, 0xffa9b6c1);
        defaultColors.put(key_avatar_backgroundArchivedHidden, 0xff66bffa);
        defaultColors.put(key_avatar_backgroundRed, 0xffe56555);
        defaultColors.put(key_avatar_backgroundOrange, 0xfff28c48);
        defaultColors.put(key_avatar_backgroundViolet, 0xff8e85ee);
        defaultColors.put(key_avatar_backgroundGreen, 0xff76c84d);
        defaultColors.put(key_avatar_backgroundCyan, 0xff5fbed5);
        defaultColors.put(key_avatar_backgroundBlue, 0xff549cdd);
        defaultColors.put(key_avatar_backgroundPink, 0xfff2749a);


        defaultColors.put(key_actionBarDefault, 0xffffffff);//0xff527da3);
        defaultColors.put(key_actionBarActionModeDefault, 0xffffffff);
        defaultColors.put(key_actionBarActionModeDefaultTop, 0x10000000);
        defaultColors.put(key_actionBarDefaultTitle, 0xff212121);
        defaultColors.put(key_actionBarDefaultSubtitle, 0xff666666);
        defaultColors.put(key_actionBarDefaultSelector, 0xff406d94);
        defaultColors.put(key_actionBarActionModeDefaultSelector, 0xff406d94);
        defaultColors.put(key_actionBarWhiteSelector, 0x1d000000);
        defaultColors.put(key_actionBarDefaultSearch, 0xff212121);
        defaultColors.put(key_actionBarDefaultSearchPlaceholder, 0x88ffffff);
        defaultColors.put(key_actionBarDefaultSubmenuItem, 0xff222222);
        defaultColors.put(key_actionBarDefaultSubmenuItemIcon, 0xff676b70);
        defaultColors.put(key_actionBarDefaultSubmenuBackground, 0xffffffff);
        defaultColors.put(key_actionBarTabActiveText, 0xffffffff);
        defaultColors.put(key_actionBarTabUnactiveText, 0xffd5e8f7);
        defaultColors.put(key_actionBarTabLine, 0xffffffff);
        defaultColors.put(key_actionBarTabSelector, 0xff406d94);

        defaultColors.put(key_chip_privateBackground, 0xffab47bc);

        defaultColors.put(key_listSelector, 0x0f000000);

        defaultColors.put(key_emptyListPlaceholder, 0xff959595);
        defaultColors.put(key_progressCircle, 0xff1c93e3);

        defaultColors.put(key_switchTrack, 0xffb0b5ba);
        defaultColors.put(key_switchTrackChecked, 0xff52ade9);
        defaultColors.put(key_switchTrackBlue, 0xff828e99);
        defaultColors.put(key_switchTrackBlueChecked, 0xff3c88c7);
        defaultColors.put(key_switchTrackBlueThumb, 0xffffffff);
        defaultColors.put(key_switchTrackBlueThumbChecked, 0xffffffff);
        defaultColors.put(key_switchTrackBlueSelector, 0x17404a53);
        defaultColors.put(key_switchTrackBlueSelectorChecked, 0x21024781);
        defaultColors.put(key_switch2Track, 0xfff57e7e);
        defaultColors.put(key_switch2TrackChecked, 0xff52ade9);

        defaultColors.put(key_actionBarDefaultIcon, 0xff212121);
        defaultColors.put(key_actionBarActionModeDefaultIcon, 0xff676a6f);
        defaultColors.put(key_fastScrollActive, 0xff52a3db);
        defaultColors.put(key_fastScrollInactive, 0xffc9cdd1);
        defaultColors.put(key_fastScrollText, 0xffffffff);
        defaultColors.put(key_colorPrimaryTextWhite, 0xff212121);
        defaultColors.put(key_surfaceBackground, 0xffffffff);

        defaultColors.put(key_divider, 0xffd9d9d9);
        defaultColors.put(key_graySection, 0xfff5f5f5);
        defaultColors.put(key_graySectionText, 0xff82878A);

        defaultColors.put(key_drawerBackground, 0xffffffff);
        defaultColors.put(key_colorPrimaryTextSurface, 0xffffffff);
        defaultColors.put(key_drawerHeaderBackground, 0xff1976d2);
        defaultColors.put(key_drawerHeaderStroke, 0xff1976d2);
        defaultColors.put(key_drawerImageColor, 0xff1976d2);
        defaultColors.put(key_drawerTextColor, 0xff2e2e2e);

        defaultColors.put(key_imageColorDisabled, 0xff546e7a);
        defaultColors.put(key_imageColorPrimary, 0xff2196f3);
        defaultColors.put(key_imageColor, 0xff616161);

        defaultColors.put(key_seekbarColor, 0xff1976d2);
        defaultColors.put(key_seekbarBackground, 0xffebedf0);

        defaultColors.put(key_buttonBackgroundBlue, 0xff205EFF);
        defaultColors.put(key_buttonTextColor, 0xffffffff);
        defaultColors.put(key_outlineColor, 0xff757575);
        defaultColors.put(key_navigationBarColor, 0xffffffff);
        defaultColors.put(key_fabColor, 0xff1976d2);
        defaultColors.put(key_imageColorOnSurface, 0xffffffff);

        defaultColors.put(key_tabSelectedBlueText, 0xff2196f3);
        defaultColors.put(key_tabSelectedIndicator, 0xff2196f3);
        defaultColors.put(key_windowBackgroundWhiteVerifiedText, 0xff4caf50);
        defaultColors.put(key_windowBackgroundWhiteOrangeText, 0xfff4511e);

        defaultColors.put(key_badge_background, 0xffffffff);
        defaultColors.put(key_badge_verified, 0xff129CE8);
        defaultColors.put(key_badge_betaTester, 0xff129CE8);
        defaultColors.put(key_badge_premium, 0xff129CE8);
        defaultColors.put(key_badge_vip, 0xff129CE8);
        defaultColors.put(key_badge_moderator, 0xff129CE8);
        defaultColors.put(key_badge_admin, 0xff129CE8);
        defaultColors.put(key_badge_developer, 0xff129CE8);

        defaultColors.put(key_windowBackgroundWhitePlacholder, 0xFFE0E0E0);
        defaultColors.put(key_windowBackgroundWhiteCursor, 0xff2196f3);

        themes = new ArrayList<>();
        themesDict = new HashMap<>();


        ThemeInfo theme = new ThemeInfo();
        theme.name = "EagleX Light";
        theme.uuid = "light";
        theme.isDark = false;
        theme.isDefault = true;
        theme.previewColor = 0xff9e9e9e;
        theme.colors = new HashMap<>();
        theme.colors.putAll(defaultColors);
        themes.add(currentTheme = theme);
        themesDict.put(theme.uuid, theme);

        defaultColorsDark.put(key_dialogBackground, 0xff262626);
        defaultColorsDark.put(key_dialogLineProgress, 0xff527da3);
        defaultColorsDark.put(key_dialogLineProgressBackground, 0xffdbdbdb);
        defaultColorsDark.put(key_dialog_inlineProgressBackground, 0xf6f0f2f5);
        defaultColorsDark.put(key_dialog_inlineProgress, 0xff6b7378);
        defaultColorsDark.put(key_dialogIcon, 0xffdedede);
        defaultColorsDark.put(key_dialogButton, 0xff4991cc);
        defaultColorsDark.put(key_dialogButtonSelector, 0x0fffffff);
        defaultColorsDark.put(key_dialogScrollGlow, 0xfff5f6f7);
        defaultColorsDark.put(key_dialogProgressCircle, 0xff289deb);
        defaultColorsDark.put(key_dialogTextBlue, 0xff2f8cc9);
        defaultColorsDark.put(key_dialogTextBlack, 0xffdedede);
        defaultColorsDark.put(key_dialogTextLink, 0xff1976d2);
        defaultColors.put(key_dialogCheckboxSquareBackground, 0xff43a0df);
        defaultColors.put(key_dialogCheckboxSquareCheck, 0xffffffff);
        defaultColors.put(key_dialogCheckboxSquareUnchecked, 0xff737373);
        defaultColors.put(key_dialogCheckboxSquareDisabled, 0xffb0b0b0);
        defaultColorsDark.put(key_checkbox, 0xff5ec245);
        defaultColorsDark.put(key_checkboxCheck, 0xff1976d2);
        defaultColorsDark.put(key_checkboxDisabled, 0xffb0b9c2);


        defaultColorsDark.put(key_windowBackgroundWhiteValueText, 0xff999999);
        defaultColorsDark.put(key_windowBackgroundWhiteLinkText, 0xff2196f3);
        defaultColorsDark.put(key_windowBackgroundWhite, 0xff202125);
        defaultColorsDark.put(key_windowBackgroundWhiteBlackText, 0xffdedede);
        defaultColorsDark.put(key_windowBackgroundWhiteRedText, 0xffcd5a5a);
        defaultColorsDark.put(key_windowBackgroundWhiteGrayText2, 0xff979a9f);
        defaultColorsDark.put(key_windowBackgroundWhiteGrayIcon, 0xff9b9fa6);
        defaultColorsDark.put(key_windowBackgroundGray, 0xfff0f0f0);
        defaultColorsDark.put(key_windowBackgroundWhiteBlueHeader, 0xff1976d2);
        defaultColorsDark.put(key_windowBackgroundWhiteBlueText, 0xff1976d2);
        defaultColorsDark.put(key_avatar_border, 0xff2196f3);

        defaultColorsDark.put(key_fab_actionPressedBackground, 0x1d000000);
        defaultColorsDark.put(key_actionBarDefault, 0xff202125);//0xff527da3);
        defaultColorsDark.put(key_actionBarActionModeDefault, 0xffffffff);
        defaultColorsDark.put(key_actionBarActionModeDefaultTop, 0x10000000);
        defaultColorsDark.put(key_actionBarDefaultTitle, 0xffdedede);
        defaultColorsDark.put(key_actionBarDefaultSubtitle, 0xff999999);
        defaultColorsDark.put(key_actionBarDefaultSelector, 0xff406d94);
        defaultColorsDark.put(key_actionBarActionModeDefaultSelector, 0xff406d94);
        defaultColorsDark.put(key_actionBarWhiteSelector, 0x1d000000);
        defaultColorsDark.put(key_actionBarDefaultSearch, 0xffdedede);
        defaultColorsDark.put(key_actionBarDefaultSubmenuItem, 0xffdedede);
        defaultColorsDark.put(key_actionBarDefaultSubmenuItemIcon, 0xffdedede);
        defaultColorsDark.put(key_actionBarDefaultSubmenuBackground, 0xff262626);

        defaultColorsDark.put(key_chip_privateBackground, 0xffab47bc);

        defaultColorsDark.put(key_listSelector, 0x0fffffff);

        defaultColorsDark.put(key_emptyListPlaceholder, 0xff959595);
        defaultColorsDark.put(key_progressCircle, 0xff1c93e3);

        defaultColorsDark.put(key_switchTrack, 0xffb0b5ba);
        defaultColorsDark.put(key_switchTrackChecked, 0xff52ade9);
        defaultColorsDark.put(key_switchTrackBlue, 0xff828e99);
        defaultColorsDark.put(key_switchTrackBlueChecked, 0xff3c88c7);
        defaultColorsDark.put(key_switchTrackBlueThumb, 0xffdedede);
        defaultColorsDark.put(key_switchTrackBlueThumbChecked, 0xffffffff);
        defaultColorsDark.put(key_switchTrackBlueSelector, 0x17404a53);
        defaultColorsDark.put(key_switchTrackBlueSelectorChecked, 0x21024781);
        defaultColorsDark.put(key_switch2Track, 0xfff57e7e);
        defaultColorsDark.put(key_switch2TrackChecked, 0xff52ade9);
        defaultColors.put(key_checkboxSquareBackground, 0xff43a0df);
        defaultColors.put(key_checkboxSquareCheck, 0xffffffff);
        defaultColors.put(key_checkboxSquareUnchecked, 0xff737373);
        defaultColors.put(key_checkboxSquareDisabled, 0xffb0b0b0);

        defaultColorsDark.put(key_actionBarDefaultIcon, 0xffdedede);
        defaultColorsDark.put(key_actionBarActionModeDefaultIcon, 0xff676a6f);
        defaultColorsDark.put(key_fastScrollActive, 0xff52a3db);
        defaultColorsDark.put(key_fastScrollInactive, 0xffc9cdd1);
        defaultColorsDark.put(key_fastScrollText, 0xffffffff);
        defaultColorsDark.put(key_colorPrimaryTextWhite, 0xffdedede);

        defaultColorsDark.put(key_surfaceBackground, 0xff202125);
        defaultColorsDark.put(key_divider, 0xff4B4C50);

        defaultColorsDark.put(key_drawerBackground, 0xff202125);
        defaultColorsDark.put(key_colorPrimaryTextSurface, 0xffdedede);
        defaultColorsDark.put(key_drawerHeaderBackground, 0xff1976d2);
        defaultColorsDark.put(key_drawerHeaderStroke, 0xff1976d2);
        defaultColorsDark.put(key_drawerImageColor, 0xff1976d2);
        defaultColorsDark.put(key_drawerTextColor, 0xffdedede);

        defaultColorsDark.put(key_imageColorDisabled, 0xff91949A);
        defaultColorsDark.put(key_imageColorPrimary, 0xff2196f3);
        defaultColorsDark.put(key_imageColor, 0xff9b9fa6);

        defaultColorsDark.put(key_seekbarColor, 0xff1976d2);
        defaultColorsDark.put(key_seekbarBackground, 0xffebedf0);

        defaultColorsDark.put(key_buttonBackgroundBlue, 0xff205EFF);
        defaultColorsDark.put(key_buttonTextColor, 0xffffffff);
        defaultColorsDark.put(key_outlineColor, 0xff707070);
        defaultColorsDark.put(key_navigationBarColor, 0xff202125);
        defaultColorsDark.put(key_fabColor, 0xff1976d2);
        defaultColorsDark.put(key_imageColorOnSurface, 0xffffffff);

        defaultColorsDark.put(key_tabSelectedBlueText, 0xff2196f3);
        defaultColorsDark.put(key_tabSelectedIndicator, 0xff2196f3);
        defaultColorsDark.put(key_windowBackgroundWhiteVerifiedText, 0xff4caf50);
        defaultColorsDark.put(key_windowBackgroundWhiteOrangeText, 0xfff4511e);

        defaultColorsDark.put(key_badge_background, 0xff212121);
        defaultColorsDark.put(key_badge_verified, 0xff129CE8);
        defaultColorsDark.put(key_badge_betaTester, 0xff129CE8);
        defaultColorsDark.put(key_badge_premium, 0xff129CE8);
        defaultColorsDark.put(key_badge_vip, 0xff129CE8);
        defaultColorsDark.put(key_badge_moderator, 0xff129CE8);
        defaultColorsDark.put(key_badge_admin, 0xff129CE8);
        defaultColorsDark.put(key_badge_developer, 0xff129CE8);

        defaultColorsDark.put(key_windowBackgroundWhitePlacholder, 0xff3b3c3f);
        defaultColorsDark.put(key_windowBackgroundWhiteCursor, 0xff2196f3);

        theme = new ThemeInfo();
        theme.name = "EagleX Dark";
        theme.uuid = "dark";
        theme.isDark = true;
        theme.isDefault = true;
        theme.previewColor = 0xff121212;
        theme.colors = new HashMap<>();
        theme.colors.putAll(defaultColorsDark);
        themes.add(currentTheme = theme);
        themesDict.put(theme.uuid, theme);

		/*theme = new ThemeInfo();
theme.name = "Test";
theme.uuid = "hfhdhsga";
theme.isDark = true;
theme.isDefault = false;
theme.previewColor = 0xfffe6262;
theme.colors = new HashMap<>();
theme.colors.putAll(defaultColorsDark);
themes.add(theme);
themesDict.put(theme.uuid, theme);*/

        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);

        String selectedTheme = preferences.getString("theme", null);
        String themesString = preferences.getString("themes", null);

        //FileLog.d("saved themes: " + themesString);
        if (themesString != null) {
            try {
                ArrayList<ThemeInfo> others = new ArrayList<>();
                others.addAll(new Gson().fromJson(themesString, new TypeToken<ArrayList<ThemeInfo>>() {
                }.getType()));
                for (ThemeInfo other : others) {
                    if (!other.uuid.equals("light") && !other.uuid.equals("dark")) {
                        themes.add(other);
                    }
                }
            } catch (Exception e) {
                AndroidUtilities.showToast(e.toString());
            }
        } else {
            preferences.edit().putString("themes", new Gson().toJson(themes)).commit();
        }

        if (selectedTheme != null) {
            boolean exists = false;
            for (ThemeInfo cth : themes) {
                if (cth.uuid.equals(selectedTheme)) {
                    applyTheme(cth);
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                applyTheme(themes.get(0));
            }
        } else {
            applyTheme(themes.get(0));
        }
    }

    public static void saveCurrentTheme(ThemeInfo theme, boolean finalSave, boolean newTheme, boolean inEdit) {
        SharedPreferences themesConfig = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        String themesString = themesConfig.getString("themes", null);
        SharedPreferences.Editor editor = themesConfig.edit();
        ArrayList<ThemeInfo> savedThemes;

        if (themesString == null) {
            savedThemes = new ArrayList<>();
            savedThemes.add(theme);
        } else {
            savedThemes = new Gson().fromJson(themesString, new TypeToken<ArrayList<ThemeInfo>>() {
            }.getType());
            boolean exists = false;
            for (ThemeInfo thms : savedThemes) {
                if (thms.equals(theme)) {
                    thms.colors.putAll(inEdit ? currentColors : theme.colors);
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                savedThemes.add(theme);
            }

        }


        themesString = new Gson().toJson(savedThemes);
        editor.putString("themes", themesString).commit();
        editor.putString("theme", theme.uuid).commit();


        //NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdateTheme, themes.size());
        //AndroidUtilities.addToClipboard(themesString);
        //themes.clear();
        //themes.addAll(savedThemes);
        //FileLog.d("saving theme: " + themesString);
    }

    @SuppressLint("ApplySharedPref")
    public static void importTheme(ThemeInfo themeInfo) {
        SharedPreferences themesConfig = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        String themesString = themesConfig.getString("themes", null);
        SharedPreferences.Editor editor = themesConfig.edit();

        ArrayList<ThemeInfo> themes = new Gson().fromJson(themesString, new TypeToken<ArrayList<ThemeInfo>>() {
        }.getType());

        if (!themes.contains(themeInfo)) {
            themes.add(themeInfo);
        } else {
            themes.set(themes.indexOf(themeInfo), themeInfo);
        }

        editor.putString("themes", new Gson().toJson(themes)).commit();

        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdateTheme, themes.size());
    }

    public static ThemeInfo getCurrentTheme() {
        return currentTheme;
    }

    public static void applyTheme(ThemeInfo themeInfo) {
        currentTheme = themeInfo;
        refreshThemeColors();
    }

    public static int getColor(String key) {
        return getColor(key, null, false);
    }

    public static int getColor(String key, boolean[] isDefault) {
        return getColor(key, isDefault, false);
    }

    public static int getColor(String key, boolean[] isDefault, boolean ignoreAnimation) {
        if (key.contains("#")) {
            return Color.parseColor(key);
        }


        if (animatingColors != null) {
            Integer color = animatingColors.get(key);
            if (color != null) {
                return color;
            }

        }

        try {

            Integer color = currentColors.get(key);
            if (color != null) {
                return currentColors.get(key);
            } else {
                return defaultColors.get(key);
            }

        } catch (Exception e) {
            AndroidUtilities.showToast("Key doesn't exist: " + key);
        }
        return 0;


    }

    public static void setColor(String key, int color, boolean useDefault) {
        if (useDefault) {
            currentColors.put(key, getDefaultColor(key));
        } else {
            currentColors.put(key, color);
        }
    }

    public static int getDefaultColor(String key) {
        Integer value = null;
        value = defaultColors.get(key);
        if (currentTheme.isDark) {
            value = defaultColorsDark.get(key);
        }

        if (value == null) {
            return 0xffff0000;
        }
        return value;
    }

    public static void refreshThemeColors() {
        currentColors.clear();
        currentColors.putAll(currentTheme.colors);
    }

    public static boolean hasThemeKey(String key) {
        return currentColors.containsKey(key);
    }

    public static Integer getColorOrNull(String key) {
        Integer color = currentColors.get(key);
        if (color == null) {
            color = defaultColors.get(key);
        }

        if (color != null && (key_windowBackgroundWhite.equals(key) || key_windowBackgroundGray.equals(key) || key_actionBarDefault.equals(key))) {
            color |= 0xff000000;
        }
        return color;
    }

    public static HashMap<String, Integer> getDefaultMap(boolean isDark) {

        if (isDark) {
            return defaultColorsDark;
        }
        return defaultColors;

    }

    public static int getNonAnimatedColor(String key) {
        return getColor(key, null, true);
    }

    public static void setDefaultColor(String key, int color) {
        defaultColors.put(key, color);
    }

    public static void setDrawableColor(Drawable drawable, int color) {
        if (drawable == null) {
            return;
        }
        if (drawable instanceof ShapeDrawable) {
            ((ShapeDrawable) drawable).getPaint().setColor(color);
        } else {
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
    }

    @SuppressLint("PrivateApi")
    private static Drawable getStateDrawable(Drawable drawable, int index) {
        if (Build.VERSION.SDK_INT >= 29 && drawable instanceof StateListDrawable) {
            return ((StateListDrawable) drawable).getStateDrawable(index);
        } else {
            if (StateListDrawable_getStateDrawableMethod == null) {
                try {
                    StateListDrawable_getStateDrawableMethod = StateListDrawable.class.getDeclaredMethod("getStateDrawable", int.class);
                } catch (Throwable ignore) {

                }
            }
            if (StateListDrawable_getStateDrawableMethod == null) {
                return null;
            }
            try {
                return (Drawable) StateListDrawable_getStateDrawableMethod.invoke(drawable, index);
            } catch (Exception ignore) {

            }
            return null;
        }
    }

    public static Drawable createBarSelectorDrawable(int color) {
        return createBarSelectorDrawable(color, true);
    }

    public static Drawable createBarSelectorDrawable(int color, boolean masked) {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable maskDrawable = null;
            if (masked) {
                maskPaint.setColor(0xffffffff);
                maskDrawable = new Drawable() {
                    @Override
                    public void draw(Canvas canvas) {
                        Rect bounds = getBounds();
                        canvas.drawCircle(bounds.centerX(), bounds.centerY(),
                                AndroidUtilities.dp(18), maskPaint);
                    }

                    @Override
                    public void setAlpha(int alpha) {

                    }

                    @Override
                    public void setColorFilter(ColorFilter colorFilter) {

                    }

                    @Override
                    public int getOpacity() {
                        return PixelFormat.UNKNOWN;
                    }
                };
            }
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{}
                    },
                    new int[]{
                            color
                    });
            return new RippleDrawable(colorStateList, null, maskDrawable);
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{
                    android.R.attr.state_pressed
            }, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{
                    android.R.attr.state_focused
            }, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{
                    android.R.attr.state_selected
            }, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{
                    android.R.attr.state_activated
            }, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{}, new ColorDrawable(0x00000000));
            return stateListDrawable;
        }
    }


    public static int getEventType() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int minutes = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        int eventType = -1;
        if (monthOfYear == 11 && dayOfMonth >= 24 && dayOfMonth <= 31 || monthOfYear == 0 && dayOfMonth == 1) {
            eventType = 0;
        } else if (monthOfYear == 1 && dayOfMonth == 14) {
            eventType = 1;
        } else if (monthOfYear == 9 && dayOfMonth >= 30 || monthOfYear == 10 && dayOfMonth == 1 && hour < 12) {
            eventType = 2;
        }
        return eventType;
    }

    public static ShapeDrawable createCircleDrawable(int size, int color) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize(size, size);
        ShapeDrawable defaultDrawable = new ShapeDrawable(ovalShape);
        defaultDrawable.setIntrinsicWidth(size);
        defaultDrawable.setIntrinsicHeight(size);
        defaultDrawable.getPaint().setColor(color);
        return defaultDrawable;
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int size, int iconRes) {
        return createCircleDrawableWithIcon(size, iconRes, 0);
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int size, int iconRes, int stroke) {
        Drawable drawable;
        if (iconRes != 0) {
            drawable = ApplicationLoader.applicationContext.getResources().getDrawable(iconRes).mutate();
        } else {
            drawable = null;
        }
        return createCircleDrawableWithIcon(size, drawable, stroke);
    }

    public static CombinedDrawable createCircleDrawableWithIcon(int size, Drawable drawable, int stroke) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize(size, size);
        ShapeDrawable defaultDrawable = new ShapeDrawable(ovalShape);
        Paint paint = defaultDrawable.getPaint();
        paint.setColor(0xffffffff);
        if (stroke == 1) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(2));
        } else if (stroke == 2) {
            paint.setAlpha(0);
        }
        CombinedDrawable combinedDrawable = new CombinedDrawable(defaultDrawable, drawable);
        combinedDrawable.setCustomSize(size, size);
        return combinedDrawable;
    }

    public static Drawable createRoundRectDrawableWithIcon(int rad, int iconRes) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        defaultDrawable.getPaint().setColor(0xffffffff);
        Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(iconRes).mutate();
        return new CombinedDrawable(defaultDrawable, drawable);
    }


    public static Drawable createSimpleSelectorDrawable(Context context, int resource, int defaultColor, int pressedColor) {
        Resources resources = context.getResources();
        Drawable defaultDrawable = resources.getDrawable(resource).mutate();
        if (defaultColor != 0) {
            defaultDrawable.setColorFilter(new PorterDuffColorFilter(defaultColor, PorterDuff.Mode.MULTIPLY));
        }
        Drawable pressedDrawable = resources.getDrawable(resource).mutate();
        if (pressedColor != 0) {
            pressedDrawable.setColorFilter(new PorterDuffColorFilter(pressedColor, PorterDuff.Mode.MULTIPLY));
        }
        StateListDrawable stateListDrawable = new StateListDrawable() {
            @Override
            public boolean selectDrawable(int index) {
                if (Build.VERSION.SDK_INT < 21) {
                    Drawable drawable = Theme.getStateDrawable(this, index);
                    ColorFilter colorFilter = null;
                    if (drawable instanceof BitmapDrawable) {
                        colorFilter = ((BitmapDrawable) drawable).getPaint().getColorFilter();
                    } else if (drawable instanceof NinePatchDrawable) {
                        colorFilter = ((NinePatchDrawable) drawable).getPaint().getColorFilter();
                    }
                    boolean result = super.selectDrawable(index);
                    if (colorFilter != null) {
                        drawable.setColorFilter(colorFilter);
                    }
                    return result;
                }
                return super.selectDrawable(index);
            }
        };
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }


    public static Drawable createRoundRectDrawable(int rad, int defaultColor) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        defaultDrawable.getPaint().setColor(defaultColor);
        return defaultDrawable;
    }

    public static Drawable createSimpleSelectorRoundRectDrawable(int rad, int defaultColor, int pressedColor) {
        return createSimpleSelectorRoundRectDrawable(rad, defaultColor, pressedColor, pressedColor);
    }

    public static Drawable createSimpleSelectorRoundRectDrawable(int rad, int defaultColor, int pressedColor, int maskColor) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        defaultDrawable.getPaint().setColor(defaultColor);
        ShapeDrawable pressedDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        pressedDrawable.getPaint().setColor(maskColor);
        if (Build.VERSION.SDK_INT >= 21) {
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{StateSet.WILD_CARD},
                    new int[]{pressedColor}
            );
            return new RippleDrawable(colorStateList, defaultDrawable, pressedDrawable);
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, pressedDrawable);
            stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
            return stateListDrawable;
        }
    }

    public static Drawable createSelectorDrawableFromDrawables(Drawable normal, Drawable pressed) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressed);
        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, pressed);
        stateListDrawable.addState(StateSet.WILD_CARD, normal);
        return stateListDrawable;
    }

    public static Drawable createSimpleSelectorCircleDrawable(int size, int defaultColor, int pressedColor) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize(size, size);
        ShapeDrawable defaultDrawable = new ShapeDrawable(ovalShape);
        defaultDrawable.getPaint().setColor(defaultColor);
        ShapeDrawable pressedDrawable = new ShapeDrawable(ovalShape);
        if (Build.VERSION.SDK_INT >= 21) {
            pressedDrawable.getPaint().setColor(0xffffffff);
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{StateSet.WILD_CARD},
                    new int[]{pressedColor}
            );
            return new RippleDrawable(colorStateList, defaultDrawable, pressedDrawable);
        } else {
            pressedDrawable.getPaint().setColor(pressedColor);
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
            stateListDrawable.addState(new int[]{android.R.attr.state_focused}, pressedDrawable);
            stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
            return stateListDrawable;
        }
    }

    public static void setCombinedDrawableColor(Drawable combinedDrawable, int color, boolean isIcon) {
        if (!(combinedDrawable instanceof CombinedDrawable)) {
            return;
        }
        Drawable drawable;
        if (isIcon) {
            drawable = ((CombinedDrawable) combinedDrawable).getIcon();
        } else {
            drawable = ((CombinedDrawable) combinedDrawable).getBackground();
        }
        if (drawable instanceof ColorDrawable) {
            ((ColorDrawable) drawable).setColor(color);
        } else {
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
    }


    public static Drawable getRoundRectSelectorDrawable(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable maskDrawable = createRoundRectDrawable(AndroidUtilities.dp(3), 0xffffffff);
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{StateSet.WILD_CARD},
                    new int[]{(color & 0x00ffffff) | 0x19000000}
            );
            return new RippleDrawable(colorStateList, null, maskDrawable);
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, createRoundRectDrawable(AndroidUtilities.dp(3), (color & 0x00ffffff) | 0x19000000));
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, createRoundRectDrawable(AndroidUtilities.dp(3), (color & 0x00ffffff) | 0x19000000));
            stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0x00000000));
            return stateListDrawable;
        }
    }

    public static Drawable createSelectorWithBackgroundDrawable(int backgroundColor, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable maskDrawable = new ColorDrawable(backgroundColor);
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{StateSet.WILD_CARD},
                    new int[]{color}
            );
            return new RippleDrawable(colorStateList, new ColorDrawable(backgroundColor), maskDrawable);
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(color));
            stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(backgroundColor));
            return stateListDrawable;
        }
    }

    public static Drawable getCurrentHolidayDrawable() {
        if ((System.currentTimeMillis() - lastHolidayCheckTime) >= 60 * 1000) {
            lastHolidayCheckTime = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int monthOfYear = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int minutes = calendar.get(Calendar.MINUTE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            canStartHolidayAnimation = monthOfYear == 0 && dayOfMonth == 1 && minutes <= 10 && hour == 0;
            if (dialogs_holidayDrawable == null) {
                if (monthOfYear == 11 && dayOfMonth >= 31 || monthOfYear == 0 && dayOfMonth == 1) {
                    dialogs_holidayDrawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.newyear);
                    dialogs_holidayDrawableOffsetX = -AndroidUtilities.dp(3);
                    dialogs_holidayDrawableOffsetY = -AndroidUtilities.dp(1);
                }
            }
        }
        return dialogs_holidayDrawable;
    }

    public static int getCurrentHolidayDrawableXOffset() {
        return dialogs_holidayDrawableOffsetX;
    }

    public static int getCurrentHolidayDrawableYOffset() {
        return dialogs_holidayDrawableOffsetY;
    }


    public static Drawable getSelectorDrawable(boolean whiteBackground) {
        return getSelectorDrawable(getColor(key_listSelector), whiteBackground);
    }

    public static Drawable getSelectorDrawable(int color, boolean whiteBackground) {
        if (whiteBackground) {
            return getSelectorDrawable(color, key_windowBackgroundWhite);
        } else {
            return createSelectorDrawable(color, 2);
        }
    }

    public static Drawable getSelectorDrawable(int color, String backgroundColor) {
        if (backgroundColor != null) {
            if (Build.VERSION.SDK_INT >= 21) {
                Drawable maskDrawable = new ColorDrawable(0xffffffff);
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{StateSet.WILD_CARD},
                        new int[]{color}
                );
                return new RippleDrawable(colorStateList, new ColorDrawable(getColor(backgroundColor)), maskDrawable);
            } else {
                StateListDrawable stateListDrawable = new StateListDrawable();
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(color));
                stateListDrawable.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(color));
                stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(getColor(backgroundColor)));
                return stateListDrawable;
            }
        } else {
            return createSelectorDrawable(color, 2);
        }
    }

    public static Drawable createSelectorDrawable(int color) {
        return createSelectorDrawable(color, 1, -1);
    }

    public static Drawable createSelectorDrawable(int color, int maskType) {
        return createSelectorDrawable(color, maskType, -1);
    }

    public static Drawable createSelectorDrawable(int color, int maskType, int radius) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable maskDrawable = null;
            if ((maskType == 1 || maskType == 5) && Build.VERSION.SDK_INT >= 23) {
                maskDrawable = null;
            } else if (maskType == 1 || maskType == 3 || maskType == 4 || maskType == 5 || maskType == 6 || maskType == 7) {
                maskPaint.setColor(0xffffffff);
                maskDrawable = new Drawable() {

                    RectF rect;

                    @Override
                    public void draw(Canvas canvas) {
                        Rect bounds = getBounds();
                        if (maskType == 7) {
                            if (rect == null) {
                                rect = new RectF();
                            }
                            rect.set(bounds);
                            canvas.drawRoundRect(rect, AndroidUtilities.dp(6), AndroidUtilities.dp(6), maskPaint);
                        } else {
                            int rad;
                            if (maskType == 1 || maskType == 6) {
                                rad = AndroidUtilities.dp(20);
                            } else if (maskType == 3) {
                                rad = (Math.max(bounds.width(), bounds.height()) / 2);
                            } else {
                                rad = (int) Math.ceil(Math.sqrt((bounds.left - bounds.centerX()) * (bounds.left - bounds.centerX()) + (bounds.top - bounds.centerY()) * (bounds.top - bounds.centerY())));
                            }
                            canvas.drawCircle(bounds.centerX(), bounds.centerY(), rad, maskPaint);
                        }
                    }

                    @Override
                    public void setAlpha(int alpha) {

                    }

                    @Override
                    public void setColorFilter(ColorFilter colorFilter) {

                    }

                    @Override
                    public int getOpacity() {
                        return PixelFormat.UNKNOWN;
                    }
                };
            } else if (maskType == 2) {
                maskDrawable = new ColorDrawable(0xffffffff);
            }
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{StateSet.WILD_CARD},
                    new int[]{color}
            );
            RippleDrawable rippleDrawable = new RippleDrawable(colorStateList, null, maskDrawable);
            if (Build.VERSION.SDK_INT >= 23) {
                if (maskType == 1) {
                    rippleDrawable.setRadius(radius <= 0 ? AndroidUtilities.dp(20) : radius);
                } else if (maskType == 5) {
                    rippleDrawable.setRadius(RippleDrawable.RADIUS_AUTO);
                }
            }
            return rippleDrawable;
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(color));
            stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0x00000000));
            return stateListDrawable;
        }
    }

    public static Drawable createCircleSelectorDrawable(int color, int leftInset, int rightInset) {
        if (Build.VERSION.SDK_INT >= 21) {
            maskPaint.setColor(0xffffffff);
            Drawable maskDrawable = new Drawable() {

                @Override
                public void draw(Canvas canvas) {
                    Rect bounds = getBounds();
                    final int rad = (Math.max(bounds.width(), bounds.height()) / 2) + leftInset + rightInset;
                    canvas.drawCircle(bounds.centerX() - leftInset + rightInset, bounds.centerY(), rad, maskPaint);
                }

                @Override
                public void setAlpha(int alpha) {
                }

                @Override
                public void setColorFilter(ColorFilter colorFilter) {
                }

                @Override
                public int getOpacity() {
                    return PixelFormat.UNKNOWN;
                }
            };
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{StateSet.WILD_CARD},
                    new int[]{color}
            );
            return new RippleDrawable(colorStateList, null, maskDrawable);
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(color));
            stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0x00000000));
            return stateListDrawable;
        }
    }

    public static void setMaskDrawableRad(Drawable rippleDrawable, int top, int bottom) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        if (rippleDrawable instanceof RippleDrawable) {
            RippleDrawable drawable = (RippleDrawable) rippleDrawable;
            int count = drawable.getNumberOfLayers();
            for (int a = 0; a < count; a++) {
                Drawable layer = drawable.getDrawable(a);
                if (layer instanceof RippleRadMaskDrawable) {
                    drawable.setDrawableByLayerId(android.R.id.mask, new RippleRadMaskDrawable(top, bottom));
                    break;
                }
            }
        }
    }

    public static Drawable createRadSelectorDrawable(int color, int topRad, int bottomRad) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= 21) {
            maskPaint.setColor(0xffffffff);
            Drawable maskDrawable = new RippleRadMaskDrawable(topRad, bottomRad);
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{StateSet.WILD_CARD},
                    new int[]{color}
            );
            return new RippleDrawable(colorStateList, null, maskDrawable);
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(color));
            stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0x00000000));
            return stateListDrawable;
        }
    }

    public static void createCommonResources(Context context) {
        if (dividerPaint == null) {
            dividerPaint = new Paint();
            dividerPaint.setStrokeWidth(1);
        }
        checkboxSquare_checkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        checkboxSquare_checkPaint.setStyle(Paint.Style.STROKE);
        checkboxSquare_checkPaint.setStrokeWidth(AndroidUtilities.dp(2));
        checkboxSquare_checkPaint.setStrokeCap(Paint.Cap.ROUND);
        checkboxSquare_eraserPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        checkboxSquare_eraserPaint.setColor(0);
        checkboxSquare_eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        checkboxSquare_backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public static void createDialogDrawable(Context context) {
        Resources resources = context.getResources();
        moveUpDrawable = resources.getDrawable(R.drawable.preview_open);
    }


    public static void setDrawableColorByKey(Drawable drawable, String key) {
        if (key == null) {
            return;
        }
        setDrawableColor(drawable, getColor(key));
    }


    public static void setSelectorDrawableColor(Drawable drawable, int color, boolean selected) {
        if (drawable instanceof StateListDrawable) {
            try {
                Drawable state;
                if (selected) {
                    state = getStateDrawable(drawable, 0);
                    if (state instanceof ShapeDrawable) {
                        ((ShapeDrawable) state).getPaint().setColor(color);
                    } else {
                        state.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    }
                    state = getStateDrawable(drawable, 1);
                } else {
                    state = getStateDrawable(drawable, 2);
                }
                if (state instanceof ShapeDrawable) {
                    ((ShapeDrawable) state).getPaint().setColor(color);
                } else {
                    state.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                }
            } catch (Throwable ignore) {

            }
        } else if (Build.VERSION.SDK_INT >= 21 && drawable instanceof RippleDrawable) {
            RippleDrawable rippleDrawable = (RippleDrawable) drawable;
            if (selected) {
                rippleDrawable.setColor(new ColorStateList(
                        new int[][]{StateSet.WILD_CARD},
                        new int[]{color}
                ));
            } else {
                if (rippleDrawable.getNumberOfLayers() > 0) {
                    Drawable drawable1 = rippleDrawable.getDrawable(0);
                    if (drawable1 instanceof ShapeDrawable) {
                        ((ShapeDrawable) drawable1).getPaint().setColor(color);
                    } else {
                        drawable1.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    }
                }
            }
        }
    }


    public static void applyCommonTheme() {
        if (dividerPaint == null) {
            return;
        }
        dividerPaint.setColor(getColor(key_divider));
    }

    public static boolean isAnimatingColor() {
        return animatingColors != null;
    }

    public static void setAnimatingColor(boolean animating) {
        animatingColors = animating ? new HashMap<>() : null;
    }

    public static void setAnimatedColor(String key, int value) {
        if (animatingColors == null) {
            return;
        }
        animatingColors.put(key, value);
    }

    public static boolean canStartHolidayAnimation() {
        return false;
    }

    public static class ThemeInfo {

        public String name;
        public String uuid;
        public boolean isDark;
        public boolean isDefault;
        public int previewColor;
        public HashMap<String, Integer> colors;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ThemeInfo) {
                ThemeInfo thm = (ThemeInfo) obj;
                return thm != null && this.uuid.equals(thm.uuid);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (this.uuid.concat(this.name)).hashCode();
        }

        public String getJsonString() {
            String array = new Gson().toJson(this);
            return array;
        }

        public String getKey() {
            return uuid;
        }

        public boolean isDark() {
            return isDark;
        }
    }

    /**
     * AdaptiveRipple implements methods for creating ripple drawables
     * Ripple color is based on background color, shifting saturation and value
     * <p>
     * There are four public static methods:
     * circle(String|int backgroundColor?, float radius?)
     * - creates ripple drawable for circle buttons, draws only ripples
     * radius (by default) can be RADIUS_TO_BOUNDS (-1): will be calculated to touch bounds edges
     * radius can be RADIUS_OUT_BOUNDS (-2): will be calculated to touch bounds corners (fill entire bounds)
     * otherwise radius is described in dp
     * filledCircle(String|Drawable|int background?, String|int backgroundColor?, float radius?)
     * - creates ripple drawable for circle buttons, draws ripples and background
     * (!) it's not recommended to use only drawable background, as it would be impossible to get background color
     * (there would be an attempt to cast drawable to ColorDrawable to retrieve a color)
     * rect(String|int backgroundColor?, ...float radii?)
     * - creates ripple drawable for rectangular buttons, draws only ripples
     * radii array is allowed to have 0, 1, 2, 3, 4, 8 elements:
     * 0 - no rounding
     * 1 - all roundings
     * 2 - top, bottom roundings
     * 3 - top-left, top-right, bottom roundings
     * 4 - top-left, top-right, bottom-right, bottom-left roundings
     * 8 - top-left-x, top-left-y, top-right-x, top-right-y,
     * bottom-right-x, bottom-right-y, bottom-left-x, bottom-left-y roundings
     * in case it has more, would be downgraded to nearest (ex.: 5 -> 4, 10 -> 8)
     * filledRect(String|Drawable|int background?, String|int backgroundColor?, ...float radii?)
     * - creates ripple drawable for rectangular buttons, draws ripples and background
     * <p>
     * by default, backgroundColor is getColor(Theme.key_windowBackgroundWhite)
     */
    public static class AdaptiveRipple {

        public static final float RADIUS_TO_BOUNDS = -1;
        public static final float RADIUS_OUT_BOUNDS = -2;

        private static final String defaultBackgroundColorKey = Theme.key_windowBackgroundWhite;

        public static Drawable circle() {
            return circle(Theme.getColor(defaultBackgroundColorKey), RADIUS_TO_BOUNDS);
        }

        public static Drawable circle(String backgroundColorKey) {
            return circle(Theme.getColor(backgroundColorKey), RADIUS_TO_BOUNDS);
        }

        public static Drawable circle(String backgroundColorKey, float radius) {
            return circle(Theme.getColor(backgroundColorKey), radius);
        }

        public static Drawable circle(int backgroundColor) {
            return circle(backgroundColor, RADIUS_TO_BOUNDS);
        }

        public static Drawable circle(int backgroundColor, float radius) {
            return createCircle(calcRippleColor(backgroundColor), radius);
        }

        public static Drawable filledCircle() {
            return filledCircle(null, Theme.getColor(defaultBackgroundColorKey), RADIUS_TO_BOUNDS);
        }

        public static Drawable filledCircle(Drawable background) {
            return filledCircle(background, Theme.getColor(defaultBackgroundColorKey), RADIUS_TO_BOUNDS);
        }

        public static Drawable filledCircle(String backgroundColorKey) {
            return filledCircle(null, Theme.getColor(backgroundColorKey), RADIUS_TO_BOUNDS);
        }

        public static Drawable filledCircle(Drawable background, String backgroundColorKey) {
            return filledCircle(background, Theme.getColor(backgroundColorKey), RADIUS_TO_BOUNDS);
        }

        public static Drawable filledCircle(String backgroundColorKey, float radius) {
            return filledCircle(null, Theme.getColor(backgroundColorKey), radius);
        }

        public static Drawable filledCircle(Drawable background, String backgroundColorKey, float radius) {
            return filledCircle(background, Theme.getColor(backgroundColorKey), radius);
        }

        public static Drawable filledCircle(int backgroundColor) {
            return filledCircle(null, backgroundColor, RADIUS_TO_BOUNDS);
        }

        public static Drawable filledCircle(int backgroundColor, float radius) {
            return filledCircle(null, backgroundColor, radius);
        }

        public static Drawable filledCircle(Drawable background, int backgroundColor, float radius) {
            return createCircle(background, calcRippleColor(backgroundColor), radius);
        }

        public static Drawable rect() {
            return rect(Theme.getColor(defaultBackgroundColorKey));
        }

        public static Drawable rect(String backgroundColorKey) {
            return rect(Theme.getColor(backgroundColorKey));
        }

        public static Drawable rect(String backgroundColorKey, float... radii) {
            return rect(Theme.getColor(backgroundColorKey), radii);
        }

        public static Drawable rect(int backgroundColor) {
            return rect(backgroundColor, 0);
        }

        public static Drawable rect(int backgroundColor, float... radii) {
            return createRect(0, calcRippleColor(backgroundColor), radii);
        }

        public static Drawable filledRect() {
            return filledRect(Theme.getColor(defaultBackgroundColorKey), 0);
        }

        public static Drawable filledRect(Drawable background) {
            int backgroundColor = background instanceof ColorDrawable ? ((ColorDrawable) background).getColor() : Theme.getColor(defaultBackgroundColorKey);
            return filledRect(background, backgroundColor, 0);
        }

        public static Drawable filledRect(String backgroundColorKey) {
            return filledRect(Theme.getColor(backgroundColorKey));
        }

        public static Drawable filledRect(Drawable background, String backgroundColorKey) {
            return filledRect(background, Theme.getColor(backgroundColorKey));
        }

        public static Drawable filledRect(String backgroundColorKey, float... radii) {
            return filledRect(Theme.getColor(backgroundColorKey), radii);
        }

        public static Drawable filledRect(Drawable background, String backgroundColorKey, float... radii) {
            return filledRect(background, Theme.getColor(backgroundColorKey), radii);
        }

        public static Drawable filledRect(int backgroundColor) {
            return createRect(backgroundColor, calcRippleColor(backgroundColor));
        }

        public static Drawable filledRect(int backgroundColor, float... radii) {
            return createRect(backgroundColor, calcRippleColor(backgroundColor), radii);
        }

        public static Drawable filledRect(Drawable background, int backgroundColor, float... radii) {
            return createRect(background, calcRippleColor(backgroundColor), radii);
        }

        private static Drawable createRect(int rippleColor, float... radii) {
            return createRect(0, rippleColor, radii);
        }

        private static Drawable createRect(int backgroundColor, int rippleColor, float... radii) {
            Drawable background = null;
            if (backgroundColor != 0) {
                if (hasNonzeroRadii(radii)) {
                    background = new ShapeDrawable(new RoundRectShape(calcRadii(radii), null, null));
                    ((ShapeDrawable) background).getPaint().setColor(backgroundColor);
                } else {
                    background = new ColorDrawable(backgroundColor);
                }
            }
            return createRect(
                    background,
                    rippleColor,
                    radii
            );
        }

        private static Drawable createRect(Drawable background, int rippleColor, float... radii) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Drawable maskDrawable = null;
                if (hasNonzeroRadii(radii)) {
                    maskDrawable = new ShapeDrawable(new RoundRectShape(calcRadii(radii), null, null));
                    ((ShapeDrawable) maskDrawable).getPaint().setColor(0xffffffff);
                }
                return new RippleDrawable(
                        new ColorStateList(
                                new int[][]{StateSet.WILD_CARD},
                                new int[]{rippleColor}
                        ),
                        background,
                        maskDrawable
                );
            } else {
                StateListDrawable stateListDrawable = new StateListDrawable();
                Drawable ripple;
                if (hasNonzeroRadii(radii)) {
                    ripple = new ShapeDrawable(new RoundRectShape(calcRadii(radii), null, null));
                    ((ShapeDrawable) ripple).getPaint().setColor(rippleColor);
                } else {
                    ripple = new ColorDrawable(rippleColor);
                }
                Drawable pressed = new LayerDrawable(new Drawable[]{background, ripple});
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressed);
                stateListDrawable.addState(new int[]{android.R.attr.state_selected}, pressed);
                stateListDrawable.addState(StateSet.WILD_CARD, background);
                return stateListDrawable;
            }
        }

        private static Drawable createCircle(int rippleColor) {
            return createCircle(0, rippleColor, RADIUS_TO_BOUNDS);
        }

        private static Drawable createCircle(int rippleColor, float radius) {
            return createCircle(0, rippleColor, radius);
        }

        private static Drawable createCircle(int backgroundColor, int rippleColor, float radius) {
            return createCircle(
                    backgroundColor == 0 ? null : new CircleDrawable(radius, backgroundColor),
                    rippleColor,
                    radius
            );
        }

        private static Drawable createCircle(Drawable background, int rippleColor, float radius) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return new RippleDrawable(
                        new ColorStateList(
                                new int[][]{StateSet.WILD_CARD},
                                new int[]{rippleColor}
                        ),
                        background,
                        new CircleDrawable(radius)
                );
            } else {
                StateListDrawable stateListDrawable = new StateListDrawable();
                Drawable ripple = new CircleDrawable(radius, rippleColor);
                Drawable pressed = new LayerDrawable(new Drawable[]{background, ripple});
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressed);
                stateListDrawable.addState(new int[]{android.R.attr.state_selected}, pressed);
                stateListDrawable.addState(StateSet.WILD_CARD, background);
                return stateListDrawable;
            }
        }

        private static class CircleDrawable extends Drawable {

            private static Paint maskPaint;
            private Paint paint;
            private float radius;

            public CircleDrawable(float radius) {
                this.radius = radius;
                if (this.maskPaint == null) {
                    this.maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    this.maskPaint.setColor(0xffffffff);
                }
                this.paint = this.maskPaint;
            }

            public CircleDrawable(float radius, int paintColor) {
                this.radius = radius;
                this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                this.paint.setColor(paintColor);
            }

            @Override
            public void draw(@NonNull Canvas canvas) {
                android.graphics.Rect bounds = getBounds();
                float rad;
                if (Math.abs(radius - RADIUS_TO_BOUNDS) < 0.01f) {
                    rad = Math.max(bounds.width(), bounds.height()) / 2;
                } else if (Math.abs(radius - RADIUS_OUT_BOUNDS) < 0.01f) {
                    rad = (int) Math.ceil(Math.sqrt((bounds.left - bounds.centerX()) * (bounds.left - bounds.centerX()) + (bounds.top - bounds.centerY()) * (bounds.top - bounds.centerY())));
                } else {
                    rad = AndroidUtilities.dp(radius);
                }
                canvas.drawCircle(bounds.centerX(), bounds.centerY(), rad, paint);
            }

            @Override
            public void setAlpha(int i) {
            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {
            }

            @Deprecated
            @Override
            public int getOpacity() {
                return PixelFormat.TRANSPARENT;
            }
        }

        private static float[] calcRadii(float... radii) {
            if (radii.length == 0) {
                return new float[]{0, 0, 0, 0, 0, 0, 0, 0};
            } else if (radii.length == 1) {
                return new float[]{dp(radii[0]), dp(radii[0]), dp(radii[0]), dp(radii[0]), dp(radii[0]), dp(radii[0]), dp(radii[0]), dp(radii[0])};
            } else if (radii.length == 2) {
                return new float[]{dp(radii[0]), dp(radii[0]), dp(radii[0]), dp(radii[0]), dp(radii[1]), dp(radii[1]), dp(radii[1]), dp(radii[1])};
            } else if (radii.length == 3) {
                return new float[]{dp(radii[0]), dp(radii[0]), dp(radii[1]), dp(radii[1]), dp(radii[2]), dp(radii[2]), dp(radii[2]), dp(radii[2])};
            } else if (radii.length < 8) {
                return new float[]{dp(radii[0]), dp(radii[0]), dp(radii[1]), dp(radii[1]), dp(radii[2]), dp(radii[2]), dp(radii[3]), dp(radii[3])};
            } else {
                return new float[]{dp(radii[0]), dp(radii[1]), dp(radii[2]), dp(radii[3]), dp(radii[4]), dp(radii[5]), dp(radii[6]), dp(radii[7])};
            }
        }

        private static boolean hasNonzeroRadii(float... radii) {
            for (int i = 0; i < Math.min(8, radii.length); ++i) {
                if (radii[i] > 0) {
                    return true;
                }
            }
            return false;
        }

        private static float[] tempHSV;

        private static int calcRippleColor(int backgroundColor) {
            if (tempHSV == null) {
                tempHSV = new float[3];
            }
            Color.colorToHSV(backgroundColor, tempHSV);
            if (tempHSV[1] > 0.01f) {
                // when saturation is too low, hue is ignored
                // so changing saturation at that point would reveal ignored hue (usually red, hue=0)
                tempHSV[1] = Math.min(1, Math.max(0, tempHSV[1] + (isCurrentThemeDark() ? -.25f : .25f)));
                tempHSV[2] = Math.min(1, Math.max(0, tempHSV[2] + (isCurrentThemeDark() ? .05f : -.05f)));
            } else {
                tempHSV[2] = Math.min(1, Math.max(0, tempHSV[2] + (isCurrentThemeDark() ? .1f : -.1f)));
            }
            return Color.HSVToColor(127, tempHSV);
        }

        private static boolean isCurrentThemeDark() {
            return currentTheme.isDark();
        }
    }

    private static int dp(float value) {
        return AndroidUtilities.dp(value);
    }

    public static class RippleRadMaskDrawable extends Drawable {
        private final Path path = new Path();
        private final RectF rect = new RectF();
        private final float[] radii = new float[8];
        private int topRad;
        private int bottomRad;

        public RippleRadMaskDrawable(int top, int bottom) {
            topRad = top;
            bottomRad = bottom;
        }

        public void setRadius(int top, int bottom) {
            topRad = top;
            bottomRad = bottom;
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            radii[0] = radii[1] = radii[2] = radii[3] = AndroidUtilities.dp(topRad);
            radii[4] = radii[5] = radii[6] = radii[7] = AndroidUtilities.dp(bottomRad);
            rect.set(getBounds());
            path.addRoundRect(rect, radii, Path.Direction.CW);
            canvas.drawPath(path, maskPaint);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }
    }
}