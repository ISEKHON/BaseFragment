package xyz.illuminate.app;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.SparseArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import xyz.illuminate.ui.ActionBar.BaseFragment;

public class FileLoader {

    public static final int MEDIA_DIR_CACHE = 4;
    public static final int IMAGE_TYPE_ANIMATION = 2;
    private static final FileLoader[] Instance = new FileLoader[0];
    public final String PATH_DOCUMENT = "document";
    public final String PATH_TREE = "tree";
    public final String URL_COLON = "%3A";
    public final String URL_SLASH = "%2F";
    public boolean permissionGranted = false;
    public SharedPreferences config;


    public FileLoader(int instance) {
    }


    public FileLoader() {
        config = ApplicationLoader.applicationContext.getSharedPreferences("config", 0);
    }

    public static FileLoader getInstance(int num) {
        FileLoader localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (FileLoader.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new FileLoader(num);
                }
            }
        }
        return localInstance;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf('.') + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public static String fixFileName(String fileName) {
        if (fileName != null) {
            fileName = fileName.replaceAll("[\u0001-\u001f<>\u202E:\"/\\\\|?*\u007f]+", "").trim();
        }
        return fileName;
    }

    public static File getDirectory(int type) {
        SparseArray<File> mediaDirs = new SparseArray<>();
        File cachePath = AndroidUtilities.getCacheDir();
        if (!cachePath.isDirectory()) {
            try {
                cachePath.mkdirs();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (!new File(cachePath, ".nomedia").exists()) {
            AndroidUtilities.createEmptyFile(new File(cachePath, ".nomedia"));
        }
        mediaDirs.put(FileLoader.MEDIA_DIR_CACHE, cachePath);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("cache path = " + cachePath);
        }
        File dir = mediaDirs.get(type);
        if (dir == null && type != FileLoader.MEDIA_DIR_CACHE) {
            dir = mediaDirs.get(FileLoader.MEDIA_DIR_CACHE);
        }
        try {
            if (dir != null && !dir.isDirectory()) {
                dir.mkdirs();
            }
        } catch (Exception e) {

        }
        return dir;
    }

    public static File getInternalCacheDir() {
        return ApplicationLoader.applicationContext.getCacheDir();
    }

    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        return copyFile(sourceFile, destFile, -1);
    }

    public static boolean copyFile(InputStream sourceFile, File destFile, int maxSize) throws IOException {
        FileOutputStream out = new FileOutputStream(destFile);
        byte[] buf = new byte[4096];
        int len;
        int totalLen = 0;
        while ((len = sourceFile.read(buf)) > 0) {
            Thread.yield();
            out.write(buf, 0, len);
            totalLen += len;
            if (maxSize > 0 && totalLen >= maxSize) {
                break;
            }
        }
        out.getFD().sync();
        out.close();
        return true;
    }

    public static String checkDirectory(int mediaDirCache) {
        return "/sdcard/Android/data/xyz.illuminate.app/files/";
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public String getTreeDocumentId(Uri uri) {
        List<String> paths = uri.getPathSegments();
        if (paths.size() < 2 || !PATH_TREE.equals(paths.get(0))) {
            return null;
        }
        return paths.get(1);
    }

    public boolean isTree(Uri uri) {
        List<String> paths = uri.getPathSegments();
        return paths.size() == 2 && PATH_TREE.equals(paths.get(0));
    }

    public void onActivityResults(Context context, Intent data) {
        data.getData();
    }

    public void requestPermission(Context context) {
        if (!permissionGranted) {
            //new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).setFlags(67);
        }
    }

    public void requestPermission(BaseFragment fragment, int requestCode) {
        if (!permissionGranted) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            //intent.setFlags(67);
            fragment.startActivityForResult(Intent.createChooser(intent, "Select folder"), requestCode);
        }
    }

    public void createNewFile(String path) {
        int lastSep = path.lastIndexOf(File.separator);
        if (lastSep > 0) {
            String dirPath = path.substring(0, lastSep);
            makeDir(dirPath);
        }

        File file = new File(path);

        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile(String path) {
        createNewFile(path);

        StringBuilder sb = new StringBuilder();
        FileReader fr = null;
        try {
            fr = new FileReader(new File(path));

            char[] buff = new char[1024];
            int length = 0;

            while ((length = fr.read(buff)) > 0) {
                sb.append(new String(buff, 0, length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public void writeFile(String path, String str) {
        createNewFile(path);
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(new File(path), false);
            fileWriter.write(str);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null)
                    fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void copyFile(String sourcePath, String destPath) {
        if (!isExistFile(sourcePath)) return;
        createNewFile(destPath);

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(sourcePath);
            fos = new FileOutputStream(destPath, false);

            byte[] buff = new byte[1024];
            int length = 0;

            while ((length = fis.read(buff)) > 0) {
                fos.write(buff, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void copyDir(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        File[] files = oldFile.listFiles();
        File newFile = new File(newPath);
        if (!newFile.exists()) {
            newFile.mkdirs();
        }
        for (File file : files) {
            if (file.isFile()) {
                copyFile(file.getPath(), newPath + "/" + file.getName());
            } else if (file.isDirectory()) {
                copyDir(file.getPath(), newPath + "/" + file.getName());
            }
        }
    }

    public void moveFile(String sourcePath, String destPath) {
        copyFile(sourcePath, destPath);
        deleteFile(sourcePath);
    }

    public void deleteFile(String path) {
        File file = new File(path);

        if (!file.exists()) return;

        if (file.isFile()) {
            file.delete();
            return;
        }

        File[] fileArr = file.listFiles();

        if (fileArr != null) {
            for (File subFile : fileArr) {
                if (subFile.isDirectory()) {
                    deleteFile(subFile.getAbsolutePath());
                }

                if (subFile.isFile()) {
                    subFile.delete();
                }
            }
        }

        file.delete();
    }

    public boolean isExistFile(String path) {
        File file = new File(path);
        return file.exists();
    }

    public void makeDir(String path) {
        if (!isExistFile(path)) {
            File file = new File(path);
            file.mkdirs();
        }
    }

    public void listDir(String path, ArrayList<String> list) {
        File dir = new File(path);
        if (!dir.exists() || dir.isFile()) return;

        File[] listFiles = dir.listFiles();
        if (listFiles == null || listFiles.length <= 0) return;

        if (list == null) return;
        list.clear();
        for (File file : listFiles) {
            list.add(file.getAbsolutePath());
        }
    }

    public boolean isDirectory(String path) {
        if (!isExistFile(path)) return false;
        return new File(path).isDirectory();
    }

    public boolean isFile(String path) {
        if (!isExistFile(path)) return false;
        return new File(path).isFile();
    }

    public long getFileLength(String path) {
        if (!isExistFile(path)) return 0;
        return new File(path).length();
    }

    public String getExternalStorageDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public String getPackageDataDir(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath();
    }

    public String getPublicDir(String type) {
        return Environment.getExternalStoragePublicDirectory(type).getAbsolutePath();
    }

    public String convertUriToFilePath(final Context context, final Uri uri) {
        String path = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    path = Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);

                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                }

                final Uri contentUri = ContentUris
                        .withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                path = getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                path = getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            path = getDataColumn(context, uri, null, null);
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }

        if (path != null) {
            try {
                return URLDecoder.decode(path, "UTF-8");
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        final String column = MediaStore.Images.Media.DATA;
        final String[] projection = {
                column
        };

        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {

        }
        return null;
    }

    public interface FileLoaderDelegate {
        void fileUploadProgressChanged(String location, long uploadedSize, long totalSize, boolean isEncrypted);

        void fileDidUploaded(String location, byte[] key, byte[] iv, long totalFileSize);

        void fileDidFailedUpload(String location, boolean isEncrypted);

        void fileDidLoaded(String location, File finalFile, int type);

        void fileDidFailedLoad(String location, int state);

        void fileLoadProgressChanged(String location, long uploadedSize, long totalSize);
    }

}