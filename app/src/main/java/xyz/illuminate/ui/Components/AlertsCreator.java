package xyz.illuminate.ui.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.HashMap;

import xyz.illuminate.app.AndroidUtilities;
import xyz.illuminate.app.ApplicationLoader;
import xyz.illuminate.app.LocaleController;
import xyz.illuminate.app.R;
import xyz.illuminate.app.Utilities;
import xyz.illuminate.ui.ActionBar.AlertDialog;
import xyz.illuminate.ui.ActionBar.BaseFragment;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Cells.RadioColorCell;
import xyz.illuminate.ui.LaunchActivity;

public class AlertsCreator {

    public static Dialog processError(HashMap<String, String> hashMap, BaseFragment baseFragment) {
        String statusCode = hashMap.get("status_code");
        String server_message = hashMap.get("message");
        if (statusCode.equals("ERR_PARAMS_NOT_PROVIDED")) {
            return showSimpleAlert(baseFragment, "Error", server_message);
        } else if (statusCode.equals("ERR_SHA256_ACCESS_DENIED")) {
            return showSimpleAlert(baseFragment, "Access Denied", "The APK has been modified");
        } else if (statusCode.equals("ERR_ALREADY_IN_USE")) {
            return showSimpleAlert(baseFragment, "Error", "Account you're trying to login is already in-use on another device");
        } else if (statusCode.equals("ERR_INVALID_EMAIL")) {
            return showSimpleAlert(baseFragment, "Error", "The key you provided is invalid");
        } else if (statusCode.equals("ERR_UNKNOWN_USERNAME")) {
            return showSimpleAlert(baseFragment, "Error", "The username you provided is invalid");
        } else if (statusCode.equals("ERR_AMBIGUOUS_USERNAME")) {
            return showSimpleAlert(baseFragment, "Error", "The username contains invalid characters");
        } else if (statusCode.equals("ERR_INVALID_PASSWORD")) {
            return showSimpleAlert(baseFragment, "Error", "Invalid password while doing authentication");
        } else if (statusCode.equals("ERR_EMAIL_NOT_VERIFIED")) {
            return showSimpleAlert(baseFragment, "Error", "The account user trying to access hasn't verified the email yet");
        } else if (statusCode.equals("ERR_TOO_MANY_REQUESTS")) {
            return showSimpleAlert(baseFragment, "Rate Limit", "To much requests sent to server please try again later " + server_message);
        } else if (statusCode.equals("ERR_USER_EXPIRED")) {
            return showSimpleAlert(baseFragment, "Error", "The key you entered is expired");
        }
        return showSimpleAlert(baseFragment, "This app is developed by MrCode403");
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, final String text) {
        return createSimpleAlert(context, null, text);
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, final String title, final String text) {
        if (context == null || text == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title == null ? LocaleController.getString("AppName", R.string.AppName) : title);
        builder.setMessage(text);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        return builder;
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, final String text) {
        return showSimpleAlert(baseFragment, null, text);
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, final String title, final String text) {
        if (text == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        AlertDialog.Builder builder = createSimpleAlert(baseFragment.getParentActivity(), title, text);
        Dialog dialog = builder.create();
        baseFragment.showDialog(dialog);
        return dialog;
    }


    public static Toast showSimpleToast(BaseFragment baseFragment, final String text) {
        if (text == null) {
            return null;
        }
        Context context;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            context = baseFragment.getParentActivity();
        } else {
            context = ApplicationLoader.applicationContext;
        }
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
        return toast;
    }

    public static AlertDialog showUpdateAppAlert(final Context context, final HashMap<String, String> data) {
        if (context == null || data == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("UpdateAvailable", R.string.UpdateAvailable));
        builder.setMessage(data.get("update_message"));
        builder.setPositiveButton(LocaleController.getString("Update", R.string.Update), null);
        if (Utilities.parseInt(data.get("canSkipUpdate")).intValue() <= 42) {
            builder.setNegativeButton(LocaleController.getString("Later", R.string.Later), (dialog, which) -> {
                Intent i = new Intent(context, LaunchActivity.class);
                Activity find = AndroidUtilities.findActivity(context);
                find.startActivity(i);
                find.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                find.finish();
            });
        }
        AlertDialog create = builder.create();
        create.show();
        create.setCancelable(false);
        create.setCanceledOnTouchOutside(false);
        return create;
    }

    public static AlertDialog showMaintenanceAlert(final Context context, final HashMap<String, String> data) {
        if (context == null || data == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("ServerMessage", R.string.ServerMessage));
        builder.setSubtitle(LocaleController.getString("MaintenanceBreak", R.string.MaintenanceBreak));
        builder.setMessage(data.get("server_message"));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        AlertDialog create = builder.create();
        create.show();
        create.setCancelable(false);
        create.setCanceledOnTouchOutside(false);
        return create;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static AlertDialog.Builder createDrawOverlayPermissionDialog(Activity activity, DialogInterface.OnClickListener onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(LocaleController.getString("AppNameFull", R.string.AppNameFull));
        // builder.setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", R.string.PermissionDrawOverlay));
        builder.setPositiveButton(LocaleController.getString("Enable", R.string.Enable), (dialogInterface, i) -> {
            if (activity != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName())));
                }
            }
        });
        builder.notDrawBackgroundOnTopView(true);
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), onCancel);
        return builder;
    }

    public static Dialog createSingleChoiceDialog(Activity parentActivity, final String[] options, final String title, final int selected, final DialogInterface.OnClickListener listener) {
        final LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        for (int a = 0; a < options.length; a++) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4), 0, AndroidUtilities.dp(4), 0);
            cell.setTag(a);
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(options[a], selected == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(v -> {
                int sel = (Integer) v.getTag();
                builder.getDismissRunnable().run();
                listener.onClick(null, sel);
            });
        }

        builder.setTitle(title);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

}
