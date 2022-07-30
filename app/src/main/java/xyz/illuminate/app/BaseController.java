package xyz.illuminate.app;


import android.content.Context;

import xyz.illuminate.server.ConnectionsManager;

public class BaseController {

    Context mContext;
    private AccountInstance parentAccountInstance;

    public BaseController(Context context) {
        this.mContext = context;
        parentAccountInstance = AccountInstance.getInstance(context);
    }

    public BaseController(int num) {
        // super(num);
    }

    public BaseController() {

    }

    protected final AccountInstance getAccountInstance() {
        return parentAccountInstance;
    }


    protected final MediaDataController getMediaDataController() {
        return parentAccountInstance.getMediaDataController();
    }

    protected final ConnectionsManager getConnectionsManager() {
        return parentAccountInstance.getConnectionsManager();
    }


    protected final NotificationCenter getNotificationCenter() {
        return parentAccountInstance.getNotificationCenter();
    }

    protected final UserConfig getUserConfig() {
        return parentAccountInstance.getUserConfig();
    }

    protected final FileLoader getFileLoader() {
        return parentAccountInstance.getFileLoader();
    }

}
