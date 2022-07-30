package xyz.illuminate.app;

import android.content.Context;

import xyz.illuminate.server.ConnectionsManager;

public class AccountInstance {

    private static volatile AccountInstance Instance;
    Context context;

    public AccountInstance(Context instance) {
        this.context = instance;
    }

    public static AccountInstance getInstance(Context context) {
        AccountInstance localInstance = Instance;
        if (localInstance == null) {
            synchronized (AccountInstance.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new AccountInstance(context);
                }
            }
        }
        return localInstance;
    }

    public static AccountInstance getInstance() {
        return Instance;
    }

    public MediaDataController getMediaDataController() {
        return MediaDataController.getInstance();
    }

    public ConnectionsManager getConnectionsManager() {
        return ConnectionsManager.getInstance(context);
    }

    public NotificationCenter getNotificationCenter() {
        return NotificationCenter.getInstance();
    }

    public UserConfig getUserConfig() {
        return UserConfig.getInstance(context);
    }

    public FileLoader getFileLoader() {
        return FileLoader.getInstance(0);
    }

    public DownloadController getDownloadController() {
        return DownloadController.getInstance(context);
    }
}
