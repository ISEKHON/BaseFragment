package xyz.illuminate.app;

public interface CallBackTask {

    void onCopyPreExecute();

    void onCopyProgressUpdate(int progress);

    void onCopyPostExecute(String path, boolean wasSuccessful, String reason);
}