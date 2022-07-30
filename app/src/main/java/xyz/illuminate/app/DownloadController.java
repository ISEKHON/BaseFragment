package xyz.illuminate.app;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.URLUtil;

import java.io.IOException;

public class DownloadController extends AsyncTask<String, Integer, String> {

    public static volatile DownloadController Instance;
    private static FileDownloadProgressListener mListener;
    private static String path, filename, result;
    private static double size, sumCount;
    public Context mContext;

    public DownloadController(Context context) {
        mContext = context;
    }

    public static DownloadController getInstance() {
        return Instance;
    }

    public static DownloadController getInstance(Context context) {
        if (Instance == null) {
            synchronized (UserConfig.class) {
                Instance = new DownloadController(context);
            }
        }
        return Instance;
    }

    public void setDownloadListener(FileDownloadProgressListener listener) {
        mListener = listener;
    }

    public void setPathWhereToDownload(String mPath) {
        if (mPath == null) {
            mListener.onFailedDownload("Path not found");
            return;
        }
        path = mPath;
    }

    public void setFileNameToDownload(String name) {
        if (name == null) {
            mListener.onFailedDownload("File name not found");
            return;
        }
        filename = name;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... address) {
        try {
            filename = URLUtil.guessFileName(address[0], null, null);
            int resCode = -1;
            java.io.InputStream in = null;
            java.net.URL url = new java.net.URL(address[0]);
            java.net.URLConnection urlConn = url.openConnection();
            if (!(urlConn instanceof java.net.HttpURLConnection)) {
                throw new java.io.IOException("URL is not an Http URL");
            }
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            resCode = httpConn.getResponseCode();
            if (resCode == java.net.HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
                size = httpConn.getContentLength();

            } else {
                result = "There was an error";
            }
            // path = FileLoader.getInstance().getExternalStorageDir().concat("/".concat("YOUR FOLDER".concat("/".concat("DOWNLOADS".concat("/".concat(filename))))));//ileLoader.getInstance().writeFile(path, "");
            java.io.File file = new java.io.File(path);

            java.io.OutputStream output = new java.io.FileOutputStream(file);
            try {
                int bytesRead;
                sumCount = 0;
                byte[] buffer = new byte[1024];
                while ((bytesRead = in.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    sumCount += bytesRead;
                    if (size > 0) {
                        publishProgress((int) Math.round(sumCount * 100 / size));
                    }
                }
            } finally {
                output.close();
            }
            result = "saved";
            in.close();
        } catch (IOException e) {
            result = e.getMessage();
        } catch (Exception e) {
            result = e.toString();
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mListener.onProgressDownload(values[values.length - 1]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s.equals("saved")) {
            mListener.onSuccessDownload(filename, true);
            return;
        }
        mListener.onFailedDownload(s);
    }

    public interface FileDownloadProgressListener {
        void onFailedDownload(String error);

        void onSuccessDownload(String fileName, boolean success);

        void onProgressDownload(long progress);
    }
}
