package xyz.illuminate.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class UserConfig extends BaseController {

    public static String TAG = "UserConfig";
    private static volatile UserConfig Instance;
    public boolean draftsLoaded;
    private final HashMap<String, String> data = new HashMap<>();

    public UserConfig(Context context) {
        super(context);
    }

    public static UserConfig getInstance(Context context) {
        if (UserConfig.Instance == null) {
            synchronized (UserConfig.class) {
                UserConfig.Instance = new UserConfig(context);
            }
        }
        return UserConfig.Instance;
    }

    public static UserConfig getInstance() {
        return Instance;
    }

    private SharedPreferences getPreferences() {
        return ApplicationLoader.applicationContext.getSharedPreferences(TAG, 0);
    }

    public boolean isClientActivated() {
        return getPreferences().getBoolean("loggedIn", false);
    }

    public boolean isClientInfoAvailable() {
        return getPreferences().getBoolean("info", false);
    }

    public boolean isClientExpired() {
        return getPreferences().getBoolean("expired", false);
    }

    public int isVipClient() {
        return getPreferences().getInt("isVip", 0);
    }

    public boolean isServerSelected() {
        return getPreferences().getBoolean("s", false);
    }

    public boolean isBitSelected() {
        return getPreferences().getBoolean("b", false);
    }

    public boolean isVersionSelected() {
        return getPreferences().getBoolean("v", false);
    }

    public int getId() {
        return getPreferences().getInt("id", 0);
    }

    public String getUsername() {
        return getPreferences().getString("username", "");
    }

    public String getPassword() {
        return getPreferences().getString("pass", "");
    }

    public String getKey() {
        return getPreferences().getString("key", "");
    }

    public String getClientExpiryTime() {
        return getPreferences().getString("expiry", "");
    }

    public String getSelectedVersion() {
        return getPreferences().getString("version", "Not Selected");
    }

    public String getSelectedVersionPackage() {
        return getPreferences().getString("pkgSelected", null);
    }

    public int getSelectedServer() {
        return getPreferences().getInt("selectedServer", 0);
    }

    public void setSelectedServer(int which) {
        final SharedPreferences.Editor edit = getPreferences().edit();
        edit.putInt("selectedServer", which);
        edit.putBoolean("s", true);
        edit.apply();
    }

    public int getSelectedBit() {
        return getPreferences().getInt("selectedBit", 0);
    }

    public void setSelectedBit(int which) {
        final SharedPreferences.Editor edit = getPreferences().edit();
        edit.putInt("selectedBit", which);
        edit.putBoolean("b", true);
        edit.apply();
    }

    public void setClientInfoAvailable() {
        final SharedPreferences.Editor edit = getPreferences().edit();
        edit.putBoolean("info", true);
        edit.apply();
    }

    public void setSelectedVersion(String version, String pkg) {
        final SharedPreferences.Editor edit = getPreferences().edit();
        edit.putString("version", version);
        edit.putString("pkg", pkg);
        edit.putBoolean("v", true);
        edit.apply();
    }

    public void saveTime(String time, int vip) {
        final SharedPreferences.Editor edit = getPreferences().edit();
        edit.putString("expiry", time);
        edit.putInt("isVip", vip);
        edit.apply();
    }

    /*TODO public void fetchClientData(BaseFragment fragment, String username) {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", getUserConfig().getUsername());
        map.put("sha256", AndroidUtilities.getCertificateSHA256Fingerprints());
        getConnectionsManager().post(BuildVars.DB + "getUserInfo.php", map, new ConnectionsManager.RequestListener() {
            @Override
            public void onErrorResponse(String error) {
                FileLog.e(error);
            }
            @Override
            public void onResponse(Object response) {
                    data = new Gson().fromJson((String) response, new TypeToken<HashMap<String, String>>() {
                    }.getType());
                try {
                    if (data.get("status").equals("success")) {
                        getPreferences().getString("UserData",data.toString());
                    } else {
                        AlertsCreator.processError(data, fragment);
                    }
                   } catch (Exception e) {
                    FileLog.d(e.toString());
                }
            }
            @Override
            public void onResponse(byte[] bytes) {}
        }, TAG);
        }*/

    public void saveLoginDetails(HashMap<String, String> data) {
        final SharedPreferences.Editor edit = getPreferences().edit();
        edit.putBoolean("loggedIn", true);
        edit.putString("username", data.get("username"));
        edit.putString("pass", data.get("pass"));
        edit.putString("key", data.get("key"));
        edit.apply();
    }

    public void logout() {
        final SharedPreferences.Editor edit = getPreferences().edit();
        edit.putBoolean("loggedIn", false);
        edit.putString("username", null);
        edit.putString("pass", null);
        edit.putString("key", null);
        edit.apply();
    }

    public void saveConfig(boolean b) {
    }

    public long getClientUserId() {
        return getId();
    }

    public class User {
        public static final int ADMIN = 1;
        public static final int DEV = 69;
        public static final int UNLIMITED = 50;
        public static final int DEFAULT = 0;
        public static final int VIP = 99;
    }
}
