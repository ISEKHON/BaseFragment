package xyz.illuminate.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import xyz.illuminate.server.TLRPC;
import xyz.illuminate.ui.ActionBar.AlertDialog;

public class MediaController implements NotificationCenter.NotificationCenterDelegate {

    @SuppressLint("StaticFieldLeak")
    private static volatile MediaController Instance;
    private Activity baseActivity;
    private View feedbackView;

    public MediaController() {
        AndroidUtilities.runOnUIThread(() -> {
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.didReceiveNewMessages);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.removeAllMessagesFromDialog);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.musicDidLoad);
            NotificationCenter.getInstance().addObserver(MediaController.this, NotificationCenter.mediaDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(MediaController.this, NotificationCenter.playerDidStartPlaying);

        });
    }

    public static MediaController getInstance() {
        MediaController localInstance = Instance;
        if (localInstance == null) {
            synchronized (MediaController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new MediaController();
                }
            }
        }
        return localInstance;
    }

    public static void saveFile(String fullPath, Context context, final int type, final String name, final String mime) {
        saveFile(fullPath, context, type, name, mime, null);
    }

    public static void saveFile(String fullPath, Context context, final int type, final String name, final String mime, final Runnable onSaved) {
        if (fullPath == null || context == null) {
            return;
        }

        File file = null;
        if (!TextUtils.isEmpty(fullPath)) {
            file = new File(fullPath);
            if (!file.exists() || AndroidUtilities.isInternalUri(Uri.fromFile(file))) {
                file = null;
            }
        }

        if (file == null) {
            return;
        }

        final File sourceFile = file;
        final boolean[] cancelled = new boolean[]{false};
        if (sourceFile.exists()) {

            AlertDialog progressDialog = null;
            final boolean[] finished = new boolean[1];
            if (context != null && type != 0) {
                try {
                    final AlertDialog dialog = new AlertDialog(context, 2);
                    dialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(true);
                    dialog.setOnCancelListener(d -> cancelled[0] = true);
                    AndroidUtilities.runOnUIThread(() -> {
                        if (!finished[0]) {
                            dialog.show();
                        }
                    }, 250);
                    progressDialog = dialog;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            final AlertDialog finalProgress = progressDialog;

            new Thread(() -> {
                try {

                    boolean result = true;
                    if (Build.VERSION.SDK_INT >= 29) {
                        result = saveFileInternal(type, sourceFile, null);
                    } else {
                        File destFile;
                        if (type == 0) {
                            destFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Telegram");
                            destFile.mkdirs();
                            destFile = new File(destFile, AndroidUtilities.generateFileName(0, FileLoader.getFileExtension(sourceFile)));
                        } else if (type == 1) {
                            destFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "Telegram");
                            destFile.mkdirs();
                            destFile = new File(destFile, AndroidUtilities.generateFileName(1, FileLoader.getFileExtension(sourceFile)));
                        } else {
                            File dir;
                            if (type == 2) {
                                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            } else {
                                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                            }
                            dir = new File(dir, "Telegram");
                            dir.mkdirs();
                            destFile = new File(dir, name);
                            if (destFile.exists()) {
                                int idx = name.lastIndexOf('.');
                                for (int a = 0; a < 10; a++) {
                                    String newName;
                                    if (idx != -1) {
                                        newName = name.substring(0, idx) + "(" + (a + 1) + ")" + name.substring(idx);
                                    } else {
                                        newName = name + "(" + (a + 1) + ")";
                                    }
                                    destFile = new File(dir, newName);
                                    if (!destFile.exists()) {
                                        break;
                                    }
                                }
                            }
                        }
                        if (!destFile.exists()) {
                            destFile.createNewFile();
                        }
                        long lastProgress = System.currentTimeMillis() - 500;
                        try (FileInputStream inputStream = new FileInputStream(sourceFile); FileChannel source = inputStream.getChannel(); FileChannel destination = new FileOutputStream(destFile).getChannel()) {
                            long size = source.size();
                            try {
                                @SuppressLint("DiscouragedPrivateApi") Method getInt = FileDescriptor.class.getDeclaredMethod("getInt$");
                                int fdint = (Integer) getInt.invoke(inputStream.getFD());
                                if (AndroidUtilities.isInternalUri(fdint)) {
                                    if (finalProgress != null) {
                                        AndroidUtilities.runOnUIThread(() -> {
                                            try {
                                                finalProgress.dismiss();
                                            } catch (Exception e) {
                                                FileLog.e(e);
                                            }
                                        });
                                    }
                                    return;
                                }
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                            for (long a = 0; a < size; a += 4096) {
                                if (cancelled[0]) {
                                    break;
                                }
                                destination.transferFrom(source, a, Math.min(4096, size - a));
                                if (finalProgress != null) {
                                    if (lastProgress <= System.currentTimeMillis() - 500) {
                                        lastProgress = System.currentTimeMillis();
                                        final int progress = (int) ((float) a / (float) size * 100);
                                        AndroidUtilities.runOnUIThread(() -> {
                                            try {
                                                finalProgress.setProgress(progress);
                                            } catch (Exception e) {
                                                FileLog.e(e);
                                            }
                                        });
                                    }
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                            result = false;
                        }
                        if (cancelled[0]) {
                            destFile.delete();
                            result = false;
                        }
                        if (result) {
                            if (type == 2) {
                                DownloadManager downloadManager = (DownloadManager) ApplicationLoader.applicationContext.getSystemService(Context.DOWNLOAD_SERVICE);
                                downloadManager.addCompletedDownload(destFile.getName(), destFile.getName(), false, mime, destFile.getAbsolutePath(), destFile.length(), true);
                            } else {
                                AndroidUtilities.addMediaToGallery(destFile.getAbsoluteFile());
                            }
                        }
                    }
                    if (result && onSaved != null) {
                        AndroidUtilities.runOnUIThread(onSaved);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (finalProgress != null) {
                    AndroidUtilities.runOnUIThread(() -> {
                        try {
                            if (finalProgress.isShowing()) {
                                finalProgress.dismiss();
                            } else {
                                finished[0] = true;
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    });
                }
            }).start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static boolean saveFileInternal(int type, File sourceFile, String filename) {
        try {
            int selectedType = type;
            ContentValues contentValues = new ContentValues();
            String extension = MimeTypeMap.getFileExtensionFromUrl(sourceFile.getAbsolutePath());
            String mimeType = null;
            if (extension != null) {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            Uri uriToInsert = null;
            if ((type == 0 || type == 1) && mimeType != null) {
                if (mimeType.startsWith("image")) {
                    selectedType = 0;
                }
                if (mimeType.startsWith("video")) {
                    selectedType = 1;
                }
            }
            if (selectedType == 0) {
                if (filename == null) {
                    filename = AndroidUtilities.generateFileName(0, extension);
                }
                uriToInsert = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                File dirDest = new File(Environment.DIRECTORY_PICTURES, "Telegram");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, dirDest + File.separator);
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
            } else if (selectedType == 1) {
                if (filename == null) {
                    filename = AndroidUtilities.generateFileName(1, extension);
                }
                File dirDest = new File(Environment.DIRECTORY_MOVIES, "Telegram");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, dirDest + File.separator);
                uriToInsert = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filename);
            } else if (selectedType == 2) {
                if (filename == null) {
                    filename = sourceFile.getName();
                }
                File dirDest = new File(Environment.DIRECTORY_DOWNLOADS, "Telegram");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, dirDest + File.separator);
                uriToInsert = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, filename);
            } else {
                if (filename == null) {
                    filename = sourceFile.getName();
                }
                File dirDest = new File(Environment.DIRECTORY_MUSIC, "Telegram");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, dirDest + File.separator);
                uriToInsert = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, filename);
            }

            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);

            Uri dstUri = ApplicationLoader.applicationContext.getContentResolver().insert(uriToInsert, contentValues);
            if (dstUri != null) {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                OutputStream outputStream = ApplicationLoader.applicationContext.getContentResolver().openOutputStream(dstUri);
                AndroidUtilities.copyFile(fileInputStream, outputStream);
                fileInputStream.close();
            }
            return true;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    @SuppressLint("Range")
    public static String getFileName(Uri uri) {
        if (uri == null) {
            return "";
        }
        try {
            String result = null;
            if (uri.getScheme().equals("content")) {
                try (Cursor cursor = ApplicationLoader.applicationContext.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
            return result;
        } catch (Exception e) {
            FileLog.e(e);
        }
        return "";
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    public void setBaseActivity(Activity activity, boolean set) {
        if (set) {
            baseActivity = activity;
        } else if (baseActivity == activity) {
            baseActivity = null;
        }
    }

    public void setFeedbackView(View view, boolean set) {
        if (set) {
            feedbackView = view;
        } else if (feedbackView == view) {
            feedbackView = null;
        }
    }

    public static class SavedFilterState {
        public float saturationValue;
        public float fadeValue;
        public float grainValue;
        public int blurType;
        public float sharpenValue;
        public float blurAngle;
    }

    public static class CropState {
        public float cropPx;
        public float cropPy;
        public float cropScale = 1;
        public float cropRotate;
        public float cropPw = 1;
        public float cropPh = 1;
        public int transformWidth;
        public int transformHeight;
        public int transformRotation;
        public boolean mirrored;

        public float stateScale;
        public float scale;
        public Matrix matrix;
        public int width;
        public int height;
        public boolean freeform;
        public float lockedAspectRatio;

        public boolean initied;
    }

    public static class MediaEditState {

        public CharSequence caption;

        public String thumbPath;
        public String imagePath;
        public String filterPath;
        public String paintPath;
        public String croppedPaintPath;
        public String fullPaintPath;

        public ArrayList<TLRPC.MessageEntity> entities;
        public SavedFilterState savedFilterState;
        public ArrayList mediaEntities;
        public ArrayList croppedMediaEntities;
        public long averageDuration;
        public boolean isFiltered;
        public boolean isPainted;
        public boolean isCropped;
        public int ttl;

        public CropState cropState;

        public String getPath() {
            return null;
        }

        public void reset() {
            caption = null;
            thumbPath = null;
            filterPath = null;
            imagePath = null;
            paintPath = null;
            croppedPaintPath = null;
            isFiltered = false;
            isPainted = false;
            isCropped = false;
            ttl = 0;
            mediaEntities = null;

            entities = null;
            savedFilterState = null;
            cropState = null;
        }

        public void copyFrom(MediaEditState state) {
            caption = state.caption;

            thumbPath = state.thumbPath;
            imagePath = state.imagePath;
            filterPath = state.filterPath;
            paintPath = state.paintPath;
            croppedPaintPath = state.croppedPaintPath;
            fullPaintPath = state.fullPaintPath;

            entities = state.entities;
            savedFilterState = state.savedFilterState;
            mediaEntities = state.mediaEntities;
            croppedMediaEntities = state.croppedMediaEntities;
            averageDuration = state.averageDuration;
            isFiltered = state.isFiltered;
            isPainted = state.isPainted;
            isCropped = state.isCropped;
            ttl = state.ttl;

            cropState = state.cropState;
        }
    }

    public static class PhotoEntry extends MediaEditState {
        public int bucketId;
        public int imageId;
        public long dateTaken;
        public int duration;
        public int width;
        public int height;
        public long size;
        public String path;
        public int orientation;
        public boolean isVideo;
        public boolean isMuted;
        public boolean canDeleteAfter;

        public PhotoEntry(int bucketId, int imageId, long dateTaken, String path, int orientation, boolean isVideo, int width, int height, long size) {
            this.bucketId = bucketId;
            this.imageId = imageId;
            this.dateTaken = dateTaken;
            this.path = path;
            this.width = width;
            this.height = height;
            this.size = size;
            if (isVideo) {
                this.duration = orientation;
            } else {
                this.orientation = orientation;
            }
            this.isVideo = isVideo;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public void reset() {
            if (isVideo) {
                if (filterPath != null) {
                    new File(filterPath).delete();
                    filterPath = null;
                }
            }
            super.reset();
        }
    }

}
