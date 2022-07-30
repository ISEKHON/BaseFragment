package xyz.illuminate.app;

import android.content.Intent;

import xyz.illuminate.ui.ActionBar.BaseFragment;

public class FilePicker {

    public static void pickSingleFile(BaseFragment parentActivity, String mimeType, int requestCode) {
        try {
            parentActivity.startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType(mimeType), "Choose a file"), requestCode);
        } catch (Exception e) {
            AndroidUtilities.showToast(e.toString());
            FileLog.e(e);
        }
    }

    public static void pickMultipleFiles(BaseFragment parentActivity, String mimeType, int requestCode) {
        parentActivity.startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType(mimeType).putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true), "Choose a file"), requestCode);
    }
}
